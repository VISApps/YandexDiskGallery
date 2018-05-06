package org.visapps.yandexdiskgallery.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.visapps.yandexdiskgallery.YandexDiskGallery;
import org.visapps.yandexdiskgallery.models.DiskItem;
import org.visapps.yandexdiskgallery.models.PassportResponse;
import org.visapps.yandexdiskgallery.repository.SharedPrefsService;
import org.visapps.yandexdiskgallery.repository.YDGalleryDatabase;
import org.visapps.yandexdiskgallery.repository.YandexDiskService;
import org.visapps.yandexdiskgallery.repository.YandexPassportService;
import org.visapps.yandexdiskgallery.utils.RequestError;
import org.visapps.yandexdiskgallery.utils.SingleLiveEvent;

import java.io.IOException;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class MainActivityViewModel extends ViewModel{

    // Размер страницы
    private static final int PAGE_ITEMS_COUNT = 50;
    // Размер превью
    private static final String PREVIEW_SIZE = "XL";

    // Заголовок авторизации для запросов
    private String authorization;
    // Флаг, означающий, что страниц для загрзки больше нет
    private boolean havemore=true;
    // Флаг, означающий, что данные загружены и не нужно делат ьнвоый запрос при пересоздании активити из-за нехватки памяти
    private boolean dataloaded=false;

    private SharedPrefsService sharedPrefsService;
    private YandexDiskService yandexDiskService;
    private YandexPassportService yandexPassportService;
    private YDGalleryDatabase database;

    private SingleLiveEvent<Boolean> AuthObservable;
    private SingleLiveEvent<RequestError> ErrorObservable;
    private MutableLiveData<Boolean> RefresherObservable;
    private MutableLiveData<Boolean> LoadMoreObservable;
    private LiveData<List<DiskItem>> ItemsObservable;
    private LiveData<PassportResponse> PassportObserbable;

    private CompositeDisposable disposables;

    public MainActivityViewModel() {
        super();
        sharedPrefsService = YandexDiskGallery.getInstance().getSharedPrefsService();
        yandexDiskService = YandexDiskGallery.getInstance().getYandexDiskService();
        yandexPassportService = YandexDiskGallery.getInstance().getYandexPassportService();
        database = YandexDiskGallery.getInstance().getDatabase();
        disposables = new CompositeDisposable();
        AuthObservable = new SingleLiveEvent<>();
        ErrorObservable = new SingleLiveEvent<>();
        RefresherObservable = new MutableLiveData<>();
        LoadMoreObservable = new MutableLiveData<>();
    }

    public boolean isDataloaded() {
        return dataloaded;
    }

    public void setDataloaded(boolean dataloaded) {
        this.dataloaded = dataloaded;
    }

    public boolean isHavemore() {
        return havemore;
    }

    public void setHavemore(boolean havemore) {
        this.havemore = havemore;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Отписываемся от операций при уничтожении View Model
        disposables.clear();
    }


    // Инициализация после авторизации
    public void init(){
        PassportObserbable = database.passportResponseDao().getPassport();
        ItemsObservable = database.diskItemDao().getAll();
        String token = sharedPrefsService.getToken();
        if(token ==null){
            AuthObservable.postValue(false);
        }
        else{
            authorization = "OAuth " + token;
            loadPassport();
            refreshItems();
        }
    }

    // Выход из аккаунта
    public void logout(){
        dataloaded = false;
        havemore = true;
        // Очищаем данные
        disposables.add(Observable.just(clearData())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> {
                    AuthObservable.postValue(false); // Оповещаем активити о выхоед из аккаунта
                }));
    }

    public LiveData<Boolean> getAuthObservable(){
        return AuthObservable;
    }

    public LiveData<RequestError> getErrorObservable(){
        return ErrorObservable;
    }

    public LiveData<Boolean> getRefresherObservable(){
        return RefresherObservable;
    }

    public LiveData<Boolean> getLoadMoreObservable(){
        return LoadMoreObservable;
    }

    public LiveData<PassportResponse> getPassportObserbable(){
        if(PassportObserbable == null){
            PassportObserbable = database.passportResponseDao().getPassport();
        }
        return PassportObserbable;
    }

    public LiveData<List<DiskItem>> getItemsObservable(){
        if(ItemsObservable == null){
            // Если Activity запускается впервые, создаем Live Data и проверяем, сохранен ли токен
            ItemsObservable = database.diskItemDao().getAll();
            String token = sharedPrefsService.getToken();
            if(token ==null){
                // Если токен не сохранен, запрашиваем авторизацию.
                AuthObservable.postValue(false);
            }
            else{
                // Если токен сохранен, формируем заголовок авторизации и начинаем обновление данных
                authorization = "OAuth " + token;
                if(!dataloaded){
                    // Если активити не было пересоздано, требуется обновить данные
                    loadPassport();
                    refreshItems();
                }
            }
        }
        return ItemsObservable;
    }

    public void refreshItems(){
        disposables.add(yandexDiskService.getFiles(authorization,String.valueOf(PAGE_ITEMS_COUNT),"image","0",PREVIEW_SIZE,"true")
                .doOnSuccess(response -> {
                    // При успешной загрузке, если количество изображений меньше размера страницы, значит больше страниц нет
                    if(response.getItems().size() >=PAGE_ITEMS_COUNT){
                        havemore = true;
                    }
                    else{
                        havemore = false;
                    }
                })
                // фильтруем список изображений и удаляем из него изображения, у которых нет ссылки на превью
                .flatMap(response -> Observable.fromIterable(response.getItems())
                        .filter(item -> item.getPreview() != null)
                        .toList()
                        .map(items -> {
                            response.setItems(items);
                            return response;
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(__ -> RefresherObservable.postValue(true))
                .subscribe(response -> {
                    // в фоновом потоке добавляем полученные изображения в БД и обновляем состояние в активити
                    dataloaded = true;
                    database.diskItemDao().refresh(response.getItems());
                    RefresherObservable.postValue(false);
                }, throwable -> {
                    RefresherObservable.postValue(false);
                    handleError(throwable);
                }));
    }

    public void loadmoreItems(){
        if(havemore){
            disposables.add(database.diskItemDao().getCount() //получем количество ужезагруженных изображений и после этого выполняем загрузку новой страницы
                    .flatMap(count -> yandexDiskService.getFiles(authorization, String.valueOf(PAGE_ITEMS_COUNT),"image", String.valueOf(count-1), PREVIEW_SIZE,"true"))
                    .doOnSuccess(response -> {
                        // При успешной загрузке, если количество изображений меньше размера страницы, значит больше страниц нет
                        if(response.getItems().size() >=PAGE_ITEMS_COUNT){
                            havemore = true;
                        }
                        else{
                            havemore = false;
                        }
                        // Ожидание для плавного исчезновения индикатора загрузки
                        Thread.sleep(500);
                    })
                    // фильтруем список изображений и удаляем из него изображения, у которых нет ссылки на превью
                    .flatMap(response -> Observable.fromIterable(response.getItems())
                            .filter(item -> item.getPreview() != null)
                            .toList()
                            .map(items -> {
                                response.setItems(items);
                                return response;
                            }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnSubscribe(__ -> LoadMoreObservable.postValue(true))
                    .subscribe(response -> {
                        // в фоновом потоке добавляем полученные изображения в БД и обновляем состояние в активити
                        if(response.getItems().size() > 0){
                            database.diskItemDao().insert(response.getItems());
                        }
                        LoadMoreObservable.postValue(false);
                    }, throwable -> {
                        LoadMoreObservable.postValue(false);
                        handleError(throwable);
                    }));
        }
    }

    public void loadPassport(){
        disposables.add(yandexPassportService.getInfo("OAuth " + sharedPrefsService.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(response -> {
                    database.passportResponseDao().login(response); // Сохраняем данные аккаунта в БД
                }, throwable -> {

                }));
    }

    // Метод обработки ошибок
    private void handleError(Throwable t){
        if (t instanceof HttpException) {
            int code = ((HttpException) t).code();
            switch (code){
                case 403:
                    logout(); // Если код ошибки 403, значит авторизация с данным токеном не удалась и нужно заново запросить авторизацию
                    break;
                case 503:
                    ErrorObservable.postValue(RequestError.ServiceUnavailable); //Оповещаем активити о необходимости создания сообщения об ошибке
                    break;
                default:
                    ErrorObservable.postValue(RequestError.ServerError);
                    break;
            }
        }
        else if(t instanceof IOException){
            ErrorObservable.postValue(RequestError.NetworkError);
        }
        else{
            ErrorObservable.postValue(RequestError.UnknownError);
        }
    }

    // Метод для очистки данных
    private boolean clearData(){
        try{
            sharedPrefsService.clear();
            database.diskItemDao().deleteall();
            database.passportResponseDao().deletepassport();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }


}

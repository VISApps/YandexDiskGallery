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

    private static final int PAGE_ITEMS_COUNT = 50;
    private static final String PREVIEW_SIZE = "XL";

    private String authorization;
    private boolean havemore=true;
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
        disposables.clear();
    }


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

    public void logout(){
        dataloaded = false;
        havemore = true;
        disposables.add(Observable.just(clearData())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(response -> {
                    AuthObservable.postValue(false);
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
            ItemsObservable = database.diskItemDao().getAll();
            String token = sharedPrefsService.getToken();
            if(token ==null){
                AuthObservable.postValue(false);
            }
            else{
                authorization = "OAuth " + token;
                if(!dataloaded){
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
                    if(response.getItems().size() >=PAGE_ITEMS_COUNT){
                        havemore = true;
                    }
                    else{
                        havemore = false;
                    }
                })
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
            disposables.add(database.diskItemDao().getCount()
                    .flatMap(count -> yandexDiskService.getFiles(authorization, String.valueOf(PAGE_ITEMS_COUNT),"image", String.valueOf(count-1), PREVIEW_SIZE,"true"))
                    .doOnSuccess(response -> {
                        if(response.getItems().size() >=PAGE_ITEMS_COUNT){
                            havemore = true;
                        }
                        else{
                            havemore = false;
                        }
                        Thread.sleep(500);
                    })
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
                    database.passportResponseDao().login(response);
                }, throwable -> {

                }));
    }

    private void handleError(Throwable t){
        if (t instanceof HttpException) {
            int code = ((HttpException) t).code();
            switch (code){
                case 403:
                    logout();
                    break;
                case 503:
                    ErrorObservable.postValue(RequestError.ServiceUnavailable);
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

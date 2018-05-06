package org.visapps.yandexdiskgallery.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.visapps.yandexdiskgallery.YandexDiskGallery;
import org.visapps.yandexdiskgallery.models.DiskItem;
import org.visapps.yandexdiskgallery.repository.YDGalleryDatabase;

import java.util.List;

public class ImageActivityViewModel extends ViewModel{

    private YDGalleryDatabase database;
    private LiveData<List<DiskItem>> ItemsObservable;

    public ImageActivityViewModel(){
        super();
        database = YandexDiskGallery.getInstance().getDatabase();
    }

    // Live Data для получения списка изображений из БД
    public LiveData<List<DiskItem>> getItemsObservable(){
        if(ItemsObservable == null){
            ItemsObservable = database.diskItemDao().getAll();
        }
        return ItemsObservable;
    }
}

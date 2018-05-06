package org.visapps.yandexdiskgallery;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.visapps.yandexdiskgallery.repository.SharedPrefsService;
import org.visapps.yandexdiskgallery.repository.YDGalleryDatabase;
import org.visapps.yandexdiskgallery.repository.YandexDiskService;
import org.visapps.yandexdiskgallery.repository.YandexPassportService;

public class YandexDiskGallery extends Application{

    public static YandexDiskGallery instance;

    private SharedPrefsService sharedPrefsService;
    private YandexDiskService yandexDiskService;
    private YandexPassportService yandexPassportService;
    private YDGalleryDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPrefsService = new SharedPrefsService(getApplicationContext());
        yandexDiskService = new YandexDiskService();
        yandexPassportService = new YandexPassportService();
        database = Room.databaseBuilder(this, YDGalleryDatabase.class, "database").build();
    }

    public static YandexDiskGallery getInstance() {
        return instance;
    }

    public SharedPrefsService getSharedPrefsService(){
        return sharedPrefsService;
    }

    public YandexDiskService getYandexDiskService(){
        return yandexDiskService;
    }

    public YandexPassportService getYandexPassportService(){
        return yandexPassportService;
    }

    public YDGalleryDatabase getDatabase(){
        return database;
    }

}

package org.visapps.yandexdiskgallery.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.visapps.yandexdiskgallery.models.DiskItem;
import org.visapps.yandexdiskgallery.models.PassportResponse;

@Database(entities = {DiskItem.class, PassportResponse.class}, version = 1)
public abstract class YDGalleryDatabase extends RoomDatabase {

    // Класс для работы с базой данных Realm

    public abstract DiskItemDao diskItemDao();
    public abstract PassportResponseDao passportResponseDao();

}

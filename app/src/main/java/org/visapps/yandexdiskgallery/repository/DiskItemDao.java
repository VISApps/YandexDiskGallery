package org.visapps.yandexdiskgallery.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.visapps.yandexdiskgallery.models.DiskItem;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class DiskItemDao {

    @Query("SELECT * FROM diskitem")
    public abstract LiveData<List<DiskItem>> getAll();

    @Query("SELECT * FROM diskitem WHERE id = :id")
    public abstract DiskItem getById(long id);

    @Query("SELECT COUNT(*) FROM diskitem")
    public abstract Single<Integer> getCount();

    @Insert
    public abstract void insert(List<DiskItem> items);

    @Query("DELETE FROM diskitem")
    public abstract void deleteall();

    @Transaction
    public void refresh(List<DiskItem> items){
        deleteall();
        insert(items);
    }






}

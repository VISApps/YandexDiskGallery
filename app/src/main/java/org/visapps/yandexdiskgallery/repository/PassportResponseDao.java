package org.visapps.yandexdiskgallery.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.visapps.yandexdiskgallery.models.PassportResponse;

@Dao
public abstract class PassportResponseDao {

    @Query("SELECT * FROM passportresponse LIMIT 1")
    public abstract LiveData<PassportResponse> getPassport();

    @Insert
    public abstract void insert(PassportResponse response);

    @Query("DELETE FROM passportresponse")
    public abstract void deletepassport();

    @Transaction
    public void login(PassportResponse response){
        deletepassport();
        insert(response);
    }
}

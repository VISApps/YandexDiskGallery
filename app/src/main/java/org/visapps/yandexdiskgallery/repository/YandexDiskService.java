package org.visapps.yandexdiskgallery.repository;

import org.visapps.yandexdiskgallery.models.DiskResponse;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class YandexDiskService {

    // Сервис REST API Яндекс Диска

    private YandexDiskAPI api;
    private static final String URL = "https://cloud-api.yandex.net/v1/";

    public YandexDiskService(){
        // Инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(YandexDiskAPI.class);
    }

    public Single<DiskResponse> getLastUploaded(String token, String limit, String media_type, String preview_size, String preview_crop){
        return api.getLastUploaded(token,limit,media_type,preview_size,preview_crop);
    }

    public Single<DiskResponse> getFiles(String token, String limit, String media_type, String offset, String preview_size, String preview_crop){
        return api.getFiles(token,limit,media_type,offset,preview_size,preview_crop);
    }

}

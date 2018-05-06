package org.visapps.yandexdiskgallery.repository;

import org.visapps.yandexdiskgallery.models.PassportResponse;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class YandexPassportService {

    // Сервис REST API Яндекс Диска

    private YandexPassportAPI api;
    private static final String URL = "https://login.yandex.ru/";

    public YandexPassportService(){
        // Инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(YandexPassportAPI.class);
    }

    public Single<PassportResponse> getInfo(String token){
        return api.getInfo(token);
    }

}

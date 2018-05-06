package org.visapps.yandexdiskgallery.repository;

import org.visapps.yandexdiskgallery.models.PassportResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface YandexPassportAPI {

    // Интерфейс REST API Яндекс Паспорта. Методы возвращают Single для использования в связке с RxJava

    @GET("info?format=json")
    Single<PassportResponse> getInfo(@Header("Authorization") String token);
}

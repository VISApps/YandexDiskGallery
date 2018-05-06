package org.visapps.yandexdiskgallery.repository;

import org.visapps.yandexdiskgallery.models.PassportResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface YandexPassportAPI {

    @GET("info?format=json")
    Single<PassportResponse> getInfo(@Header("Authorization") String token);
}

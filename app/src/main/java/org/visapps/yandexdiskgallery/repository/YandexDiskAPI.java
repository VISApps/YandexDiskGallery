package org.visapps.yandexdiskgallery.repository;

import org.visapps.yandexdiskgallery.models.DiskResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YandexDiskAPI {

    @GET("disk/resources/last-uploaded")
    Single<DiskResponse> getLastUploaded(@Header("Authorization") String token, @Query("limit") String limit, @Query("media_type") String media_type, @Query("preview_size") String preview_size, @Query("preview_crop") String preview_crop);

    @GET("disk/resources/files")
    Single<DiskResponse> getFiles(@Header("Authorization") String token, @Query("limit") String limit, @Query("media_type") String media_type, @Query("offset") String offset, @Query("preview_size") String preview_size, @Query("preview_crop") String preview_crop);
}

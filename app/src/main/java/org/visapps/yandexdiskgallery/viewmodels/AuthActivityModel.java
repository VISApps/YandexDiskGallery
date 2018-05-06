package org.visapps.yandexdiskgallery.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;

import org.visapps.yandexdiskgallery.YandexDiskGallery;

public class AuthActivityModel {

    private AuthActivityCallback callback;
    private Context context;
    private YandexAuthSdk sdk;

    public AuthActivityModel(AuthActivityCallback callback, Context context){
        this.callback = callback;
        this.context = context;
        // Инициализируем YandexAuthSdk
        sdk = new YandexAuthSdk(context, new YandexAuthOptions(context, true));
    }

    public void auth(){
        // Создаем интент для активити авторизации
        callback.onCreateLoginIntent(sdk.createLoginIntent(context,null));
    }

    public void savedata(int resultCode, @Nullable Intent data){
        // Сохраняем токен при успешной авторизации
        try {
            final YandexAuthToken yandexAuthToken = sdk.extractToken(resultCode, data);
            if (yandexAuthToken != null) {
                YandexDiskGallery.getInstance().getSharedPrefsService().setToken(yandexAuthToken.getValue());
                callback.onAuth();
            }
        } catch (YandexAuthException e) {
            callback.onError();
        }
    }
}

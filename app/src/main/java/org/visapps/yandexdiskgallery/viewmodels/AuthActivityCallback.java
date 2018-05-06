package org.visapps.yandexdiskgallery.viewmodels;

import android.content.Intent;

public interface AuthActivityCallback {
    void onCreateLoginIntent(Intent intent);
    void onAuth();
    void onError();
}

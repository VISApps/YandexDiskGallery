package org.visapps.yandexdiskgallery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.visapps.yandexdiskgallery.R;
import org.visapps.yandexdiskgallery.viewmodels.AuthActivityCallback;
import org.visapps.yandexdiskgallery.viewmodels.AuthActivityModel;

public class AuthActivity extends AppCompatActivity implements AuthActivityCallback {

    // Код запроса для интента активити авторизации
    public static final int REQUEST_CODE_YA_LOGIN = 2001;
    // View модель
    private AuthActivityModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button loginbutton = findViewById(R.id.loginbutton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработчик нажатия кнопки авторизации
                model.auth();
            }
        });
        // Инциализируем View Model
        model = new AuthActivityModel(this,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_YA_LOGIN) {
            // Получаем результат из активити авторизации и просим модель сохранить данные
            model.savedata(resultCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCreateLoginIntent(Intent intent) {
        // Получем команду на запуск активити авторизации
        startActivityForResult(intent, REQUEST_CODE_YA_LOGIN);
    }

    @Override
    public void onAuth() {
        // При успешной авторизации отправляем результат в Main Activity
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onError() {
        // Тут планиурется показать что-то при ошибке авторизации
    }
}

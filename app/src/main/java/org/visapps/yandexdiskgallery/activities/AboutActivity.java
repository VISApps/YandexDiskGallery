package org.visapps.yandexdiskgallery.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.visapps.yandexdiskgallery.R;

public class AboutActivity extends AppCompatActivity {

    // Ссылка на Github репозиторий
    private static final String GITHUB_URI = "https://github.com/VISApps/YandexDiskGallery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Добавляем листенер для иконки навигации в тулбаре
                finish();
            }
        });
        CardView aboutcard = findViewById(R.id.aboutcard);
        aboutcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Пустой метод для создания riple эффекта при нажатии
            }
        });
        CardView githubcard = findViewById(R.id.githubcard);
        githubcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // При нажатии открываем Github репозиторий в браузере
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URI));
                startActivity(browserIntent);
            }
        });
        TextView version = findViewById(R.id.version);
        try{
            // Пытаемся получить название версии приложения
            String versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            version.setText(getString(R.string.version) + ": " + versionName);
        }
        catch(PackageManager.NameNotFoundException e){
            // В случае ошибки скрываем TextView с названием версии
            version.setVisibility(View.GONE);
        }
    }

}

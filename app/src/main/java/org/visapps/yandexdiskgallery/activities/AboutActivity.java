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

    private static final String GITHUB_URI = "https://github.com/VISApps/CinemaOnline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CardView aboutcard = findViewById(R.id.aboutcard);
        aboutcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        CardView githubcard = findViewById(R.id.githubcard);
        githubcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URI));
                startActivity(browserIntent);
            }
        });
        TextView version = findViewById(R.id.version);
        try{
            String versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            version.setText(getString(R.string.version) + ": " + versionName);
        }
        catch(PackageManager.NameNotFoundException e){
            version.setVisibility(View.GONE);
        }
    }

}

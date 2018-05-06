package org.visapps.yandexdiskgallery.repository;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefsService {

    private SharedPreferences account;

    public SharedPrefsService(Context context){
        account = context.getSharedPreferences("Account", MODE_PRIVATE);
    }

    public String getToken(){
        return account.getString("token",null);
    }

    public void setToken(String token){
        SharedPreferences.Editor editor = account.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void clear(){
        SharedPreferences.Editor editor = account.edit();
        editor.putString("token", null);
        editor.putString("name", null);
        editor.putString("email", null);
        editor.putString("avatar", null);
        editor.apply();
    }
}

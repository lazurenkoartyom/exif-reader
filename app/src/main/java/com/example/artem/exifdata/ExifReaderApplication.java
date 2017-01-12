package com.example.artem.exifdata;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.artem.exifdata.util.Constants;

/**
 * Created by Artem_Lazurenko on 12.01.2017.
 */

public class ExifReaderApplication extends Application {
    private static String dropBoxAuthToken = "";

    @Override
    public void onCreate() {
        super.onCreate();
        readPreferences();
    }

    public static String getDropBoxAuthToken() {
        return dropBoxAuthToken;
    }


    public void saveDBAuthToken(String accessToken) {
        SharedPreferences pref = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
        pref.edit().putString(Constants.KEY_DROPBOX_AUTH_TOKEN, accessToken).apply();
    }

    private void readPreferences() {
        SharedPreferences pref = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
        dropBoxAuthToken = pref.getString(Constants.KEY_DROPBOX_AUTH_TOKEN, "");
    }
}

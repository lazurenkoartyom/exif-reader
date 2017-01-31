package com.example.artem.exifdata;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.example.artem.exifdata.util.Constants;

/**
 * Created by Artem_Lazurenko on 12.01.2017.
 */

public class ExifReaderApplication extends Application {
    private static String dropBoxAuthToken = "";
    private static Context applicationContext;

    public static Context getAppContext() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
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

    public static void hideSoftInput(IBinder appWindowToken, int inputMethodManagerFlags) {
        int immFlags =
                ((inputMethodManagerFlags == InputMethodManager.HIDE_NOT_ALWAYS)
                        || (inputMethodManagerFlags == InputMethodManager.HIDE_IMPLICIT_ONLY)
                        || (inputMethodManagerFlags == 0))
                        ? inputMethodManagerFlags
                        : 0;

        ((InputMethodManager) getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(appWindowToken, immFlags);
    }
}

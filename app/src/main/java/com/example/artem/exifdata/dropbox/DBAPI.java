package com.example.artem.exifdata.dropbox;

import android.text.TextUtils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.util.Constants;

/**
 * Created by Artem_Lazurenko on 27.01.2017.
 */

public class DBAPI {
    private static DropboxAPI<AndroidAuthSession> mDBApi;

    private DBAPI(){
    }

    public static DropboxAPI<AndroidAuthSession> getInstance() {
        if(mDBApi == null) {
            synchronized (DBAPI.class) {
                if(mDBApi == null) {
                    initDBApi();
                    return mDBApi;
                }
            }
        }
        return mDBApi;
    }

    private static void initDBApi() {
        String dbAccessToken = ExifReaderApplication.getDropBoxAuthToken();
        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        if(TextUtils.isEmpty(dbAccessToken)) {
            mDBApi.getSession().startOAuth2Authentication(ExifReaderApplication.getAppContext());
        } else {
            mDBApi.getSession().setOAuth2AccessToken(dbAccessToken);
        }
    }
}

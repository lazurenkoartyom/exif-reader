package com.example.artem.exifdata.filelist.handler;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.MainActivity;
import com.example.artem.exifdata.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Artem_Lazurenko on 12.01.2017.
 */

public class FileListHandlerActivity extends Activity {
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private ArrayList<String> filelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filelist = getIntent().getStringArrayListExtra(Constants.KEY_FILELIST);

        initDropboxAPI();

/*
        if(!TextUtils.isEmpty(ExifReaderApplication.getDropBoxAuthToken())) {
            sendFiles(filelist);
            finish();
        }
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                ((ExifReaderApplication)getApplication()).saveDBAuthToken(accessToken);
                sendFiles(filelist);
                finish();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    private void initDropboxAPI() {
        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        mDBApi.getSession().startOAuth2Authentication(FileListHandlerActivity.this);
    }

    private void sendFiles(final ArrayList<String> filelist) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String fileName : filelist) {
                    File file = new File(fileName);
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        DropboxAPI.Entry response = mDBApi.putFile('/' + file.getName(), inputStream,
                                file.length(), null, null);
                        Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                    } catch (IOException | DropboxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

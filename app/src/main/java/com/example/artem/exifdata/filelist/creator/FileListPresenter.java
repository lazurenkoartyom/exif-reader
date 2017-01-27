package com.example.artem.exifdata.filelist.creator;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;

import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.filelist.FileListContract;
import com.example.artem.exifdata.filelist.handler.FilesHandler;
import com.example.artem.exifdata.mainscreen.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * Created by Artem_Lazurenko on 27.01.2017.
 */

public class FileListPresenter implements FileListContract.Presenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final Context context;
    private final FileListContract.FileListView view;
    private FilesHandler filesHandler;
    private GoogleApiClient mGoogleApiClient;

    public FileListPresenter(Context context, FilesHandler filesHandler, FileListContract.FileListView view) {
        this.context = context.getApplicationContext();
        this.filesHandler = filesHandler;
        this.view = view;
    }

    @Override
    public void start() {
        initGoogleAPIClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void handleFileList(List<String> fileList) {
        filesHandler.handleFileList(fileList);
    }

    private void initGoogleAPIClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ExifReaderApplication.getAppContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            Snackbar.make(coordinatorLayout, "Need to add permissions for coarse and fine location.", Snackbar.LENGTH_LONG);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        view.setLocation(location);
//        fab.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        fab.setEnabled(true);
    }
}

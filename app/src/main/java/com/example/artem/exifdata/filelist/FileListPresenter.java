package com.example.artem.exifdata.filelist;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.filelist.creator.FileListCreator;
import com.example.artem.exifdata.filelist.creator.MatchRegionPredicate;
import com.example.artem.exifdata.filelist.handler.FilesHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Artem_Lazurenko on 27.01.2017.
 */

public class FileListPresenter implements FileListContract.Presenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final Context context;
    private final FileListContract.FileListView view;
    private FilesHandler filesHandler;
    private GoogleApiClient mGoogleApiClient;
    private List<String> list = new LinkedList<>();
    private double radius = 20D;
    private Location location;
    {
        location = new Location("Fictive");
        location.setLatitude(49.233D);
        location.setLongitude(28.468D);
    }

    public FileListPresenter(Context context, FilesHandler filesHandler, FileListContract.FileListView view) {
        this.context = context.getApplicationContext();
        this.filesHandler = filesHandler;
        this.view = view;
    }

    @Override
    public void start() {
        initGoogleAPIClient();
        mGoogleApiClient.connect();
        view.setLocation(location);
        view.setRadius(radius);
    }

    @Override
    public void handleFileList(List<String> fileList) {
        filesHandler.handleFileList(fileList);
    }

    @Override
    public void reloadFileList() {
        loadFileList();
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
        Location loc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mGoogleApiClient.disconnect();
        if(location != null) {
            location = loc;
        }
        view.setLocation(location);
        loadFileList();
    }

    private void loadFileList() {
        String strRadius = view.getRadius();
        Double radius = 0D;
        if(!TextUtils.isEmpty(strRadius)) {
            radius = Double.valueOf(strRadius);
        }
        list.clear();
        FileListCreator.getFileList(list
                , new MatchRegionPredicate(location, radius)
                , Environment.getExternalStorageDirectory().getPath() + '/' + Environment.DIRECTORY_DCIM);
        view.showList(list);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}

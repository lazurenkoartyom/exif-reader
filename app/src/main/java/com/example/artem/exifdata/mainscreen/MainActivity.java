package com.example.artem.exifdata.mainscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.R;
import com.example.artem.exifdata.databinding.ActivityMainBinding;
import com.example.artem.exifdata.filelist.creator.FileListCreator;
import com.example.artem.exifdata.filelist.creator.MatchRegionPredicate;
import com.example.artem.exifdata.instantupload.ImageInstantUploadService;
import com.example.artem.exifdata.instantupload.UploadPicture;
import com.example.artem.exifdata.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String SEND_FILE_INTENT = "sfi";

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private GoogleApiClient mGoogleApiClient;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private List<String> list = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, ImageInstantUploadService.class);
        startService(i);
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABClick();
            }
        });

        initGoogleAPIClient();
        initDropboxAPI();
    }

    protected void onStart() {
        super.onStart();
        fab.setEnabled(false);
        mGoogleApiClient.connect();
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
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }

    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void initGoogleAPIClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initDropboxAPI() {
        String dbAccessToken = ExifReaderApplication.getDropBoxAuthToken();
        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        if(TextUtils.isEmpty(dbAccessToken)) {
            mDBApi.getSession().startOAuth2Authentication(this);
        } else {
            mDBApi.getSession().setOAuth2AccessToken(dbAccessToken);
        }
    }

    private void onFABClick() {
        list.clear();
        String strRadius = etRadius.getText().toString();
        Double radius = TextUtils.isEmpty(strRadius) ? 0D : Double.valueOf(strRadius);
        FileListCreator.getFileList(list
                , new MatchRegionPredicate(location, radius)
                , Environment.getExternalStorageDirectory().getPath() + '/' + Environment.DIRECTORY_DCIM);
        adapter.clear();
        if(list.isEmpty()) {
            adapter.notifyDataSetChanged();
            btnHandle.setVisibility(View.GONE);
        } else {
            adapter.addAll(list);
            btnHandle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Snackbar.make(coordinatorLayout, "Need to add permissions for coarse and fine location.", Snackbar.LENGTH_LONG);
                            return;
                        }
                        location = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        binding.setLocation(location);
                    }
                });
            }
        }, 1000);
//        fillparams(location);
        fab.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        fab.setEnabled(true);
    }
}

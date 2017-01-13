package com.example.artem.exifdata;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
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
import android.view.View;
import android.widget.EditText;

import com.example.artem.exifdata.databinding.ActivityMainBinding;
import com.example.artem.exifdata.filelist.creator.FileListCreator;
import com.example.artem.exifdata.filelist.creator.MatchRegionPredicate;
import com.example.artem.exifdata.filelist.handler.FileListHandlerActivity;
import com.example.artem.exifdata.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.clContainer)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.etCurrentLatitude)
    EditText etCurrentLatitude;
    @BindView(R.id.etCurrentLongitude)
    EditText etCurrentLongitude;
    @BindView(R.id.etRadius)
    EditText etRadius;
    @BindView(R.id.rvResults)
    RecyclerView rvResults;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.btnHandle)
    View btnHandle;

    private FileListAdapter adapter;
    private GoogleApiClient mGoogleApiClient;

    private Location location;
    private Double radius = 20D;
    private List<String> list = new LinkedList<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        location = new Location("Fictive");
        location.setLatitude(49.233D);
        location.setLongitude(28.468D);
        binding.setLocation(location);
        binding.setRadius(radius);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABClick();
            }
        });

        btnHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFileList();
            }
        });

        adapter = new FileListAdapter();
        rvResults.setAdapter(adapter);
        rvResults.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvResults.setNestedScrollingEnabled(false);

        initGoogleAPIClient();
    }

    protected void onStart() {
        fab.setEnabled(false);
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

    private void handleFileList() {
        Intent i = new Intent(this, FileListHandlerActivity.class);
        i.putStringArrayListExtra(Constants.KEY_FILELIST, new ArrayList<>(list));
        startActivity(i);
    }
}

package com.example.artem.exifdata;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.artem.exifdata.filelist.handler.FileListHandlerActivity;
import com.example.artem.exifdata.models.FileData;
import com.example.artem.exifdata.util.Constants;
import com.example.artem.exifdata.filelist.creator.FileListCreator;
import com.example.artem.exifdata.filelist.creator.MatchRegionPredicate;
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

    private FileListAdapter adapter;
    private GoogleApiClient mGoogleApiClient;

    private Location location;
    private List<String> list = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABClick();
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

    private void fillparams(Location location) {
        if (location != null) {
            etCurrentLatitude.setText(String.valueOf(location.getLatitude()));
            etCurrentLongitude.setText(String.valueOf(location.getLongitude()));
        }
    }

    private void onFABClick() {
        String strRadius = etRadius.getText().toString();
        Double radius = TextUtils.isEmpty(strRadius) ? 0D : Double.valueOf(strRadius);
        FileListCreator.getFileList(list
                , new MatchRegionPredicate(location, radius)
                , Environment.getExternalStorageDirectory().getPath() + '/' + Environment.DIRECTORY_DCIM);
        adapter.clear();
        adapter.addAll(list);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        fillparams(location);
        fab.setEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        location = new Location("Fictive");
        location.setLatitude(49.233D);
        location.setLongitude(28.468D);
        fillparams(location);
        fab.setEnabled(true);
    }

    public void handleFileList(View view) {
        Intent i = new Intent(this, FileListHandlerActivity.class);
        i.putStringArrayListExtra(Constants.KEY_FILELIST, new ArrayList<>(list));
        startActivity(i);
    }
}

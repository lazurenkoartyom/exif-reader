package com.example.artem.exifdata.mainscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.artem.exifdata.ExifReaderApplication;
import com.example.artem.exifdata.R;
import com.example.artem.exifdata.dropbox.DBAPI;
import com.example.artem.exifdata.filelist.handler.DBUploadFileListHandler;
import com.example.artem.exifdata.filelist.FileListContract;
import com.example.artem.exifdata.filelist.FileListPresenter;
import com.example.artem.exifdata.instantupload.ImageInstantUploadService;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1235;
    private static final String TAG = MainActivity.class.getCanonicalName();

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private FileListContract.Presenter presenter;
    private Uri uri;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent i = new Intent(this, ImageInstantUploadService.class);
        startService(i);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout mDrawerLayout = ButterKnife.findById(this, R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.drawer_opened
                , R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(mDrawerToggle != null)
                mDrawerToggle.syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mDrawerToggle.syncState();
            }
        };

        mDrawerToggle.syncState();

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.camera_navigation_menu_item) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String targetFilename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
                    uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, targetFilename);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.setType("application/vnd.google.panorama360+jpg");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                    return true;
                } else if (item.getItemId() == R.id.view_panorama_navigation_menu_item) {
                    StreetViewPanoramaFragment streetViewPanoramaFragment = new StreetViewPanoramaFragment();
                    streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                            new OnStreetViewPanoramaReadyCallback() {
                                @Override
                                public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                                    // Only set the panorama to SYDNEY on startup (when no panoramas have been
                                    // loaded which is when the savedInstanceState is null).
                                    if (savedInstanceState == null) {
                                        LatLng latLng = new LatLng(51.5138173,-0.1197711);
                                        panorama.setPosition(latLng);
                                    }
                                }
                            });
                    getFragmentManager().beginTransaction().add(R.id.coordinatorLayout, streetViewPanoramaFragment).addToBackStack("street").commit();
                    mDrawerLayout.closeDrawers();
                    ExifReaderApplication.hideSoftInput(mDrawerLayout.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        HomeFragment fragment = new HomeFragment();
        getFragmentManager().beginTransaction().add(R.id.coordinatorLayout, fragment).commit();

        presenter = new FileListPresenter(this, new DBUploadFileListHandler(getApplicationContext()
                , DBAPI.getInstance()
                , ""), fragment);
        fragment.setPresenter(presenter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABClick();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, uri.toString());
        }
    }

    private void onFABClick() {
        presenter.reloadFileList();
    }
}

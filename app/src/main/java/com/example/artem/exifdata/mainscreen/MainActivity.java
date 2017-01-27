package com.example.artem.exifdata.mainscreen;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.artem.exifdata.R;
import com.example.artem.exifdata.dropbox.DBAPI;
import com.example.artem.exifdata.dropbox.DBUploadFileListHandler;
import com.example.artem.exifdata.filelist.FileListContract;
import com.example.artem.exifdata.filelist.creator.FileListPresenter;
import com.example.artem.exifdata.instantupload.ImageInstantUploadService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String SEND_FILE_INTENT = "sfi";

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    private FileListContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent i = new Intent(this, ImageInstantUploadService.class);
        startService(i);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        HomeFragment fragment = new HomeFragment();
        getFragmentManager().beginTransaction().add(R.id.coordinatorLayout, fragment).commit();

        presenter = new FileListPresenter(this, new DBUploadFileListHandler(getApplicationContext()
                , DBAPI.getInstance()
                , ""), fragment);

        presenter.start();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFABClick();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        fab.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
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
*/

    }

    private void onFABClick() {
/*
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
*/
    }
}

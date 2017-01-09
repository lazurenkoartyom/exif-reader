package com.example.artem.exifdata;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tvLattitude)    TextView tvLattitude;
    @BindView(R.id.tvLongitude)    TextView tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenFileClick(view);
            }
        });
    }

    private void onOpenFileClick(View view) {
        File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        OpenFileDialog fileDialog = new OpenFileDialog(this, mPath, null);
        fileDialog.addFileListener(new OpenFileDialog.FileSelectedListener() {
            public void fileSelected(String file) {
                readAndDisplayMetadata(file);
            }
        });
        fileDialog.showDialog();
    }

    void readAndDisplayMetadata( String file ) {
        try {
            ExifInterface exifInterface = new ExifInterface(file);
            float[] coords = new float[2];
            exifInterface.getLatLong(coords);
            tvLattitude.setText(String.valueOf(coords[0]));
            tvLongitude.setText(String.valueOf(coords[1]));
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

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
}

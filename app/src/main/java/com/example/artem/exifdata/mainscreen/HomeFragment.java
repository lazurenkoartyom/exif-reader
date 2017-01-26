package com.example.artem.exifdata.mainscreen;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.artem.exifdata.R;
import com.example.artem.exifdata.filelist.FileListAdapter;
import com.example.artem.exifdata.filelist.FileListContract;
import com.example.artem.exifdata.instantupload.UploadPicture;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Artem_Lazurenko on 26.01.2017.
 */

public class HomeFragment extends Fragment implements FileListContract {
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
    @BindView(R.id.btnHandle)
    View btnHandle;

    private FileListAdapter adapter;
    private ViewDataBinding binding;
    private Location location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        ButterKnife.bind(binding.getRoot());

        location = new Location("Fictive");
        location.setLatitude(49.233D);
        location.setLongitude(28.468D);

        adapter = new FileListAdapter();
        rvResults.setAdapter(adapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvResults.setNestedScrollingEnabled(false);

        btnHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFileList();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setLocation(Location location) {
        binding.setLocation(location);
    }

    public void setRadius(double radius) {
        binding.setRadius(radius);
    }

    private void handleFileList() {
        new UploadPicture(this, mDBApi, "", list).execute();
    }
}

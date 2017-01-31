package com.example.artem.exifdata.mainscreen;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.annotations.NonNull;
import com.example.artem.exifdata.R;
import com.example.artem.exifdata.databinding.FragmentMainBinding;
import com.example.artem.exifdata.filelist.FileListAdapter;
import com.example.artem.exifdata.filelist.FileListContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Artem_Lazurenko on 26.01.2017.
 */

public class HomeFragment extends Fragment implements FileListContract.FileListView {
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
    private FragmentMainBinding binding;
    private FileListContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        ButterKnife.bind(this, binding.getRoot());

        adapter = new FileListAdapter();
        rvResults.setAdapter(adapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvResults.setNestedScrollingEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void setLocation(@NonNull Location location) {
        binding.setLocation(location);
    }

    @Override
    public void setRadius(double radius) {
        binding.setRadius(radius);
    }

    @Override
    public void setPresenter(FileListContract.Presenter fileListPresenter) {
        this.presenter = fileListPresenter;
    }

    @Override
    public void showList(final List<String> fileNames) {
        if(fileNames.isEmpty()) {
            btnHandle.setVisibility(View.GONE);
        } else {
            btnHandle.setVisibility(View.VISIBLE);
            btnHandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.handleFileList(fileNames);
                }
            });
        }
        adapter.updateDataSource(fileNames);
    }

    @Override
    public String getRadius() {
        return etRadius.getText() == null ? null : etRadius.getText().toString();
    }
}

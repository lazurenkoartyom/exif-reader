package com.example.artem.exifdata;

import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.artem.exifdata.models.FileData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Artem_Lazurenko on 11.01.2017.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private List<String> dataSource = new LinkedList<>();

    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FileListAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private String getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public boolean addAll(Collection<String> items) {
        boolean added = dataSource.addAll(items);
        notifyDataSetChanged();
        return added;
    }

    private boolean addItemIntern(String item) {
        boolean added = dataSource.add(item);
        return added;
    }

    public void clear() {
        dataSource.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFileName)  TextView tvFileName;
        @BindView(R.id.tvLatitude) TextView tvLatitude;
        @BindView(R.id.tvLongitude) TextView tvLongitude;
        private float[] coord = new float[2];
        private boolean isGeoTagPresent;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String item) {
            try {
                isGeoTagPresent = new ExifInterface(item).getLatLong(coord);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            tvFileName.setText(item);
            if(isGeoTagPresent) {
                tvLatitude.setText(String.valueOf(coord[0]));
                tvLongitude.setText(String.valueOf(coord[1]));
            }
        }
    }
}

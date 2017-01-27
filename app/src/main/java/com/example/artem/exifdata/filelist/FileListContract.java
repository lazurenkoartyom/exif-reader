package com.example.artem.exifdata.filelist;

import android.location.Location;

import com.example.artem.exifdata.BasePresenter;
import com.example.artem.exifdata.BaseView;

import java.util.List;

/**
 * Created by Artem_Lazurenko on 26.01.2017.
 */
public interface FileListContract {
    interface FileListView extends BaseView<Presenter> {

        void setLocation(Location location);

        void setRadius(double radius);
    }


    interface Presenter extends BasePresenter {
        void handleFileList(List<String> fileList);
    }
}

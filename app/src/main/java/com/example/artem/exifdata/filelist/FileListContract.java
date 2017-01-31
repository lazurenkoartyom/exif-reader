package com.example.artem.exifdata.filelist;

import android.location.Location;
import android.support.annotation.NonNull;

import com.example.artem.exifdata.BasePresenter;
import com.example.artem.exifdata.BaseView;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Artem_Lazurenko on 26.01.2017.
 */
public interface FileListContract {
    interface FileListView extends BaseView<Presenter> {

        void setLocation(@NonNull Location location);

        void setRadius(double radius);

        void showList(List<String> fileNames);

        String getRadius();
    }


    interface Presenter extends BasePresenter {
        void handleFileList(List<String> fileList);

        void reloadFileList();
    }
}

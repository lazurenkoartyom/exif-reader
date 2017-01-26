package com.example.artem.exifdata.filelist;

import com.example.artem.exifdata.BasePresenter;
import com.example.artem.exifdata.BaseView;

/**
 * Created by Artem_Lazurenko on 26.01.2017.
 */
public interface FileListContract {
    interface FileListView extends BaseView<FileListPresenter> {

    }


    interface FileListPresenter extends BasePresenter {
    }
}

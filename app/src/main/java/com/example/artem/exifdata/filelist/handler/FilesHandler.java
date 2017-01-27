package com.example.artem.exifdata.filelist.handler;

import java.util.List;

/**
 * Created by Artem_Lazurenko on 27.01.2017.
 */

public interface FilesHandler {
    void handleFileList(List<String> fileList);

    void handleFile(String fileName);
}

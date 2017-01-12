package com.example.artem.exifdata.filelist.creator;

import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.internal.util.Predicate;
import com.example.artem.exifdata.models.FileData;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by Artem_Lazurenko on 11.01.2017.
 */

public class FileListCreator {
    private static final String TAG = FileListCreator.class.getCanonicalName();

    /**
     * Returns list of files that fits the predicate
     * @return List<String> - list of filenames
     */
    public static boolean getFileList(final List<String> fileList, final @NonNull Predicate<String> predicate, final @NonNull String basePath) {
        final File f = new File(basePath);
        boolean isFile;

        try {
            isFile = f.isFile();
        } catch (SecurityException sEx) {
            Log.d(TAG, sEx.getLocalizedMessage());
            return false;
        }

        if(isFile) {
            return predicate.apply(basePath) && fileList.add(basePath);
        } else {
            if(f.list() == null) return false;
            for(String path : f.list()) {
                getFileList(fileList, predicate, basePath + '/' + path);
            }
            return true;
        }
    }
}

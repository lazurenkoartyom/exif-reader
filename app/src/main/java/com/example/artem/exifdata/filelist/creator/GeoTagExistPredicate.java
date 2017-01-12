package com.example.artem.exifdata.filelist.creator;

import android.media.ExifInterface;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.io.IOException;

/**
 * Created by Artem_Lazurenko on 11.01.2017.
 */

public class GeoTagExistPredicate implements Predicate<String> {
    private static final String TAG = GeoTagExistPredicate.class.getCanonicalName();

    @Override
    public boolean apply(String fileName) {
        try {
            ExifInterface exifInterface = new ExifInterface(fileName);
            float[] coordinates = new float[2];
            boolean geoTagPresent = exifInterface.getLatLong(coordinates);
            return geoTagPresent;
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }
}

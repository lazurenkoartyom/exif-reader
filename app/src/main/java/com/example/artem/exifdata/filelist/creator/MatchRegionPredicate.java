package com.example.artem.exifdata.filelist.creator;

import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import com.android.internal.util.Predicate;

import java.io.IOException;

/**
 * Created by Artem_Lazurenko on 11.01.2017.
 */

public class MatchRegionPredicate implements Predicate<String> {
    private static final String TAG = MatchRegionPredicate.class.getCanonicalName();

    private final double radius;
    private final Location location;

    public MatchRegionPredicate(Location location, double radius) {
        this.location = location;
        this.radius = radius;
    }

    @Override
    public boolean apply(String fileName) {
        try {
            ExifInterface exifInterface = new ExifInterface(fileName);
            float[] coordinates = new float[2];
            boolean geoTagPresent = exifInterface.getLatLong(coordinates);
            if(geoTagPresent) {
                Location photoLocation = new Location("artificial");
                photoLocation.setLatitude(coordinates[0]);
                photoLocation.setLongitude(coordinates[1]);
                float distance = location.distanceTo(photoLocation);
                return distance < radius;
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }
}

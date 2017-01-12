package com.example.artem.exifdata.models;

/**
 * Created by Artem_Lazurenko on 11.01.2017.
 */
public class FileData {
    private float[] coordinates;
    private String fileName;

    public FileData(float[] coordinates, String fileName) {
        this.coordinates = coordinates;
        this.fileName = fileName;
    }

    public float getLatitude() {
        return coordinates[0];
    }

    public float getLongitude() {
        return coordinates[1];
    }

    public String getFileName() {
        return fileName;
    }
}

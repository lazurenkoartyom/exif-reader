package com.example.artem.exifdata.instantupload;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.artem.exifdata.dropbox.DBAPI;
import com.example.artem.exifdata.filelist.handler.DBUploadFileListHandler;

/**
 * Created by Artem_Lazurenko on 13.01.2017.
 */
public class ImageInstantUploadService extends IntentService {
    public static final String TAG = ImageInstantUploadService.class.getCanonicalName();

    private PhotosObserver instUploadObserver = new PhotosObserver();

    public ImageInstantUploadService(){
        super("");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ImageInstantUploadService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false,
                        instUploadObserver);
        Log.d(TAG, "registered content observer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private class PhotosObserver extends ContentObserver {

        public PhotosObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            String fileName = readFromMediaStore(getApplicationContext(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Log.d(TAG, "detected picture" + fileName);
            new DBUploadFileListHandler(getBaseContext(), DBAPI.getInstance(), "").execute(fileName);
        }
    }

    private String readFromMediaStore(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, "date_added DESC");
        if (cursor.moveToNext()) {
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(dataColumn);
/*
            int mimeTypeColumn = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
            String mimeType = cursor.getString(mimeTypeColumn);
*/
            return filePath;
        }
        cursor.close();
        return "";
    }
}

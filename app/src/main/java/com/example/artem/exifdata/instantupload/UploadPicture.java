package com.example.artem.exifdata.instantupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

/**
 * Here we show uploading a file in a background thread, trying to show
 * typical exception handling and flow of control for an app that uploads a
 * file from Dropbox.
 */
public class UploadPicture extends AsyncTask<Void, Long, Boolean> {
    public static final String TAG = UploadPicture.class.getCanonicalName();

    private DropboxAPI<?> mApi;
    private String mPath;
    private List<String> mFileNames;

    private DropboxAPI.UploadRequest mRequest;
    private Context mContext;

    private String mErrorMsg;


    public UploadPicture(Context context, DropboxAPI<?> api, String dropboxPath,
                         List<String> fileNames) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mApi = api;
        mPath = dropboxPath;
        mFileNames = fileNames;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = true;
        for (String filename : mFileNames) {
            success &= sendFile(filename);
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.d(TAG, "files " + Arrays.toString(mFileNames.toArray()) + " uploaded successfully.");
            showToast("Image successfully uploaded");
        } else {
            showToast(mErrorMsg);
        }
    }

    private boolean sendFile(String filename) {
        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(filename);
            String path = mPath + file.getName();
            mRequest = mApi.putFileOverwriteRequest(path, fis, file.length(), null);

            if (mRequest != null) {
                mRequest.upload();
                return true;
            }

        } catch (DropboxUnlinkedException e) {
            // This session wasn't authenticated properly or user unlinked
            mErrorMsg = "This app wasn't authenticated properly.";
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "This file is too big to upload";
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Upload canceled";
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be
                // thumbnailed)
            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
            } else {
                // Something else
            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
        } catch (FileNotFoundException e) {
        }
        return false;
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }
}

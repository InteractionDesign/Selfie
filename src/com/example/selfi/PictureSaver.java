package com.example.selfi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PictureSaver implements PictureCallback {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "PictureSaver";
    private Activity parent; // reference to activity that called this picture saver method
    private String mCurrentPhotoPath;

    public PictureSaver(Activity parent) {
        this.parent = parent;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return;
        }
        mCurrentPhotoPath = pictureFile.getAbsolutePath();
        galleryAddPic();

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        } 

        // send the path back to "welcome" activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("photoPath", mCurrentPhotoPath);
        parent.setResult(Activity.RESULT_OK, resultIntent);
        
        // kill the activity
        parent.finish();
    }

    /** Create a File for saving an image or video */
    @SuppressLint("SimpleDateFormat")
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Selfiapp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        parent.sendBroadcast(mediaScanIntent);
    }
}
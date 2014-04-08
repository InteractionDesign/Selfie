package com.example.selfi;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity caller;
    private Size mPictureSize; // width = 1600, height = 1200 works

    public CameraPreview(Activity caller, Camera camera) {
        super(caller);
        this.caller = caller;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        
        List<Size> picSizes = mCamera.getParameters().getSupportedPictureSizes();
        mPictureSize = getOptimalPictureSize(picSizes);
    }
    
    private Size getOptimalPictureSize(List<Size> pictureSizes){
    	// we assume they are ordered from highest to lowest
    	// we want to find one with 4/3 aspect ratio
    	for (Size size: pictureSizes) {
    		if (((double) size.width / 4) - ((double) size.height / 3) < 0.0001) {
    			return size;
    		}
    	}
    	return pictureSizes.get(0);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mPictureSize.height, mPictureSize.width);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("dimochka", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    	
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){ }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Parameters parameters = mCamera.getParameters();
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);

        mCamera.setParameters(parameters);
        // here 1 stands for front camera
        setCameraDisplayOrientation(this.caller, 1);        
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("dimochka", "Error starting camera preview: " + e.getMessage());
        }
    }
    
    public void setCameraDisplayOrientation(Activity activity, int cameraId) {
        CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }
    
 
    
}
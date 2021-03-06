package com.example.selfi;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Activity caller;
	private Size mPictureSize; // width = 1600, height = 1200 works
	private int currentEffect;

	public CameraPreview(Activity caller, Camera camera) {
		super(caller);
		this.caller = caller;
		mCamera = camera;
		currentEffect = 0;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);

		List<Size> picSizes = mCamera.getParameters().getSupportedPictureSizes();
		mPictureSize = getOptimalPictureSize(picSizes);
	}

	@SuppressLint("NewApi")
	private Size getOptimalPictureSize(List<Size> pictureSizes) {
		// we assume they are ordered from highest to lowest
		// we want to find one with 4/3 aspect ratio
		Display display = caller.getWindowManager().getDefaultDisplay();
		Point windowSize = new Point();
		display.getSize(windowSize);
		int width = windowSize.x;

		for (Size size : pictureSizes) {
			if (((double) size.width / 4) - ((double) size.height / 3) < 0.0001) {
				if (!(size.width < width * 0.90)) {
					return size;
				}
			}
		}
		return getBestPreviewSize();
	}
	
	@SuppressLint("NewApi")
	private Camera.Size getBestPreviewSize()
	  {
	        Display display = caller.getWindowManager().getDefaultDisplay();
	        Point windowSize = new Point();
	        display.getSize(windowSize);
	        int width = windowSize.x;
	        int height = windowSize.y;

	        Camera.Size result = null;
	        Camera.Parameters p = mCamera.getParameters();
	        for (Camera.Size size : p.getSupportedPreviewSizes()) {
	            if ((size.width <= width && size.height <= height)
	                    || (size.height <= width && size.width <= height)) {
	                if (result == null) {
	                    result = size;
	                } else {
	                    int resultArea = result.width * result.height;
	                    int newArea = size.width * size.height;

	                    if ((newArea > resultArea)) {
	                        result = size;
	                    }
	                }
	            }
	        }
	        result.width = 1280;
	        result.height = 720;
	        return result;
	  }


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mPictureSize.height, mPictureSize.width);
	}

	public void surfaceCreated(SurfaceHolder holder) {
	    if (mCamera == null) {
	        return;
	    }
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			changeFilter("first");
		} catch (IOException e) {
			Log.d("dimochka", "Error setting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	
	public void setCamera(Camera cam) {
	    mCamera = cam;
	}

	

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	    // If your preview can change or rotate, take care of those events here.
	    // Make sure to stop the preview before resizing or reformatting it.

	    if (mHolder.getSurface() == null) {
	      // preview surface does not exist
	      return;
	    }

	    // stop preview before making changes
	    try {
	      mCamera.stopPreview();
	    } catch (Exception e) {
	    }

	    // set preview size and make any resize, rotate or
	    // reformatting changes here
	    Parameters parameters = mCamera.getParameters();
	    mPictureSize = getBestPreviewSize();
	        //parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
	        parameters.setPreviewSize(mPictureSize.width, mPictureSize.height);

	        mCamera.setParameters(parameters);
	    // here 1 stands for front camera
	    setCameraDisplayOrientation(this.caller, 1);
	    // start preview with new settings
	    try {
	      mCamera.setPreviewDisplay(mHolder);
	      mCamera.startPreview();

	    } catch (Exception e) {
	      Log.d("dimochka",
	          "Error starting camera preview: " + e.getMessage());
	    }
	  } 

	public void changeFilter(String direction){
		Parameters parameters = mCamera.getParameters();
		//parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
		List<String> effectList = parameters.getSupportedColorEffects();
		
		// prevent the app from crashing on the phones that do not support colours
		if (effectList != null) {
		    
    		
    		if(direction.equalsIgnoreCase("right")){
    			currentEffect += 1;
    			if(currentEffect == effectList.size()){
    				currentEffect = 0;
    			}
    		}else if(direction.equalsIgnoreCase("left")){
    			currentEffect -= 1;
    			if(currentEffect == -1){
    				currentEffect = effectList.size() -1;
    			}
    		}else{
    			currentEffect = 0;
    		}
    		parameters.setColorEffect(effectList.get(currentEffect));
    		
		} 

		mCamera.setParameters(parameters);
	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId) {
		CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {            
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}


}
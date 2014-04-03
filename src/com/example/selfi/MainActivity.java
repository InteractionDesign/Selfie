package com.example.selfi;


import com.example.selfi.ShakeDetector.OnShakeListener;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import 	android.os.CountDownTimer;

public class MainActivity extends Activity {
	
	private Camera mCamera;
    private CameraPreview mPreview;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private static boolean alreadyShook = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
//        Button captureButton = (Button) findViewById(R.id.button_capture);
//        captureButton.setOnClickListener(new View.OnClickListener() {
//        	@Override public void onClick(View v) {
//            	startCountdown();
//            }
//        });
      
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        
        
        
        mShakeDetector.setOnShakeListener(new OnShakeListener() {
        	 @Override public void onShake(int count) {
        		 if (alreadyShook) {
        			 return;
        		 }
        		 alreadyShook = true;
        		 startCountdown();
            }
        });   
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(1); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
        mSensorManager.unregisterListener(mShakeDetector);
    }
	
	@Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        
    }
	
	public void startCountdown() {
		new CountDownTimer(5005, 1000) {
            public void onTick(long millisUntilFinished) {
            	final Toast toast = Toast.makeText(MainActivity.this, 
            			"seconds remaining: " + millisUntilFinished / 1000, 
            			Toast.LENGTH_SHORT);
            	toast.show();
            	Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       toast.cancel(); 
                   }
            }, 999);
            }

            public void onFinish() {
            	Toast.makeText(MainActivity.this, 
            			"done!", 
            			Toast.LENGTH_SHORT).show();
            	mCamera.takePicture(null, null, new PictureSaver());
            	alreadyShook = false;
            }
         }.start();  
	}
}

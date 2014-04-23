package com.example.selfi;


import com.example.selfi.ShakeDetector.OnShakeListener;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
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
		
		// hide the title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// Create an instance of Camera
		getCameraInstance();
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
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getCameraInstance(){
		if (mCamera == null) {
			
			//
			int defaultCameraId = 0;
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			    Camera.getCameraInfo(i, cameraInfo);
			    if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
			        defaultCameraId = i;
			    }
			}
			
			try {
				mCamera = Camera.open(defaultCameraId); 
			}
			catch (Exception e){
				Log.d("CameraActivity", "Error when getting a camera instance: " + e.getMessage());
			}
		}
	}

	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
		mSensorManager.unregisterListener(mShakeDetector);
	}

	@Override
	public void onResume() {
		super.onResume();
		getCameraInstance();
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
				
				MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.beep2);
                mp.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }

                });   
                mp.start();
			}

			public void onFinish() {
				Toast.makeText(MainActivity.this, 
						"done!", 
						Toast.LENGTH_SHORT).show();
				mCamera.takePicture(null, null, new PictureSaver(MainActivity.this));
				alreadyShook = false;
			}
		}.start();  
	}
	
	
}

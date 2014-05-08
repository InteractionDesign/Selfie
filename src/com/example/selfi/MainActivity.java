package com.example.selfi;



import com.example.selfi.ShakeDetector.OnShakeListener;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import 	android.os.CountDownTimer;

public class MainActivity extends Activity implements SensorEventListener{

	private static final float SHAKE_THRESHOLD_GRAVITY = 2.3F;
	public Camera mCamera;
	private CameraPreview mPreview;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private static boolean alreadyShook = false;
	private SensorManager sensorManager;
	private MediaPlayer mp;
	private boolean facingDown;
	private FrameLayout preview;
	private DrawStuff dw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		// hide the title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// Create an instance of Camera
		getCameraInstance();
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		dw = new DrawStuff(this.getApplicationContext(), 1);
		preview.addView(dw);
		facingDown = false;



		//        Button captureButton = (Button) findViewById(R.id.button_capture);
		//        captureButton.setOnClickListener(new View.OnClickListener() {
		//        	@Override public void onClick(View v) {
		//            	startCountdown();
		//            }
		//        });

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();
		//mCamera.startPreview();

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

			try{
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}catch(Exception e){
				Log.d("CameraActivity", "Kan inte stoppa kameran! " + e.getMessage());
			}
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
		dw.actionStarted(true);
		preview.postInvalidate();
		new CountDownTimer(2005, 1000) {
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

				mp = MediaPlayer.create(MainActivity.this, R.raw.beep2);
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
				mp = MediaPlayer.create(MainActivity.this, R.raw.shutter);
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
					}

				});   
				mp.start();
				//mCamera.stopPreview();
			}
		}.start();  
	}
	//	@Override
	//	protected void onStop() {
	//		super.onStop();
	//		 if (mCamera != null) {
	//		        // Call stopPreview() to stop updating the preview surface.
	//		    	mCamera.stopPreview();
	//		        mCamera.release();
	//		        mCamera = null;
	//		    }
	//	}


	public Camera getCamera(){
		return mCamera;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(mCamera != null){
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			
			float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;
 
            // gForce will be close to 1 when there is no movement.
            float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);
 
			
			if(gForce < SHAKE_THRESHOLD_GRAVITY){
			if(x > 2 && z < -1 && !facingDown){
				mPreview.changeFilter("left");
				facingDown = true;
				
			}else if(x < -2 && z < -1 && !facingDown){
				mPreview.changeFilter("right");
				facingDown = true;
				
			}if(x <= 2 && z >= 0){
			
				facingDown = false;
			}
			}
		}
		}
	}
	@Override
	public void onBackPressed() {
	   
	}


}

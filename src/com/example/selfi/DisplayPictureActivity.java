package com.example.selfi;

import java.io.File;

import com.example.selfi.ShakeDetector.OnShakeListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

@SuppressLint("NewApi")
public class DisplayPictureActivity extends Activity {

	protected static final int SPLASH_SCREEN = 3;
	private static final int CAMERA_PICTURE = 2;
	private static final String EXTRA_MESSAGE = "com.example.selfi";
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_display_picture);
		

		Intent intent = getIntent();
		String imagePath = intent.getStringExtra(WelcomeActivity.EXTRA_MESSAGE);

		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			Drawable drawable = new BitmapDrawable(getResources(), myBitmap);
			FrameLayout canvas = (FrameLayout) findViewById(R.id.display_picture);
			canvas.setBackground(drawable);

		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();

		mShakeDetector.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake(int count) {

				Intent i = new Intent(DisplayPictureActivity.this,
						SplashScreen.class);
				startActivityForResult(i, SPLASH_SCREEN);
				

			}
		});
		FrameLayout canvas = (FrameLayout) findViewById(R.id.display_picture);
		DrawStuff dw = new DrawStuff(this.getApplicationContext(), 2);
		canvas.addView(dw);
		
		

	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CAMERA_PICTURE) {
				String photoPath = data.getStringExtra("photoPath");
				Intent i = new Intent(DisplayPictureActivity.this,
						DisplayPictureActivity.class);
				i.putExtra(EXTRA_MESSAGE, photoPath);
				startActivity(i);

			} else if (requestCode == SPLASH_SCREEN) {
				Intent i = new Intent(DisplayPictureActivity.this,
						MainActivity.class);
				startActivityForResult(i, CAMERA_PICTURE);

			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mShakeDetector);
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

	}
	@Override
	public void onBackPressed() {
	   
	}
}

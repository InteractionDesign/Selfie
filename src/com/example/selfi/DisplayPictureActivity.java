package com.example.selfi;

import java.io.File;

import com.example.selfi.ShakeDetector.OnShakeListener;
import android.os.Bundle;
import android.app.Activity;

import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;

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
		setContentView(R.layout.activity_display_picture);
		// Show the Up button in the action bar.
		//setupActionBar();

		Intent intent = getIntent();
		String imagePath = intent.getStringExtra(WelcomeActivity.EXTRA_MESSAGE);

		File imgFile = new File(imagePath);
		if (imgFile.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());

			ImageView myImage = (ImageView) findViewById(R.id.imageviewid);
			myImage.setImageBitmap(myBitmap);

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

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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

package com.example.selfi;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements SensorEventListener, LocationListener{
	private SensorManager sensorManager;

	public Button button;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;
	private String selectedImagePath;
	private static final int SELECT_PICTURE = 1;
	public static final String EXTRA_MESSAGE = "com.example.selfi";
	private TextView latituteField;
	private TextView longitudeField;
	private TextView city;
	private LocationManager locationManager;
	private String provider;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_welcome);
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				//startActivityForResult(intent, 0); 
				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(i);	
			}
		});

		// ShakeDetector initialization
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();

		//Removed this functionality from WelcomeActivity
		/* mShakeDetector.setOnShakeListener(new OnShakeListener() {

            @Override
            public void onShake(int count) {*/
		/*
		 * The following method, "handleShakeEvent(count):" is a stub //
		 * method you would use to setup whatever you want done once the
		 * device has been shook.
		 */
		/*         handleShakeEvent(count);
            }

            private void handleShakeEvent(int count) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });*/


		((Button) findViewById(R.id.button1))
		.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent,
						"Select Picture"), SELECT_PICTURE);
			}
		});

		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		latituteField = (TextView) findViewById(R.id.TextView02);
		longitudeField = (TextView) findViewById(R.id.TextView04);
		city = (TextView) findViewById(R.id.TextView06);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
		/*	latituteField.setText("Location not available");
			longitudeField.setText("Location not available");*/
			onLocationChanged(location);

		}


	}


	@Override
	public void onResume() {
		super.onResume();
		// Add the following line to register the Session Manager Listener onResume
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

		//   locationManager.requestLocationUpdates(provider, 400, 1, this);

		for (String s : locationManager.getAllProviders()) {
			locationManager.requestLocationUpdates("gps", 1, 1, WelcomeActivity.this);
			//locationManager.requestLocationUpdates(provider, 400, 1, WelcomeActivity.this);

		}
	}

	@Override
	public void onPause() {
		// Add the following line to unregister the Sensor Manager onPause
		mSensorManager.unregisterListener(mShakeDetector);
		super.onPause();
		locationManager.removeUpdates(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
				Intent i = new Intent(WelcomeActivity.this, DisplayPictureActivity.class);
				i.putExtra(EXTRA_MESSAGE, selectedImagePath);
				startActivity(i);


			}
		}
	}

	/**
	 * helper to retrieve the path of an image URI
	 */
	public String getPath(Uri uri) {
		// just some safety built in 
		if( uri == null ) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if( cursor != null ){
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fallback here
		return uri.getPath();
	}

	public void onAccuracyChanged(Sensor sensor,int accuracy){

	}



	public void onSensorChanged(SensorEvent event){


		// check sensor type
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

			// assign directions
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];




			if(x<-5){
				Context context = WelcomeActivity.this;
				String text ="right";

				int duration = Toast.LENGTH_SHORT;

				Toast.makeText(context, text, duration).show();
			}
			if(x>5){
				Context context = WelcomeActivity.this;
				String text ="left";

				int duration = Toast.LENGTH_SHORT;

				Toast.makeText(context, text, duration).show();
			}
			if(z<0){
				Context context = WelcomeActivity.this;
				String text ="forward";

				int duration = Toast.LENGTH_SHORT;


				Toast.makeText(context, text, duration).show();
			}
		}

	}
	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude());
		int lng = (int) (location.getLongitude());
		//int lat = 56;
		//int lng = 13;
		
		latituteField.setText(String.valueOf(lat));
		longitudeField.setText(String.valueOf(lng));

		String cityName = "Not Found";                 
		Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());        
		try 
		{  
		//	List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);  
			List<Address> addresses = gcd.getFromLocation(lat, lng, 1); 
			if (addresses.size() > 0) 
			{ 
				cityName = addresses.get(0).getLocality();  
				// you should also try with addresses.get(0).toSring();
				System.out.println(cityName); 
			}
		} catch (IOException e) 
		{                 
			e.printStackTrace();  
		} 

		String s = "\n\nMy Currrent City is: "+cityName;
		city.setText(s);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}
}

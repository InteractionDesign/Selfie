package com.example.selfi;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	
    public Button button;
    private String selectedImagePath;
    private static final int SELECT_PICTURE = 1;
    private static final int CAMERA_PICTURE = 2;
    public static final String EXTRA_MESSAGE = "com.example.selfi";


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
				startActivityForResult(i, CAMERA_PICTURE);		
            }
        });
  
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
    }
    
 
    
   
    @Override
    public void onResume() {
        super.onResume();
    }
 
    @Override
    public void onPause() {
        super.onPause();
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
            } else if(requestCode == CAMERA_PICTURE) {
            	String photoPath = data.getStringExtra("photoPath");
                Intent i = new Intent(WelcomeActivity.this, DisplayPictureActivity.class);
                i.putExtra(EXTRA_MESSAGE, photoPath);
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
    
}

package com.example.selfi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SplashScreen extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.splash_screen);
    	
    	Timer splashThread = new Timer(this);
            
        splashThread.start();
         
    	
    }
    public void timerFinished(){
    	setResult(Activity.RESULT_OK);
        // kill the activity
        finish();
    }

}

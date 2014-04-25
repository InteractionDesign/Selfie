package com.example.selfi;

import android.app.Activity;
import android.util.Log;

public class Timer extends Thread {
	SplashScreen splashScreen;

	public Timer(SplashScreen splashScreen) {
	this.splashScreen = splashScreen;
	}

	@Override
    public void run() {
       try {
          int waited = 0;
          while (waited < 1500) {
             sleep(100);
             waited += 100;
          }
       } catch (InterruptedException e) {
          Log.e("Hej", "HEssst");
       } finally {
    	  splashScreen.timerFinished();
          
       }
    }
}

package com.example.selfi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class DrawStuff extends View {
  private Bitmap shakecamera;
  private double width;
  private double height;
  private boolean action;
  private int activity;

  @SuppressLint("NewApi")
public DrawStuff(Context context, int i) {
    super(context);
    activity = i;
    if(activity == 1){
    shakecamera = BitmapFactory.decodeResource(getResources(),R.drawable.shakecamera);
    }else if(activity == 2){
    shakecamera = BitmapFactory.decodeResource(getResources(),R.drawable.shakecamera_black);
    }else{
    shakecamera = BitmapFactory.decodeResource(getResources(),R.drawable.shakecamera);
    }
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    width = size.x;
    height = size.y;
    action = false;
  }
  public void actionStarted(boolean bol){
    if(bol){
      action = true;
    }
  }
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    double new_height;
    if(activity == 1){
    	new_height = height - 0.2*height;
    }else{
    	new_height = height - 0.18*height;
    }
    if (!action){
    canvas.drawBitmap(shakecamera, (float) width/4, (float) new_height, null);
    }
  } 

}

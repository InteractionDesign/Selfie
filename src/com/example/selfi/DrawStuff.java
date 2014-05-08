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

@SuppressLint("ViewConstructor")
public class DrawStuff extends View {
    private Bitmap shakecamera;
    private Bitmap tilt_right;
    private Bitmap tilt_left;
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
    tilt_left = BitmapFactory.decodeResource(getResources(),R.drawable.tilt_left);
    tilt_right = BitmapFactory.decodeResource(getResources(),R.drawable.tilt_right);
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

        int bitmapWidth =  shakecamera.getWidth();
        int bitmapHeight = shakecamera.getHeight();
        if (!action) {
            canvas.drawBitmap(shakecamera, (float) ((width - bitmapWidth) / 2),
                    (float) (height - (bitmapHeight * 1.2 ) ), null);
        }
        if(activity == 1){
        	if(!action){
        		canvas.drawBitmap(tilt_left, (float) ((float) width-width+0.05*width), (float) height/2, null);
        		canvas.drawBitmap(tilt_right, (float) ((float) width-0.35*width), (float) height/2, null);
        	}
        }
    }
 

}

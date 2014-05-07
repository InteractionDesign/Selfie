package com.example.selfi;

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

	public DrawStuff(Context context) {
		super(context);
		shakecamera = BitmapFactory.decodeResource(getResources(),R.drawable.shakecamera); 
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	
		double new_height = height - 0.2*height;
		canvas.drawBitmap(shakecamera, (float) width/4, (float) new_height, null);
	}	

}

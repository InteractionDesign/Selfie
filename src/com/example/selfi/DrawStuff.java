package com.example.selfi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class DrawStuff extends View {
	private Bitmap shakecamera;

	public DrawStuff(Context context) {
		super(context);
		shakecamera = BitmapFactory.decodeResource(getResources(),R.drawable.shakecamera); 
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);	

		canvas.drawBitmap(shakecamera, 180, 900, null);
	}	

}

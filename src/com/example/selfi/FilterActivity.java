package com.example.selfi;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;




public class FilterActivity extends Activity {

    private ImageView imgMain ;
    private static final int SELECT_PHOTO = 100;
    private Bitmap src;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);
		
        imgMain = (ImageView) findViewById(R.drawable.chowchow);
        src = BitmapFactory.decodeResource(getResources(), R.drawable.chowchow);
    }

    public void buttonClicked(View v){

        Toast.makeText(this,"Processing...",Toast.LENGTH_SHORT).show();
        ImageFilters imgFilter = new ImageFilters();
        
        if(v.getId() == R.id.btn_pick_img){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
//        else if(v.getId() == R.id.effect_highlight)
//            saveBitmap(imgFilter.applyHighlightEffect(src), "effect_highlight");
      
        else if(v.getId() == R.id.effect_black)
            saveBitmap(imgFilter.applyBlackFilter(src),"effect_black");
      

      
     

    }

    private void saveBitmap(Bitmap bmp,String fileName){
        try {
            imgMain.setImageBitmap(bmp);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName+".png");
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG,90,fos);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    Bitmap bmp = decodeUri(selectedImage);
                    if(bmp !=null){
                        src = bmp;
                        imgMain.setImageBitmap(src);
                    }
                }
        }
    }

    private Bitmap decodeUri(Uri selectedImage)  {

        try {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
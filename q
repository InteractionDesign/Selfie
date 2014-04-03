[1mdiff --git a/src/com/example/selfi/CameraPreview.java b/src/com/example/selfi/CameraPreview.java[m
[1mindex 86cca1e..af83ab6 100644[m
[1m--- a/src/com/example/selfi/CameraPreview.java[m
[1m+++ b/src/com/example/selfi/CameraPreview.java[m
[36m@@ -1,10 +1,12 @@[m
 package com.example.selfi;[m
 [m
 import java.io.IOException;[m
[32m+[m
 import android.annotation.SuppressLint;[m
[31m-import android.content.Context;[m
[32m+[m[32mimport android.app.Activity;[m
 import android.hardware.Camera;[m
 import android.util.Log;[m
[32m+[m[32mimport android.view.Surface;[m
 import android.view.SurfaceHolder;[m
 import android.view.SurfaceView;[m
 [m
[36m@@ -12,9 +14,11 @@[m [mimport android.view.SurfaceView;[m
 public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {[m
     private SurfaceHolder mHolder;[m
     private Camera mCamera;[m
[32m+[m[32m    private Activity caller;[m
 [m
[31m-    public CameraPreview(Context context, Camera camera) {[m
[31m-        super(context);[m
[32m+[m[32m    public CameraPreview(Activity caller, Camera camera) {[m
[32m+[m[32m        super(caller);[m
[32m+[m[32m        this.caller = caller;[m
         mCamera = camera;[m
 [m
         // Install a SurfaceHolder.Callback so we get notified when the[m
[36m@@ -35,8 +39,11 @@[m [mpublic class CameraPreview extends SurfaceView implements SurfaceHolder.Callback[m
         }[m
     }[m
 [m
[32m+[m[32m    @Override[m
     public void surfaceDestroyed(SurfaceHolder holder) {[m
[31m-        // empty. Take care of releasing the Camera preview in your activity.[m
[32m+[m[41m    [m	[32mmCamera.stopPreview();[m
[32m+[m[32m        mCamera.release();[m
[32m+[m[32m        mCamera = null;[m
     }[m
 [m
     public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {[m
[36m@@ -51,13 +58,14 @@[m [mpublic class CameraPreview extends SurfaceView implements SurfaceHolder.Callback[m
         // stop preview before making changes[m
         try {[m
             mCamera.stopPreview();[m
[31m-        } catch (Exception e){[m
[31m-          // ignore: tried to stop a non-existent preview[m
[31m-        }[m
[32m+[m[32m        } catch (Exception e){ }[m
 [m
         // set preview size and make any resize, rotate or[m
         // reformatting changes here[m
[31m-[m
[32m+[m[41m        [m
[32m+[m[32m        // here 1 stands for front camera[m
[32m+[m[32m        setCameraDisplayOrientation(this.caller, 1, mCamera);[m
[32m+[m[41m        [m
         // start preview with new settings[m
         try {[m
             mCamera.setPreviewDisplay(mHolder);[m
[36m@@ -68,4 +76,31 @@[m [mpublic class CameraPreview extends SurfaceView implements SurfaceHolder.Callback[m
         }[m
     }[m
     [m
[32m+[m[32m    public static void setCameraDisplayOrientation(Activity activity,[m
[32m+[m[32m            int cameraId, android.hardware.Camera camera) {[m
[32m+[m[32m        android.hardware.Camera.CameraInfo info =[m
[32m+[m[32m                new android.hardware.Camera.CameraInfo();[m
[32m+[m[32m        android.hardware.Camera.getCameraInfo(cameraId, info);[m
[32m+[m[32m        int rotation = activity.getWindowManager().getDefaultDisplay()[m
[32m+[m[32m                .getRotation();[m
[32m+[m[32m        int degrees = 0;[m
[32m+[m[32m        switch (rotation) {[m
[32m+[m[32m            case Surface.ROTATION_0: degrees = 0; break;[m
[32m+[m[32m            case Surface.ROTATION_90: degrees = 90; break;[m
[32m+[m[32m            case Surface.ROTATION_180: degrees = 180; break;[m
[32m+[m[32m            case Surface.ROTATION_270: degrees = 270; break;[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        int result;[m
[32m+[m[32m        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {[m
[32m+[m[32m            result = (info.orientation + degrees) % 360;[m
[32m+[m[32m            result = (360 - result) % 360;  // compensate the mirror[m
[32m+[m[32m        } else {  // back-facing[m
[32m+[m[32m            result = (info.orientation - degrees + 360) % 360;[m
[32m+[m[32m        }[m
[32m+[m[32m        camera.setDisplayOrientation(result);[m
[32m+[m[32m    }[m
[32m+[m[41m    [m
[32m+[m[41m [m
[32m+[m[41m    [m
 }[m
\ No newline at end of file[m

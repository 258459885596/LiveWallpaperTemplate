package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class CameraLiveWallpaper extends WallpaperService {


    public static void setToWallPaper(Context context) {

        WallpaperUtil.setToWallPaper(context,
                "com.martinrgb.livewallpapertemplate.CameraLiveWallpaper",true);

    }


    @Override
    public Engine onCreateEngine() {
        return new CameraEngine();
    }


    class CameraEngine extends Engine implements Camera.PreviewCallback {

        private Camera mCamera;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i("aaa","oncreate");
            startPreview();
            setTouchEventsEnabled(true);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopPreview();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {

                startPreview();
            } else {
                Log.i("aaa","wallpager invisible");
                stopPreview();
            }
        }



        public void startPreview() {

            if (mCamera == null) {
                Log.i("aaa", "wallpager startPreview " + System.currentTimeMillis());

                try {
                    mCamera = Camera.open(0);
                    if (mCamera != null) {

                        Camera.Parameters parameters = mCamera.getParameters();
                        //List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                        //Camera.Size previewSize = previewSizes.get(1); //480h x 720w

                        //parameters.setPreviewSize(previewSize.width, previewSize.height);

                        for (Camera.Size previewSize: mCamera.getParameters().getSupportedPreviewSizes())
                        {
                            // if the size is suitable for you, use it and exit the loop.
                            parameters.setPreviewSize(previewSize.width, previewSize.height);
                            break;
                        }

                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                        mCamera.setParameters(parameters);

                        mCamera.setDisplayOrientation(90);
                        mCamera.setPreviewDisplay(getSurfaceHolder());

                        mCamera.startPreview();
                    }
                } catch (Exception e) {
                    Log.i("aaa","wallpager "+e.getMessage());
                }


            }

        }

        public void stopPreview() {
            if (mCamera != null) {
                try {
                    mCamera.stopPreview();
                    mCamera.setPreviewCallback(null);

                } catch (Exception e) {
                    Log.i("aaa", "Exception " + System.currentTimeMillis());
                } finally {
                    mCamera.release();
                    mCamera = null;
                }


                Log.i("aaa", "wallpager stopPreview " + System.currentTimeMillis());
            }
        }


        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            mCamera.addCallbackBuffer(data);
        }
    }



}

package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Created by liumeng on 2017/5/12.
 */

public class CameraLiveWallpaper extends WallpaperService {


    @Override
    public Engine onCreateEngine() {
        return new CameraEngine();
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, CameraLiveWallpaper.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent);
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
                        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                        Camera.Size previewSize = previewSizes.get(1); //480h x 720w

                        parameters.setPreviewSize(previewSize.width, previewSize.height);
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

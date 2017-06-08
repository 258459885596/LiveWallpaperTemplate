package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * thx for https://github.com/songixan/Wallpaper
 */
public class GIFLiveWallpaper extends WallpaperService {

    //###################### Setting ######################
    //private static String GIFNAME = "testgif.gif";

    public Engine onCreateEngine() {
        return new GIFWallpaperEngine();
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, GIFLiveWallpaper.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent);
    }


    private class GIFWallpaperEngine extends WallpaperService.Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

        private final int frameDuration = 0;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;
        private int mSurfaceWidth;
        private int mSurfaceHeight;
        private int mMovieWidth;
        private int mMovieHeight;
        private float scaleRatio;

        private SharedPreferences mPreferences;

        private volatile boolean mIsSurfaceCreated;

        public GIFWallpaperEngine() {
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
            mPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            try {
                movie = Movie.decodeStream(
                        getResources().getAssets().open(MainActivity.getGIF()));
            }catch(IOException e){
                Log.d("GIF", "Could not load asset");
            }

            mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mPreferences.registerOnSharedPreferenceChangeListener(this);

            mIsSurfaceCreated =true;
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mMovieWidth = movie.width();
            mMovieHeight = movie.height();

            if((float)mMovieWidth/mSurfaceWidth > (float)mMovieHeight/mSurfaceHeight){

                scaleRatio = (float) mSurfaceWidth/mMovieWidth;
            }
            else {
                scaleRatio = (float) mSurfaceHeight/mMovieHeight;
            }

            mIsSurfaceCreated = true;
        }


        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mIsSurfaceCreated = false;

        }

        private Runnable drawGIF = new Runnable() {
            public void run() {
                draw();
            }
        };


        private void draw() {
            if (visible) {
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                // Adjust size and position so that
                // the image looks good on your screen
                canvas.scale(scaleRatio,scaleRatio);
                movie.draw(canvas, 0, 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        }
    }


}  
package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

public class GIFLiveWallpaper extends WallpaperService {

    //###################### Setting ######################
    private static String LOCAL_GIF = "testgif.gif";

    public Engine onCreateEngine() {

        return new GIFWallpaperEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void setToWallPaper(Context context) {

        try {
            WallpaperManager.getInstance(context).clear();
        }catch (IOException e) {
            e.printStackTrace();
        }

        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, GIFLiveWallpaper.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent);
    }



    private class GIFWallpaperEngine extends WallpaperService.Engine {

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
        }



        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);



            try {

                if(MainActivity.gifName == null){
                    Movie video = Movie.decodeStream(
                            getResources().getAssets().open(LOCAL_GIF));
                    movie = video;
                }
                else {
                    File imageFile =  new File(MainActivity.gifPath, MainActivity.gifName);
                    final int readLimit = 16 * 1024;
                    if(imageFile != null){
                        InputStream mInputStream =  new BufferedInputStream(new FileInputStream(imageFile), readLimit);
                        mInputStream.mark(readLimit);
                        Movie video = Movie.decodeStream(mInputStream);
                        movie = video;                        } else {
                        Log.w(TAG, "GIF image is not available!");
                    }
                }

            }catch(IOException e){
                Log.d("GIF", "Could not load asset");
            }
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

    }


}  
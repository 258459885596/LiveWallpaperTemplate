package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.app.WallpaperInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class VideoLiveWallpaper extends WallpaperService {

    //###################### Setting ######################
    public String LOCAL_VIDEO = "testvideo.mp4";

    public Engine onCreateEngine() {
        return new VideoWallpaperEngine();
    }


    public static void setToWallPaper(Context context) {


        WallpaperUtil.setToWallPaper(context,
                "com.martinrgb.livewallpapertemplate.VideoLiveWallpaper",true);

    }


    class VideoWallpaperEngine extends WallpaperService.Engine {

        private MediaPlayer mMediaPlayer;

        private int mSurfaceWidth;
        private int mSurfaceHeight;
        private int mMovieWidth;
        private int mMovieHeight;
        private float scaleRatio;
        private Surface mSurface;
        private int SETSIZE = 1;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);


        }

        @Override
        public void onDestroy() {
            super.onDestroy();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            //否则进入 Home 还 Play
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(holder.getSurface());
            try {
                if(MainActivity.videoName == null){
                    AssetManager assetMg = getApplicationContext().getAssets();
                    AssetFileDescriptor fileDescriptor = assetMg.openFd(LOCAL_VIDEO);
                    mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                            fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                }
                else {
                    String filePath = MainActivity.videoPath+MainActivity.videoName;
                    mMediaPlayer.setDataSource(filePath);
                }

                //循环
                //mMediaPlayer.setLooping(true);
                mMediaPlayer.setVolume(0, 0);
                mMediaPlayer.prepare();
//                mMediaPlayer.setVideoScalingMode(MediaPlayer
//                        .VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                mMediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * 播放器异常事件
             */
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    mMediaPlayer.release();
                    return false;
                }
            });


            /**
             * 播放器準備事件
             */
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    try {
                        mp.start();
                        //给ui 界面发送消息 这里有个延时是设置 如果不设置延时 会出现 获得视频的高宽为零的文件
                        uiHandler.sendEmptyMessageDelayed(SETSIZE, 1000);

                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("start mediaplayer", e.toString());
                    }

                }
            });

        }


        private Handler uiHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == SETSIZE){
                    mMovieHeight = mMediaPlayer.getVideoHeight();
                    mMovieWidth = mMediaPlayer.getVideoWidth();

                }
            };
        };

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);

        }
    }


}  
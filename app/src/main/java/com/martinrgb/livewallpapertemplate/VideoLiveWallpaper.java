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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class VideoLiveWallpaper extends WallpaperService {

    //###################### Setting ######################
    public String LOCAL_VIDEO = "testvideo.mp4";
    public static final String WALLPAPER_PREVIEW_PACKAGE = "com.android.wallpaper.livepicker";
    public static final String WALLPAPER_PREVIEW_CLASSNAME = "com.android.wallpaper.livepicker.LiveWallpaperPreview";
    static final String EXTRA_LIVE_WALLPAPER_INTENT = "android.live_wallpaper.intent";
    static final String EXTRA_LIVE_WALLPAPER_SETTINGS = "android.live_wallpaper.settings";
    static final String EXTRA_LIVE_WALLPAPER_PACKAGE = "android.live_wallpaper.package";

    public Engine onCreateEngine() {
        return new VideoWallpaperEngine();
    }

    public static class LiveWallpaperInfo {
        public Drawable thumbnail;
        public WallpaperInfo info;
        public Intent intent;
    }

    public static void setToWallPaper(Context context) {

        try {
            WallpaperManager.getInstance(context).clear();
        }catch (IOException e) {
            e.printStackTrace();
        }
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, VideoLiveWallpaper.class));
        context.startActivity(intent);

//        Intent intent = new Intent();
//        intent.setComponent(ComponentName.unflattenFromString("com.android.wallpaper.livepicker/com.android.wallpaper.livepicker.LiveWallpaperPreview"));
//        //intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);

//        PackageManager packageManager = context.getPackageManager();
//        List<ResolveInfo> list =  packageManager.queryIntentServices(
//                                new Intent(WallpaperService.SERVICE_INTERFACE),
//                                PackageManager.GET_META_DATA);
//        ResolveInfo resolveInfo = list.get(0);
//
//        搬运 SoundTrack 的 RFLX
//
//        WallpaperInfo info = null;
//        try {
//            info = new WallpaperInfo(context, resolveInfo);
//        } catch (XmlPullParserException e) {
//            Log.w("LOG", "Skipping wallpaper " + resolveInfo.serviceInfo, e);
//        } catch (IOException e) {
//            Log.w("LOG", "Skipping wallpaper " + resolveInfo.serviceInfo, e);
//        }
//
//        LiveWallpaperInfo liveWallpaperInfo = new LiveWallpaperInfo();
//        liveWallpaperInfo.intent = new Intent(WallpaperService.SERVICE_INTERFACE);
//        liveWallpaperInfo.intent.setClassName(info.getPackageName(), info.getServiceName());
//        liveWallpaperInfo.info = info;
//
//        showLiveWallpaperPreview(liveWallpaperInfo.info,liveWallpaperInfo.intent,context);

    }


    public static void showLiveWallpaperPreview(WallpaperInfo info, Intent intent, Context context) {
        if (info == null) return;

               Intent preview = new Intent();
               preview.setComponent(new ComponentName(WALLPAPER_PREVIEW_PACKAGE, WALLPAPER_PREVIEW_CLASSNAME));
               preview.putExtra(EXTRA_LIVE_WALLPAPER_INTENT, intent);
               preview.putExtra(EXTRA_LIVE_WALLPAPER_SETTINGS, info.getSettingsActivity());
               preview.putExtra(EXTRA_LIVE_WALLPAPER_PACKAGE, info.getPackageName());
               context.startActivity(preview);
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

                mMediaPlayer.setLooping(true);
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
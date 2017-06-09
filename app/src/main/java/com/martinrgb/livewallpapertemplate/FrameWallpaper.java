package com.martinrgb.livewallpapertemplate;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.martinrgb.livewallpapertemplate.frameutil.FrameDrawable;
import com.martinrgb.livewallpapertemplate.frameutil.UpdateThread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by MartinRGB on 2017/2/21.
 */

public class FrameWallpaper extends WallpaperService {


    //###################### Setting ######################

    public static final int MIN_UPDATE_RATE = 8;
    public int mCurFrame = 0;
    private String LOCAL_FRAME_NAME = "testframe";
    private String FRAME_NAME;
    public boolean isOneShot = false;
    public boolean isControl = false;
    public int frameNumber = 80;

    private int mControlFrame = 0; //可以用Spring，结合滑动位置控制


    //###################### Listener ######################
    private OnFrameListener mOnFrameListener;
    public interface OnFrameListener {

        void onFrameStart();
        void onFrameEnd();

    }

    public void setOnFrameListener(OnFrameListener onFrameListener) {
        mOnFrameListener = onFrameListener;
    }

    private void callOnFrameEnd() {
        if (null != mOnFrameListener) {
            mOnFrameListener.onFrameEnd();
        }
    }

    private void callOnFrameStart() {
        if (null != mOnFrameListener) {
            mOnFrameListener.onFrameStart();
        }
    }

    @Override
    public Engine onCreateEngine(){
        return new GLEngine();
    }


    public static void setToWallPaper(Context context) {
        try {
            WallpaperManager.getInstance(context).clear();
        }catch (IOException e) {
            e.printStackTrace();
        }

        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, FrameWallpaper.class));
        intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent);
    }

    public class GLEngine extends Engine{
        private static final String TAG = "GLEngine";

        //###################Life Cycle###################
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate(" + surfaceHolder + ")");


        }

        //#Destory
        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d(TAG, "onDestroy()");
        }

        //#Visibile改变
        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "onVisibilityChanged(" + visible + ")");
            if (visible) {
                start();
            } else {
                stop();
            }
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            mIsSurfaceCreated = true;
            clearSurface();
            setSpringSystem();
            addAsset(getApplicationContext());

        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mIsSurfaceCreated = true;
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mIsSurfaceCreated = false;
            stop();

        }

        //###################Touch Event###################
        private float mDistanceProgress = 0;
        private float mStartTouchEventY = 0;
        @Override
        public void onTouchEvent(MotionEvent event){
            {
                if(event !=null){
                    //*把坐标系转换成OpenGL坐标系(后期还要转换成GLSL内部归一化坐标系)
                    //u_mouse
                    final float normalizedX = (float)event.getX() / (float) mSurfaceWidth;
                    final float normalizedY = -(float)event.getY() / (float) mSurfaceHeight;

                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            mDistanceProgress = 0;
                            mStartTouchEventY = event.getY(0);
                            break;

                        case MotionEvent.ACTION_UP:
                            if(mDistanceProgress>0.6){
                                mSpring.setEndValue(1.);
                            }
                            else{
                                mSpring.setEndValue(0);
                            }

                            break;

                        case MotionEvent.ACTION_MOVE:
                            float nowDistanceY =  mStartTouchEventY - event.getY(0);
                            mDistanceProgress = Math.max(0,Math.min(1,nowDistanceY/550));
                            mSpring.setEndValue((float) mDistanceProgress);
                            break;
                        default:
                    }

                }
            }

        }

        //##################### Spring System Part ######################
        private SpringSystem mSpringSystem;
        private Spring mSpring;
        private final SpringConfig mconfig = SpringConfig.fromOrigamiTensionAndFriction(100, 15);

        private void setSpringSystem() {
            mSpringSystem = SpringSystem.create();
            mSpring = mSpringSystem.createSpring();
            mSpring.setSpringConfig(mconfig);

            mSpring.addListener(new SimpleSpringListener() {

                @Override
                public void onSpringUpdate(Spring mSpring) {

                    float value = (float) mSpring.getCurrentValue();

                    float mapValue = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 0, frameNumber);
                    int intMapValue = (int) mapValue;

                    //mFrameAnimationView.drawNext();
                    mControlFrame = intMapValue;


                }
            });
        }

        //###################### Init ######################
        private boolean mOneShot;
        private long mStart;
        private long mDuration;
        private List<FrameDrawable> mFrameDrawables = new ArrayList<>();
        private volatile boolean mIsSurfaceCreated;
        private volatile int mSurfaceWidth;
        private volatile int mSurfaceHeight;


        private void addAsset(Context context){
            List<String> frameList = new ArrayList<String>();
            try {


                if(MainActivity.frameName == null){
                    FRAME_NAME = LOCAL_FRAME_NAME;
                    final String[] frames = context.getAssets().list(FRAME_NAME);

                    if (null != frames) {
                        frameList = Arrays.asList(frames);
                    }

                }
                else {
                    FRAME_NAME = MainActivity.frameName;
                    String path = MainActivity.framePath;
                    File directory = new File(path);
                    File[] files = directory.listFiles();

                    for (int i = 0; i < files.length; i++) {
//
                        if(i==0){
                            FRAME_NAME = getFileNameNoEx(files[0].getName());
                        }

                        if (files[i].isFile()) {
                           frameList.add(files[i].getName());
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //按帧图片的序列号排序
            if (null != frameList) {
                Collections.sort(frameList, new Comparator<String>() {

                    //根据情况修改
                    private final String MATCH_FRAME_NUM = String.format("(?<=%s_).*(?=.jpg)", FRAME_NAME);
                    private final Pattern p = Pattern.compile(MATCH_FRAME_NUM);

                    @Override
                    public int compare(String lhs, String rhs) {
                        try {
                            final Matcher lhsMatcher = p.matcher(lhs);
                            final Matcher rhsMatcher = p.matcher(rhs);
                            if (lhsMatcher.find()
                                    && rhsMatcher.find()) {
                                return Integer.valueOf(lhsMatcher.group()) - Integer.valueOf(rhsMatcher.group());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                //添加序列帧
                List<FrameDrawable> frameDrawables = new ArrayList<>();

                if(MainActivity.frameName == null){

                    for (String framePath : frameList) {
                        FrameDrawable frameDrawable = new FrameDrawable(FRAME_NAME + "/" + framePath, 8,context);
                        frameDrawables.add(frameDrawable);

                    }
                }
                else {

                    for (String framePath : frameList) {
                        FrameDrawable frameDrawable = new FrameDrawable(framePath, 8,context);
                        frameDrawables.add(frameDrawable);

                    }
                }

                addFrameDrawable(frameDrawables); //添加序列帧
                if(isOneShot){
                    setOneShot(true);
                }
                else {
                    setOneShot(false);
                }

                start();
            }
        }

        //去掉扩展名后，再去一位
        public String getFileNameNoEx(String filename) {
            if ((filename != null) && (filename.length() > 0)) {
                int dash = filename.lastIndexOf('_');
                if ((dash >-1) && (dash < (filename.length()))) {
                    return filename.substring(0, dash);
                }
            }

            return filename;
        }

        //### Animating Or Not
        private AtomicBoolean mIsAnimating = new AtomicBoolean(false);
        public boolean isAnimating() {
            return mIsAnimating.get();
        }

        public void setOneShot(boolean oneShot) {
            if (!isRunning()) {
                mOneShot = oneShot;
            }
        }
        public void setDuration(long duration) {
            if (!isRunning()) {
                mDuration = duration;
            }
        }
        public void addFrameDrawable(FrameDrawable frameDrawable) {
            if (!isRunning()) { //在绘制的时候不允许添加
                mFrameDrawables.add(frameDrawable);
            }
        }
        public void addFrameDrawable(List<FrameDrawable> frameDrawableList) {
            if (!isRunning()) {
                mFrameDrawables.clear();
                mFrameDrawables.addAll(frameDrawableList);
            }
        }

        //###################### Start & Stop ######################
        private int mFrameUpdateRate = MIN_UPDATE_RATE;
        private UpdateThread mUpdateThread;
        private boolean mIsUpdateStarted;

        public synchronized void start() {
            if (isRunning()) return;
            if (mFrameDrawables.isEmpty()) {
                callOnFrameEnd();
                return;
            }
            if (mDuration == 0) {
                for (FrameDrawable frameDrawable : mFrameDrawables) {
                    if (null != frameDrawable) {
                        mDuration += frameDrawable.mDuration;
                    }
                }
            }
            if (mDuration == 0) {
                callOnFrameEnd();
                return;
            }
            startUpdate();
        }

        public synchronized void stop() {
            if (mIsAnimating.get()) {
                mIsAnimating.set(false);
                callOnFrameEnd();
            }
            mCurFrame = -1;
            stopUpdate();
        }

        public boolean isRunning() {
            return mIsUpdateStarted;
        }

        protected void startUpdate() {
            if (mIsUpdateStarted) return;
            mUpdateThread = new UpdateThread("Animator Update Thread") {

                @Override
                public void run() {
                    try {
                        while (!isQuited()
                                && !Thread.currentThread().isInterrupted()) {
                            long drawTime = drawSurface();
                            long diffTime = mFrameUpdateRate - drawTime;
                            if (isQuited()) {
                                break;
                            }
                            if (diffTime > 0) {
                                SystemClock.sleep(diffTime);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            mIsUpdateStarted = true;
            mUpdateThread.start();
        }

        protected void stopUpdate() {
            mIsUpdateStarted = false;
            if (null != mUpdateThread) {
                UpdateThread updateThread = mUpdateThread;
                mUpdateThread = null;
                updateThread.quit();
                updateThread.interrupt();
            }
        }



        //###################### Draw ######################
        private RectF RECT = new RectF();
        private Paint PAINT = new Paint();
        private int mCurRepeats;
//            static {
//                PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                PAINT.setColor(Color.TRANSPARENT);
//            }

        final protected long drawSurface() {
            if (!mIsSurfaceCreated) {
                return 0;
            }
            if (mSurfaceWidth == 0
                    || mSurfaceHeight == 0) {
                return 0;
            }

            final long startTime = SystemClock.uptimeMillis();
            if (mIsSurfaceCreated) {
                Canvas canvas = getSurfaceHolder().lockCanvas();
                if (null != canvas) {
                    drawFrame(canvas);
                    if (mIsSurfaceCreated) {
                        getSurfaceHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
            return SystemClock.uptimeMillis() - startTime;
        }


        protected void drawFrame(Canvas canvas){
            final int numFrames = mFrameDrawables.size();
            final int lastFrame = numFrames - 1;
            final long curTime = SystemClock.uptimeMillis();

            if (mStart != 0
                    && curTime - mStart >= mDuration) {
                if (mOneShot
                        && mIsAnimating.get()) {
                    mIsAnimating.set(false);

                    new Thread(new Runnable(){
                        @Override
                        public void run () {
                            callOnFrameEnd();
                        }
                    }).start();


                    mStart = 0;
                    mCurRepeats = 0;
                }
            }
            int nextFrame = mCurFrame + 1;
            if (mOneShot && nextFrame > lastFrame) {
                nextFrame = lastFrame;
            }
            if (!mOneShot && nextFrame >= numFrames) {
                nextFrame = lastFrame;
                if (mStart != 0
                        && curTime - mStart >= mDuration) {
                    nextFrame = 0;
                }
            }
            if (nextFrame == 0) { //第一帧的时候开始记录时间
                mIsAnimating.set(true);
                mStart = curTime;
                if (++mCurRepeats == 1) {//第一次播放动画的时候回调

                    new Thread(new Runnable(){
                        @Override
                        public void run () {
                            callOnFrameStart();
                        }
                    }).start();
                }
            }
            mCurFrame = nextFrame;

            if(isControl){

                drawNext(canvas, mControlFrame, curTime);
            }
            else {

                drawNext(canvas, nextFrame, curTime);
            }
        };

        public void drawNext(Canvas canvas, int nextFrame, long start) {
            long frameDuration = 0;
            FrameDrawable frameDrawable = mFrameDrawables.get(nextFrame);
            if (null != frameDrawable) {
                frameDuration = frameDrawable.mDuration;
                clearCanvas(canvas);
                frameDrawable.draw(canvas, start);
            }
            final long cost = SystemClock.uptimeMillis() - start;
            Log.d(TAG, "frame cost :" + cost);
            if (frameDuration > cost) {
                try {
                    Thread.sleep(frameDuration - cost);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        final protected void clearSurface() {
            if (mIsSurfaceCreated) {
                Canvas canvas = getSurfaceHolder().lockCanvas();
                if (null != canvas) {
                    clearCanvas(canvas);
                    if (mIsSurfaceCreated) {
                        getSurfaceHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        final protected void clearCanvas(Canvas canvas) {
            canvas.drawColor(Color.TRANSPARENT,
                    PorterDuff.Mode.CLEAR);
            RECT.set(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawRect(RECT, PAINT);
        }

    }


}



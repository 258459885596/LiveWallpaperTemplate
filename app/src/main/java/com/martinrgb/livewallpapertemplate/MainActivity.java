package com.martinrgb.livewallpapertemplate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


//两个进程这个设计原理 - android:process,后期还是保留，
// 然后重新写跨进程访问的方案，用静态变量肯定是不合适了，
// 把GIFNAME相关逻辑从activity提取出来，封装到一个专门的业务类，然后这个业务类搞成单例的

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_FILE_PICKER_RESULT = 0;
    private static final int GIF_FILE_PICKER_RESULT = 1;
    private static final int VERT_FILE_PICKER_RESULT = 2;
    private static final int FRAG_FILE_PICKER_RESULT = 3;
    private static final int FRAME_FOLDER_PICKER_RESULT = 4;
    private ExFilePicker mExFilePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        mExFilePicker = new ExFilePicker();
        mExFilePicker.setCanChooseOnlyOneItem(true);
        mExFilePicker.setQuitButtonEnabled(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setVideoToWallPaper(View view) {
        VideoLiveWallpaper.setToWallPaper(this);
    }

    public void setGIFToWallPaper(View view) {
        GIFLiveWallpaper.setToWallPaper(this);
    }

    public void setCameraToWallPaper(View view) {
        CameraLiveWallpaper.setToWallPaper(this);
    }

    public void setShaderToWallPaper(View view) {
        ShaderWallpaper.setToWallPaper(this);
    }

    public void setFrameToWallPaper(View view){
        FrameWallpaper.setToWallPaper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.video_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,VIDEO_FILE_PICKER_RESULT);
                break;

            case R.id.gif_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,GIF_FILE_PICKER_RESULT);
                break;

            case R.id.vert_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,VERT_FILE_PICKER_RESULT);
                break;

            case R.id.frag_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
                mExFilePicker.start(this,FRAG_FILE_PICKER_RESULT);
                break;

            case R.id.frame_setting:
                mExFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);
                mExFilePicker.start(this,FRAME_FOLDER_PICKER_RESULT);
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }


    public static String gifPath;
    public static String gifName;

    public static String videoPath;
    public static String videoName;

    public static String vertPath;
    public static String vertName;
    public static String fragPath;
    public static String fragName;

    public static String framePath;
    public static String frameName;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
                if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                videoPath = result.getPath();
                videoName = stringBuilder.toString();

                VideoLiveWallpaper.setToWallPaper(this);
            }
        }
        else if (requestCode == GIF_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                gifPath = result.getPath();
                gifName = stringBuilder.toString();

                GIFLiveWallpaper.setToWallPaper(this);
            }


        }
        else if (requestCode == VERT_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                vertPath = result.getPath();
                vertName = stringBuilder.toString();

                ShaderWallpaper.setToWallPaper(this);
            }
        }
        else if (requestCode == FRAG_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                fragPath = result.getPath();
                fragName = stringBuilder.toString();

                ShaderWallpaper.setToWallPaper(this);
            }
        }

        else if (requestCode == FRAME_FOLDER_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result.getNames().get(0));
                frameName = stringBuilder.toString();
                framePath = result.getPath() + frameName;

                FrameWallpaper.setToWallPaper(this);

                Log.e("PATH",framePath);
                Log.e("NAME",frameName);
            }
        }
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("mGIFPath",gifPath);
//        outState.putString("mGIFName",gifName);
//
//        outState.putString("mVideoPath",videoPath);
//        outState.putString("mVideoName",videoName);
//
//        outState.putString("mVertPath",vertPath);
//        outState.putString("mVertName",vertName);
//        outState.putString("mFragPath",fragPath);
//        outState.putString("mFragName",fragName);
//
//        outState.putString("mFramePath",framePath);
//        outState.putString("mFrameName",frameName);
//
//
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        gifPath = savedInstanceState.getString("mGIFPath");
//        gifName = savedInstanceState.getString("mGIFName");
//
//        videoPath = savedInstanceState.getString("mVideoPath");
//        videoName = savedInstanceState.getString("mVideoName");
//
//        vertPath = savedInstanceState.getString("mVertPath");
//        vertName = savedInstanceState.getString("mVertName");
//        fragPath = savedInstanceState.getString("mFragPath");
//        fragName = savedInstanceState.getString("mFragName");
//
//        framePath = savedInstanceState.getString("mFramePath");
//        frameName = savedInstanceState.getString("mFrameName");
//    }

    }

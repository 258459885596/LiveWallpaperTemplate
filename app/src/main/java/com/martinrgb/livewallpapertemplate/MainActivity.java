package com.martinrgb.livewallpapertemplate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

import com.martinrgb.livewallpapertemplate.R;


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result.getNames().get(0));
        }
        else if (requestCode == GIF_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result.getNames().get(0));
            MainActivity.setGIF(stringBuilder.toString());
        }
        else if (requestCode == VERT_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result.getNames().get(0));
        }
        else if (requestCode == FRAG_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result.getNames().get(0));
        }

        else if (requestCode == FRAME_FOLDER_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(result.getNames().get(0));
        }
    }

    private static String gifName ="testgif.gif";
    public static void setGIF(String string)
    {
        gifName = string;
    }
    public static String getGIF()
    {
        return gifName;
    }
}

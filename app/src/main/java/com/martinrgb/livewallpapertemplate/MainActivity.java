package com.martinrgb.livewallpapertemplate;

import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.martinrgb.livewallpapertemplate.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

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
}

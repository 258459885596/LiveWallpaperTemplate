package com.martinrgb.livewallpapertemplate;

import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.martinrgb.livewallpapertemplate.R;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //VideoLiveWallpaper.setToWallPaper(this);
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
}

package com.martinrgb.livewallpapertemplate.shaderprograms.shaderutil;

import android.util.Log;

/**
 * Created by zhanghongyang01 on 17/5/14.
 */

public class SimpleL {

    private static final String sTag = "WallPaper";

    public static void d(String msg, Object... params) {

        Log.d(sTag, String.format(msg, params));

    }

}

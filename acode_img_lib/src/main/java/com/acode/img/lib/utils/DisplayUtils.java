package com.acode.img.lib.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * user:yangtao
 * date:2018/3/211803
 * email:yangtao@bjxmail.com
 * introduce:获取屏幕参数
 */
public class DisplayUtils {
    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }
    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getHeight();
    }
}

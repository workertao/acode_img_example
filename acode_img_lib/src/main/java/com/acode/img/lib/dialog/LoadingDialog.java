package com.acode.img.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.acode.img.lib.R;

/**
 * user:yangtao
 * date:2018/4/281641
 * email:yangtao@bjxmail.com
 * introduce:功能
 */
public class LoadingDialog extends Dialog {
    //是否关闭dialog
    public boolean isDismiss = true;

    public LoadingDialog(@NonNull Context context) {
        this(context, true);
    }

    public LoadingDialog(@NonNull Context context, boolean cancelable) {
        super(context, R.style.loading_dialog);
        // 初始化View.
        initView(context, cancelable);
    }

    /**
     * 初始化view
     */
    private void initView(Context context, boolean cancelable) {
        View v = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView loadingImage = (ImageView) v.findViewById(R.id.img);
        // 加载动画 hyperspace:超空间;多维空间.
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用ImageView显示动画
        loadingImage.startAnimation(hyperspaceJumpAnimation);
        // 屏蔽事件,即不可以用“返回键”取消.
        setCancelable(cancelable);
        setContentView(layout);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowing() && isDismiss) {
                dismiss();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

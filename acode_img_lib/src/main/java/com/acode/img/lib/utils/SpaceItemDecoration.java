package com.acode.img.lib.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * user:yangtao
 * date:2018/3/221824
 * email:yangtao@bjxmail.com
 * introduce:功能
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(Context context, int space) {
        this.space = DimenUtils.dip2px(context,space);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        //由于每行都只有3个,所以第一个都是3的倍数,把左边距设为0
        if (parent.getChildLayoutPosition(view) % 3 == 0) {
            outRect.left = 0;
        }
    }
}
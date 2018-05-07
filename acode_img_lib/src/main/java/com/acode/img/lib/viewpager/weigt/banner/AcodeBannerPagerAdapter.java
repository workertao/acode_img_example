package com.acode.img.lib.viewpager.weigt.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.acode.img.lib.R;
import com.acode.img.lib.viewpager.listener.AcodeClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * user：yangtao.
 * date：2017/7/7
 * describe：ViewPager适配器adapter
 */

public class AcodeBannerPagerAdapter extends PagerAdapter {
    private final ArrayList<Object> mViewCaches = new ArrayList<>();

    private List<String> urls;

    private Context context;

    private int count;

    private AcodeClickListener acodeClickListener;

    public AcodeBannerPagerAdapter setAcodeClickListener(AcodeClickListener acodeClickListener) {
        this.acodeClickListener = acodeClickListener;
        return this;
    }

    public AcodeBannerPagerAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
        this.count = urls.size();
    }

    /**
     * PagerAdapter管理数据大小
     */
    @Override
    public int getCount() {
        // 当只有一张图片的时候，不滑动，返回1即可
        if (count == 1) {
            return 1;
        } else {
            // 否则循环播轮播，返回Int型的最大值
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 关联key 与 obj是否相等，即是否为同一个对象
     */
    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    /**
     * 销毁当前page的相隔2个及2个以上的item时调用
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView imageView = (ImageView) object;
        container.removeView(imageView);
        // 加到缓存里
        mViewCaches.add(imageView);
    }

    /**
     * 当前的page的前一页和后一页也会被调用，如果还没有调用或者已经调用了destroyItem
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView;
        if (mViewCaches.isEmpty()) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) mViewCaches.remove(0);
        }
        //获取当前图片的下标
        //记录：i 是当前图片的下标，因为我们设置的轮播size是无限大，但是我们要展示的图片数量是有限的。
        int i = position % count;
        //展示图片
        Glide.with(context)
                .load(urls.get(i))
//                //增加图片站位图
//                .placeholder(R.mipmap.dst)
////                //增加异常站位图【加载失败，网络异常】
//                .error(R.mipmap.src)
                .into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acodeClickListener != null) {
                    acodeClickListener.onAcodeVpClick(position % count);
                }
            }
        });
        return imageView;
    }
}

package com.acode.img.lib.viewpager.weigt.banner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;


import com.acode.img.lib.R;
import com.acode.img.lib.data.AcodeVpConfig;

import java.util.ArrayList;

/**
 * user：yangtao.
 * date：2017/7/6
 * describe：这是个啥？
 */

public class AcodeBannerVpActivity extends Activity {
    //图片轮播
    private AcodeBannerVp acodeBannerVp;

    private ArrayList<String> urls;

    private ArrayList<String> urls1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_banner);
        initView();
    }

    private void initView() {
        urls = new ArrayList<>();
        urls1 = new ArrayList<>();
        urls.add("https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1507705313&di=008689b4906839d96faf7f874cb6ac6c&src=http://img.zcool.cn/community/016371583d1cdba8012060c8047065.jpg@900w_1l_2o_100sh.jpg");
        urls.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg");
        urls.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
        urls.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg");
        urls.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg");
        urls1.add("不悔梦归处，只恨太匆匆");
        urls1.add("我喜欢丁香，白色的");
        urls1.add("紫色的，都喜欢");
        urls1.add("我喜欢他，是我的");
        urls1.add("不是我的，都喜欢");
        acodeBannerVp = (AcodeBannerVp) findViewById(R.id.acode_banner_vp);
        acodeBannerVp.setData(urls)
                .setAcodePointGravity(AcodeVpConfig.RIGHT)
                .setFirstItem(47)
                .setIsAutoPlay(true)
                .setAcodeVpAnim(5)
                .setAcodeTitleText(urls1)
                .start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("post", "************************************onStart");
        acodeBannerVp.startAutoPlay();
    }

    @Override
    protected void onStop() {
        Log.d("post", "************************************onStop");
        super.onStop();
        acodeBannerVp.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        Log.d("post", "************************************onDestroy");
        super.onDestroy();
    }
}

package com.acode.img.lib.viewpager.weigt.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.acode.img.lib.R;
import com.acode.img.lib.data.AcodeVpConfig;
import com.acode.img.lib.viewpager.transformer.AccordionTransformer;
import com.acode.img.lib.viewpager.transformer.BackgroundToForegroundTransformer;
import com.acode.img.lib.viewpager.transformer.CubeInTransformer;
import com.acode.img.lib.viewpager.transformer.CubeOutTransformer;
import com.acode.img.lib.viewpager.transformer.DefaultTransformer;
import com.acode.img.lib.viewpager.transformer.DepthPageTransformer;
import com.acode.img.lib.viewpager.transformer.FlipHorizontalTransformer;
import com.acode.img.lib.viewpager.transformer.FlipVerticalTransformer;
import com.acode.img.lib.viewpager.transformer.ForegroundToBackgroundTransformer;
import com.acode.img.lib.viewpager.transformer.RotateDownTransformer;
import com.acode.img.lib.viewpager.transformer.RotateUpTransformer;
import com.acode.img.lib.viewpager.transformer.ScaleInOutTransformer;
import com.acode.img.lib.viewpager.transformer.StackTransformer;
import com.acode.img.lib.viewpager.transformer.TabletTransformer;
import com.acode.img.lib.viewpager.transformer.ZoomInTransformer;
import com.acode.img.lib.viewpager.transformer.ZoomOutSlideTransformer;
import com.acode.img.lib.viewpager.transformer.ZoomOutTranformer;

import java.util.ArrayList;
import java.util.List;

/**
 * user：yangtao.
 * date：2017/7/6
 * describe：这是个啥？
 */

public class AcodeBannerVp extends FrameLayout implements ViewPager.OnPageChangeListener {
    private ViewPager vp_banner;
    //存放title中文本
    private TextView acode_tv_title;
    //存放title中点点的layout
    private LinearLayout acode_layout_point;
    //点点未选中的图
    private Drawable indicatorsPointsSelect = getResources().getDrawable(R.drawable.gray_radius);
    //点点选中的图
    private Drawable indicatorsPointsSelected = getResources().getDrawable(R.drawable.white_radius);
    //数据
    private List<String> strUrls;
    //图片数量
    private int count;
    //存放底部的点点图
    private ArrayList<ImageView> indicatorsPoints;
    //点点的宽高
    private int pointW, pointH;
    //点点所在的layout的位置
    private int gravity;
    //上一个下标
    private int lastPostion;
    //首次展示从第几页开始
    private int firstPosition = AcodeVpConfig.FIRST_POSITION;
    //是否是无线滚动
    private boolean isCircle = true;
    //是否是自动播放
    private boolean isAutoPlay;
    //适配器
    private AcodeBannerPagerAdapter acodePagerAdapter;
    //handler
    private Handler handler = new Handler();
    //动画类
    private List<Class<? extends ViewPager.PageTransformer>> transformers = new ArrayList<>();
    //title文本内容
    private List<String> titleTexts;

    private Context context;

    public AcodeBannerVp(@NonNull Context context) {
        this(context, null);
    }

    public AcodeBannerVp(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initVpView();
    }

    //10001banner viewpager 展示
    private void initVpView() {
        //读取自定义的布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.activity_viewpager_banner_list, null);
        //自定义viewpager，虽然并没有自定义什么
        vp_banner = (ViewPager) view.findViewById(R.id.vp_banner);
        //title展示的text
        acode_tv_title = (TextView) view.findViewById(R.id.acode_tv_title);
        //点点所在布局的layout
        acode_layout_point = (LinearLayout) view.findViewById(R.id.acode_layout_point);
        //获取点点的宽高
        pointW = AcodeVpConfig.POINT_WIDTH;
        //获取点点的宽高
        pointH = AcodeVpConfig.POINT_HEIGHT;
        this.addView(view);
        initAnim();
    }


    /**
     * 设置数据源
     *
     * @param urls 图片展示的地址
     * @return 当前对象
     */
    public AcodeBannerVp setData(List<String> urls) {
        this.strUrls = urls;
        this.count = urls.size();
        return this;
    }

    /**
     * 设置当前显示的下标
     * 主要用于首次加载的时候    用户自动设置展示的图片下标
     *
     * @param position 第一次展示的图片下标
     * @return 当前对象
     */
    public AcodeBannerVp setFirstItem(int position) {
        this.firstPosition = position % count;
        /**
         * lastPostion的主要作用是为了改变点点的状态而设定的。
         * 当滑动到下个图片的时候，点点要跟随改变。同时，下个图片的点点变成选中状态，上一个要变成未选中状态。此功能在onPageSelected()中实现
         */
        lastPostion = firstPosition;
        return this;
    }

    //设置是否自动轮播
    public AcodeBannerVp setIsAutoPlay(boolean b) {
        this.isAutoPlay = b;
        return this;
    }

    /**
     * 设置点点的位置
     */
    public AcodeBannerVp setAcodePointGravity(int type) {
        switch (type) {
            case AcodeVpConfig.LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case AcodeVpConfig.CENTER:
                gravity = Gravity.CENTER;
                break;
            case AcodeVpConfig.RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }
        //直接设置点点的位置
        acode_layout_point.setGravity(gravity);
        return this;
    }

    /**
     * 设置指示器
     * 初始化点点图
     * 有多少图创建多少点
     */
    private void setIndicators() {
        //集合为空，创建集合
        if (indicatorsPoints == null) {
            indicatorsPoints = new ArrayList<>();
        }
        //初始化的时候将点点集合清空，确保每回我们不会重复添加，消耗内存
        indicatorsPoints.clear();
        //移除父容器的的所有view，同样为了避免重复添加view，消耗资源
        acode_layout_point.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView img = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointW, pointH);
            params.leftMargin = AcodeVpConfig.POINT_MARGIN;
            img.setLayoutParams(params);
            //将第一个设置成我们选中的图片,如果有title文本内容，也设置文本
            if (i == 0 && firstPosition == 0) {
                img.setImageDrawable(indicatorsPointsSelected);
                if (titleTexts != null) {
                    acode_tv_title.setText(titleTexts.get(0));
                }
            } else {
                img.setImageDrawable(indicatorsPointsSelect);
            }
            //将点点添加到父容器
            acode_layout_point.addView(img);
            //将点点图片增加到集合里
            indicatorsPoints.add(img);
        }
    }

    /**
     * 设置适配器viewpagers
     */
    private void setAdapter() {
        acodePagerAdapter = new AcodeBannerPagerAdapter(context, strUrls);
        //渲染
        vp_banner.setAdapter(acodePagerAdapter);
        //设置滑动监听
        vp_banner.setOnPageChangeListener(this);
        //设置当前展示下标
        vp_banner.setCurrentItem(firstPosition);
    }

    /**
     * 设置切换动画
     *
     * @param position 并不知道是干嘛的  抄别人的....
     * @return
     */
    public AcodeBannerVp setAcodeVpAnim(int position) {
        try {
            if (transformers != null && transformers.size() > 0) {
                setPageTransformer(true, transformers.get(position).newInstance());
            }
        } catch (Exception e) {
            Log.e("post", "Please set the PageTransformer class");
        }
        return this;
    }

    /**
     * Set a {@link ViewPager.PageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    public AcodeBannerVp setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        vp_banner.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    /**
     * 初始化动画集合，添加数据
     */
    public void initAnim() {
        transformers.add(DefaultTransformer.class);
        transformers.add(AccordionTransformer.class);
        transformers.add(BackgroundToForegroundTransformer.class);
        transformers.add(ForegroundToBackgroundTransformer.class);
        transformers.add(CubeInTransformer.class);
        transformers.add(CubeOutTransformer.class);
        transformers.add(DepthPageTransformer.class);
        transformers.add(FlipHorizontalTransformer.class);
        transformers.add(FlipVerticalTransformer.class);
        transformers.add(RotateDownTransformer.class);
        transformers.add(RotateUpTransformer.class);
        transformers.add(ScaleInOutTransformer.class);
        transformers.add(StackTransformer.class);
        transformers.add(TabletTransformer.class);
        transformers.add(ZoomInTransformer.class);
        transformers.add(ZoomOutTranformer.class);
        transformers.add(ZoomOutSlideTransformer.class);
    }

    /**
     * 设置title文本
     *
     * @param strs title文本
     */
    public AcodeBannerVp setAcodeTitleText(List<String> strs) {
        this.titleTexts = strs;
        return this;
    }

    //========================================OnPageChangeListener 页面切换监听==============================================
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //此方法用来监听滑动
    }

    @Override
    public void onPageSelected(int position) {
        //滑动结束后将修改点点的状态，因为我们的size设置的无限大，所以position会无线增加，我们需要计算当前页的下标。position % count
        indicatorsPoints.get(position % count).setImageDrawable(indicatorsPointsSelected);
        indicatorsPoints.get(lastPostion % count).setImageDrawable(indicatorsPointsSelect);
        if (titleTexts != null && titleTexts.size() != 0) {
            acode_tv_title.setText(titleTexts.get(position % titleTexts.size()));
        }
        //记录上一页的position
        lastPostion = position;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        //此方法监听滑动的三种状态 state 0(静止状态)   1(滑动中)   2(滑动结束)
        switch (state) {
            case 0:
                startAutoPlay();
                break;
            case 1:
                stopAutoPlay();
                break;
            case 2:
                startAutoPlay();
                break;
        }
    }


    //=====================================自动播放======================================================

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                int page = vp_banner.getCurrentItem() + 1;
                vp_banner.setCurrentItem(page);
                if (handler != null && task != null) {
                    handler.postDelayed(task, AcodeVpConfig.DELAYTIME);
                }
            }
        }
    };

    public void startAutoPlay() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, AcodeVpConfig.DELAYTIME);
    }

    public void stopAutoPlay() {
        handler.removeCallbacks(task);
    }

    /**
     * 开始渲染界面
     */
    public AcodeBannerVp start() {
        setIndicators();
        setAdapter();
        return this;
    }
}

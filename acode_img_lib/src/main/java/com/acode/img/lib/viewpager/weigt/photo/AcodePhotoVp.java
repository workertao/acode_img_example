package com.acode.img.lib.viewpager.weigt.photo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.acode.img.lib.R;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.data.AcodeVpConfig;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.SingleImagePhotosUtils;
import com.acode.img.lib.viewpager.listener.AcodeClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * user：yangtao.
 * date：2017/9/30
 */

public class AcodePhotoVp extends LinearLayout implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager vp_photo;
    //取消
    private TextView tv_cancel;
    //页数
    private TextView tv_num_size;
    //选中的条目
    private TextView tv_select_nub_size;
    //选中的图片
    private ImageView img_select_icon;
    //当前下标
    private int index;
    //已经选中
    private LinearLayout ll_select_root;
    //数据
    private List<ImagePhoto> imagePhotos;
    //图片数量
    private int count;
    //首次展示从第几页开始
    private int firstPosition = AcodeVpConfig.FIRST_POSITION;
    //适配器
    private AcodePhotoPagerAdapter acodePhotoPagerAdapter;

    private AcodeClickListener acodeClickListener;

    private Context context;

    public AcodePhotoVp(Context context) {
        this(context, null);
    }

    public AcodePhotoVp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPhotoView();
    }


    //10002相册图片展示
    public void initPhotoView() {
        //读取自定义的布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.activity_viewpager_photo_list, null);
        //自定义photoVpView，虽然并没有自定义什么
        vp_photo = (ViewPager) view.findViewById(R.id.vp_photo);
        tv_select_nub_size = (TextView) view.findViewById(R.id.tv_select_nub_size);
        img_select_icon = (ImageView) view.findViewById(R.id.img_select_icon);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_num_size = (TextView) view.findViewById(R.id.tv_num_size);
        ll_select_root = (LinearLayout) view.findViewById(R.id.ll_select_root);

        ll_select_root.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        this.addView(view);
    }

    /**
     * 设置数据源
     *
     * @param imagePhotos 图片展示的地址
     * @return 当前对象
     */
    public AcodePhotoVp setData(List<ImagePhoto> imagePhotos) {
        this.imagePhotos = imagePhotos;
        this.count = imagePhotos.size();
        setNubSize(1, count);
        return this;
    }

    /**
     * 设置当前页数
     *
     * @param nub
     * @param size
     */
    private void setNubSize(int nub, int size) {
        tv_num_size.setText(nub + "/" + size);
    }

    /**
     * 设置监听事件
     *
     * @param acodeClickListener
     * @return
     */
    public AcodePhotoVp setOnAcodeClickListener(AcodeClickListener acodeClickListener) {
        this.acodeClickListener = acodeClickListener;
        return this;
    }

    /**
     * 设置当前显示的下标
     * 主要用于首次加载的时候    用户自动设置展示的图片下标
     *
     * @param position 第一次展示的图片下标
     * @return 当前对象
     */
    public AcodePhotoVp setFirstItem(int position) {
        this.firstPosition = position;
        /**
         * lastPostion的主要作用是为了改变点点的状态而设定的。
         * 当滑动到下个图片的时候，点点要跟随改变。同时，下个图片的点点变成选中状态，上一个要变成未选中状态。此功能在onPageSelected()中实现
         */
        return this;
    }

    /**
     * 设置适配器viewpagers
     */
    private void setAdapter() {
        acodePhotoPagerAdapter = new AcodePhotoPagerAdapter(context, imagePhotos);
        //渲染
        vp_photo.setAdapter(acodePhotoPagerAdapter);
        //设置滑动监听
        vp_photo.setOnPageChangeListener(this);
        //设置当前展示下标
        vp_photo.setCurrentItem(firstPosition);
        //检测当前第一张图有么有选中
        onIsSelect(imagePhotos.get(firstPosition).isSelect());
    }

    public AcodePhotoVp setSelectPhotoData(ArrayList<ImagePhoto> selectPhotoData) {
        tv_select_nub_size.setText("(" + getSize() + "/" + AcodeCameraConfig.MAX_SIZE + ")");
        return this;
    }

    //========================================OnPageChangeListener 页面切换监听==============================================
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //此方法用来监听滑动
    }

    @Override
    public void onPageSelected(int position) {
        //记录上一页的position
        setNubSize(position % imagePhotos.size() + 1, count);
        if (imagePhotos == null || imagePhotos.size() == 0) {
            return;
        }
        onIsSelect(imagePhotos.get(position % imagePhotos.size()).isSelect());
        index = position % imagePhotos.size();
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("post", "");
    }


    /**
     * 开始渲染界面
     */
    public AcodePhotoVp start() {
        setAdapter();
        return this;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_select_root) {
            ImagePhoto imagePhoto = imagePhotos.get(index);
            if (imagePhoto.isBad()) {
                Toast.makeText(context, "图片顺坏", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getSize() < AcodeCameraConfig.MAX_SIZE || SingleImagePhotosUtils.getIntance().getSelectImagePhotos().contains(imagePhoto)) {
                SingleImagePhotosUtils.getIntance().setSelectState(index);
                onIsSelect(SingleImagePhotosUtils.getIntance().getCurrentSelect(index));
                updateSelectPhotoDataUI();
                return;
            }
            Toast.makeText(context, "最多选" + AcodeCameraConfig.MAX_SIZE + "张", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v.getId() == R.id.tv_cancel) {
            ((Activity) context).finish();
            return;
        }
    }

    //修改界面ui
    private void updateSelectPhotoDataUI() {
        if (SingleImagePhotosUtils.getIntance().getSelectSize() != 0) {
            tv_select_nub_size.setText("(" + getSize() + "/" + AcodeCameraConfig.MAX_SIZE + ")");
        } else {
            tv_select_nub_size.setText("");
        }
    }


    //区分当前图片是否选中
    private void onIsSelect(boolean isSelect) {
        if (isSelect) {
            img_select_icon.setImageResource(R.mipmap.icon_selected);
            return;
        }
        img_select_icon.setImageResource(R.mipmap.icon_select);
    }

    private int getSize() {
        return SingleImagePhotosUtils.getIntance().getSelectSize();
    }
}

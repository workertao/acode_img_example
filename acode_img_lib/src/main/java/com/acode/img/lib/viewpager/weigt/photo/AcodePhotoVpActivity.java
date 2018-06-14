package com.acode.img.lib.viewpager.weigt.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.acode.img.lib.R;
import com.acode.img.lib.base.AcodeBaseActivity;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.SingleImagePhotosUtils;
import com.acode.img.lib.viewpager.listener.AcodeClickListener;

import java.util.ArrayList;

/**
 * user：yangtao.
 * date：2017/7/6
 * describe：这是个啥？
 */

public class AcodePhotoVpActivity extends AcodeBaseActivity implements AcodeClickListener {
    //自定义相册
    private AcodePhotoVp acodePhotoVp;

    private ArrayList<ImagePhoto> imagePhotos;

    private ArrayList<ImagePhoto> selectPhotoData;

    @Override
    public int initSystemBarColor() {
        return R.color.acode_theme_style;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_photo);
        initView();
    }

    private void initView() {
        imagePhotos = SingleImagePhotosUtils.getIntance().getCurrentImagePhotos();
        selectPhotoData = SingleImagePhotosUtils.getIntance().getSelectImagePhotos();
        int position = getIntent().getIntExtra("position", 0);
        if (imagePhotos == null || imagePhotos.size() == 0) {
            return;
        }
        acodePhotoVp = (AcodePhotoVp) findViewById(R.id.acode_photo_vp);
        acodePhotoVp.setData(imagePhotos)
                .setFirstItem(position)
                .setSelectPhotoData(selectPhotoData)
                .setOnAcodeClickListener(this)
                .start();
    }

    @Override
    public void onAcodeVpClick(int position) {

    }
}

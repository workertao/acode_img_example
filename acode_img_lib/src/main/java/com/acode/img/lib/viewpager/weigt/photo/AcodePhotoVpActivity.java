package com.acode.img.lib.viewpager.weigt.photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.acode.img.lib.R;
import com.acode.img.lib.base.AcodeBaseActivity;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.entity.ImagePhoto;
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

    private int cameraSize;

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
        imagePhotos = (ArrayList<ImagePhoto>) getIntent().getSerializableExtra("imagePhotos");
        selectPhotoData = (ArrayList<ImagePhoto>) getIntent().getSerializableExtra("selectPhotoData");
        cameraSize = getIntent().getIntExtra("cameraSize",0);
        int position = getIntent().getIntExtra("position", 0);
        if (imagePhotos == null || imagePhotos.size() == 0) {
            return;
        }
        acodePhotoVp = (AcodePhotoVp) findViewById(R.id.acode_photo_vp);
        acodePhotoVp.setData(imagePhotos)
                .setCameraSize(cameraSize)
                .setFirstItem(position)
                .setSelectPhotoData(selectPhotoData)
                .setOnAcodeClickListener(this)
                .start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("post", "************************************onStart");
//        acodeVp.startAutoPlay();
    }

    @Override
    protected void onStop() {
        Log.d("post", "************************************onStop");
        super.onStop();
//        acodeVp.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        Log.d("post", "************************************onDestroy");
        super.onDestroy();

    }

    @Override
    public void onAcodeVpClick(int position) {

    }

    @Override
    public void onResultPhotoData(ArrayList<ImagePhoto> imagePhotos, ArrayList<ImagePhoto> selectPhotoData) {
        this.imagePhotos = imagePhotos;
        Intent intent = new Intent();
        intent.putExtra("imagePhotos", imagePhotos);
        intent.putExtra("selectPhotoData", selectPhotoData);
        setResult(AcodeCameraConfig.GOTO_VP_RESPONSE, intent);
        this.finish();
    }

    @Override
    public void back() {
        this.finish();
    }
}

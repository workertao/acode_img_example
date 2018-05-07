package com.acode.img.lib.helper;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.acode.img.lib.entity.ImagePhoto;

import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/5/41546
 * email:yangtao@bjxmail.com
 * introduce:接口
 */
public interface IAcoderImgLibHelper {
    /**
     * 拍照
     */
    void takePhoto();

    /**
     * 获取照片列表
     */
    void getPhotoList();

    /**
     * 删除某个
     *
     * @param position
     */
    void remove(int position);

    /**
     * 查看大图
     * @param imagePhotos      图片集合
     * @param selectPhotoData  选中的图片集合
     * @param position         第一个展示的下标
     */
    void showBigPhoto(ArrayList<ImagePhoto> imagePhotos, ArrayList<ImagePhoto> selectPhotoData, int position);


    /**
     * 相册/相机返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);


    /**
     * 权限返回结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}

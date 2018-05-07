package com.acode.img.lib.helper;

import com.acode.img.lib.entity.ImagePhoto;

import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/5/41600
 * email:yangtao@bjxmail.com
 * introduce:相册库的回调监听
 */
public interface AcodeImgLibListener {
    /**
     * 获取选中的照片
     *
     * @param imagePhotos 相机拍照和相册库的照片集合
     */
    void getImagePhotos(ArrayList<ImagePhoto> imagePhotos);
}

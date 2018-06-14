package com.acode.img.lib.utils;

import android.media.Image;
import android.util.Log;

import com.acode.img.lib.entity.ImagePhoto;

import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/6/131616
 * email:yangtao@bjxmail.com
 * introduce:功能
 */
public class SingleImagePhotosUtils {
    private static SingleImagePhotosUtils intance = null;

    //所有照片
    private ArrayList<ImagePhoto> allImagePhotos;

    //当前的相册下的所有相片
    private ArrayList<ImagePhoto> currentImagePhotos;

    //选中的相片
    private ArrayList<ImagePhoto> selectImagePhotos = new ArrayList<>();

    public static SingleImagePhotosUtils getIntance() {
        if (intance == null) {
            intance = new SingleImagePhotosUtils();
        }
        return intance;
    }

    public ArrayList<ImagePhoto> getAllImagePhotos() {
        return allImagePhotos;
    }

    public SingleImagePhotosUtils setAllImagePhotos(ArrayList<ImagePhoto> allImagePhotos) {
        this.allImagePhotos = allImagePhotos;
        return this;
    }

    public ArrayList<ImagePhoto> getCurrentImagePhotos() {
        return currentImagePhotos;
    }

    public SingleImagePhotosUtils setCurrentImagePhotos(ArrayList<ImagePhoto> currentImagePhotos) {
        this.currentImagePhotos = currentImagePhotos;
        return this;
    }

    public ArrayList<ImagePhoto> getSelectImagePhotos() {
        return selectImagePhotos;
    }

    public SingleImagePhotosUtils setSelectImagePhotos(ArrayList<ImagePhoto> selectImagePhotos) {
        this.selectImagePhotos = selectImagePhotos;
        return this;
    }

    //设置选中状态
    public void setSelectState(int position) {
        ImagePhoto imagePhoto = getCurrentImagePhotos().get(position);
        if (imagePhoto.isSelect()) {
            //设置未选中
            selectImagePhotos.remove(imagePhoto);
            imagePhoto.setSelect(false);
        } else {
            //设置选中
            imagePhoto.setIndex(System.currentTimeMillis());
            imagePhoto.setSelect(true);
            selectImagePhotos.add(imagePhoto);
        }
    }

    //获取选中的图片数量
    public int getSelectSize() {
        return selectImagePhotos.size();
    }

    //获取当前imagephoto是否选中
    public boolean getCurrentSelect(int position) {
        return currentImagePhotos.get(position).isSelect();
    }

    //删除
    public void remove(int position) {
        ImagePhoto imagePhoto = getCurrentImagePhotos().get(position);
        //设置未选中
        selectImagePhotos.remove(imagePhoto);
        imagePhoto.setSelect(false);
    }

    //释放资源
    public void release() {
        intance = null;
    }
}

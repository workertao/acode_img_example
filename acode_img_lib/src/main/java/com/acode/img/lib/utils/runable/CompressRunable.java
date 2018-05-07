package com.acode.img.lib.utils.runable;

import android.text.TextUtils;
import android.util.Log;

import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.CompressUtils;

import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/4/281135
 * email:yangtao@bjxmail.com
 * introduce:压缩线程
 */
public class CompressRunable implements Runnable {
    //压缩单图
    private ImagePhoto imagePhoto;

    //压缩多图
    private ArrayList<ImagePhoto> imagePhotos;

    //单图压缩回调
    private CompressListener compressListener;

    //多图压缩回调
    private AllCompressListener allCompressListener;


    public CompressRunable(ImagePhoto imagePhoto, CompressListener compressListener) {
        this.imagePhoto = imagePhoto;
        this.compressListener = compressListener;
    }

    public CompressRunable(ArrayList<ImagePhoto> imagePhotos, AllCompressListener allCompressListener) {
        this.imagePhotos = imagePhotos;
        this.allCompressListener = allCompressListener;
    }

    @Override
    public void run() {
        if (imagePhoto != null) {
            compressOne();
            return;
        }
        if (imagePhotos != null && imagePhotos.size() > 0) {
            compressAll();
        }
    }

    //压缩单图
    private void compressOne() {
        String compressPath = CompressUtils.compress(imagePhoto.getPath());
        imagePhoto.setCompressPath(compressPath);
        if (compressListener == null) {
            return;
        }
        compressListener.onCompressComplete(imagePhoto);
    }

    //压缩多图
    private void compressAll() {
        for (int i = 0; i < imagePhotos.size(); i++) {
            //判断当前图片是否压缩
            if (!TextUtils.isEmpty(imagePhotos.get(i).getCompressPath())) {
                //跳过本次压缩
                Log.d("post", "已压缩：" + i);
                if (i == imagePhotos.size() - 1 && allCompressListener != null) {
                    allCompressListener.onAllCompressComplete(imagePhotos);
                }
                continue;
            }
            Log.d("post", "压缩中:" + i);
            String compressPath = CompressUtils.compress(imagePhotos.get(i).getPath());
            Log.d("post", "压缩完成:" + i);
            imagePhotos.get(i).setCompressPath(compressPath);
            if (i == imagePhotos.size() - 1 && allCompressListener != null) {
                allCompressListener.onAllCompressComplete(imagePhotos);
            }
        }
    }

    public interface CompressListener {
        //单图压缩完成
        void onCompressComplete(ImagePhoto imagePhoto);

    }

    public interface AllCompressListener {
        //多图压缩完成
        void onAllCompressComplete(ArrayList<ImagePhoto> imagePhotos);
    }
}

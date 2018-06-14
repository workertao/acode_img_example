package com.acode.img.lib.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.acode.img.lib.camera.AcodeCameraActivity;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.photo.AcodePhotoListActivity;
import com.acode.img.lib.utils.CompressUtils;
import com.acode.img.lib.utils.PermissionUtils;
import com.acode.img.lib.utils.SingleImagePhotosUtils;
import com.acode.img.lib.utils.runable.CompressRunable;
import com.acode.img.lib.viewpager.weigt.photo.AcodePhotoVpActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * user:yangtao
 * date:2018/5/41545
 * email:yangtao@bjxmail.com
 * introduce:相机/相册帮助类
 */
public class AcodeImgLibHelper implements IAcoderImgLibHelper {
    //上下文
    private Object context;

    //权限工具类
    private PermissionUtils permissionUtils;

    //相册工具类的回调监听
    private AcodeImgLibListener acodeImgLibListener;

    /**
     * @param context
     * @param acodeImgLibListener 选中照片回调
     * @param count               设置最大选中数量
     */
    public AcodeImgLibHelper(Object context, AcodeImgLibListener acodeImgLibListener, int count) {
        this.context = context;
        this.acodeImgLibListener = acodeImgLibListener;
        setCount(count);
        permissionUtils = new PermissionUtils((Activity) context);
    }

    //设置最大的选中数量
    @Override
    public void setCount(int count) {
        if (count < 1) {
            AcodeCameraConfig.MAX_SIZE = 1;
        } else if (count > 9) {
            AcodeCameraConfig.MAX_SIZE = 9;
        } else {
            AcodeCameraConfig.MAX_SIZE = count;
        }
    }

    @Override
    public void takePhoto() {
        if (AcodeCameraConfig.MAX_SIZE == 1) {
            SingleImagePhotosUtils.getIntance().getSelectImagePhotos().clear();
        }
        boolean isFlag = permissionUtils.requestPermission(AcodeCameraConfig.PREMISSION_CAMERA
                , permissionUtils.request_permission);
        if (isFlag) {
            Intent intent = new Intent((Activity) context, AcodeCameraActivity.class);
            ((Activity) context).startActivityForResult(intent, AcodeCameraConfig.TAKE_PHOTO_REQUEST);
        }
    }

    @Override
    public void getPhotoList() {
        if (AcodeCameraConfig.MAX_SIZE == 1) {
            SingleImagePhotosUtils.getIntance().getSelectImagePhotos().clear();
        }
        boolean isFlag = permissionUtils.requestPermission(AcodeCameraConfig.PREMISSION_ALBUM
                , permissionUtils.request_permission);
        if (isFlag) {
            Intent intent1 = new Intent((Activity) context, AcodePhotoListActivity.class);
            ((Activity) context).startActivityForResult(intent1, AcodeCameraConfig.SELECT_VP_REQUEST);
        }
    }

    @Override
    public void remove(int position) {
        remove(SingleImagePhotosUtils.getIntance().getSelectImagePhotos().get(position));
    }

    @Override
    public void remove(ImagePhoto imagePhoto) {
        if (SingleImagePhotosUtils.getIntance().getSelectImagePhotos().contains(imagePhoto)) {
            SingleImagePhotosUtils.getIntance().getSelectImagePhotos().remove(imagePhoto);
        }
        notifydata();
    }

    @Override
    public void showBigPhoto(ArrayList<ImagePhoto> currentData, ArrayList<ImagePhoto> selectData, int position) {
        ArrayList<ImagePhoto> currentImagePhotos = new ArrayList<>();
        currentImagePhotos.addAll(currentData);
        SingleImagePhotosUtils.getIntance().setCurrentImagePhotos(currentImagePhotos);
        SingleImagePhotosUtils.getIntance().setSelectImagePhotos(selectData);
        Intent intent = new Intent((Activity) context, AcodePhotoVpActivity.class);
        intent.putExtra("position", position);
        ((Activity) context).startActivityForResult(intent, AcodeCameraConfig.GOTO_VP_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AcodeCameraConfig.TAKE_PHOTO_REQUEST:
                if (data == null) {
                    return;
                }
                Bundle bundle1 = data.getBundleExtra("bundle");
                if (bundle1 == null) {
                    return;
                }
                ImagePhoto imagePhoto = (ImagePhoto) bundle1.getSerializable("imagePhoto");
                Log.d("aocde", imagePhoto.getPath());
                if (imagePhoto == null) {
                    return;
                }
                cameraCompress(imagePhoto);
                break;
            case AcodeCameraConfig.SELECT_VP_REQUEST:
                notifydata();
                break;
            case AcodeCameraConfig.GOTO_VP_REQUEST:
                acodeImgLibListener.getImagePhotos(SingleImagePhotosUtils.getIntance().getSelectImagePhotos());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //遍历循环查看用户是否授权，如果有一个没有授权就return
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        switch (requestCode) {
            case AcodeCameraConfig.PREMISSION_CAMERA:
                Intent intent = new Intent((Activity) context, AcodeCameraActivity.class);
                ((Activity) context).startActivityForResult(intent, AcodeCameraConfig.TAKE_PHOTO_REQUEST);
                break;
            case AcodeCameraConfig.PREMISSION_ALBUM:
                Intent intent1 = new Intent((Activity) context, AcodePhotoListActivity.class);
                ((Activity) context).startActivityForResult(intent1, AcodeCameraConfig.SELECT_VP_REQUEST);
                break;
        }
    }

    /**
     * 压缩拍的照片
     *
     * @param imagePhoto 要压缩的照片
     */
    private void cameraCompress(ImagePhoto imagePhoto) {
        CompressUtils.compressOne(imagePhoto, new CompressRunable.CompressListener() {
            @Override
            public void onCompressComplete(final ImagePhoto imagePhoto) {
                //如果只能选择一张，则将之前选中的全部清空
                if (AcodeCameraConfig.MAX_SIZE == 1) {
                    SingleImagePhotosUtils.getIntance().getSelectImagePhotos().clear();
                }
                SingleImagePhotosUtils.getIntance().getSelectImagePhotos().add(imagePhoto);
                notifydata();
            }
        });
    }

    /**
     * 数据更新
     * 组合相册和相机的数据
     * 按照选中顺序排序
     */
    private void notifydata() {
        if (AcodeCameraConfig.MAX_SIZE > 1) {
            //根据时间戳排序
            Collections.sort(SingleImagePhotosUtils.getIntance().getSelectImagePhotos(), new Comparator<ImagePhoto>() {
                @Override
                public int compare(ImagePhoto ip1, ImagePhoto ip2) {
                    if (ip1.getIndex() < ip2.getIndex()) {
                        return -1;
                    } else if (ip1.getIndex() > ip2.getIndex()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                acodeImgLibListener.getImagePhotos(SingleImagePhotosUtils.getIntance().getSelectImagePhotos());
            }
        });
    }

    /**
     * 释放资源
     */
    public void release() {
        SingleImagePhotosUtils.getIntance().release();
    }
}

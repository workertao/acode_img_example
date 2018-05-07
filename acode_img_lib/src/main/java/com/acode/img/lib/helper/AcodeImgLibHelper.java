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

    //拍照的数据集
    private ArrayList<ImagePhoto> imgCameras;

    //相册选中的照片
    private ArrayList<ImagePhoto> imgPhotos;

    //所有的照片集合
    private ArrayList<ImagePhoto> all;

    //相册工具类的回调监听
    private AcodeImgLibListener acodeImgLibListener;

    public AcodeImgLibHelper(Object context, AcodeImgLibListener acodeImgLibListener) {
        this.context = context;
        this.acodeImgLibListener = acodeImgLibListener;
        permissionUtils = new PermissionUtils((Activity) context);
        imgCameras = new ArrayList<>();
        imgPhotos = new ArrayList<>();
    }

    @Override
    public void takePhoto() {
        boolean isFlag = permissionUtils.requestPermission(AcodeCameraConfig.PREMISSION_CAMERA
                , permissionUtils.request_permission);
        if (isFlag) {
            Intent intent = new Intent((Activity) context, AcodeCameraActivity.class);
            ((Activity) context).startActivityForResult(intent, AcodeCameraConfig.TAKE_PHOTO_REQUEST);
        }
    }

    @Override
    public void getPhotoList() {
        boolean isFlag = permissionUtils.requestPermission(AcodeCameraConfig.PREMISSION_ALBUM
                , permissionUtils.request_permission);
        if (isFlag) {
            Intent intent1 = new Intent((Activity) context, AcodePhotoListActivity.class);
            intent1.putExtra("selectPhotoData", imgPhotos);
            intent1.putExtra("cameraSize", imgCameras.size());
            ((Activity) context).startActivityForResult(intent1, AcodeCameraConfig.SELECT_VP_REQUEST);
        }
    }

    @Override
    public void remove(int position) {
        if (all == null || all.size() == 0) {
            return;
        }
        ImagePhoto imagePhoto = all.get(position);
        if (imgPhotos.contains(imagePhoto)) {
            imgPhotos.remove(imagePhoto);
        }
        if (imgCameras.contains(imagePhoto)) {
            imgCameras.remove(imagePhoto);
        }
        notifydata();
    }

    @Override
    public void showBigPhoto(ArrayList<ImagePhoto> imagePhotos, ArrayList<ImagePhoto> selectPhotoData, int position) {
        Intent intent = new Intent((Activity) context, AcodePhotoVpActivity.class);
        intent.putExtra("imagePhotos", imagePhotos);
        intent.putExtra("selectPhotoData", selectPhotoData);
        intent.putExtra("position", position);
        ((Activity) context).startActivityForResult(intent, AcodeCameraConfig.GOTO_VP_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case AcodeCameraConfig.TAKE_PHOTO_REQUEST:
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
                //相册选中返回数据处理
                if (imgPhotos != null) {
                    imgPhotos.clear();
                    imgPhotos = null;
                }
                //获取绑定的数据源
                Bundle bundle = data.getBundleExtra("bundle");
                if (bundle == null) {
                    return;
                }
                //获取绑定的返回的数据源
                imgPhotos = (ArrayList<ImagePhoto>) bundle.getSerializable("selectPhotoData");
                notifydata();
                Log.d("post", "相册地址：" + imgPhotos.toString());
                break;
            case AcodeCameraConfig.GOTO_VP_REQUEST:
                //viewpager返回的数据，里边包含了拍照的照片，先清空然后重新添加。
                if (imgPhotos != null) {
                    imgPhotos.clear();
                }
                if (imgCameras != null) {
                    imgCameras.clear();
                }
                ArrayList<ImagePhoto> imagePhotos = (ArrayList<ImagePhoto>) data.getSerializableExtra("selectPhotoData");
                for (ImagePhoto imagePhoto1 : imagePhotos) {
                    if (imagePhoto1.getPhotoType().equals(ImagePhoto.PHOTO_TYPE_CAMERA)) {
                        imgCameras.add(imagePhoto1);
                    }
                    if (imagePhoto1.getPhotoType().equals(ImagePhoto.PHOTO_TYPE_SYSTEM_LIST)) {
                        imgPhotos.add(imagePhoto1);
                    }
                }
                //跳过合并和排序的步骤notifydata()，直接返回给回调类
                acodeImgLibListener.getImagePhotos(imagePhotos);
                Log.d("post", "相册地址：" + imagePhotos.toString());
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
                intent1.putExtra("selectPhotoData", imgPhotos);
                intent1.putExtra("cameraSize", imgCameras.size());
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
                //将照片添加到集合中
                imgCameras.add(imagePhoto);
                notifydata();
                Log.d("post", "拍照地址：" + imgCameras.toString());
            }
        });
    }

    /**
     * 数据更新
     * 组合相册和相机的数据
     * 按照选中顺序排序
     */
    private void notifydata() {
        if (all != null) {
            all.clear();
            all = null;
        }
        all = new ArrayList<>();
        if (imgCameras != null) {
            all.addAll(imgCameras);
        }
        if (imgPhotos != null) {
            all.addAll(imgPhotos);
        }
        //根据时间戳排序
        Collections.sort(all, new Comparator<ImagePhoto>() {
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
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                acodeImgLibListener.getImagePhotos(all);
            }
        });
    }
}

package com.acode.img.lib.camera;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.acode.img.lib.R;
import com.acode.img.lib.base.AcodeBaseActivity;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.FileUtils;
import com.acode.img.lib.utils.PhotoClipperUtil;

import java.io.File;
import java.io.IOException;


/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:相机拍照的页面
 */

public class AcodeCameraActivity extends AcodeBaseActivity {
    //裁剪保存的地址
    private File compressFile;

    //拍照保存的地址
    private File cameraFile;

    @Override
    public int initSystemBarColor() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera();
    }

    /**
     * 拍照获取图片
     **/
    public void camera() {
        //新建一个File，传入文件夹目录
        try {
            File file = FileUtils.mkdirsFile(FileUtils.getCameraFilePath());
            //创建File对象，用于存储拍照后的图片
            cameraFile = new File(file, FileUtils.getCameraFileNmae());
            cameraFile.createNewFile();
            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(cameraFile));
            startActivityForResult(intent, AcodeCameraConfig.TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照
            case AcodeCameraConfig.TAKE_PHOTO:
                //取消拍照
                if (resultCode != RESULT_OK) {
                    AcodeCameraActivity.this.finish();
                    return;
                }
                //写入到相册
                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, cameraFile.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    AcodeCameraActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(getFileUri(cameraFile));
                    AcodeCameraActivity.this.sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //创建拍照的实体
                ImagePhoto imagePhoto = new ImagePhoto();
                imagePhoto.setId((int) System.currentTimeMillis());
                imagePhoto.setPath(cameraFile.getPath());
                imagePhoto.setSelect(true);
                imagePhoto.setIndex(System.currentTimeMillis());
                imagePhoto.setPhotoType(ImagePhoto.PHOTO_TYPE_CAMERA);
                //回调到上一个页面
                Intent intent1 = new Intent();
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("imagePhoto", imagePhoto);
                intent1.putExtra("bundle", bundle1);
                setResult(AcodeCameraConfig.TAKE_PHOTO_RESPONSE, intent1);
                AcodeCameraActivity.this.finish();
                break;
            //获取裁剪后的图片
            case AcodeCameraConfig.SELECT_CLIPPER_PIC:
                if (data == null) {
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("photo_path", compressFile.getPath());
                intent.putExtra("bundle", bundle);
                setResult(AcodeCameraConfig.TAKE_PHOTO_RESPONSE, intent);
                AcodeCameraActivity.this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪大图
     *
     * @param context
     * @param uri
     */
    private void clipperBigPic(Context context, Uri uri) {
        if (null == uri) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url = PhotoClipperUtil.getPath(context, uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }
        //发送裁剪命令
        intent.putExtra("crop", true);
        //X方向上的比例
        intent.putExtra("aspectX", 1);
        //Y方向上的比例
        intent.putExtra("aspectY", 1);
        //裁剪区的宽
        intent.putExtra("outputX", 124);
        //裁剪区的高
        intent.putExtra("outputY", 124);
        //是否保留比例
        intent.putExtra("scale", true);
        //返回数据
        intent.putExtra("return-data", true);
        //输出图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //裁剪图片保存位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(createCompressFile()));
        startActivityForResult(intent, AcodeCameraConfig.SELECT_CLIPPER_PIC);
    }

    /**
     * 获取文件的uri
     *
     * @param file
     * @return
     */
    private Uri getFileUri(File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 创建保存裁剪之后的图片路径
     *
     * @return
     */
    private File createCompressFile() {
        //新建一个File，传入文件夹目录
        try {
            File file = FileUtils.mkdirsFile(FileUtils.getCompressFilePath());
            compressFile = new File(file, FileUtils.getCompressFileName());
            compressFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressFile;
    }
}

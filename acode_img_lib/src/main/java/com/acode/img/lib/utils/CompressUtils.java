package com.acode.img.lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.runable.CompressRunable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/4/181342
 * email:yangtao@bjxmail.com
 * introduce:图片压缩工具类
 */
public class CompressUtils {

    /**
     * 压缩多图
     *
     * @param imagePhotos
     * @return
     */
    public static ArrayList<ImagePhoto> compressAll(ArrayList<ImagePhoto> imagePhotos, final CompressRunable.AllCompressListener allCompressListener) {
        if (imagePhotos == null || allCompressListener == null) {
            return new ArrayList<>();
        }
        new Thread(new CompressRunable(imagePhotos, new CompressRunable.AllCompressListener() {
            @Override
            public void onAllCompressComplete(ArrayList<ImagePhoto> compressImagePhotos) {
                //多图压缩成功
                if (allCompressListener == null) {
                    return;
                }
                allCompressListener.onAllCompressComplete(compressImagePhotos);
            }
        })).start();
        return imagePhotos;
    }


    /**
     * 压缩单张图
     *
     * @param imagePhoto
     */
    public static void compressOne(final ImagePhoto imagePhoto, final CompressRunable.CompressListener compressListener) {
        new Thread(new CompressRunable(imagePhoto, new CompressRunable.CompressListener() {
            @Override
            public void onCompressComplete(ImagePhoto imagePhoto) {
                if (compressListener == null) {
                    return;
                }
                compressListener.onCompressComplete(imagePhoto);
            }
        })).start();
    }


    /**
     * 压缩单图
     *
     * @param imagePath 图片地址
     * @return
     */
    public static String compress(String imagePath) {
        Bitmap originBmp = null;
        Bitmap bmp = null;
        FileOutputStream fos = null;
        try {
            //质量压缩
            BitmapFactory.Options options = new BitmapFactory.Options();
            //色彩模式选用RGB_565  每个像素点占得字节少
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //获取要压缩的图片bmp
            originBmp = getBitmapFromSDCard(imagePath, 540, 900);
            //获取旋转之后的图片
            bmp = PictureUtils.rotaingImageView(PictureUtils.readPictureDegree(imagePath), originBmp);
            //新建一个File，传入文件夹目录
            File new_file = FileUtils.mkdirsFile(FileUtils.getCompressFilePath());
            File compress_file = new File(new_file, FileUtils.getCompressFileName());

            // 质量压缩Bitmap到对应尺寸
            int option = 50;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 把压缩后的数据存放到baos中
            bmp.compress(Bitmap.CompressFormat.JPEG, option, baos);
            //写入图片
            fos = new FileOutputStream(compress_file);
            fos.write(baos.toByteArray());
            return compress_file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //释放bitmap内存
                if (originBmp != null) {
                    originBmp.recycle();
                    originBmp = null;
                }
                if (bmp != null) {
                    bmp.recycle();
                    bmp = null;
                }
                if (fos != null) {
                    //释放流
                    fos.flush();
                    fos.close();
                }
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imagePath;
    }

    /**
     * 从SD卡中获取图片并且比例压缩
     *
     * @param path    路径
     * @param mHeight 自定义高度
     * @param mWidth  自定义宽度
     * @return
     */
    public static Bitmap getBitmapFromSDCard(String path, int mHeight, int mWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //计算比例值
        options.inSampleSize = calculateInSampleSize(options, mHeight, mWidth);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算压缩比例值inSampleSize
     *
     * @param options 压缩的参数设置
     * @param mHeight 想要的高度
     * @param mWidth  想要的宽度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int mHeight, int mWidth) {
        //原尺寸大小
        int yHeight = options.outHeight;
        int yWidth = options.outWidth;

        int inSampleSize = 1;
        //如果宽度大的话根据宽度固定大小缩放
        if (yWidth > yHeight && yWidth > mWidth) {
            inSampleSize = (int) (yWidth / mWidth);
        }
        //如果高度高的话根据宽度固定大小缩放
        else if (yWidth < yHeight && yHeight > mHeight) {
            inSampleSize = (int) (yHeight / mHeight);
        }
        if (inSampleSize <= 0)
            inSampleSize = 1;
        return inSampleSize;
    }
}

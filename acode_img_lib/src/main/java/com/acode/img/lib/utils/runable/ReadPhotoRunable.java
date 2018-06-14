package com.acode.img.lib.utils.runable;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.acode.img.lib.entity.ImageFloder;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.FileUtils;
import com.acode.img.lib.utils.SingleImagePhotosUtils;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * user:yangtao
 * date:2018/4/281135
 * email:yangtao@bjxmail.com
 * introduce:读取照片线程
 */

public class ReadPhotoRunable implements Runnable {
    private final String CAMERA = "CAMERA";

    private final String CAMERA_CHINEASE = "图片相册";

    //上下文
    private Context context;

    //相册图片数据集合
    private List<String> mDirPaths = new ArrayList<>();

    //回调
    public OnReadPhotoListener onReadPhotoListener;

    public ReadPhotoRunable(Context context, OnReadPhotoListener onReadPhotoListener) {
        this.context = context;
        this.onReadPhotoListener = onReadPhotoListener;
    }

    @Override
    public void run() {
        try {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = context.getContentResolver();
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " +
                            MediaStore.Images.Media.MIME_TYPE + "=? or " +
                            MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_TAKEN + " DESC");
            if (mCursor == null) {
                if (onReadPhotoListener != null) {
                    onReadPhotoListener.onReadError();
                }
                return;
            }
            ArrayList<ImageFloder> allImageFloders = new ArrayList<>();
            ArrayList<ImagePhoto> allImagePhotos = new ArrayList<>();
            while (mCursor.moveToNext()) {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));// 1.获取图片的路径
                //保存到全部照片中
                ImagePhoto imagePhoto = new ImagePhoto();
                imagePhoto.setPath(path);
                imagePhoto.setLastModified(new File(path).lastModified());
                imagePhoto.setSelect(false);
                imagePhoto.setPhotoType(ImagePhoto.PHOTO_TYPE_SYSTEM_LIST);
                allImagePhotos.add(imagePhoto);
                //获取当前图片的父目录
                File parentFile = new File(path).getParentFile();
                if (parentFile == null || !parentFile.exists())
                    continue;//不获取sd卡根目录下的图片
                //获取图片的文件夹信息
                String parentPath = parentFile.getAbsolutePath();
                //获取图片的文件夹名字
                String parentName = parentFile.getName();
                //自定义一个model，来保存图片的信息
                ImageFloder imageFloder;
                //这个操作，可以提高查询的效率，将查询的每一个图片的文件夹的路径保存到集合中，
                //如果存在，就直接查询下一个，避免对每一个文件夹进行查询操作
                if (mDirPaths.contains(parentPath)) {
                    continue;
                } else {
                    mDirPaths.add(parentPath);//将父路径添加到集合中
                    imageFloder = new ImageFloder();
                    imageFloder.setFirstImagePath(path);
                    imageFloder.setDir(parentPath);
                    if (parentName.toUpperCase().equals(CAMERA.toUpperCase())) {
                        parentName = CAMERA_CHINEASE;
                    }
                    imageFloder.setName(parentName);
                }
                //当前相册下的所有文件【图片】
                File[] files = parentFile.listFiles(getFileterImage());
                //传入每个相册的图片个数
                imageFloder.setCount(files.length);
                //添加每一个相册
                allImageFloders.add(imageFloder);
            }
            //将所有照片排序
            Collections.sort(allImagePhotos, new Comparator<ImagePhoto>() {
                @Override
                public int compare(ImagePhoto ip1, ImagePhoto ip2) {
                    if (ip1.getLastModified() < ip2.getLastModified()) {
                        return 1;
                    } else if (ip1.getLastModified() > ip2.getLastModified()) {
                        return -1;
                    }else {
                        return 0;
                    }
                }
            });
            //保存全部图片的数据到本地文件
//            FileUtils.saveDataToFile(context, JSON.toJSONString(allImagePhotos), FileUtils.FILE_ALL_PHOTOS_NAME);
            SingleImagePhotosUtils.getIntance().setAllImagePhotos(allImagePhotos);
            SingleImagePhotosUtils.getIntance().setCurrentImagePhotos(allImagePhotos);
            //将全部照片添加到自定义的相册中  全部相册信息
            ImageFloder allImageFloder = new ImageFloder();
            allImageFloder.setName("所有照片");
            allImageFloder.setCount(allImagePhotos.size());
            allImageFloder.setDir("所有照片路径");
            allImageFloder.setFirstImagePath(allImagePhotos.get(0).getPath());
            allImageFloders.add(0, allImageFloder);
            mCursor.close();
            mDirPaths = null;
            if (onReadPhotoListener == null) {
                return;
            }
            onReadPhotoListener.onReadComplete(allImageFloders, allImagePhotos);
        } catch (Exception e) {
            e.printStackTrace();
            if (onReadPhotoListener == null) {
                return;
            }
            onReadPhotoListener.onReadError();
        }
    }

    public interface OnReadPhotoListener {
        /**
         * @param imageFloders 所有的相册
         * @param imagePhotos  读取完成后 首次展示的全部照片
         */
        void onReadComplete(ArrayList<ImageFloder> imageFloders, ArrayList<ImagePhoto> imagePhotos);

        //读取失败
        void onReadError();
    }

    //图片筛选器，过滤无效图片
    private FilenameFilter getFileterImage() {
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        };
        return filenameFilter;
    }
}

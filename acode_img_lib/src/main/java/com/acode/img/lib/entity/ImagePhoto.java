package com.acode.img.lib.entity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:图片实体
 */

public class ImagePhoto implements Serializable {
    //进度条
    public static String PROGRESS = "PROGRESS";
    //icon
    public static String ICON = "ICON";
    //拍照
    public static String PHOTO_TYPE_CAMERA = "PHOTO_TYPE_CAMERA";
    //系统相册
    public static String PHOTO_TYPE_SYSTEM_LIST = "PHOTO_TYPE_SYSTEM_LIST";
    //id
    private int id;
    //区分是进度条还是设置icon
    private String type;
    //file本地地址
    private String path;
    //上传到服务器的地址
    private String url;
    //是否选中
    private boolean isSelect;
    //进度
    private int progress;
    //压缩之后的路径
    private String compressPath;
    //图片是否顺坏
    private boolean isBad;
    //创建时间
    private long lastModified;
    //下标
    private long index;
    //区分是拍照还是相册里的照片
    private String photoType = "";

    @Override
    public String toString() {
        return "ImageP hoto{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", isSelect=" + isSelect +
                ", progress=" + progress +
                ", type=" + type +
                ", compressPath=" + compressPath +
                ", isBad=" + isBad +
                ", lastModified=" + lastModified +
                ", index=" + index +
                ", photoType=" + photoType +
                '}';
    }

    public String getPhotoType() {
        return photoType;
    }

    public ImagePhoto setPhotoType(String photoType) {
        this.photoType = photoType;
        return this;
    }

    public long getIndex() {
        return index;
    }

    public ImagePhoto setIndex(long index) {
        this.index = index;
        return this;
    }

    public long getLastModified() {
        return lastModified;
    }

    public ImagePhoto setLastModified(long lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public boolean isBad() {
        return isBad;
    }

    public ImagePhoto setBad(boolean bad) {
        isBad = bad;
        return this;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public ImagePhoto setCompressPath(String compressPath) {
        this.compressPath = compressPath;
        return this;
    }

    public String getType() {
        return type;
    }

    public ImagePhoto setType(String type) {
        this.type = type;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public ImagePhoto setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public ImagePhoto() {
    }

    public String getUrl() {
        return url;
    }

    public ImagePhoto setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getId() {
        return id;
    }

    public ImagePhoto setId(int id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ImagePhoto setPath(String path) {
        this.path = path;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public ImagePhoto setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        boolean bres = false;
        if (obj instanceof ImagePhoto) {
            ImagePhoto o = (ImagePhoto) obj;
            bres = (this.id == o.id) & (this.getPath().equals(o.getPath())) & (this.isSelect() == o.isSelect());
        }
        return bres;
    }
}

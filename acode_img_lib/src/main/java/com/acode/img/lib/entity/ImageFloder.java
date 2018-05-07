package com.acode.img.lib.entity;

/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:照片的上级目录
 */

public class ImageFloder {
    //文件夹下的图片个数
    private int count;
    //第一张图片的路径 传这个给小相册图片显示
    private String firstImagePath;
    //文件夹路径
    private String dir;
    //文件夹的名字
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ImageFloder{" +
                "count=" + count +
                ", firstImagePath='" + firstImagePath + '\'' +
                ", dir='" + dir + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

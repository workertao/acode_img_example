package com.acode.img.lib.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * user:yangtao
 * date:2018/4/281330
 * email:yangtao@bjxmail.com
 * introduce:本地文件管理工具类
 */
public class FileUtils {
    public static final String FILE_COMPRESS_DIR = "bjx_compress";

    public static final String FILE_CAMERA_DIR = "bjx_camera";

    public static final String FILE_ALL_PHOTOS_NAME = "bjx_all_photos";

    //获取系统相册的路径
    public static File getCameraFilePath() {
        return getFile(Environment.getExternalStorageDirectory(), FILE_CAMERA_DIR);
    }

    //压缩图片的文件路径
    public static File getCompressFilePath() {
        return getFile(Environment.getExternalStorageDirectory(), FILE_COMPRESS_DIR);
    }

    //拼接文件路径
    public static File getFile(File dir, String path) {
        return new File(dir.getAbsolutePath() + File.separator + path);
    }

    //获取压缩图片的名字
    public static String getCompressFileName() {
        return "bjx_compress_" + System.currentTimeMillis() + "_" + getNum();
    }

    //拍照保存的名字
    public static String getCameraFileNmae() {
        return "bjx_camera_" + System.currentTimeMillis() + "_" + getNum() + ".jpg";
    }

    //判断文件夹是否存在，如果不存在就创建，否则不创建
    public static File mkdirsFile(File file) {
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    //转换二进制流
    public static byte[] readStream(String imagepath) {
        try {
            FileInputStream fs = new FileInputStream(imagepath);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = fs.read(buffer))) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            fs.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //随机数
    private static int getNum() {
        //压缩之后保存的路径
        Random rand = new Random();
        int num = rand.nextInt(50) * rand.nextInt(100);
        return num;
    }

    /**
     * 将数据存到文件中
     *
     * @param context  context
     * @param data     需要保存的数据
     * @param fileName 文件名
     */
    public static void saveDataToFile(Context context, String data, String fileName) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从文件中读取数据
     *
     * @param context  context
     * @param fileName 文件名
     * @return 从文件中读取的数据
     */
    public static String loadDataFromFile(Context context, String fileName) {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            /**
             * 注意这里的fileName不要用绝对路径，只需要文件名就可以了，系统会自动到data目录下去加载这个文件
             */
            fileInputStream = context.openFileInput(fileName);
            bufferedReader = new BufferedReader(
                    new InputStreamReader(fileInputStream));
            String result = "";
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}

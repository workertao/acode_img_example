package com.acode.img.lib.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * user:yangtao
 * date:2018/4/411625
 * email:yangtao@bjxmail.com
 * introduce:相机相册权限
 */
public class PermissionUtils {

    public String[] request_permission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public int requestPermissionCount = 1;

    private Activity mContext;

    public PermissionUtils(Activity mContext) {
        this.mContext = mContext;
    }

    /**
     * 是否可继续执行
     *
     * @param requestCode
     * @param permission
     * @return false不能 true可以
     */
    @SuppressLint("NewApi")
    public boolean requestPermission(int requestCode, String[] permission) {
        //版本判断
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        //减少是否拥有权限
        List<String> permission_list = new ArrayList<>();
        List<String> permission_not_ask = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            int checkCallPhonePermission = ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), permission[i]);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                permission_list.add(permission[i]);
                boolean isShouldShow = mContext.shouldShowRequestPermissionRationale(permission[i]);
                if (requestCode > 0 && !isShouldShow) {
                    permission_not_ask.add(permission[i]);
                }
            }
        }
        if (requestPermissionCount == 1) {
            requestPermissionCount++;
            if (permission_list == null || permission_list.size() == 0) {
                return true;
            }
            //弹出对话框接收权限
            String[] requestList = permission_list.toArray(new String[permission_list.size()]);
            if (requestList.length > 0) {
                ActivityCompat.requestPermissions(mContext, requestList, requestCode);
                return false;
            }

        } else {
            requestPermissionCount++;
            if (permission_not_ask.size() > 0) {//存在不再提示的权限
                Log.d("post", "permission_not_ask:" + permission_not_ask.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("某些权限被永久禁止了，请在设置->应用权限管理中恢复授权")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create().show();
                return false;
            } else {
                String[] requestList = permission_list.toArray(new String[permission_list.size()]);
                if (requestList.length > 0) {
                    ActivityCompat.requestPermissions(mContext, requestList, requestCode);
                    return false;
                }
            }
        }
        return true;
    }
}

package com.acode.img.lib.photo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acode.img.lib.R;
import com.acode.img.lib.base.AcodeBaseActivity;
import com.acode.img.lib.utils.SpaceItemDecoration;
import com.acode.img.lib.data.AcodeCameraConfig;
import com.acode.img.lib.entity.ImageFloder;
import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.utils.runable.CompressRunable;
import com.acode.img.lib.utils.CompressUtils;
import com.acode.img.lib.utils.FileUtils;
import com.acode.img.lib.utils.runable.ReadPhotoRunable;
import com.acode.img.lib.viewpager.weigt.photo.AcodePhotoVpActivity;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:照片列表页面
 */

public class AcodePhotoListActivity extends AcodeBaseActivity
        implements View.OnClickListener,
        PopWindowAlbum.AlbunClickListener,
        AcodeRvPhotoListAdapter.OnPhotoClickListener {
    //相册图片列表
    private RecyclerView rv_photos_list;
    //相册图片列表适配器
    private AcodeRvPhotoListAdapter acodeRvPhotoListAdapter;
    //相册集合
    private List<ImageFloder> mImageFloders;
    //加载中
    private TextView tv_loading;
    //popwindow
    private PopWindowAlbum popWindowAlbum;
    //相册名称
    private TextView tv_album_name;
    //相册名称
    private LinearLayout ll_album_name;
    //返回
    private TextView tv_cancel;
    //当前相册路径
    private File currentAlbumDir;
    //当前相册
    private ImageFloder currentImageFloder;
    //确定
    private TextView tv_confirm;
    //已经选中的图片集合
    private ArrayList<ImagePhoto> selectPhotoData = new ArrayList<>();
    //选中的拍照数量
    private int cameraSize;

    private View view_space_line;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public int initSystemBarColor() {
        return R.color.acode_theme_style;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("post","onCreate");
        setContentView(R.layout.activity_photo_list);
        initView();
        initAlbumAndPhoto();
    }

    private void initView() {
        rv_photos_list = (RecyclerView) findViewById(R.id.rv_photos_list);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        tv_album_name = (TextView) findViewById(R.id.tv_album_name);
        ll_album_name = (LinearLayout) findViewById(R.id.ll_album_name);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        view_space_line = findViewById(R.id.view_space_line);

        ll_album_name.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        selectPhotoData = (ArrayList<ImagePhoto>) getIntent().getSerializableExtra("selectPhotoData");
        cameraSize = getIntent().getIntExtra("cameraSize", 0);
        updateSelectPhotoDataUI();
        initReclyView();
    }

    private void initReclyView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        acodeRvPhotoListAdapter = new AcodeRvPhotoListAdapter(this);
        rv_photos_list.setLayoutManager(gridLayoutManager);
        rv_photos_list.addItemDecoration(new SpaceItemDecoration(this, 4));
        rv_photos_list.setAdapter(acodeRvPhotoListAdapter);
    }

    //获取图片的路径和父路径 及 图片size
    private void initAlbumAndPhoto() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "检测到没有内存卡", Toast.LENGTH_LONG).show();
            return;
        }
        showProgress();
        new Thread(new ReadPhotoRunable(AcodePhotoListActivity.this, new ReadPhotoRunable.OnReadPhotoListener() {
            @Override
            public void onReadComplete(ArrayList<ImageFloder> imageFloders, ArrayList<ImagePhoto> imagePhotos) {
                //初始化全部照片
                mImageFloders = imageFloders;
                mImageFloders.get(0).setFirstImagePath(imagePhotos.get(0).getPath());
                currentImageFloder = imageFloders.get(0);
                currentAlbumDir = new File(imageFloders.get(0).getDir());
                //更新UI
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onReadError() {
                //更新UI
                mHandler.sendEmptyMessage(2);
            }

        })).start();
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (acodeRvPhotoListAdapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 3 : metrics.widthPixels / 5;
            acodeRvPhotoListAdapter.setLayoutParams(size);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissProgress();
            switch (msg.what) {
                case 1:
                    initPhotoList(); //初始化相册
                    initPopWindowAlbum();//初始化相册列表的窗口
                    break;
                case 2:
                    tv_loading.setVisibility(View.VISIBLE);
                    rv_photos_list.setVisibility(View.GONE);
                    break;
            }
        }
    };

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

    //初始化相册popwindow
    private void initPopWindowAlbum() {
        if (popWindowAlbum == null) {
            popWindowAlbum = new PopWindowAlbum(AcodePhotoListActivity.this, mImageFloders);
            popWindowAlbum.setAlbunClickListener(this);
        }
    }

    //首次加载初始化数据，全部照片
    private void initPhotoList() {
        String photoData = FileUtils.loadDataFromFile(AcodePhotoListActivity.this, FileUtils.FILE_ALL_PHOTOS_NAME);
        ArrayList<ImagePhoto> currentPhotosData = (ArrayList<ImagePhoto>) JSON.parseArray(photoData, ImagePhoto.class);
        tv_album_name.setText(currentImageFloder.getName() + "(" + currentImageFloder.getCount() + ")");
        if (currentPhotosData == null || currentPhotosData.size() == 0) {
            Toast.makeText(AcodePhotoListActivity.this, "没有查询到图片", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < currentPhotosData.size(); i++) {
            for (int j = 0; j < selectPhotoData.size(); j++) {
                if (currentPhotosData.get(i).getPath().equals(selectPhotoData.get(j).getPath())) {
                    currentPhotosData.get(i).setSelect(true);
                }
            }
        }
        updateUi(false, currentPhotosData);
    }

    //点击相册更新数据
    private void updatePhotoList() {
        tv_album_name.setText(currentImageFloder.getName() + "(" + currentImageFloder.getCount() + ")");
        try {
            if (currentAlbumDir == null) {
                Toast.makeText(this, "没有查询到图片", Toast.LENGTH_LONG).show();
                return;
            }
            //获取文件夹下的图片集合
            File[] files = currentAlbumDir.listFiles(getFileterImage());
            //==================相册图片实体======================
            if (files == null || files.length == 0) {
                return;
            }
            ArrayList<ImagePhoto> currentPhotosData = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                ImagePhoto imagePhoto = new ImagePhoto();
                imagePhoto.setId(i);
                imagePhoto.setPath(files[i].getPath());
                imagePhoto.setLastModified(files[i].lastModified());
                imagePhoto.setSelect(false);
                imagePhoto.setPhotoType(ImagePhoto.PHOTO_TYPE_SYSTEM_LIST);
                for (int j = 0; j < selectPhotoData.size(); j++) {
                    if (files[i].getPath().equals(selectPhotoData.get(j).getPath())) {
                        imagePhoto.setSelect(true);
                    }
                }
                currentPhotosData.add(imagePhoto);
            }
            updateUi(true, currentPhotosData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新界面UI
    private void updateUi(boolean isSort, ArrayList<ImagePhoto> currentPhotosData) {
        //排序
        if (isSort) {
            Collections.sort(currentPhotosData, new Comparator<ImagePhoto>() {
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
        }
        tv_loading.setVisibility(View.GONE);
        rv_photos_list.setVisibility(View.VISIBLE);
        acodeRvPhotoListAdapter = new AcodeRvPhotoListAdapter(this);
        acodeRvPhotoListAdapter.setData(currentPhotosData);
        rv_photos_list.setAdapter(acodeRvPhotoListAdapter);
        acodeRvPhotoListAdapter.setOnPhotoClickListener(this);
        orientationBasedUI(1);
    }

    //选中事件
    @Override
    public void onSelectClick(int position, ImagePhoto imagePhoto, ArrayList<ImagePhoto> imagePhotos) {
        //当选中的图片小于指定数量的时候  或者  选中的图片集合包含当前图片
        if (getSize() < AcodeCameraConfig.MAX_SIZE || selectPhotoData.contains(imagePhoto)) {
            //设置选中图片对象为选中状态
            imagePhoto.setSelect(!imagePhoto.isSelect());
            //更新选中图片对象数据
            imagePhotos.set(position, imagePhoto);
            setSelectPhotoData(imagePhoto);
            updateSelectPhotoDataUI();
            acodeRvPhotoListAdapter.notifyItemChanged(position, AcodePhotoListActivity.class.getSimpleName());
            return;
        }
        Toast.makeText(this, "最多选" + AcodeCameraConfig.MAX_SIZE + "张", Toast.LENGTH_SHORT).show();
    }

    //当前选中图片的总数量
    private int getSize() {
        return selectPhotoData.size() + cameraSize;
    }

    //修改界面ui
    private void updateSelectPhotoDataUI() {
        if (getSize() != 0) {
            tv_confirm.setText("确定(" + getSize() + "/" + AcodeCameraConfig.MAX_SIZE + ")");
        } else {
            tv_confirm.setText("确定");
        }
    }

    //将选中的图片返回到指定集合中
    private void setSelectPhotoData(ImagePhoto imagePhoto) {
        if (imagePhoto.isSelect()) {
            imagePhoto.setIndex(System.currentTimeMillis());
            selectPhotoData.add(imagePhoto);
            return;
        }
        selectPhotoData.remove(getIndex(imagePhoto));
    }

    //获取被选中的实体下标
    private int getIndex(ImagePhoto imagePhoto) {
        for (int i = 0; i < selectPhotoData.size(); i++) {
            if (selectPhotoData.get(i).getPath().equals(imagePhoto.getPath())) {
                return i;
            }
        }
        return 0;
    }

    //跳转事件
    @Override
    public void onIntentClick(int position, ImagePhoto imagePhoto, ArrayList<ImagePhoto> imagePhotos) {
        Intent intent = new Intent(AcodePhotoListActivity.this, AcodePhotoVpActivity.class);
        intent.putExtra("imagePhotos", imagePhotos);
        intent.putExtra("selectPhotoData", selectPhotoData);
        intent.putExtra("position", position);
        intent.putExtra("cameraSize", cameraSize);
        startActivityForResult(intent, AcodeCameraConfig.GOTO_VP_REQUEST);
    }

    //相册列表点击事件
    @Override
    public void onAlbumClick(ImageFloder imageFloder) {
        currentImageFloder = imageFloder;
        currentAlbumDir = new File(currentImageFloder.getDir());
        //如果点击的是全部照片
        if (imageFloder.getName().equals("所有照片")) {
            initPhotoList();
            return;
        }
        updatePhotoList();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_album_name) {
            if (popWindowAlbum == null || mImageFloders == null) {
                initAlbumAndPhoto();
                return;
            }
            popWindowAlbum.show(view_space_line);
            return;
        }
        if (v.getId() == R.id.tv_confirm) {
            if (selectPhotoData == null || selectPhotoData.size() == 0) {
                finish();
                return;
            }
            showProgress();
            CompressUtils.compressAll(selectPhotoData, new CompressRunable.AllCompressListener() {
                @Override
                public void onAllCompressComplete(ArrayList<ImagePhoto> imagePhotos) {
                    AcodePhotoListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("selectPhotoData", selectPhotoData);
                            intent.putExtra("bundle", bundle);
                            setResult(AcodeCameraConfig.SELECT_VP_RESPONSE, intent);
                            finish();
                        }
                    });
                }
            });
            return;
        }
        if (v.getId() == R.id.tv_cancel) {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case AcodeCameraConfig.GOTO_VP_REQUEST:
                ArrayList<ImagePhoto> imagePhotos = (ArrayList<ImagePhoto>) data.getSerializableExtra("imagePhotos");
                ArrayList<ImagePhoto> selectPhotoData = (ArrayList<ImagePhoto>) data.getSerializableExtra("selectPhotoData");
                this.selectPhotoData.clear();
                this.selectPhotoData = selectPhotoData;
                updateSelectPhotoDataUI();
                acodeRvPhotoListAdapter.setData(imagePhotos);
                acodeRvPhotoListAdapter.notifyDataSetChanged();
                break;
        }
    }
}

package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acode.img.lib.entity.ImagePhoto;
import com.acode.img.lib.helper.AcodeImgLibHelper;
import com.acode.img.lib.helper.AcodeImgLibListener;
import com.acode.img.lib.helper.IAcoderImgLibHelper;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AcodeImgLibListener {
    private RecyclerView rv;
    private IAcoderImgLibHelper acodeImgLibHelper;
    private Button btn_camera;
    private Button btn_album;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        acodeImgLibHelper = new AcodeImgLibHelper(this, this);
        myAdapter = new MyAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(myAdapter);
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_album = (Button) findViewById(R.id.btn_album);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acodeImgLibHelper.takePhoto();
            }
        });
        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acodeImgLibHelper.getPhotoList();
            }
        });
    }

    @Override
    public void getImagePhotos(ArrayList<ImagePhoto> imagePhotos) {
        myAdapter.setData(imagePhotos);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        acodeImgLibHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        acodeImgLibHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public class MyAdapter extends RecyclerView.Adapter {
        private ArrayList<ImagePhoto> imagePhotos;

        public void setData(ArrayList<ImagePhoto> imagePhotos) {
            this.imagePhotos = imagePhotos;
        }

        public ArrayList<ImagePhoto> getData() {
            return this.imagePhotos;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ImagePhoto imagePhoto = imagePhotos.get(position);
            if (holder instanceof MyViewHolder) {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                Glide.with(MainActivity.this)
                        .load(imagePhoto.getPath())
                        .into(myViewHolder.iv_photo);
                myViewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acodeImgLibHelper.remove(position);
                    }
                });
                File file = new File(imagePhoto.getCompressPath());
                File file1 = new File(imagePhoto.getPath());
                myViewHolder.tv_photo_info.setText("原图：" + (file1.length() / 1024) + "k\n现图：" + (file.length() / 1024) + "K");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acodeImgLibHelper.showBigPhoto(imagePhotos, imagePhotos, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return imagePhotos != null ? imagePhotos.size() : 0;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView iv_photo;
            ImageView iv_delete;
            TextView tv_photo_info;

            public MyViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter, parent, false));
                iv_photo = itemView.findViewById(R.id.iv_photo);
                iv_delete = itemView.findViewById(R.id.iv_delete);
                tv_photo_info = itemView.findViewById(R.id.tv_photo_info);
            }
        }
    }
}

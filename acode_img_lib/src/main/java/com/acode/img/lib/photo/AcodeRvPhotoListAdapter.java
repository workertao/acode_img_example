package com.acode.img.lib.photo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.acode.img.lib.R;
import com.acode.img.lib.utils.DimenUtils;
import com.acode.img.lib.utils.DisplayUtils;
import com.acode.img.lib.entity.ImagePhoto;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * user:yangtao
 * date:2018/3/221848
 * email:yangtao@bjxmail.com
 * introduce:图片展示适配器
 */
public class AcodeRvPhotoListAdapter extends RecyclerView.Adapter {
    private ArrayList<ImagePhoto> imagePhotos;
    private Context context;
    protected int size;
    private int imgHeight;

    public void setLayoutParams(int size) {
        this.size = size;
    }

    public AcodeRvPhotoListAdapter(Context context) {
        this.context = context;
        int lineSpace = DimenUtils.dip2px(context, 4 * 2);//横向间距
        imgHeight = (DisplayUtils.getDisplayWidth(context) - lineSpace) / 3;
    }

    public void setData(ArrayList<ImagePhoto> imagePhotos) {
        this.imagePhotos = imagePhotos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_photo_list_item, parent, false);
        AcodeRvPhotoListHolder holder = new AcodeRvPhotoListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (payloads.isEmpty()) {
            //整体刷新
            onBindViewHolder(holder, position);
            return;
        }
        final ImagePhoto imagePhoto = imagePhotos.get(position);
        if (holder instanceof AcodeRvPhotoListHolder) {
            AcodeRvPhotoListHolder acodeRvPhotoListHolder = (AcodeRvPhotoListHolder) holder;
            //默认设置未选中，没有图层
            acodeRvPhotoListHolder.view_top.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            acodeRvPhotoListHolder.img_photo_check.setImageResource(R.mipmap.icon_select);
            if (imagePhoto.isSelect()) {
                //设置选中按钮，展示图层
                acodeRvPhotoListHolder.img_photo_check.setImageResource(R.mipmap.icon_selected);
                acodeRvPhotoListHolder.view_top.setBackgroundColor(Color.parseColor("#b0000000"));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ImagePhoto imagePhoto = imagePhotos.get(position);
        if (holder instanceof AcodeRvPhotoListHolder) {
            AcodeRvPhotoListHolder acodeRvPhotoListHolder = (AcodeRvPhotoListHolder) holder;
            //默认设置未选中，没有图层
            acodeRvPhotoListHolder.view_top.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            acodeRvPhotoListHolder.img_photo_check.setImageResource(R.mipmap.icon_select);
            if (imagePhoto.isSelect()) {
                //设置选中按钮，展示图层
                acodeRvPhotoListHolder.img_photo_check.setImageResource(R.mipmap.icon_selected);
                acodeRvPhotoListHolder.view_top.setBackgroundColor(Color.parseColor("#b0000000"));
            }
            acodeRvPhotoListHolder.img_photo.getLayoutParams().width = size;
            acodeRvPhotoListHolder.img_photo.getLayoutParams().height = size;
            acodeRvPhotoListHolder.view_top.getLayoutParams().width = size;
            acodeRvPhotoListHolder.view_top.getLayoutParams().height = size;
            //设置宽高
            gvMathParams(acodeRvPhotoListHolder.rl_photo_root);
            //加载图片
            String tag = (String) acodeRvPhotoListHolder.img_photo.getTag(R.string.tag_key);
            if (!imagePhoto.getPath().equals(tag)) {
                acodeRvPhotoListHolder.img_photo.setTag(R.string.tag_key, imagePhoto.getPath());
                RequestOptions options = new RequestOptions()
                        .placeholder(R.mipmap.ic_default_album)
                        .error(R.mipmap.ic_default_album)
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                //设置图片
                Glide.with(context)
                        .load(imagePhoto.getPath())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imagePhotos.get(position).setBad(true);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                imagePhotos.get(position).setBad(false);
                                return false;
                            }
                        })
                        .apply(options)
                        .into(acodeRvPhotoListHolder.img_photo);
                //设置点击事件
                acodeRvPhotoListHolder.img_photo_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePhoto.isBad()) {
                            Toast.makeText(context, "图片顺坏", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (onPhotoClickListener != null) {
                            onPhotoClickListener.onSelectClick(position, imagePhoto, imagePhotos);
                        }
                    }
                });
                //跳转预览
                acodeRvPhotoListHolder.view_top.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onPhotoClickListener != null) {
                            onPhotoClickListener.onIntentClick(position, imagePhoto, imagePhotos);
                        }
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return imagePhotos != null ? imagePhotos.size() : 0;
    }

    public class AcodeRvPhotoListHolder extends RecyclerView.ViewHolder {
        ImageView img_photo;
        ImageView img_photo_check;
        View view_top;
        RelativeLayout rl_photo_root;

        public AcodeRvPhotoListHolder(View itemView) {
            super(itemView);
            img_photo = (ImageView) itemView.findViewById(R.id.img_photo);
            img_photo_check = (ImageView) itemView.findViewById(R.id.img_photo_check);
            view_top = itemView.findViewById(R.id.view_top);
            rl_photo_root = (RelativeLayout) itemView.findViewById(R.id.rl_photo_root);
        }
    }

    private OnPhotoClickListener onPhotoClickListener;


    public AcodeRvPhotoListAdapter setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
        return this;
    }

    public interface OnPhotoClickListener {
        void onSelectClick(int position, ImagePhoto imagePhoto, ArrayList<ImagePhoto> imagePhotos);

        void onIntentClick(int position, ImagePhoto imagePhoto, ArrayList<ImagePhoto> imagePhotos);
    }

    private void gvMathParams(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = imgHeight;
        view.setLayoutParams(params);
    }
}

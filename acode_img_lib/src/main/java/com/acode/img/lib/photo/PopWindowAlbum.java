package com.acode.img.lib.photo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.acode.img.lib.R;
import com.acode.img.lib.entity.ImageFloder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:相册弹窗
 */

public class PopWindowAlbum {
    private PopupWindow popupWindow;

    private Context context;

    private List<ImageFloder> imageFloders;

    private PopAlbumListAdapter popAlbumListAdapter;

    private AlbunClickListener albunClickListener;

    public PopWindowAlbum setAlbunClickListener(AlbunClickListener albunClickListener) {
        this.albunClickListener = albunClickListener;
        return this;
    }

    public interface AlbunClickListener {
        void onAlbumClick(ImageFloder imageFloder);
    }


    public PopWindowAlbum(Context context, List<ImageFloder> imageFloders) {
        this.context = context;
        this.imageFloders = imageFloders;
        if (imageFloders == null && imageFloders.size() == 0) {
            return;
        }
        initPopupWindow();
    }

    /**
     * 初始化机构列表的pop分类实例
     */
    public void initPopupWindow() {
        View contentView = View.inflate(context, R.layout.pop_album_list, null);// 动态加载
        ListView lv_album_list = (ListView) contentView.findViewById(R.id.lv_album_list);
        popAlbumListAdapter = new PopAlbumListAdapter();
        lv_album_list.setAdapter(popAlbumListAdapter);
        // 全屏显示，将内容设置在底部
        popupWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT) {
            @Override
            public void showAsDropDown(View anchor) {
                if (Build.VERSION.SDK_INT >= 24) {
                    Rect rect = new Rect();
                    anchor.getGlobalVisibleRect(rect);
                    int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                    setHeight(h);
                }
                super.showAsDropDown(anchor);
            }
        };
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        lv_album_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if (popAlbumListAdapter == null) {
                    return;
                }
                popAlbumListAdapter.setSelectIndex(position);
                popAlbumListAdapter.notifyDataSetChanged();
                ImageFloder imageFloder = (ImageFloder) popAlbumListAdapter.getItem(position);
                if (albunClickListener != null) {
                    albunClickListener.onAlbumClick(imageFloder);
                }
            }
        });
    }

    public class PopAlbumListAdapter extends BaseAdapter {
        private int selectIndex = -1;

        @Override
        public int getCount() {
            return imageFloders == null ? 0 : imageFloders.size();
        }

        @Override
        public Object getItem(int position) {
            return imageFloders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_pop_album_list_item, null);
                holder.im_album_icon = (ImageView) convertView.findViewById(R.id.im_album_icon);
                holder.tv_album_name = (TextView) convertView.findViewById(R.id.tv_album_name);
                holder.tv_album_num = (TextView) convertView.findViewById(R.id.tv_album_num);
                holder.im_album_select = (ImageView) convertView.findViewById(R.id.im_album_select);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageFloder imageFloder = imageFloders.get(position);
            holder.tv_album_name.setText(imageFloder.getName());
            holder.tv_album_num.setText(String.valueOf(imageFloder.getCount()));
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.ic_default_album)
                    .error(R.mipmap.ic_default_album)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            //设置图片
            Glide.with(context)
                    .load(imageFloder.getFirstImagePath())
                    .apply(options)
                    .into(holder.im_album_icon);
            if (selectIndex == -1 && imageFloder.getName().toUpperCase().equals("camera".toUpperCase())) {
                holder.im_album_select.setImageResource(R.mipmap.ic_gfq_duihao);
            } else if (selectIndex == position) {
                holder.im_album_select.setImageResource(R.mipmap.ic_gfq_duihao);
            } else {
                holder.im_album_select.setImageResource(0);
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView im_album_icon;
            TextView tv_album_name;
            TextView tv_album_num;
            ImageView im_album_select;
        }

        private void setSelectIndex(int selectIndex) {
            this.selectIndex = selectIndex;
        }
    }

    public void show(View view) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            Log.d("post", "view_height:" + view.getHeight());
//            popupWindow.showAtLocation(view, Gravity.BOTTOM,0,view.getHeight());
//            popupWindow.showAsDropDown(view,0,0,Gravity.BOTTOM);
            popupWindow.showAsDropDown(view);
        }
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}

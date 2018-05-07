package com.acode.img.lib.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * user:yangtao
 * date:2018/3/231006
 * email:yangtao@bjxmail.com
 * introduce:AcodeBaseAdapter
 */
public abstract class AcodeBaseAdapter<T> extends BaseAdapter {
    protected ArrayList<T> data;
    protected Context context;
    protected LayoutInflater layoutInflater;

    protected int size;

    public AcodeBaseAdapter(Context context, ArrayList<T> arrayList) {
        this.data = arrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setLayoutParams(int size) {
        this.size = size;
    }

    public void releaseResources() {
        data = null;
        context = null;
    }

    public void setData(ArrayList<T> arrayList) {
        this.data = arrayList;
    }
}

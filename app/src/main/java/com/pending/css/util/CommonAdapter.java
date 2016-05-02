package com.pending.css.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/4/22.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context mContext;//上下文对象
    protected List<T> mDatas;//Item显示数据
    protected LayoutInflater mInflater;//定义用来获取布局
    private int layoutId;//布局文件
    public CommonAdapter(Context context, List<T> datas, int layoutId)
    {
        this.mContext = context;
        this.mDatas = datas;
        this.layoutId = layoutId;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //抽象方法
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //初始化ViewHolder类
        ViewHolder holder = ViewHolder.get(mContext,convertView, parent, layoutId,position);

        //
        convert(holder,getItem(position));
        //返回View
        return holder.getConvertView();
    }
    public abstract void convert(ViewHolder holder,T t);


}

package com.pending.css.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ViewHolder {
    private SparseArray<View> mView;
    private int mPosition;
    private View mConvertView;

    public int getmPosition() {
        return mPosition;
    }

    public ViewHolder(Context context, ViewGroup parent,
                      int layoutId, int position) {
        this.mPosition = position;
        this.mView = new SparseArray<View>();//初始化
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    /**
     * 入口方法
     *
     * @auther css
     * created at 2016/4/22 7:47
     */
    public static ViewHolder get(Context context, View conventView, ViewGroup parent,
                                 int layoutId, int position)
    {
        if (conventView == null)
        {
            return new ViewHolder(context,parent,layoutId,position);
        }
        else
        {
            ViewHolder holder = (ViewHolder) conventView.getTag();
            holder.mPosition = position;
            return holder;
        }

    }

    //获取View
    public View getConvertView()
    {
        return mConvertView;
    }

    /**
     *通过viewId获取控件
     *@auther css
     *created at 2016/4/22 8:00
     */
    public <T extends View>T getView(int viewId)
    {
        //从SparseArray中取出View
        View view = mView.get(viewId);
        //如果为空，则重新实例化控件
        if (view == null)
        {
            //mConvertView父布局，viewId是控件的id
            view = mConvertView.findViewById(viewId);
            //把控件以键值对的形式存到SparseArray中，(控件id,控件实例);
            mView.put(viewId,view);
        }
        return (T)view;
    }

    //为TextView设置text方法
    public ViewHolder setText(int viewId, String text)
    {
        TextView tv  = getView(viewId);
        tv.setText(text);
        return this;
    }

    //为ImageView设置图片方法
    public ViewHolder setIcon(int viewId, int iconId)
    {
        ImageView iv  =  getView(viewId);
        iv.setImageResource(iconId);
        return this;
    }

    public ViewHolder setButtonDrawable(int viewId,int bitmap)
    {
        CheckBox cb = getView(viewId);
        cb.setButtonDrawable(bitmap);
        return this;
    }

    //为TextView设置text方法
    public ViewHolder setText(int viewId, String text,int color)
    {
        TextView tv  = getView(viewId);
        tv.setText(text);
        tv.setTextColor(color);
        return this;
    }

}

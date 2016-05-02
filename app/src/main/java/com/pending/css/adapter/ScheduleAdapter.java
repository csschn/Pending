package com.pending.css.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.pending.css.bean.Schedule;
import com.pending.css.login.R;
import com.pending.css.util.CommonAdapter;
import com.pending.css.util.ViewHolder;

import java.util.List;

import bean.dao.ScheduleDao;

/**
 * Created by Administrator on 2016/4/18.
 */
public class ScheduleAdapter extends CommonAdapter<Schedule> {

    public ScheduleAdapter(Context context,List<Schedule> data,int layoutId)
    {
        super(context, data, layoutId);
    }

    public void refreshAdapter()
    {
        notifyDataSetChanged();
    }

    /**
     *从数据集中删除数据
     *@auther css
     *created at 2016/4/23 11:10
     */
    public void remove(int position)
    {
        Log.d("content",position+mDatas.get(position).getContent());
        ScheduleDao.deleteScheduleData(mContext,mDatas.get(position));
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void convert(final ViewHolder holder, final Schedule schedule) {

        holder.setIcon(R.id.iv_loop_in_lv, R.mipmap.loop_picture);
        holder.setText(R.id.tv_show_time_in_lv, schedule.getRemind_time_lable().substring(12));
        holder.setText(R.id.tv_show_content_in_lv,schedule.getContent());
        if (schedule.getIs_current_year() == 0)
        {
            String date = schedule.getRemind_time_lable().substring(0,8);
            holder.setText(R.id.tv_show_date_in_lv,date, Color.parseColor("#419DE4"));
        }
        else
        {
            String date = schedule.getRemind_time_lable().substring(5,11);
            holder.setText(R.id.tv_show_date_in_lv,date);
        }
        final CheckBox cb = holder.getView(R.id.cb_is_finish_in_lv);
        switch (schedule.getPriority())//位Check设置优先级颜色显示
        {
            case 0:
                cb.setButtonDrawable(R.drawable.checkbox_for_addpending_0);
                break;
            case 1:
                cb.setButtonDrawable(R.drawable.checkbox_for_addpending_1);
                break;
            case 2:
                cb.setButtonDrawable(R.drawable.checkbox_for_addpending_2);
                break;
            case 3:
                cb.setButtonDrawable(R.drawable.checkbox_for_addpending_3);
                break;
        }
        cb.setChecked(schedule.getStatus());
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                T.showDefind(mContext, holder.getmPosition() + "");
                schedule.setStatus(cb.isChecked());
                if (schedule.getStatus() == true)
                {
                    ScheduleDao.doFinishSchedule(mContext,schedule,true);
                    mDatas.remove(holder.getmPosition());
                    notifyDataSetChanged();
                }
                else
                {
                    ScheduleDao.doFinishSchedule(mContext,schedule,false);
                    mDatas.remove(holder.getmPosition());
                    notifyDataSetChanged();
                }

            }
        });

    }


}

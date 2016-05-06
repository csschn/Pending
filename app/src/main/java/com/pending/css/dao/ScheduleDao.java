package com.pending.css.dao;

import android.content.Context;
import android.util.Log;

import com.pending.css.bean.Schedule;
import com.pending.css.util.T;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/4/21.
 */
public class ScheduleDao {



    public static void doFinishSchedule(final Context context, Schedule scheduleData,boolean status) {
        Schedule schedule = new Schedule();
        schedule = scheduleData;
        schedule.setStatus(status);
//        String sql = "update Schedule set status = true where obiectId = '"+objectId+"'";
        schedule.update(context, schedule.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.d("success", "success");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d("success", "onFailure");
                T.showDefind(context, "Sorry,操作未完成。");
            }
        });
    }
    public static void deleteScheduleData(final Context context, Schedule scheduleData) {
        Schedule schedule = new Schedule();
        schedule = scheduleData;
//        String sql = "update Schedule set status = true where obiectId = '"+objectId+"'";
        schedule.delete(context, schedule.getObjectId(), new DeleteListener() {
            @Override
            public void onSuccess() {
                T.showDefind(context,"删除成功！");
            }

            @Override
            public void onFailure(int i, String s) {
                T.showDefind(context,"删除失败！");
            }
        });
    }


    /**
     *格式化日期显示格式
     *@auther css
     *created at 2016/4/21 10:12
     */
    public static String timeFormatString(int value)
    {
        return value >= 10 ? value+"" : "0"+value;
    }

}

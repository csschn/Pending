package com.pending.css.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by css on 2016/3/19.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();//新建列表存储活动
    public static void addActivity(Activity activity)//新增活动到列表
    {
        activities.add(activity);
    }
    public static void removeActivity(Activity activity)//移除活动出列表
    {
        activities.remove(activity);
    }
    public static void finishAll()//移除所有活动
    {
        for(Activity activity : activities)
        {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
    }
}

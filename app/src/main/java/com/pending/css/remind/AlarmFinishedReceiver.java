package com.pending.css.remind;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.pending.css.bean.Schedule;
import com.pending.css.login.R;
import com.pending.css.util.ActivityCollector;
import com.pending.css.util.MyApplication;

/**
 * Created by Administrator on 2016/4/24.
 */
public class AlarmFinishedReceiver extends BroadcastReceiver {

    private NotificationManager manager;
    private int notificationId ;
    private String tickerShow;
    private String whenShow;
    private String titleShow;
    private String contentShow;
    private Intent intent1;
    private Schedule schedule;
    Context mContext = MyApplication.getContext();
    @Override
    public void onReceive(Context context, Intent intent) {
        schedule = (Schedule) intent.getSerializableExtra("schedule_data");
        tickerShow = "您有新的代办事项！";
        whenShow = schedule.getRemind_time_lable().substring(10);
        titleShow = "来自Pending的提醒";
        contentShow = schedule.getContent();
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationId = schedule.getRemind_time_id();
        sendNotification(context);
        ActivityCollector.finishAll();
    }

    private void sendNotification(Context context)
    {
        intent1 = new Intent(mContext,ShowRemindActivity.class);//启动Activity的Intent
        //一定要这么加入个标记，不然被打开的Activity获取不到数据
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("schedule_data", schedule);
        intent1.putExtra("source","notification");//标示来自于广播启动
        PendingIntent pintent = PendingIntent.getActivity(mContext,schedule.getRemind_time_id(),intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setSmallIcon(R.drawable.iconlarge);
        builder.setTicker(tickerShow);//手机状态栏的提示
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(titleShow);
        Bitmap LargeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.iconlarge);
        builder.setLargeIcon(LargeBitmap);
        builder.setContentText(contentShow);
        builder.setContentIntent(pintent);//点击之后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置上面的三种效果
        //Android4.1以上的版本用Notification notification = builder.build();
        //Android4.1以下的版本用builder.getNotification();
        Notification notification;
        if (Build.VERSION.SDK_INT < 4.1)
        {
            notification = builder.getNotification();
        }
        else
        {
            notification  = builder.build();
        }
        manager.notify(notificationId, notification);
    }
}

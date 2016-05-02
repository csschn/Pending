package com.pending.css.remind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pending.css.bean.Schedule;
import com.pending.css.bean.User;
import com.pending.css.config.Constants;
import com.pending.css.util.DateHelper;
import com.pending.css.util.MyApplication;
import com.pending.css.util.T;

import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by Administrator on 2016/4/24.
 */
public class InitAlarmService extends Service {
    private User user ;
    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        user = BmobUser.getCurrentUser(getApplicationContext(), User.class);
        //初始化Bmob sdk
        Bmob.initialize(this, Constants.BMOB_APP_KEY);
        final Calendar calendar = Calendar.getInstance();
        String remindTimeLableTemp = calendar.get(Calendar.YEAR)+"年"+
                DateHelper.timeFormatString(calendar.get(Calendar.MONTH) + 1)+"月"+
                DateHelper.timeFormatString(calendar.get(Calendar.DAY_OF_MONTH))+"日";
        final String sql = "select * from Schedule   where  remind_time_lable like'"+remindTimeLableTemp+"%'" +
                "                       and user_id = '"+user.getObjectId()+"'" +
                "                       and status = false";
//        String sql = "select * from Schedule   where  status = false";
        Log.d("seancss",sql+"onStartCommand");
        new BmobQuery<Schedule>().doSQLQuery(MyApplication.getContext(), sql, new SQLQueryListener<Schedule>() {
            @Override
            public void done(BmobQueryResult<Schedule> result, BmobException e) {
                if (e == null) {
                    List<Schedule> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Schedule schedule = new Schedule();
                            schedule = list.get(i);
                            Log.d("seancss",schedule.getRemind_time());
                            Log.d("seancss",schedule.getRemind_time_id()+"id");
                            Long time = Long.parseLong(schedule.getRemind_time());
                            Integer id = schedule.getRemind_time_id();
                            Intent intent1 = new Intent(MyApplication.getContext(),AlarmReceiver.class);
                            intent1.putExtra("schedule_data", list.get(i));
                            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (time > calendar.getTimeInMillis())//如果超出当前时间，则删除提醒，否则加提醒
                            {
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                        time,
                                        5 * 60 * 1000,
                                        pi);
                            }
                            else
                            {
                                alarmManager.cancel(pi);
                            }
                        }
                    } else {
                        Log.d("seancss", "无数据返回");
                    }
                } else {
                    Log.i("seancss", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("seancss", "onStartCommand");
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

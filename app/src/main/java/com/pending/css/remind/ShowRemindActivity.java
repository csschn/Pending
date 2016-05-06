package com.pending.css.remind;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pending.css.bean.Schedule;
import com.pending.css.login.R;
import com.pending.css.util.ActivityCollector;
import com.pending.css.util.BaseActivity;
import com.pending.css.util.MyApplication;

import com.pending.css.dao.ScheduleDao;

/**
 * Created by Administrator on 2016/4/24.
 */
public class ShowRemindActivity extends BaseActivity implements View.OnClickListener {
    private Button btnLater;
    private Button btnFinish;
    private TextView tvContent;
    private TextView tvDate;
    private TextView tvFolder;
    private NotificationManager manager;
    private AlarmManager alarmManager;
    private Intent intent2;
    private Schedule schedule;

    //判断用户是否处理了该提醒，0未处理，1处理
    public static int alarmIsDispose = 0;
    //控制当前Activity打开的时候，响铃服务有没有被关闭，0未关闭，1关闭
    public static int alarmIsClose = 0;
    private static int startFromNotification = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.dialog_content_foralarm);
        initView();
        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_later:
//                Log.d("qqq", code + "btn_later");
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard();
                alarmIsDispose = 1;
                if (alarmIsClose == 1 && startFromNotification ==1 )
                {
//                    onDestroy();
                }else if (alarmIsClose == 0 && startFromNotification == 0)
                {
                    stopService(intent2);
                }
                Toast.makeText(MyApplication.getContext(), "提醒将在5分钟后进行！！", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
            case R.id.btn_finish:
//                Log.d("qqq", code + "  alarmIsClose"+alarmIsClose);
//                Log.d("qqq", code + "  startFromNotification"+startFromNotification);
//                Log.d("qqq", code + "  alarmIsDispose"+alarmIsDispose);
                alarmIsDispose = 1;
                //Service关闭，且是由Notification启动的Activity
                if (alarmIsClose == 1 && startFromNotification ==1 )
                {

                }else if (alarmIsClose == 0 && startFromNotification == 0)
                {
                    stopService(intent2);
                }
                alarmManager.cancel(PendingIntent.getBroadcast(MyApplication.getContext(), schedule.getRemind_time_id(), new Intent(MyApplication.getContext(), AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
                ScheduleDao.doFinishSchedule(ShowRemindActivity.this, schedule, true);
                manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(schedule.getRemind_time_id());
                Toast.makeText(MyApplication.getContext(), "提醒已经关闭！", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
        }
    }

    public void initView()
    {
        btnLater = (Button) findViewById(R.id.btn_later);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvFolder = (TextView) findViewById(R.id.tv_folder);
        btnLater.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        tvContent.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvFolder.setOnClickListener(this);
        alarmManager = (AlarmManager) ShowRemindActivity.this.getSystemService(Context.ALARM_SERVICE);
        Log.d("initData", "initView");

    }
    private void initData()
    {
        Log.d("initData","initData");
        Intent intent = getIntent();
        schedule = (Schedule) intent.getSerializableExtra("schedule_data");
        tvContent.setText(schedule.getContent());
        tvDate.setText(schedule.getRemind_time_lable());
        tvFolder.setText(schedule.getSchedule_folder_name() );
        //如果是通知栏打开的当前Activity，则直接返回。否则打开通知铃声
        if (intent.getStringExtra("source").equals("alarmReceive"))
        {
            intent2 = new Intent(ShowRemindActivity.this,PlaySoundService.class);
            intent2.putExtra("schedule_data",schedule);
//            intent2.putExtra("conn", conn.toString());
//            bindService(intent2, conn, Service.BIND_AUTO_CREATE);
            startService(intent2);
        }
        else
        {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(schedule.getRemind_time_id());
            startFromNotification = 1;
        }
    }
}

package com.pending.css.remind;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.pending.css.bean.Schedule;
import com.pending.css.util.MyApplication;
import com.pending.css.util.T;

/**
 * Created by Administrator on 2016/4/24.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private KeyguardManager  km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    @Override
    public void onReceive(Context context, Intent intent) {
        Schedule schedule = (Schedule) intent.getSerializableExtra("schedule_data");
        Log.d("qqq", "onreceive" + schedule.getContent());
        wakeAndUnlock(true);
        Intent intent1 = new Intent(context,ShowRemindActivity.class);
        intent1.putExtra("schedule_data",schedule);
        intent1.putExtra("source","alarmReceive");//标示来自于广播启动
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent1);
    }

    private void wakeAndUnlock(boolean b)
    {
        if(b)
        {
            //获取电源管理器对象
            pm=(PowerManager) MyApplication.getContext().getSystemService(Context.POWER_SERVICE);

            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

            //点亮屏幕
            wl.acquire();

            //得到键盘锁管理器对象
            km= (KeyguardManager) MyApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");

            //解锁
            kl.disableKeyguard();
        }
        else
        {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
        }

    }
}

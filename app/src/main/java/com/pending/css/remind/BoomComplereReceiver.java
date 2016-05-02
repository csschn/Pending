package com.pending.css.remind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pending.css.util.MyApplication;

/**
 * Created by Administrator on 2016/4/24.
 */
public class BoomComplereReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"开机启动广播",Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(MyApplication.getContext(),InitAlarmService.class);
        context.startService(intent1);
    }
}

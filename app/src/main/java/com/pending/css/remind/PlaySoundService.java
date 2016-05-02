package com.pending.css.remind;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import com.pending.css.bean.Schedule;
import com.pending.css.login.R;
import com.pending.css.util.ActivityCollector;


/**
 * Created by Administrator on 2016/4/24.
 */
public class PlaySoundService extends Service implements MediaPlayer.OnCompletionListener{

    //震动
    private Vibrator vibrator=null;
    private MediaPlayer mediaPlayer;
    private Schedule schedule;

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }
    public void play()
    {
        vibrator.vibrate(new long[]{1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000}, -1);
        //如果不正在播放，则开始播放
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
//        vibrator.vibrate(new long[]{1000, 2000, 3000, 4000}, -1);
    }

    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.dingding_30s);
        mediaPlayer.setOnCompletionListener(this);

//        if(!mediaPlayer.isPlaying()){
//            mediaPlayer.start();
//        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseForStopPlay();
            }
        });
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        bundle = intent.getBundleExtra("bundle");
        schedule = (Schedule) intent.getSerializableExtra("schedule_data");
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        //开始震动
        vibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        return START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void  releaseForStopPlay()
    {
        if (mediaPlayer!=null)
        {
            mediaPlayer.release();
        }
        if (vibrator!=null)
        {
            vibrator.cancel();
        }
        Intent intent = new Intent("android.css.alarmplayer.finished");
        intent.putExtra("schedule_data",schedule);
        sendBroadcast(intent);
//            //发送广播用来和Activity解除绑定
//            Intent intent1 = new Intent("android.css.AlarmPlayerIntentService.finished");
//            sendBroadcast(intent1);
        ActivityCollector.finishAll();
        ShowRemindActivity.alarmIsClose = 1;
        stopSelf();
    }

    /**
     * 用来把资源释放掉并且发送广播实现发送一个Notification
     * 如果是用户没有对该提醒进行处理，那么就发送一个广播来让Activity和该
     * service解绑，服务连接泄露
     *@auther css
     *created at 2016/4/18 14:16
     */
    public void  release()
    {
        if (mediaPlayer!=null)
        {
            mediaPlayer.release();
        }
        if (vibrator!=null)
        {
            vibrator.cancel();
        }
    }
    @Override
    public void onDestroy() {
        release();
        super.onDestroy();
    }
}

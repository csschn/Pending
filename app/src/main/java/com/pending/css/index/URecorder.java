package com.pending.css.index;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Administrator on 2016/4/25.
 */
public class URecorder implements IVoiceManager{
    private final String TAG = URecorder.class.getName();
    private String path;
    private MediaRecorder mRecorder;
    public URecorder(String path){
        this.path = path;
        mRecorder = new MediaRecorder();
    }

    /*
     * 开始录音
     * @return boolean
     */
    @Override
    public boolean start() {
        //设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(path);
        //设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //30秒限制
        mRecorder.setMaxDuration(30*1000);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        //录音
        mRecorder.start();
//        if (mRecorder.)
        return false;
    }

    /*
     * 停止录音
     * @return boolean
     */
    @Override
    public boolean stop() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        return false;
    }
}

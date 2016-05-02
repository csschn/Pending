package com.pending.css.bean;

import java.io.Serializable;
import java.util.Calendar;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/4/18.
 */
public class Schedule extends BmobObject implements Serializable {
    private Integer schedule_id;//id
    private String schedule_folder_name;
    private Integer is_current_year;//代办事项是当前年份0，不是为1
    private String user_id;
    private Integer schedule_folder_id;//清单文件夹ID
    private String remind_time;//提醒时间，数据库存储为String，用的时候转为long
    private String content;//事项内容
    private Integer repeat;//如何重复，0不重复，1每天重复，2每周重复
    private Integer priority;//优先级，0无优先级，1低，2高，3高优先级

    public String getRemind_time() {
        return remind_time;
    }

    public void setRemind_time(String remind_time) {
        this.remind_time = remind_time;
    }

    public Long getTime()
    {
        return Long.parseLong(remind_time);
    }

    private boolean status;//是否完成，0未完成，1完成
    private Integer is_dispose;//是否处理，0未处理，1处理
    private String record_url;//音频文件路径
    private String picture_url;//附加图片路径
    private Integer remind_time_id;//提醒时间闹钟的唯一标识
    private String remind_time_lable;//提醒时间的字符串形式
    private Calendar remind_time_date;//提醒时间的日期形式

    public Schedule()
    {

    }

    public Schedule(long time)
    {
        this.remind_time = time+"";
        //使用Calendar的getInstance方法实例化一个Calendar的对象
        remind_time_date = Calendar.getInstance();
        //根据传递进来的time将设置日历实例
        remind_time_date.setTimeInMillis(time);
        //格式化时间显示字符串
        remind_time_lable = remind_time_date.get(Calendar.YEAR)+"年"+
                timeFormatString(remind_time_date.get(Calendar.MONTH) + 1)+"月"+
                timeFormatString(remind_time_date.get(Calendar.DAY_OF_MONTH))+"日,"+
                timeFormatString(remind_time_date.get(Calendar.HOUR_OF_DAY))+":"+
                timeFormatString(remind_time_date.get(Calendar.MINUTE));
        remind_time_id = (int)(time / 60 / 1000);

    }

    public Integer getRemind_time_id() {
        return remind_time_id;
    }

    public void setRemind_time_id(Long remind_time_id) {
        this.remind_time_id = (int)(remind_time_id / 60 / 1000);
    }

    public int getIs_current_year() {
        return is_current_year;
    }

    public void setIs_current_year(int is_current_year) {
        this.is_current_year = is_current_year;
    }

    public String getSchedule_folder_name() {
        return schedule_folder_name;
    }

    public void setSchedule_folder_name(String schedule_folder_name) {
        this.schedule_folder_name = schedule_folder_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public boolean isStatus() {
        return status;
    }

    public void setRemind_time_lable(long time) {
        //使用Calendar的getInstance方法实例化一个Calendar的对象
        remind_time_date = Calendar.getInstance();
        //根据传递进来的time将设置日历实例
        remind_time_date.setTimeInMillis(time);
        //格式化时间显示字符串
        this.remind_time_lable = remind_time_date.get(Calendar.YEAR)+"年"+
                timeFormatString(remind_time_date.get(Calendar.MONTH) + 1)+"月"+
                timeFormatString(remind_time_date.get(Calendar.DAY_OF_MONTH))+"日,"+
                timeFormatString(remind_time_date.get(Calendar.HOUR_OF_DAY))+":"+
                timeFormatString(remind_time_date.get(Calendar.MINUTE));
    }

    public Calendar getRemind_time_date() {
        return remind_time_date;
    }

    public void setRemind_time_date(Calendar remind_time_date) {
        this.remind_time_date = remind_time_date;
    }

    public String timeFormatString(int value)
    {
        return value >= 10 ? value+"" : "0"+value;
    }

    public String getRemind_time_lable() {
        return remind_time_lable;
    }

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public int getSchedule_folder_id() {
        return schedule_folder_id;
    }

    public void setSchedule_folder_id(int schedule_folder_id) {
        this.schedule_folder_id = schedule_folder_id;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getIs_dispose() {
        return is_dispose;
    }

    public void setIs_dispose(int is_dispose) {
        this.is_dispose = is_dispose;
    }

    public String getRecord_url() {
        return record_url;
    }

    public void setRecord_url(String record_url) {
        this.record_url = record_url;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }
}

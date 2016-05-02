package com.pending.css.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/4/20.
 */
public class ScheduleFolder extends BmobObject implements Serializable {
    private String user_id ;
    private String schedule_folder_name;
    private int schedule_folder_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSchedule_folder_name() {
        return schedule_folder_name;
    }

    public void setSchedule_folder_name(String schedule_folder_name) {
        this.schedule_folder_name = schedule_folder_name;
    }

    public int getSchedule_folder_id() {
        return schedule_folder_id;
    }

    public void setSchedule_folder_id(int schedule_folder_id) {
        this.schedule_folder_id = schedule_folder_id;
    }
}

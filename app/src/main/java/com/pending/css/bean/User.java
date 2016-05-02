package com.pending.css.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by css on 2016/3/19.
 */
public class User extends BmobUser implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private int user_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    private BmobFile picture;
    private String nickName;
//    private boolean emailVerified;
    private int age;
    private String pictureUrl;


//    public boolean isEmailVerified() {
//        return emailVerified;
//    }
//
//    public void setEmailVerified(boolean emailVerified) {
//        this.emailVerified = emailVerified;
//    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }



    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
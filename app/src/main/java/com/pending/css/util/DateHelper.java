package com.pending.css.util;

/**
 * Created by Administrator on 2016/4/24.
 */
public class DateHelper {
    public static String timeFormatString(int value)
    {
        return value >= 10 ? value+"" : "0"+value;
    }
}

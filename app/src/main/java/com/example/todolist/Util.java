package com.example.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    public static String dateToString(Date date) {
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    public static Date updateDate(Date date, int year, int month, int day) {
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static Date updateTime(Date date, int hour, int minute) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
}

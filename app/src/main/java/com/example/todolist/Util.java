package com.example.todolist;

import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.view.View;

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

    public static void setViewBackgroundWithoutResettingPadding(View view, Drawable background) {
        int paddingBottom = view.getPaddingBottom();
        int paddingStart = ViewCompat.getPaddingStart(view);
        int paddingEnd = ViewCompat.getPaddingEnd(view);
        int paddingTop = view.getPaddingTop();
        ViewCompat.setBackground(view, background);
        ViewCompat.setPaddingRelative(view, paddingStart, paddingTop, paddingEnd, paddingBottom);
    }
}

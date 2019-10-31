package com.yl.myokgodemo.utils;

import android.content.Context;
import android.os.Handler;

import com.yl.myokgodemo.global.App;


/**
 * Description:工具类
 * Copyright  : Copyright (c) 2016
 * Author     : yanglong
 * Date       : 2016/11/14 21:48
 */
public class Utils {
    public static Context getContext() {
        return App.instance.context;
    }

    //dp-->px
    public static int dip2px(int dip) {
        //屏幕密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }


    //px-->dp
    public static int px2dip(int px) {
        //屏幕密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    //获取主线程的Handler对象
    public static Handler getMainThreadHandler() {
        return App.instance.handler;
    }

    //获取主线程的线程id
    public static int getMainThreadId() {
        return App.instance.mainThreadId;
    }

    //判断是否在主线程
    public static boolean isRunOnUiThread() {
        //1、获取主线程的id
        int mainThreadId = getMainThreadId();
        //2、获取当前线程的id
        int currentThreadId = android.os.Process.myTid();
        return mainThreadId == currentThreadId;
    }

    //保证一个任务一定是在主线程中运行
    //Thread:线程
    //Runnable:任务
    public static void runOnUiThread(Runnable r) {
        if (isRunOnUiThread()) {
            r.run();//在当前的线程中进行方法的调用
        } else {
            getMainThreadHandler().post(r);//将r丢到主线程的消息队列
        }
    }
}

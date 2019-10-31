package com.yl.myokgodemo.utils;

/**
 * Description:避免重复提交数据等待2秒
 * Copyright  : Copyright (c) 2017
 * Author     : yl
 * Date       : 2018/1/2
 */
public class SingleClick {
    public static final int DEFAULT_TIME =2000;
    public static long lastTime;

    public static boolean isSingle() {
        boolean isSingle;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime <= DEFAULT_TIME) {
            isSingle = true;
        } else {
            isSingle = false;
        }
        lastTime = currentTime;
        return isSingle;
    }
}
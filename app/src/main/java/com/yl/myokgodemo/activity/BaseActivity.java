package com.yl.myokgodemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yl.myokgodemo.R;
import com.yl.myokgodemo.global.App;


/**
 * Description: 所以界面的基类
 * Copyright  : Copyright (c) 2017
 * Author     : yl
 * Date       : 2017/9/30
 */

public class BaseActivity extends AppCompatActivity {
    private App mApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //打开一个activity就添加一个
        mApp = (App) getApplication();
        mApp.addActivity(this);
        setContentView(R.layout.activity_base);

    }
    //返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //销毁时调用
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApp.removeActivity(this);
        System.gc();
    }

    /**
     * 替代findviewById方法
     */
    protected <T extends View> T find(int id) {
        return (T) findViewById(id);
    }

}

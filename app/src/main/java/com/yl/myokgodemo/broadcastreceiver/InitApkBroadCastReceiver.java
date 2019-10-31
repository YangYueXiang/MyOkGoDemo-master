package com.yl.myokgodemo.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;

public class InitApkBroadCastReceiver extends BroadcastReceiver {
    private String TAG = "InitApkBroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            //删除apk文件
            deleteApkfile();
        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            deleteApkfile();
        }
    }

    public void deleteApkfile() {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/app.apk";
        rmoveFile(path);
    }

    public void rmoveFile(String path) {
        File file = new File(path);
        file.delete();
    }
}

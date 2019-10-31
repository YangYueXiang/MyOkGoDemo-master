package com.yl.myokgodemo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.yl.myokgodemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private ProgressDialog updataDialog;
    private ProgressDialog mProgressBar;
    public String TAG = "MainActivity";
    private final static int REQUEST_CODE_STORAGE = 1;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    private TextView tv;
    private ImageView mImageView;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermission(this);
        Button get = (Button) findViewById(R.id.get);
        Button post = (Button) findViewById(R.id.post);
        Button file = (Button) findViewById(R.id.file);
        Button downLoad = (Button) findViewById(R.id.downLoad);
        Button image = (Button) findViewById(R.id.image);
        mImageView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.tv);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getByOkGo("http://xawl.qbchoice.cn/staff/dashboard?access_token=71a21417e11923828d705ffd5ed5bc47");
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postByOkGo("http://xawl.qbchoice.cn/staff/index/login");
            }
        });
        downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断sdcard是否存在
                if (!Environment.MEDIA_MOUNTED.equals(
                        Environment.getExternalStorageState())) {
                    Toast.makeText(MainActivity.this, "没有找到sdcard!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //本地apk的文件地址
                String target = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
                downLoad("http://update.qbchoice.com/api/apkdown/7", target, "app.apk");
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBitmap("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File target = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/apperr.txt");
                ArrayList<File> file = new ArrayList<>();
                file.add(target);
                uploadFiles("https://pan.baidu.com/disk/home?#/all?vmode=list&path=%2Fas", "apperr.txt", file);
            }
        });
        //检测版本更新
        checkVerison();
    }

    private void checkVerison() {
        getByOkGo("http://xawl.qbchoice.cn/staff/dashboard?access_token=71a21417e11923828d705ffd5ed5bc47");
    }

    private void parseVerisonJson(String newVerisonJson) {
        try {
         /*   Gson gson = new Gson();
            Data data = gson.fromJson(newVerisonJson, Data.class);
            String downurl = data.getDownurl();
            String msg = data.getMsg();
            int serviceCode = data.getServiceCode();*/
            String msg = "新版本";
            int serviceCode = 2;
            //String mDownloadUrl = "http://" + new Urls().HTTP + downurl;
            String mDownloadUrl = "http://update.qbchoice.com/api/apkdown/7";
            if (getVersionCode() < serviceCode) {
                //有更新弹出升级提示对话框
                try {
                    showUpdateDialog(msg, mDownloadUrl, serviceCode);
                } catch (Exception e) {
                    //在升级版本弹窗还未开启就切换用户,会产生异常,在此捕获异常
                    e.printStackTrace();
                }
            } else {
                return;
            }
        } catch (Exception e) {
            //解析版本失败
            e.printStackTrace();
        }
    }

    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;//版本号
        } catch (Exception e) {
            //包名未找到异常
            e.printStackTrace();
        }
        return -1;
    }

    //弹出升级提示对话框
    private void showUpdateDialog(String mDes, final String mDownloadUrl, final int serviceCode) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("发现新版本");
            builder.setMessage(mDes);
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //下载更新
                    String target = Environment.getExternalStorageDirectory()
                            .getAbsolutePath();
                    downLoad(mDownloadUrl, target, "app.apk");
                }
            });
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            //点击返回键 点击弹窗外侧
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    return;
                }
            });

            //显示弹窗
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get请求获取数据
     *
     * @param url
     */
    private void getByOkGo(String url) {
        OkGo.get(url)                            // 请求方式和请求url
                .tag(this)                       // 请求的 tag, 主要用于取消对应的请求
                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        tv.setText(s);
                        parseVerisonJson("");
                    }
                });
    }

    /**
     * post请求
     *
     * @param url
     */
    public void postByOkGo(String url) {
        OkGo.post(url)
                .tag(this)
                .cacheKey("cachePostKey")
                .cacheMode(CacheMode.DEFAULT)
                .params("user", "17791379657")
                .params("pwd", "123456")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        tv.setText(s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        tv.setText(e.getMessage());
                    }
                });
    }

    /**
     * 下载文件
     *
     * @param url          下载地址
     * @param destFileDir  保存文件路径
     * @param destFileName 保存文件名
     */
    private void downLoad(String url, String destFileDir, String destFileName) {
        //进度弹窗
        updataDialog = new ProgressDialog(this);
        // 设置点击进度对话框外的区域对话框不消失
        updataDialog.setCanceledOnTouchOutside(false);
        updataDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平方向进度条, 用于展示进度
        updataDialog.setTitle("正在下载,请稍候...");
        updataDialog.show();

        OkGo.get(url)
                .tag(this)
                .execute(new FileCallback(destFileDir, destFileName) {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        updataDialog.dismiss();
                        Log.e(TAG, file.toString());
                        installApk(MainActivity.this, file);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
                        updataDialog.dismiss();
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                        int percent = (int) (currentSize * 100 / totalSize);
                        //更新进度弹窗
                        updataDialog.setProgress(percent);
                    }
                });
    }

    public void installApk(Context context, File file) {
        try {
            updataDialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "com.yl.myokgodemo.fileprovider"即是在清单文件中配置的authorities
                data = FileProvider.getUriForFile(context, "com.yl.myokgodemo.fileprovider", file);
                // 给目标应用一个临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                data = Uri.fromFile(file);
            }
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //回显调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (resultCode) {
                case 0:
                    return;
                case 1:
                    updataDialog.dismiss();
                    //如果进入此方法, 说明用户取消安装了
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 多文件上传
     *
     * @param url
     * @param keyName
     * @param files   文件集合
     */
    private void uploadFiles(String url, String keyName, List<File> files) {
        mProgressBar = new ProgressDialog(this);
        // 设置点击进度对话框外的区域对话框不消失
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平方向进度条, 用于展示进度
        mProgressBar.setTitle("正在上传,请稍候...");
        mProgressBar.show();
        OkGo.post(url)//
                .tag(this)//
                //.isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                //.params("param1", "paramValue1")        // 这里可以上传参数
                //.params("file1", new File("filepath1"))   // 可以添加文件上传
                //.params("file2", new File("filepath2"))     // 支持多文件同时添加上传
                .addFileParams(keyName, files)    // 这里支持一个key传多个文件
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        mProgressBar.dismiss();

                        //上传成功
                        Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mProgressBar.dismiss();
                        Log.e(TAG, e.getMessage());
                        //上传失败
                        Toast.makeText(getApplicationContext(), "上传失败！", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调上传进度(该回调在主线程,可以直接更新ui)
                        mProgressBar.setProgress((int) (100 * progress));
                        tv.setText("已上传" + currentSize / 1024 / 1024 + "MB, 共" + totalSize / 1024 / 1024 + "MB;");
                    }
                });
    }

    /**
     * 请求网络图片
     *
     * @param url
     */
    private void getBitmap(String url) {
        OkGo.get(url)//
                .tag(this)//
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        // bitmap 即为返回的图片数据
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }

    /**
     * 申请权限  SD卡的读写权限
     *
     * @param activity
     */
    private void verifyStoragePermission(MainActivity activity) {
        // 1 检测权限
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PermissionChecker.PERMISSION_GRANTED) {
            // 2 没有权限，需要申请权限 弹出对话框
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            //申请权限成功
            Toast.makeText(MainActivity.this, "授权SD卡权限成功", Toast.LENGTH_SHORT).show();
        } else {
            //申请权限失败
            Toast.makeText(MainActivity.this, "授权SD卡权限失败,可能会影响应用的使用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
        //取消所有请求
        OkGo.getInstance().cancelAll();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

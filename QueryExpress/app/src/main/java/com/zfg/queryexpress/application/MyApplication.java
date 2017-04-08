package com.zfg.queryexpress.application;

import android.app.Application;
import android.content.Context;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

/**
 * Created by ZFG on 2016/12/14.
 */
public class MyApplication extends Application{
    public static Context applicationContext;
    public static MyApplication mInstance;
    public static String url = "http://192.168.1.103:8080/QueryExpress";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化nohttp
        NoHttp.init(this);
        Logger.setDebug(true);// 开始NoHttp调试模式, 这样就能看到请求过程和日志
        init();
    }

    private void init() {
        mInstance = this;
    }
    public static MyApplication getInstance() {
        return mInstance;
    }
}

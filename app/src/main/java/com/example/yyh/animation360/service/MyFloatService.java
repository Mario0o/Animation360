package com.example.yyh.animation360.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.example.yyh.animation360.activity.FloatViewManager;


/**
 * Created by yyh on 2016/10/17.
 */
public class MyFloatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        //用来开启FloatViewManager
        FloatViewManager manager=FloatViewManager.getInstance(this);
        manager.showFloatCircleView();
        super.onCreate();
    }

}

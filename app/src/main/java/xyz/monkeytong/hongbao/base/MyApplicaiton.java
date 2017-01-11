package xyz.monkeytong.hongbao.base;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wsong on 2017/1/11.
 */

public class MyApplicaiton extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}

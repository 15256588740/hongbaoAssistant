package xyz.monkeytong.hongbao.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wsong on 2017/1/8.
 */

public class ConfigInfo extends BmobObject {
    private String userId;//用户id
    private boolean watch_notification;//监视通知
    private boolean watch_list;//监视列表
    private boolean watch_chat;//自动拆红包
    private int open_delay;//延迟拆红包
    private boolean watch_self; //拆自己的红包
    private String watch_exclude_words;//屏蔽红包文字

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getWatch_notification() {
        return watch_notification;
    }

    public void setWatch_notification(boolean watch_notification) {
        this.watch_notification = watch_notification;
    }

    public boolean getWatch_list() {
        return watch_list;
    }

    public void setWatch_list(boolean watch_list) {
        this.watch_list = watch_list;
    }

    public boolean getWatch_chat() {
        return watch_chat;
    }

    public void setWatch_chat(boolean watch_chat) {
        this.watch_chat = watch_chat;
    }

    public int getOpen_delay() {
        return open_delay;
    }

    public void setOpen_delay(int open_delay) {
        this.open_delay = open_delay;
    }

    public boolean getWatch_self() {
        return watch_self;
    }

    public void setWatch_self(boolean watch_self) {
        this.watch_self = watch_self;
    }

    public String getWatch_exclude_words() {
        return watch_exclude_words;
    }

    public void setWatch_exclude_words(String watch_exclude_words) {
        this.watch_exclude_words = watch_exclude_words;
    }
}

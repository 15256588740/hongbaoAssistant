package xyz.monkeytong.hongbao.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by wsong on 2017/1/7.
 */

public class UserInfo extends BmobObject {
    private String username;
    private String password;
    private String recall_code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecall_code() {
        return recall_code;
    }

    public void setRecall_code(String recall_code) {
        this.recall_code = recall_code;
    }
}

package xyz.monkeytong.hongbao.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.base.UserBaseActivity;
import xyz.monkeytong.hongbao.bean.UserInfo;

/**
 * Created by wsong on 2017/1/8.
 */

public class RegisterActivity extends UserBaseActivity {
    private EditText username_edit;
    private EditText password_edit;
    private EditText recall_code_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        username_edit = (EditText) findViewById(R.id.username_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        recall_code_edit = (EditText) findViewById(R.id.recall_code_edit);
    }

    /**
     * 注册
     *
     * @param v
     */
    public void register(View v) {
        String username = username_edit.getText().toString().trim();
        String pw = password_edit.getText().toString().trim();
        String recall_code = recall_code_edit.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pw)
                || TextUtils.isEmpty(recall_code)
                || username.contains(" ") || pw.contains(" ") || recall_code.contains(" ")) {
            toast("用户名、密码、安全码不能为空且不能包含空格");
            return;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(pw);
        userInfo.setRecall_code(recall_code);
        userInfo.setUsername(username);
        userInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    toast("账号注册成功");
                    //返回登录界面
                    finish();
                } else {
                    toast("账号注册失败，用户名已存在!请重新输入");
                    //清空输入框
                    username_edit.clearComposingText();
                    password_edit.clearComposingText();
                    recall_code_edit.clearComposingText();
                }
            }
        });
    }
}

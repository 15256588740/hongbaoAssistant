package xyz.monkeytong.hongbao.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.base.UserBaseActivity;
import xyz.monkeytong.hongbao.bean.UserInfo;

/**
 * Created by wsong on 2017/1/8.
 */

public class LoginActivity extends UserBaseActivity implements View.OnClickListener {
    private EditText username_edit;
    private EditText password_edit;
    private Button signin_button;
    private TextView recall_link;
    private TextView register_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        username_edit = (EditText) findViewById(R.id.username_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        signin_button = (Button) findViewById(R.id.signin_button);
        recall_link = (TextView) findViewById(R.id.recall_link);
        register_link = (TextView) findViewById(R.id.register_link);

        signin_button.setOnClickListener(this);
        recall_link.setOnClickListener(this);
        register_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button:
                login();
                break;
            case R.id.recall_link:
                recall();
                break;
            case R.id.register_link:
                register();
                break;
            default:
        }
    }

    /**
     * 跳转注册
     */
    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    /**
     * 跳转密码找回
     */
    private void recall() {
        Intent intent = new Intent(this, RecallActivity.class);
        startActivity(intent);
    }

    /**
     * 账号登录
     */
    private void login() {
        String username = username_edit.getText().toString();
        final String pw = password_edit.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pw)) {
            toast("用户名、密码不能为空!!!");
            return;
        }
        BmobQuery<UserInfo> query = new BmobQuery();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null) {
                    for (UserInfo info : list) {
                        if (info.getPassword().equals(pw)) {
                            toast("登录成功");
                            Intent intent = new Intent();
                            intent.putExtra("is_login", true);
                            intent.putExtra("userId",info.getObjectId());
                            setResult(RESULT_OK, intent);
                            finish();
                            return;
                        } else {
                            toast("登录失败，请检查账号密码");
                            return;
                        }
                    }
                    toast("用户名不存在");
                } else {
                    toast("网络错误：" + e.getMessage());
                }
            }
        });
    }


}

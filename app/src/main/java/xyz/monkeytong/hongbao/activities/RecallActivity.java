package xyz.monkeytong.hongbao.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.base.UserBaseActivity;
import xyz.monkeytong.hongbao.bean.UserInfo;

/**
 * Created by wsong on 2017/1/8.
 */

public class RecallActivity extends UserBaseActivity {

    private EditText username_edit;
    private EditText password_edit;
    private EditText password_edit_re;
    private EditText recall_code_edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recall);
        initView();
    }

    private void initView() {
        username_edit = (EditText) findViewById(R.id.username_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        password_edit_re = (EditText) findViewById(R.id.password_edit_re);
        recall_code_edit = (EditText) findViewById(R.id.recall_code_edit);
    }

    public void recall(View v) {
        String username = username_edit.getText().toString().trim();
        final String recall_code = recall_code_edit.getText().toString().trim();
        final String pw = password_edit.getText().toString().trim();
        String pw_re = password_edit_re.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(recall_code)) {
            toast("用户名、安全码不能为空");
            return;
        }

        if (!pw_re.equals(pw)) {
            toast("2次密码输入不一致，请重新输入");
            username_edit.clearComposingText();
            password_edit.clearComposingText();
            password_edit_re.clearComposingText();
            recall_code_edit.clearComposingText();
            return;
        }
        //查找用户名对应的安全码是否正确
        BmobQuery<UserInfo> query = new BmobQuery();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null) {
                    for (UserInfo info : list) {
                        if (info.getRecall_code().equals(recall_code)) {
                            //安全码正确，更新密码
                            UserInfo userinfo = new UserInfo();
                            userinfo.setPassword(pw);
                            userinfo.update(info.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        toast("密码更新成功");
                                        finish();
                                    } else {
                                        toast("密码更新失败：" + e.getMessage());
                                    }
                                }
                            });
                            return;
                        } else {
                            toast("安全码错误");
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

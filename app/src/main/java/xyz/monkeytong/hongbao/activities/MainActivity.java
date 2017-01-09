package xyz.monkeytong.hongbao.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.utils.ConnectivityUtil;
import xyz.monkeytong.hongbao.utils.UpdateTask;

public class MainActivity extends Activity implements AccessibilityManager.AccessibilityStateChangeListener {

    //开关切换按钮
    private TextView pluginStatusText;
    private ImageView pluginStatusIcon;
    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;
    private Switch mSwitch;
    private TextView mSwitchText;
    private SharedPreferences sp;
    private TextView mTv_update;
    private TextView tv_login_state;
    private LinearLayout ll_login;
    private TextView tv_setting_backup;
    private  boolean is_login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReport.initCrashReport(getApplicationContext(), "900019352", false);
        setContentView(R.layout.activity_main);
        initView();
        // 加载的设置
        explicitlyLoadPreferences();
        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        updateServiceStatus();
    }

    private void initView() {
        tv_login_state = (TextView) findViewById(R.id.tv_login_state);
        tv_setting_backup = (TextView) findViewById(R.id.tv_setting_backup);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        mTv_update = (TextView) findViewById(R.id.tv_setting_update);
        pluginStatusText = (TextView) findViewById(R.id.layout_control_accessibility_text);
        pluginStatusIcon = (ImageView) findViewById(R.id.layout_control_accessibility_icon);
        mSwitch = (Switch) findViewById(R.id.st_switch);
        mSwitchText = (TextView) findViewById(R.id.tv_check_title);
        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sp.edit();
                if (mSwitch.isChecked()) {
                    mSwitchText.setText("微信抢红包");
                    edit.putBoolean("platform", true);
                } else {
                    mSwitchText.setText("QQ抢红包");
                    edit.putBoolean("platform", false);
                }
                edit.commit();
            }
        });

        mTv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查版本更新
                new UpdateTask(MainActivity.this, true).update();
            }
        });

        ll_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_login) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    is_login = data.getBooleanExtra("is_login", false);
                    if (is_login) {
                        tv_login_state.setText("已登录");
                        tv_setting_backup.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
        }
    }

    private void explicitlyLoadPreferences() {
        //将xml文件中的默认值存储到sp中
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //WiFi连接在第一时间检查更新。
        if (ConnectivityUtil.isWifi(this) || UpdateTask.count == 0)
            new UpdateTask(this, false).update();
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        super.onDestroy();
    }

    public void openAccessibility(View view) {
        try {
            Toast.makeText(this, "点击「红包助手」" + pluginStatusText.getText(), Toast.LENGTH_SHORT).show();
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(accessibleIntent);
        } catch (Exception e) {
            Toast.makeText(this, "遇到一些问题,请手动打开系统设置->无障碍服务->微信红包", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    /**
     * 打开设置面板
     *
     * @param view
     */
    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        settingsIntent.putExtra("title", "偏好设置");
        settingsIntent.putExtra("frag_id", "GeneralSettingsFragment");
        startActivity(settingsIntent);
    }


    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 HongbaoService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            pluginStatusText.setText(R.string.service_off);
            pluginStatusIcon.setBackgroundResource(R.mipmap.ic_stop);
        } else {
            pluginStatusText.setText(R.string.service_on);
            pluginStatusIcon.setBackgroundResource(R.mipmap.ic_start);
        }
    }

    /**
     * 获取 HongbaoService 是否启用状态
     *
     * @return
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.services.HongbaoService")) {
                return true;
            }
        }
        return false;
    }
}

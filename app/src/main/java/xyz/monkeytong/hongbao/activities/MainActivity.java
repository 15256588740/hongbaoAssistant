package xyz.monkeytong.hongbao.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.base.UserBaseActivity;
import xyz.monkeytong.hongbao.bean.ConfigInfo;
import xyz.monkeytong.hongbao.ui.BottomDialogFragment;
import xyz.monkeytong.hongbao.ui.MoneyView;
import xyz.monkeytong.hongbao.ui.NavigationDialog;
import xyz.monkeytong.hongbao.utils.ConnectivityUtil;
import xyz.monkeytong.hongbao.utils.PrefUtils;
import xyz.monkeytong.hongbao.utils.UpdateTask;

public class MainActivity extends UserBaseActivity implements AccessibilityManager.AccessibilityStateChangeListener {

    //开关切换按钮
    private TextView pluginStatusText;
    private ImageView pluginStatusIcon;
    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;
    // private Switch mSwitch;
    // private TextView mSwitchText;
    private RelativeLayout layout_uber;
    private SharedPreferences sp;
    private TextView mTv_update;
    private TextView tv_login_state;
    private LinearLayout ll_login;
    private TextView tv_setting_backup;
    private boolean is_login = false;
    private String userId;
    private TextView tv_share;
    private NavigationDialog mDialog;
    private boolean isFirst = false;
    private String[] mStrings = {"微信红包", "QQ红包"};
    private MoneyView moneyview;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moneyview.start();
            mHandler.sendEmptyMessageDelayed(0, 3500);
        }
    };

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
        //显示提醒对话框

        if (!isFirst) {
            isFirst = true;
            showNavigationDialog();
        }

        mHandler.sendEmptyMessageDelayed(0, 3500);

    }


    private void initView() {
        moneyview = (MoneyView) findViewById(R.id.moneyview);
        layout_uber = (RelativeLayout) findViewById(R.id.layout_uber);
        tv_login_state = (TextView) findViewById(R.id.tv_login_state);
        tv_share = (TextView) findViewById(R.id.tv_share);
        tv_setting_backup = (TextView) findViewById(R.id.tv_setting_backup);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        mTv_update = (TextView) findViewById(R.id.tv_setting_update);
        pluginStatusText = (TextView) findViewById(R.id.layout_control_accessibility_text);
        pluginStatusIcon = (ImageView) findViewById(R.id.layout_control_accessibility_icon);
        layout_uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogFragment dialogFragment = new BottomDialogFragment();
                dialogFragment.load("红包渠道", mStrings, PrefUtils.getIntsp(MainActivity.this, "platform_which", 0));
                dialogFragment.setOnItemClickListener(new BottomDialogFragment.OnItemClickListener() {
                    @Override
                    public void OnItemClick(int which) {
                        SharedPreferences.Editor edit = sp.edit();
                        switch (which) {
                            case 0:
                                edit.putBoolean("platform", true);
                                break;
                            case 1:
                                edit.putBoolean("platform", false);
                                break;
                        }
                        edit.commit();
                        PrefUtils.setIntsp(MainActivity.this, "platform_which", which);
                    }
                });
                dialogFragment.show(getFragmentManager(), "");
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
        tv_setting_backup.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     if (is_login && !TextUtils.isEmpty(userId)) {
                                                         //向服务器提交配置信息
                                                         uploadData();

                                                     }
                                                 }
                                             }

        );
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });

    }

    /**
     * 软件分享
     */
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("红包助手");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("https://github.com/15256588740/hongbaoAssistant");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("一个帮助你在微信抢红包时战无不胜的Android应用，https://github.com/15256588740/hongbaoAssistant");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://github.com/15256588740/hongbaoAssistant");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("自动检测并且拆开红包，速度超乎你的想象");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("https://github.com/15256588740/hongbaoAssistant");
        // 启动分享GUI
        oks.show(this);
    }

    /**
     * 向服务器上传数据
     */
    private void uploadData() {
        //获取配置信息
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final ConfigInfo configInfo = new ConfigInfo();
        configInfo.setWatch_notification(sp.getBoolean("pref_watch_notification", true));
        configInfo.setWatch_list(sp.getBoolean("pref_watch_list", false));
        configInfo.setWatch_self(sp.getBoolean("pref_watch_self", true));
        configInfo.setWatch_chat(sp.getBoolean("pref_watch_chat", true));
        configInfo.setWatch_exclude_words(sp.getString("pref_watch_exclude_words", ""));
        configInfo.setOpen_delay(sp.getInt("pref_open_delay", 0));
        configInfo.setUserId(userId);
        //查询备份是否存在，已经存在更新，否则添加
        BmobQuery<ConfigInfo> query = new BmobQuery();
        query.addWhereEqualTo("userId", userId);
        query.findObjects(new FindListener<ConfigInfo>() {
            @Override
            public void done(List<ConfigInfo> list, BmobException e) {
                if (e == null) {
                    for (ConfigInfo info : list) {
                        //存在,更新数据
                        configInfo.update(info.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    toast("配置更新成功");
                                } else {
                                    toast("配置更新失败：" + e.getMessage());
                                }
                            }
                        });
                        return;
                    }
                    //不存在，添加数据
                    configInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                toast("配置上传成功");
                            } else {
                                toast("配置上传失败，请检查网络");
                            }
                        }
                    });
                } else {
                    toast("网络错误：" + e.getMessage());
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
                    userId = data.getStringExtra("userId");
                    if (is_login && !TextUtils.isEmpty(userId)) {
                        tv_login_state.setText("已登录");
                        tv_setting_backup.setVisibility(View.VISIBLE);
                        // 还原服务器备份
                        recallData();
                    }
                }
                break;
            default:
        }
    }

    /**
     * 每次一登录就自动还原服务器备份
     */
    private void recallData() {
        BmobQuery<ConfigInfo> query = new BmobQuery();
        query.addWhereEqualTo("userId", userId);
        query.findObjects(new FindListener<ConfigInfo>() {
            @Override
            public void done(List<ConfigInfo> list, BmobException e) {
                if (e == null) {
                    for (ConfigInfo info : list) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("pref_watch_notification", info.getWatch_notification());
                        editor.putBoolean("pref_watch_list", info.getWatch_list());
                        editor.putBoolean("pref_watch_self", info.getWatch_self());
                        editor.putBoolean("pref_watch_chat", info.getWatch_chat());
                        editor.putString("pref_watch_exclude_words", info.getWatch_exclude_words());
                        editor.putInt("pref_open_delay", info.getOpen_delay());
                        editor.commit();

                    }
                } else {
                    toast("网络错误：" + e.getMessage());
                }
            }
        });
    }

    private void explicitlyLoadPreferences() {
        //将xml文件中的默认值存储到sp中
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateServiceStatus();
        //WiFi连接在第一时间检查更新。
        if (ConnectivityUtil.isWifi(this) || UpdateTask.count == 0)
            new UpdateTask(this, false).update();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void openAccessibility(View view) {
        try {
            startSystemSetting();
        } catch (Exception e) {
            Toast.makeText(this, "遇到一些问题,请手动打开系统设置->无障碍服务->微信红包", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    /**
     * 开启系统设置
     */
    private void startSystemSetting() {
        Toast.makeText(this, "点击无障碍->「红包助手」" + pluginStatusText.getText(), Toast.LENGTH_SHORT).show();
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibleIntent);
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
            //服务未开启
            pluginStatusText.setText(R.string.service_on);
            pluginStatusIcon.setBackgroundResource(R.mipmap.ic_start);
        }
    }

    public void showNavigationDialog() {
        mDialog = new NavigationDialog(this);
        mDialog.setCancelable(true);
        mDialog.setOnQuickOptionformClickListener(new NavigationDialog.OnQuickOptionformClick() {
            @Override
            public void onQuickOptionClick() {
                //开启系统设置
                startSystemSetting();
            }
        });
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();


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

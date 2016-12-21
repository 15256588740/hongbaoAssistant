package xyz.monkeytong.hongbao.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.utils.UpdateTask;

/**
 * 设置fragment
 */
public class GeneralSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_preferences);
        setPrefListeners();
    }

    private void setPrefListeners() {
        // Check for updates
        // 点击检查更新选项
        Preference updatePref = findPreference("pref_etc_check_update");
        updatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new UpdateTask(getActivity().getApplicationContext(), true).update();
                return false;
            }
        });
        // 屏蔽红包文字
        Preference excludeWordsPref = findPreference("pref_watch_exclude_words");
        //不拆开包含这些文字的红包(空格间隔)
        String summary = getResources().getString(R.string.pref_watch_exclude_words_summary);
        String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_watch_exclude_words", "");
        if (value.length() > 0) {
            //显示自定义回复语
            excludeWordsPref.setSummary(summary + ":" + value);
        }
        excludeWordsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String summary = getResources().getString(R.string.pref_watch_exclude_words_summary);
                if (o != null && o.toString().length() > 0) {
                    preference.setSummary(summary + ":" + o.toString());
                } else {
                    preference.setSummary(summary);
                }
                return true;
            }
        });
    }
}

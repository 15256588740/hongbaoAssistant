package xyz.monkeytong.hongbao.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference 封装
 * @author wsong
 *
 */
public class PrefUtils {
	/**
	 * 获取boolean类型的sp值
	 */
	public static boolean getBooleansp(Context context, String key,
			boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		boolean result = sp.getBoolean(key, defValue);
		return result;
	}

	/**
	 * 设置boolean类型的sp值
	 */
	public static void setBooleansp(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 获取String类型的sp值
	 */
	public static String getStringsp(Context context, String key,
			String defValue) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		String result = sp.getString(key, defValue);
		return result;
	}
	/**
	 * 设置String类型的sp值
	 */
	public static void setStringsp(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	
	/**
	 * 获取int类型的sp值
	 */
	public static int getIntsp(Context context, String key,
			int defValue) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		int result = sp.getInt(key, defValue);
		return result;
	}

	/**
	 * 设置int类型的sp值
	 */
	public static void setIntsp(Context context, String key, int value) {
		SharedPreferences sp = context.getSharedPreferences("config",
				context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

}

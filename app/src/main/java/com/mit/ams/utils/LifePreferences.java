package com.mit.ams.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * SharedPreferences(单例模式)
 * 
 */
public class LifePreferences {

	// Setting对象
	private static LifePreferences instance;
	// 创建SharedPreferences接口
	private SharedPreferences sp;

	/**
	 * 私有构造方法
	 */
	private LifePreferences() {
	}

	/**
	 * 获取Setting对象
	 * 
	 * @return Setting对象
	 */
	public synchronized static LifePreferences getInstance() {
		if (instance == null) {
			instance = new LifePreferences();
		}
		return instance;
	}

	/**
	 * 获取SharedPreferences对象
	 * 
	 * @param context
	 *            上下文
	 */
	public LifePreferences initSP(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		}
		return instance;
	}

	/**
	 * 写入数据
	 * 
	 * @param key
	 *            Key值
	 * @param value
	 *            保存的数据
	 */
	public void writeSpData(String key, Object value) {
		Editor editor = sp.edit();
		if (value instanceof Boolean) {// Boolean
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {// String
			editor.putString(key, (String) value);
		} else if (value instanceof Long) {// Long
			editor.putLong(key, (Long) value);
		} else if (value instanceof Integer) {// Integer
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {// Float
			editor.putFloat(key, (Float) value);
		}
		// 提交数据
		editor.commit();
	}

	/**
	 * 读取数据
	 * 
	 * @param key
	 *            Key值
	 * @param defValue
	 *            默认值
	 * @return 数据
	 */
	public Object readSpData(String key, Object defValue) {
		if (defValue instanceof Boolean) {// Boolean
			return sp.getBoolean(key, (Boolean) defValue);
		} else if (defValue instanceof String) {// String
			return sp.getString(key, (String) defValue);
		} else if (defValue instanceof Long) {// Long
			return sp.getLong(key, (Long) defValue);
		} else if (defValue instanceof Integer) {// Integer
			return sp.getInt(key, (Integer) defValue);
		} else if (defValue instanceof Float) {// Float
			return sp.getFloat(key, (Float) defValue);
		} else {
			return null;
		}
	}

	public void clearSpData(){
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

}


package com.likebamboo.phoneshow.config;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 与xml数据文件相关的操作
 * 
 * @author likebamboo
 */
public class ShowPref {
	
	public static final String IS_FRIST = "isFrist";
    public static final String SHOW_TYPE = "show_type";
    public static final String TYPE_HALF_VALUE = "type_half_value";
    
    public static final int TYPE_ACTIVITY = 0x10000;

    public static final int TYPE_FULL_DIALOG = 0x10001;

    public static final int TYPE_HALF_DIALOG = 0x10002;

    public static final int TYPE_HALF_DIALOG_DEFAULT = 50;
    
    public static final String SHOW_SET_TYPE = "set_type"; //显示自己的还是联系人的铃声设置
    public static final String MY_ID = "myId"; 
    public static final String MY_NAME = "myName"; 
    public static final String MY_GOLD = "myGold"; 
    public static final String MY_SIGN = "mySign"; 
	public static final String MY_PHONENUMBER = "myPhoneNumber";
    public static final String IS_SHOW_VIDEO = "isShowVideo"; //来电是否显示视频
    public static final String DEFAULT_VIDEO_PATH = "defaultVideoPath"; 
    public static final String DEFAULT_PIC_PATH = "defaultPicPath"; 
	public static final String DEFAULT_VIDEO_ID = "defaultVideoId";
	public static final String APP_NEW_VERSION = "appNewVersion";
	public static final String JSON = "json";
	public static final String VIDEOTYPEPAGES = "videoTypePages";
	
    private static ShowPref mInstance = null;

    private Context mContext = null;

    private static String MAIN_PROCESS_PREF_NAME = "main_pref";
    private static String SERVICE_PROCESS_PREF_NAME = "service_pref";
    private SharedPreferences mSettings = null;
    private SharedPreferences.Editor mEditor = null;

    private ShowPref(Context ctx, String prefName) {
        mContext = ctx;
    	mSettings = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
    }
    
    public static ShowPref getMainInstance(Context context) {
        return getInstance(context, MAIN_PROCESS_PREF_NAME);
    }
    
    public static ShowPref getServiceInstance(Context context) {
        return getInstance(context, SERVICE_PROCESS_PREF_NAME);
    }
    
    public static ShowPref getInstance(Context context, String prefName) {
        if (mInstance == null) {
            mInstance = new ShowPref(context, prefName);
        }
        return mInstance;
    }

    /**
     * 判断是否有某一个值
     * 
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return mSettings.contains(key);
    }

    /**
     * 获取字符串
     * 
     * @param key key值
     * @return
     */
    public String loadString(String key) {
        return mSettings.getString(key, "");
    }

    /**
     * 保存字符串
     * 
     * @param key key值
     * @param value value值
     * @return
     */
    public boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    /**
     * 获取整型值
     * 
     * @param key key值
     * @return
     */
    public int loadInt(String key) {
        return mSettings.getInt(key, -1);
    }

    /**
     * 获取整型值
     * 
     * @param key key值
     * @return
     */
    public int loadInt(String key, int defValue) {
        return mSettings.getInt(key, defValue);
    }

    /**
     * 保存整型值
     * 
     * @param key key值
     * @param value value值
     * @return
     */
    public boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

}

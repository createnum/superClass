package com.likebamboo.phoneshow.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.likebamboo.phoneshow.widget.overlayActivity.OverLayActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Utils {

	/**
	 * 直接显示Toast
	 * 
	 * @param context
	 *            当前环境上下文对象
	 * @param text
	 *            内容
	 * @param isShort
	 *            是否短时间显示（false则为长时间显示）
	 */
	public static void showToast(Context context, String text, boolean isShort) {
		if (isShort) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 直接显示Toast
	 * 
	 * @param context
	 *            当前环境上下文对象
	 * @param text
	 *            内容
	 * @param isShort
	 *            是否短时间显示（false则为长时间显示）
	 */
	public static void showToast(Context context, int resId, boolean isShort) {
		if (isShort) {
			Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 直接显示Toast
	 * 
	 * @param context
	 *            当前环境上下文对象
	 * @param resId
	 *            字符串资源id
	 */
	public static void showToast(Context context, int resId) {
		showToast(context, resId, true);
	}

	/**
	 * 直接显示Toast
	 * 
	 * @param context
	 *            当前环境上下文对象
	 * @param text
	 *            内容
	 */
	public static void showToast(Context context, String text) {
		showToast(context, text, true);
	}

	/**
	 * 关闭输入法
	 */
	public static void closeEditer(Activity context) {
		View view = context.getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 判断网络是否可用
	 */
	public static boolean CheckNetworkConnection(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			// 当前网络不可用
			return false;
		}
		return true;
	}

	/**
	 * 判断wifi网络是否可用
	 */
	public static boolean IsWifiEnable(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalFileDir(Context context) {
		if (hasFroyo()) {
			final File file = context.getExternalFilesDir(null);
			return file;
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String fileDir = "/Android/data/" + context.getPackageName()
				+ "/files";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ fileDir);
	}

	public static File getCacheDir(Context context) {
		File cacheDir = null;
		try {
			cacheDir = getExternalFileDir(context);
		} catch (Exception e) {
			e.printStackTrace();
			cacheDir = context.getFilesDir();
		}
		return cacheDir;
	}

	/**
	 * >= android 2.2 版本
	 * 
	 * @return
	 */
	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	/**
	 * >= android 2.3 版本
	 * 
	 * @return
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * >= android 3.0 版本
	 * 
	 * @return
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
	}

	/**
	 * 收起输入法
	 * 
	 * @param ctx
	 * @param view
	 */
	public static void HideKeyboard(Context ctx, View view) {
		if (null == view)
			return;
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 字符串转Html
	 * 
	 * @param text
	 *            字符串
	 * @return 格式化的html。
	 */
	public static Spanned formatHtml(String text) {
		return Html.fromHtml(text);
	}


	public static void sendCloseCallUIBroadCast(Context ctx) {
		Intent i = new Intent();
		i.setAction(OverLayActivity.ACTION_CLOSE_CALL_UI);
		ctx.sendBroadcast(i);
	}
	

	/**
	 * 挂断电话
	 */
	public static synchronized void endCall(Context ctx) {
		TelephonyManager mTelMgr = (TelephonyManager) ctx
				.getSystemService(Service.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		try {
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			System.out.println("End call.");
			iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr,
					(Object[]) null);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fail to answer ring call.");
		}
	}

	public static synchronized int getCallState(Context ctx) {
		try {
			ClassLoader classLoader = ctx.getClass().getClassLoader();
			final Class<?> classCallManager = classLoader
					.loadClass("com.android.internal.telephony.CallManager");
			String TAG = "dong";
			Log.i(TAG, "dong Class loaded " + classCallManager.toString());

			Method methodGetInstance = classCallManager
					.getDeclaredMethod("getInstance");
			Log.i(TAG, "dong Method loaded " + methodGetInstance.getName());

			Object objectCallManager = methodGetInstance.invoke(null);
			Log.i(TAG, "dong Object loaded "
					+ objectCallManager.getClass().getName());

			Method methodGetState = classCallManager
					.getDeclaredMethod("getState");
			Log.i(TAG, "dong Method loaded " + methodGetState.getName());
			//
			Log.i(TAG,
					"dong Phone state = "
							+ methodGetState.invoke(objectCallManager));
			// Method methodGetState
			// =classCallManager.getDeclaredMethod("registerForPreciseCallStateChanged");
			// // methodGetState.setAccessible(true);
			// methodGetState.invoke(objectCallManager, mHandler, 101);
			// Log.i(TAG, "dong Method loaded = " + methodGetState.getName());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("dong Fail to answer ring call.");
		}
		return -1;
	}

	public static synchronized void autoAnswerPhone(final Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		// 判断是否插上了耳机
		if (!audioManager.isWiredHeadsetOn()) {

			if (android.os.Build.VERSION.SDK_INT >= 20) {
				 try {  
				        Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));  
				    } catch (IOException e) {  
				    	final String enforcedPerm = "android.permission.CALL_PRIVILEGED";  
					    Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,  
					            KeyEvent.KEYCODE_HEADSETHOOK));  
					    final Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,  
					            KeyEvent.KEYCODE_HEADSETHOOK));  
					  
					    context.sendOrderedBroadcast(btnDown, enforcedPerm);  
					    new Handler().postDelayed(new Runnable() {  
					        @Override  
					        public void run() {  
					            context.sendOrderedBroadcast(btnUp, enforcedPerm);  
					        }  
					    }, 500);  
				    } 
			}else if (android.os.Build.VERSION.SDK_INT >= 15) {
				Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);  
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);  
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT,keyEvent);  
				context.sendOrderedBroadcast(meidaButtonIntent, null); 

			} else {
				// 以下适用于Android2.3及2.3以上的版本上 ，但测试发现4.1系统上不管用。
				Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent1.putExtra("state", 1);
				localIntent1.putExtra("microphone", 1);
				localIntent1.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent1,
						"android.permission.CALL_PRIVILEGED");

				Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent2.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent1);
				context.sendOrderedBroadcast(localIntent2,
						"android.permission.CALL_PRIVILEGED");

				Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent3.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent2);
				context.sendOrderedBroadcast(localIntent3,
						"android.permission.CALL_PRIVILEGED");

				Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent4.putExtra("state", 0);
				localIntent4.putExtra("microphone", 1);
				localIntent4.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent4,
						"android.permission.CALL_PRIVILEGED");
			}

		} else {
			Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_HEADSETHOOK);
			meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
			context.sendOrderedBroadcast(meidaButtonIntent, null);
		}
	}

	/**
	 * 接听电话
	 */
	public static synchronized void answerRingingCall(Context ctx) {
		TelephonyManager mTelMgr = (TelephonyManager) ctx 
				.getSystemService(Service.TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		try {
			Method getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
			ITelephony iTelephony = null;
			iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr,
					(Object[]) null);
			iTelephony.answerRingingCall();
		} catch (Exception e) {
			autoAnswerPhone(ctx);
		}
	}
    
    
	
}

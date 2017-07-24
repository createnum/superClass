package com.zancheng.callphonevideoshow.tools;

import com.zancheng.callphonevideoshow.sdk.R;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
	public static Toast mToast;

	/**
	 * 显示吐司
	 * 
	 * @param context
	 * @param message
	 */
	public static void showToast(final Context context, final String message) {
		if (mToast == null) {
			mToast = new Toast(context);
		}
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		View view = LayoutInflater.from(context).inflate(context.getResources().getIdentifier("csx_sdk_zc_my_toast", "layout", context.getPackageName()),null);
		TextView textView = (TextView) view.findViewById(context.getResources().getIdentifier("TextViewInfo", "id", context.getPackageName()));
		textView.setText(message);
		mToast.setView(view);
		mToast.show();
	}

	/**
	 * 显示吐司异步调用
	 * 
	 * @param context
	 * @param message
	 */
	public static void showToastASK(final Context context, final String message) {
		Looper.prepare();
		if (mToast == null) {
			mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
		Looper.loop();
	}

	/**
	 * 显示吐司
	 * 
	 * @param context
	 * @param messageResId
	 */
	public static void showToast(final Context context, final int messageResId) {

		if (mToast == null) {
			mToast = Toast.makeText(context, messageResId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(messageResId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();

	}

	/**
	 * 显示吐司异步调用
	 * 
	 * @param context
	 * @param messageResId
	 */
	public static void showToastASK(final Context context,
			final int messageResId) {
		Looper.prepare();
		if (mToast == null) {
			mToast = Toast.makeText(context, messageResId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(messageResId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
		Looper.loop();
	}
}

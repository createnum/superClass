package com.example.superclassroom;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class Welcome extends Activity {

	public static Welcome context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		final View view = View.inflate(this, R.layout.welcome, null);
		setContentView(view);
		context = this;
		//渐变展示启动屏
				AlphaAnimation aa = new AlphaAnimation(0,1);
				aa.setDuration(2000);
				view.startAnimation(aa);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent();
				intent.setClass(Welcome.this, MainTabActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
		
	}

}

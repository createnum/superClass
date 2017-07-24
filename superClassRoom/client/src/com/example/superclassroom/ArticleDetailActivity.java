package com.example.superclassroom;

import java.util.List;

import com.example.db.DBManager;
import com.example.moudle.BuyClass;
import com.example.moudle.Collect;
import com.example.moudle.User;
import com.example.tools.PhoneUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleDetailActivity extends Activity {
	private ImageView collect;
	private static String imsi;
	private static DBManager dbm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.article_detail);

		imsi = PhoneUtils.getIMSI(MainTabActivity.myMainActivity);
		dbm = new DBManager(MainTabActivity.myMainActivity);
		Intent intent = getIntent();
		String fileName = intent.getStringExtra("fileName");
		String title = intent.getStringExtra("title");
		int id = intent.getIntExtra("id", -1);
		int buyMoney = intent.getIntExtra("buyMoney", -1);
		final int article_id = id;
		boolean flag = true;
		List<Integer> list = dbm.queryBuyClass(imsi);
		for (int i : list) {
			if (i == id) {
				flag = false;
			}
		}
		if (buyMoney > 0 && flag) {
			buyClass(id, buyMoney);
		}
		TextView typeName = (TextView) findViewById(R.id.textView2);
		typeName.setText(title);

		// 返回键
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArticleDetailActivity.this.finish();
			}
		});

		// 收藏操作
		collect = (ImageView) findViewById(R.id.collect);
		List<Integer> list1 = dbm.queryCollect(imsi);
		if (list1.contains(id)) {
			collect.setImageResource(R.drawable.collect_click);
		} else {
			collect.setImageResource(R.drawable.collect);
		}
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<Integer> list1 = dbm.queryCollect(imsi);
				if (list1.contains(article_id)) {
					collect.setImageResource(R.drawable.collect);
					Collect collect = new Collect(imsi, article_id);
					dbm.deleteCollect(collect);
				} else {
					collect.setImageResource(R.drawable.collect_click);
					Collect collect = new Collect(imsi, article_id);
					dbm.addCollect(collect);
				}
			}
		});
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl("file:///android_asset/articles/" + fileName);
		WebSettings webSettings = webView.getSettings();
		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);
		webSettings.setUseWideViewPort(false); // 将图片调整到适合webview的大小
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); // 支持内容重新布局
	}

	public static void buyClass(int id, int buyMoney) {
		BuyClass bc = new BuyClass(imsi);
		bc.setClass_id(id);
		dbm.addBuyClass(bc);
		List<User> users = dbm.queryUser(imsi);
		User user = new User(imsi);
		int money = users.get(0).getMoney() - buyMoney;
		user.setMoney(money);
		dbm.updateUser(user);
	}

}

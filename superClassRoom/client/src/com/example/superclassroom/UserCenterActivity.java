package com.example.superclassroom;

import com.example.db.DBManager;
import com.example.moudle.CustomDialog;
import com.example.moudle.Them;
import com.example.tools.Pay;
import com.example.tools.PhoneUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class UserCenterActivity extends FragmentActivity {
	protected Context context;
	private View collection;
	private View bought;
	private View about;
	private View set;
	private View vipStore;
	private String imsi;
	private DBManager dbm;
	private View layout;
	private static CustomDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = MainTabActivity.myMainActivity;
		dbm = new DBManager(context);
		imsi=PhoneUtils.getIMSI(context);
		setContentView(R.layout.user_center);
		initView();
		addListener();
	}

	private void initView() {
		bought = (View) findViewById(R.id.boughtRL);
		about = (View) findViewById(R.id.aboutRL);
		collection = (View) findViewById(R.id.collectionRL);
		vipStore = (View) findViewById(R.id.vipRL);
		set = (View) findViewById(R.id.setRL);
		layout = findViewById(R.id.user_bk);
		Them t1 = dbm.queryThem(imsi);
		if (t1 != null) {
			layout.setBackgroundResource(t1.getThem());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void addListener() {
		bought.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ArticleListActivity.class);
				intent.putExtra("pageIndex", 0);
				intent.putExtra("tp", 1);
				intent.putExtra("typeName",
						getResources().getString(R.string.userBought));
				UserCenterActivity.this.startActivity(intent);
			}
		});
		about.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = new CustomDialog(context, R.layout.about_dialog);
				dialog.show();
				dialog.setOnPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setOnNegativeListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		collection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, ArticleListActivity.class);
				intent.putExtra("pageIndex", 0);
				intent.putExtra("tp", 2);
				intent.putExtra("typeName",
						getResources().getString(R.string.userCollection));
				UserCenterActivity.this.startActivity(intent);
			}
		});
		vipStore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						VipStoreFragmentActivity.class);
				startActivity(intent);
			}
		});
		set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SetBakActivity.class);
				startActivity(intent);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	protected void onResumeFragments() {
		// TODO Auto-generated method stub
		super.onResumeFragments();
		Them t1 = dbm.queryThem(imsi);
		if (t1 != null) {
			layout.setBackgroundResource(t1.getThem());
		}
	}

}

package com.example.superclassroom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.db.DBManager;
import com.example.moudle.Them;
import com.example.moudle.User;
import com.example.superclassroom.R.color;
import com.example.superclassroom.VipStoreDict.VipStoreInfo;
import com.example.tools.Pay;
import com.example.tools.PhoneUtils;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabActivity extends TabActivity {

	public static Context myMainActivity = null;
	public static Boolean flag=true;
	private TabHost tabHost;

	private FrameLayout layout1, layout2, layout3;

	private ImageView tab_type, tab_recommend, tab_usercenter;
	private TextView tab_type_text_click, tab_recommend_text,
			tab_usercenter_text;
	private DBManager dbm;
	private SimpleDateFormat sdf;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		myMainActivity = this;
		try {
			initView();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initView() throws ParseException {
		// TODO Auto-generated method stub
		Pay.init(this);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		dbm = new DBManager(myMainActivity);
		String imsi = PhoneUtils.getIMSI(myMainActivity);
		Them them=dbm.queryThem(imsi);
		if(them==null){
			Them t1=new Them(imsi, R.drawable.ybxg);
			dbm.addThem(t1);
		}
		List<User> list = dbm.queryUser(imsi);
		Date dt = new Date();
		String time = sdf.format(dt);
		User user = new User(imsi);
		if (list == null || "".equals(list) || list.size() == 0) {
			user.setMoney(1000);
			user.setGiftMoney(0);
			user.setRank(0);
			user.setTime(time);
			dbm.addUser(user);
		} else if (list.get(0).getRank() != 0) {
			User u = list.get(0);
			int id = u.getRank();
			int money = u.getGiftMoney();
			VipStoreInfo info = VipStoreDict.getInstance().getVipStoreInfo(id);
			Date stime = sdf.parse(u.getTime());
			long intervalMilli = dt.getTime() - stime.getTime();
			int day = (int) (intervalMilli / (24 * 60 * 60 * 1000));
			if(money < info.countGoldNum){
				flag=false;
			}
			if (day > 1 && money < info.countGoldNum) {
				user.setMoney(u.getMoney() + info.goldNum);
				user.setGiftMoney(u.getGiftMoney() + info.goldNum);
				user.setTime(sdf.format(dt));
				user.setRank(id);
				dbm.updateUser(user);
				AlertDialog alertDialog = new AlertDialog.Builder(this)
						.setTitle("温馨提示")
						.setMessage("今日已赠送" + info.goldNum + "金币")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}

								}).create(); // 创建对话框
				alertDialog.show();
			}
		}
		tabHost = getTabHost();
		Intent intent1 = new Intent();
		intent1.setClass(MainTabActivity.this, ArticleTypeFragmentList.class);
		tabHost.addTab(tabHost.newTabSpec("1").setIndicator("1")
				.setContent(intent1));
		Intent intent2 = new Intent();
		intent2.setClass(MainTabActivity.this, RecommendFragmentList.class);
		tabHost.addTab(tabHost.newTabSpec("2").setIndicator("2")
				.setContent(intent2));

		Intent intent3 = new Intent();
		intent3.setClass(MainTabActivity.this, UserCenterActivity.class);
		tabHost.addTab(tabHost.newTabSpec("3").setIndicator("3")
				.setContent(intent3));

		layout1 = (FrameLayout) findViewById(R.id.frame_type);
		layout2 = (FrameLayout) findViewById(R.id.frame_recommend);
		layout3 = (FrameLayout) findViewById(R.id.frame_usercenter);

		layout1.setOnClickListener(l);
		layout2.setOnClickListener(l);
		layout3.setOnClickListener(l);

		tab_type = (ImageView) findViewById(R.id.tab_type_click);
		tab_recommend = (ImageView) findViewById(R.id.tab_recommend);
		tab_usercenter = (ImageView) findViewById(R.id.tab_usercenter);

		tab_type_text_click = (TextView) findViewById(R.id.tab_type_text);
		tab_recommend_text = (TextView) findViewById(R.id.tab_recommend_text);
		tab_usercenter_text = (TextView) findViewById(R.id.tab_usercenter_text);
	}

	private void dialog() {

	}

	OnClickListener l = new OnClickListener() {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0 == layout1) {
				tabHost.setCurrentTabByTag("1");

				tab_type.setImageResource(R.drawable.tab_type_click);
				tab_recommend.setImageResource(R.drawable.tab_recommend);
				tab_usercenter.setImageResource(R.drawable.tab_usercenter);

				tab_type_text_click.setTextColor((MainTabActivity.this
						.getResources().getColor(color.orange)));
				tab_recommend_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
				tab_usercenter_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
			} else if (arg0 == layout2) {
				tabHost.setCurrentTabByTag("2");

				tab_type.setImageResource(R.drawable.tab_type);
				tab_recommend.setImageResource(R.drawable.tab_recommend_click);
				tab_usercenter.setImageResource(R.drawable.tab_usercenter);

				tab_type_text_click.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
				tab_recommend_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.orange)));
				tab_usercenter_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
			} else if (arg0 == layout3) {
				tabHost.setCurrentTabByTag("3");

				tab_type.setImageResource(R.drawable.tab_type);
				tab_recommend.setImageResource(R.drawable.tab_recommend);
				tab_usercenter
						.setImageResource(R.drawable.tab_usercenter_click);

				tab_type_text_click.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
				tab_recommend_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.lightgray)));
				tab_usercenter_text.setTextColor((MainTabActivity.this
						.getResources().getColor(color.orange)));
			}

		}
	};


}
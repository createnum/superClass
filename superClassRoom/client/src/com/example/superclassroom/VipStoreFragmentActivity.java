package com.example.superclassroom;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.db.DBManager;
import com.example.db.MyData;
import com.example.moudle.CustomDialog;
import com.example.moudle.User;
import com.example.superclassroom.R.color;
import com.example.superclassroom.VipStoreDict.VipStoreInfo;
import com.example.tools.HttpRequest;
import com.example.tools.Pay;
import com.example.tools.PhoneUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VipStoreFragmentActivity extends FragmentActivity implements
		OnRefreshListener<ListView> {
	private ItemAdapter mAdapter;
	protected GridView mPullRefreshGridView;
	public Context context;
	private List<VipStoreInfo> vipList;
	private Button negativeButton;
	private static CustomDialog dialog;
	private static DBManager dbm;
	private View mView;
	private EditText editText;
	// 实现Handler
	private static Handler handler;
	// 消息
	protected static Message msg;
	protected static String pid;
	protected String app_special;
	protected String imsi;
	protected String imei;
	protected static String orderid;
	protected static String code;
	protected com.example.superclassroom.VipStoreFragmentActivity.ItemAdapter.ViewHolder holder1;
	public VipStoreInfo info1;
	public static Boolean flag;
	private VipStoreFragmentActivity vf;
	public static int id1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		negativeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mPullRefreshGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

	}

	private void initData() {
		// TODO Auto-generated method stub
		context = MainTabActivity.myMainActivity;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vip_store_dialog);
		dbm = new DBManager(context);
		mView = LayoutInflater.from(context).inflate(R.layout.vip_buy_dialog,
				null);
		vipList = VipStoreDict.getInstance().vipStoreList;
		flag = isBuyVip(vipList, PhoneUtils.getIMSI(context), dbm);
		mPullRefreshGridView = (GridView) findViewById(R.id.gview);
		negativeButton = (Button) findViewById(R.id.negativeButton);
		mAdapter = new ItemAdapter();
		mPullRefreshGridView.setAdapter(mAdapter);
		imsi = PhoneUtils.getIMSI(context);
		imei = PhoneUtils.getIMEI(context);
		app_special = getResources().getString(R.string.zcweb_appid);
		pid = getResources().getString(R.string.zcweb_pid);
		handler = new MsgHandler(VipStoreFragmentActivity.this);
		flag = MainTabActivity.flag;
	}

	class ItemAdapter extends BaseAdapter {

		private class ViewHolder {
			public TextView name;
			public TextView glod;
			public TextView money;
			private Button buy;
		}

		@Override
		public int getCount() {
			return vipList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final VipStoreInfo info = vipList.get(position);
			ViewHolder holder;
			if (convertView == null) {

				view = ((Activity) context).getLayoutInflater().inflate(
						R.layout.item_vip_store, parent, false);

				view = LayoutInflater.from(context).inflate(
						R.layout.item_vip_store, null);
				holder = new ViewHolder();
				holder.name = (TextView) view.findViewById(R.id.vipName);
				holder.glod = (TextView) view.findViewById(R.id.vipGoldd);
				holder.money = (TextView) view.findViewById(R.id.vipMoney);
				holder.buy = (Button) view.findViewById(R.id.buyVip);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.name.setText(vipList.get(position).name);
			holder.glod.setText(String.valueOf(vipList.get(position).goldNum));
			holder.money.setText(String.valueOf(vipList.get(position).money)
					+ "元/月");
			holder1 = holder;
			info1 = info;
			id1 = info.id;
			// view.setBackgroundColor(VipStoreFragmentActivity.this.getResources().getColor(TYPE_COLOR[position]));

			// Boolean flag1 = isBuyVip(info.id, PhoneUtils.getIMSI(context),
			// dbm);
			// Pay.findByTel();
			if (!flag) {
				holder1.buy.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ykt_btn));
			}
			// 判读按钮是否可以点击
			if (flag) {
				holder1.buy.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog alertDialog = new AlertDialog.Builder(
								VipStoreFragmentActivity.this)
								.setTitle("确认订购:")
								.setMessage(
										info.name + "\n资费:" + info.money
												+ "元/月")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog1,
													int which) {

												dialog = new CustomDialog(
														VipStoreFragmentActivity.this,
														R.layout.vip_buy_dialog);
												dialog.tank(info);
												getYzm(info.money);
												dialog.setOnPositiveListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														editText = dialog
																.getEditText();
														if ("".equals(editText
																.getText()
																.toString())) {
															Toast.makeText(
																	context,
																	"验证码不能为空",
																	Toast.LENGTH_LONG)
																	.show();
														} else {
															code = editText
																	.getText()
																	.toString();
															buyVipBy(
																	info.id,
																	PhoneUtils
																			.getIMSI(context),
																	dbm);
															dialog.dismiss();
															finish();
														}

													}
												});
												dialog.setOnNegativeListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														dialog.dismiss();
													}
												});
												/*final CustomDialog dialog11 = new CustomDialog(
														VipStoreFragmentActivity.this,
														R.layout.custom_dialog);
												dialog11.show();
												dialog11.setOnPositiveListener(new OnClickListener() {
													@Override
													public void onClick(View v) {
														editText = dialog11
																.getEditText();
														String phoneNum = editText
																.getText()
																.toString();
												//														int i = Pay.pay(info,
												//																phoneNum);
														int i=0;
														if (i == 0) {
															dialog = new CustomDialog(
																	VipStoreFragmentActivity.this,
																	R.layout.vip_buy_dialog);
															dialog.tank(info);
															getYzm(info.money,phoneNum);
															editText = dialog
																	.getEditText();
															code= editText
																	.getText()
																	.toString();
															dialog.setOnPositiveListener(new OnClickListener() {

																@Override
																public void onClick(
																		View v) {
																	// TODO Auto-generated method stub
																	buyVipBy(
																			info.id,
																			PhoneUtils
																					.getIMSI(context),
																			dbm,code);
																	dialog.dismiss();
																}
																
															});
															
															addMoney(imsi);
														}
														dialog11.dismiss();
													}
												});
												dialog11.setOnNegativeListener(new OnClickListener() {
													@Override
													public void onClick(View v) {
														dialog11.dismiss();
													}
												});*/
											}

										}).setNegativeButton("取消",

								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).create(); // 创建对话框
						alertDialog.show(); // 显示对话框

					}
				});
			}
			// 判读是否开通
			return view;
		}

	}

	public static void pay(String imsi) {
		// buyVipBy(id1, imsi, dbm);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	public static void buyVipBy(final int id, final String imsi, DBManager dbm) {
		String res = "";
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyMMddHHmm");
		final String paramData = "time=" + time.format(nowTime) + "&pid=" + pid
				+ "&orderid=" + orderid + "&verifycode=" + code;

		new Thread() {
			public void run() {
				final String gainCode = HttpRequest.sendGet(
						MyData.VERIFICATION_IDENTIFYINGCODE_URL, paramData);
				String resultCode;
				JSONObject dataJson;
				String orderid;
				try {
					dataJson = new JSONObject(gainCode);
					resultCode = dataJson.getString("resultCode");
					orderid = dataJson.getString("orderid");
					if ("200000".equals(resultCode)) {
						Bundle bundle = new Bundle();
						bundle.putString("imsi", imsi);
						bundle.putString("id", id+"");
						msg.setData(bundle);
						msg = handler.obtainMessage();
						msg.arg1 = 2;
						handler.sendMessage(msg);
					} else {
						msg = handler.obtainMessage();
						msg.arg1 = 3;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

		}.start();

		try {

		} catch (Exception e) {

		}

		/*List<User> users = dbm.queryUser(imsi);
		User u = users.get(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		User user = new User(imsi);
		user.setRank(id);
		user.setMoney(u.getMoney());
		user.setTime(sdf.format(new Date()));
		user.setGiftMoney(0);
		dbm.updateUser(user);
		MainTabActivity.flag = flag = false;*/
	}

	public static Boolean isBuyVip(int id, String imsi, DBManager dbm) {
		/*
		 * List<User> users = dbm.queryUser(imsi); for (User user : users) { int
		 * id1 = user.getRank(); if (id1 != 0 && id == id1) { return false; } }
		 */
		return true;
	}

	public static Boolean isBuyVip(List<VipStoreInfo> vipList, String imsi,
			DBManager dbm) {

		List<Integer> list = new ArrayList<Integer>();
		for (VipStoreInfo info : vipList) {
			list.add(info.id);
		}
		List<User> users = dbm.queryUser(imsi);
		for (User user : users) {
			int id1 = user.getRank();
			if (id1 != 0 && list.contains(id1)) {
				return false;
			}
		}
		return true;
	}

	private void getYzm(int money) {
		// TODO Auto-generated method stub
		String res = "";
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyMMddHHmm");
		String tel = PhoneUtils.getPhoneNumber(context);
		final String paramData = "imsi=" + imsi + "&imei=" + imei + "&tel="
				+ tel + "&pid=" + pid + "&money=" + money + "&app_special="
				+ app_special + "&time=" + time.format(nowTime);
		String resultCode;

		new Thread() {
			public void run() {
				final String gainCode = HttpRequest.sendGet(
						MyData.GAIN_IDENTIFYINGCODE_URL, paramData);
				String resultCode;
				JSONObject dataJson;
				String orderid;
				try {
					dataJson = new JSONObject(gainCode);
					resultCode = dataJson.getString("resultCode");
					orderid = dataJson.getString("orderid");
					if ("200000".equals(resultCode)) {
						msg = handler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putString("orderid", orderid);
						msg.setData(bundle);
						msg.arg1 = 0;
						handler.sendMessage(msg);
					} else {
						msg = handler.obtainMessage();
						msg.arg1 = 1;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();

	}

	class MsgHandler extends Handler {
		private Activity activity;

		public MsgHandler(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
			case 0:
				dialog.show();
				Bundle bundle = msg.getData();
				orderid = bundle.getString("orderid");
				break;
			case 1:
				showInfo("获取验证码失败！");
				break;
			case 2:
				addMoney(imsi);
				showInfo("订购成功！");
				break;
			case 3:
				showInfo("提交验证码错误，请重新测试！");
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

		public void showInfo(String info) {
			Toast.makeText(activity.getApplicationContext(), info,
					Toast.LENGTH_SHORT).show();
		}
	}

	public void addMoney(String imsi) {
		List<User> list = dbm.queryUser(imsi);
		User user = new User(imsi);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = new Date();
//		if (list.get(0).getRank() != 0) {
			MainTabActivity.flag = false;
			User u = list.get(0);
			int id = u.getRank();
			int money = u.getGiftMoney();
			VipStoreInfo info = VipStoreDict.getInstance().getVipStoreInfo(id);
			user.setMoney(u.getMoney() + info.goldNum);
			user.setGiftMoney(u.getGiftMoney() + info.goldNum);
			user.setTime(sdf.format(dt));
			user.setRank(id1);
			dbm.updateUser(user);
//		}
	}

}

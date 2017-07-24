package com.example.tools;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.db.MyData;
import com.example.moudle.CustomDialog;
import com.example.superclassroom.R;
import com.example.superclassroom.VipStoreDict.VipStoreInfo;
import com.example.superclassroom.VipStoreFragmentActivity;
import com.ffcs.inapppaylib.PayHelper;
import com.ffcs.inapppaylib.bean.Constants;
import com.ffcs.inapppaylib.bean.response.BaseResponse;

public class Pay {
	private static String appid;
	private static String productid;
	private static String appsecret;
	private static Context context;

	private static String zc_appid;
	private static String pid;
	private static String imsi;
	private static int money = 10;
	public static String res;
	private static EditText editText;
	private static String phoneNum;
	public static Boolean flag;
	public static String accessNo;
	public static SmsTools sms;
	/* 自定义ACTION常数，作为广播的Intent Filter识别常数 */
	public static String SMS_SEND_ACTIOIN = "SMS_SEND_ACTIOIN";
	public static String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	
	public static void init(Context ct) {
		context = ct;
		appid = context.getString(R.string.appid);
		productid = context.getString(R.string.productid);
		appsecret = context.getString(R.string.appsecret);
		zc_appid = context.getString(R.string.zc_appid);
		pid = context.getString(R.string.pid);
		accessNo = context.getString(R.string.accessNo);
		sms=new SmsTools();
		sms.init(ct);
//		findByTel(null);
	}

	public static int pay(final VipStoreInfo info,String phoneNum) {
					 if (phoneNum.matches("[1][358]\\d{9}")) {
							PayHelper payHelper = PayHelper.getInstance(context);
							payHelper.init(appid, appsecret);
							Message message = new Message();
							Bundle bundle = new Bundle();
							bundle.putString("tel", phoneNum);
							message.setData(bundle);//bundle传值，耗时，效率低  
				            handler.sendMessage(message);//发送message信息 
							payHelper.pay((Activity) context, productid, phoneNum, handler,"hkajsd");
						/* String orderid="Z"+getRandomValue(7);
						 String msg=info.productid+"*"+info.consumecode+orderid;
						 SmsManager smsManager = SmsManager.getDefault();
						 try
				         {
				            建立自定义Action常数的Intent(给PendingIntent参数之用) 
				           Intent itSend = new Intent(SMS_SEND_ACTIOIN);
				           Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
				           
				            sentIntent参数为传送后接受的广播信息PendingIntent 
				           PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0, itSend, 0);
				           
				            deliveryIntent参数为送达后接受的广播信息PendingIntent 
				           PendingIntent mDeliverPI = PendingIntent.getBroadcast(context, 0, itDeliver, 0);
				           
				            发送SMS短信，注意倒数的两个PendingIntent参数 
				           smsManager.sendTextMessage(accessNo, null, msg, mSendPI, mDeliverPI);
				           return 0;
				           
				         }
				         catch(Exception e)
				         {
				           e.printStackTrace();
				           return 1;
				         }*/
							return 0;
						}else{
							 return 1;
						}
	}

	static Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			BaseResponse resp = null;

			switch (msg.what) {
			// case Constants.RESULT_NO_CT:
			// //非电信用户
			// Toast.makeText(context, "仅限电信用户",
			// Toast.LENGTH_LONG).show();
			// break;
			case Constants.RESULT_VALIDATE_FAILURE:
				// 合法性验证失败
				resp = (BaseResponse) msg.obj;
				Toast.makeText(context,
						resp.getRes_code() + ":" + resp.getRes_message(),
						Toast.LENGTH_SHORT).show();
				break;

			case Constants.RESULT_PAY_SUCCESS:
				// 支付成功
				resp = (BaseResponse) msg.obj;
				Toast.makeText(context,
						resp.getRes_code() + ":" + resp.getRes_message(),
						Toast.LENGTH_SHORT).show();
				String tel = msg.getData().getString("tel");
				savePay(tel);
				break;

			case Constants.RESULT_PAY_FAILURE:
				// 支付失败
				resp = (BaseResponse) msg.obj;
				Toast.makeText(context,
						resp.getRes_code() + ":" + resp.getRes_message(),
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}

		}

		private void savePay(String tel) {
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi=tm.getSubscriberId();
			String params = "appid="+zc_appid
        			+"&pid="+pid+"&tel="+tel+"&imsi="+imsi+"&money="+money;
			HttpMessageThread httpMessageThread=new HttpMessageThread(context, MyData.FIND, MyData.PAY_CTBY_URL,
					params,null);
				httpMessageThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		}

	};

	public static void findByTel(VipStoreFragmentActivity vf) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi=tm.getSubscriberId();
		final String params = "imsi="+imsi+"&appid="+zc_appid;
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				res = HttpRequest.postHttpJsonByurlconnection(MyData.GET_CTBY_URL,
						params);
				if(res.equals("ok")){
					flag=false;
				}else{
					 flag=true;
				}
			}
		}).start();*/
		HttpMessageThread httpMessageThread=new HttpMessageThread(context, MyData.FIND, MyData.GET_ZXBY_URL,
						params,vf);
		httpMessageThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		
	}
	
	/*产生numSize位16进制的数*/
	public static String getRandomValue(int numSize) {
	String str = "";
	        for (int i = 0; i < numSize; i++) {
	        char temp = 0;
	        int key = (int) (Math.random() * 2);
	        switch (key) {
	    case 0:
	    temp = (char) (Math.random() * 10 + 48);//产生随机数字
	    break;
	    case 1:
	    temp = (char) (Math.random()*6 + 'a');//产生a-f
	    break;
	    default:
	    break;
	    }
	        str = str + temp;
	        }
	        return str;
	}
}

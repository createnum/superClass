package com.zancheng.callphonevideoshow.http.listenphoneend;


import com.likebamboo.phoneshow.widget.CallUIFactory;
import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

@SuppressLint("NewApi")
public class HttpMessageThreadPhoneState extends AsyncTask<Void, Void, Void> {
	
	public final static int SEND_PHONE_STATE = 0;
	public final static int GET_PHONE_STATE = 1;
	
	public final static String SEND_PHONE_ANSWER_URL = "http://182.92.149.179/dong/phoneState/sendPhoneState.php";
	public final static String GET_PHONE_STATE_URL = "http://182.92.149.179/dong/phoneState/getPhoneState.php";
	private static final int QUEST_PHONE_STATE_MAX_COUNT = 150;
	private static final int SEND_PHONE_ANSWER_QUEST_MAX_COUNT = 150;
	private static final long QUEST_INTERVAL = 600;//网络请求间隔
	public int messageType;
	public String params;
	public String url;
	private Context context;
	
	public static HttpMessageThreadPhoneState getPhoneState(Context context, String myPhoneNumber, String phoneNumber) {  
		String params = "myPhoneNumber="+myPhoneNumber+"&linkmenPhoneNumber="+phoneNumber;
		HttpMessageThreadPhoneState http = new HttpMessageThreadPhoneState(context, HttpMessageThreadPhoneState.GET_PHONE_STATE,
				HttpMessageThreadPhoneState.GET_PHONE_STATE_URL, params);
		http.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		return http;
	} 
	
	public static HttpMessageThreadPhoneState sendPhoneState(Context context, String myPhoneNum, String phoneNumber) {
		// TODO Auto-generated method stub
		String params = "myPhoneNumber="+myPhoneNum+"&linkmenPhoneNumber="+phoneNumber+"&phoneState="+MyAppData.callState;
		HttpMessageThreadPhoneState http = new HttpMessageThreadPhoneState(context, HttpMessageThreadPhoneState.SEND_PHONE_STATE,
				HttpMessageThreadPhoneState.SEND_PHONE_ANSWER_URL, params);
		http.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		return http;
	}
	 
	public HttpMessageThreadPhoneState(Context context, int messageType, String url, String params) {
		this.context = context;
		this.messageType = messageType;
		this.url = url;
		this.params = params;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int questCount = 0;
		boolean finish = false;
		while(true){
			if(isCancelled()){
				break;
			}
			try {
				Thread.sleep(QUEST_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			questCount++;
			
			String reciveStr = Tool.postHttpJsonByurlconnection(this.url, this.params);
			if(reciveStr == null){
				continue;
			}
			switch (messageType) {
			case SEND_PHONE_STATE:
				if(reciveStr.equals(String.valueOf(Constants.CS_ANSWER)) || questCount >= SEND_PHONE_ANSWER_QUEST_MAX_COUNT){
					finish = true;
				}
				break;
			case GET_PHONE_STATE:
				if(reciveStr.equals(String.valueOf(Constants.CS_ANSWER)) || questCount >= QUEST_PHONE_STATE_MAX_COUNT){
					CallUIFactory.hide(context);
					finish = true;
				}
				break;
			default:
				break;
			}
			if(finish){
				break;
			}
			
		}
		return null;
		
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
	}

	
}

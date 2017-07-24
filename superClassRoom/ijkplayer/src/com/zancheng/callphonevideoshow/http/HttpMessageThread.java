package com.zancheng.callphonevideoshow.http;

import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.content.Context;
import android.os.AsyncTask;

class HttpMessageThread extends AsyncTask<Void, Void, Void> {
	
	public int messageType;
	public String reciveStr;
	public String params;
	public String url;
	 
	HttpMessageThread(Context context, int messageType, String getShowVideoUrl, String params) {
		this.messageType = messageType;
		this.url = getShowVideoUrl;
		this.params = params;
	}

	@Override
	protected Void doInBackground(Void... params) {
        send();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(reciveStr == null){
//			Toast.makeText(context, "网络异常！", Toast.LENGTH_SHORT).show();
//			reciveStr = ShowPref.getMainInstance(context).loadString(ShowPref.JSON);
			return;
		}
		reciveStr = reciveStr.trim();
//		ShowPref.getMainInstance(context).putString(ShowPref.JSON, reciveStr);
		switch (messageType) {
		case NetworkManager.USER_INFO:
			MyAppData.getInst().responseGetUserInfo(reciveStr);
			break;
		case NetworkManager.UPDATE_USER_INFO:
		case NetworkManager.VIDEO_URL_BY_ID_BY_DIRECT:
			MyAppData.getInst().dataUpdateComplete(messageType, reciveStr);
			break;
		default:
			break;
		}
		
	}
	
	private void send() {
	    long startTime = System.currentTimeMillis();
		reciveStr = Tool.postHttpJsonByurlconnection(url, params);
		System.out.println(" http connet time :"+(System.currentTimeMillis() - startTime));
	}
	
}

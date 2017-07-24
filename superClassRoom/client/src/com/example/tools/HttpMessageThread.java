package com.example.tools;


import java.util.List;

import org.apache.http.NameValuePair;

import com.example.db.MyData;
import com.example.superclassroom.MyAppData;
import com.example.superclassroom.VipStoreFragmentActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class HttpMessageThread extends AsyncTask<Void, Void, Void> {
	
	public int messageType;
	public String reciveStr;
	public String params;
	public String url;
	public Context context;
	public VipStoreFragmentActivity vf; 
	
	HttpMessageThread(Context context, int messageType, String getShowVideoUrl, String params,VipStoreFragmentActivity vf) {
		if(null != context){
			this.context = context;
		}
		this.messageType = messageType;
		this.url = getShowVideoUrl;
		this.params = params;
		this.vf=vf;
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
//			Toast.makeText(context, "网络异常�?", Toast.LENGTH_SHORT).show();
			return;
		}
		reciveStr = reciveStr.trim();
		switch (messageType) {
		
		case MyData.FIND:
			MyAppData.getInst().datapdata(messageType,reciveStr,vf);
			break;
		default:
			break;
		}
		
	}
	
	private void send() {
		reciveStr = HttpRequest.postHttpJsonByurlconnection(url, params);
	}
	
}

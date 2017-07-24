package com.zancheng.callphonevideoshow.http;


import java.util.List;




import com.likebamboo.phoneshow.widget.CallUIFactory;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class HttpMessageThreadGetVideoUrl extends AsyncTask<Void, Void, Void> {

	protected int messageType;
	protected String reciveStr;
	protected String params;
	protected String url;
	protected Context context;
	protected String number;

	protected HttpMessageThreadGetVideoUrl() {  
	       super();  
	}  
	 
	protected HttpMessageThreadGetVideoUrl(Context context, int messageType, String getShowVideoUrl, String params, String number) {
		this.messageType = messageType;
		this.url = getShowVideoUrl;
		this.params = params;
		this.context = context;
		this.number = number;
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
			Toast.makeText(context, "网络异常！", Toast.LENGTH_SHORT).show();
			return;
		}
		CallUIFactory.dataloadingComplete(context, messageType, reciveStr, number);
	}
	
	private void send() {
		reciveStr = Tool.postHttpJsonByurlconnection(url, params).trim();
	}
}

package com.zancheng.callphonevideoshow.http;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.show.commonShow.HttpFragmentActivity;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.content.Context;
import android.os.AsyncTask;

class HttpMessageThreadLoadVideoList extends AsyncTask<Void, Void, Void> {
	private String UPDATE_TAG_MESSAGE = "needUpdate";
	private HttpFragmentActivity httpFragmentActivity;
	private boolean needUpdate = true;
	private String parames;
	private int lastVideoServerId;
	private String messageKey;
	private String url;
	private boolean resultSuccess;
	private int videoType;
	
	protected HttpMessageThreadLoadVideoList(final Context context, String url, String parames, String messageKey, String lastVideoServerId, int videoType) {  
	       super();  
	       this.url = url;
	       this.videoType  = videoType;
	       if(null != context){
	    	   this.httpFragmentActivity = (HttpFragmentActivity)context;
			}
	       
	       this.parames = parames;
	       this.messageKey = messageKey;
	       this.lastVideoServerId = Integer.valueOf(lastVideoServerId);
	}  
	 
	
	@Override
	protected Void doInBackground(Void... params) {
        
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		resultSuccess = loadVideoData();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(!resultSuccess){
			return;
		}
		if(null != httpFragmentActivity){
			httpFragmentActivity.dataloadingComplete(needUpdate);
		}
	}
	
	private boolean loadVideoData() {
        String json = Tool.postHttpJsonByurlconnection(url, parames);
		if(json == null){
			return false;
		}
    	JSONArray jsonArray = null;
		try {
			JSONObject jsonObject1 =new JSONObject(json);
           if(null != httpFragmentActivity && lastVideoServerId == 0){
	           	httpFragmentActivity.videoList.clear();
	   		}
			jsonArray = new JSONArray(jsonObject1.getString(messageKey));
			for (int i=0;i<jsonArray.length();i++)
	         {
	            JSONObject jsonObject = (JSONObject)jsonArray.opt(i); 
	            VideoInfo videoInfo = new VideoInfo();
	            videoInfo.id = jsonObject.getInt("id");
	            videoInfo.author = jsonObject.getString("author");
	            String picPath = jsonObject.getString("imgPath");
	            if(Tool.isHttpPath(picPath)){
	            	videoInfo.imgPath = picPath;
	            }else{
	            	 videoInfo.imgPath = Constants.SERVER_IP_URL + picPath;
	            }
	            videoInfo.imgType = jsonObject.getInt("imgType");
	            videoInfo.title = jsonObject.getString("title");
	            String videoPath = jsonObject.getString("videoPath");
	            if(Tool.isHttpPath(videoPath)){
	            	videoInfo.videoPath = videoPath;
	            }else{
	            	 videoInfo.videoPath = Constants.SERVER_IP_URL + videoPath;
	            }
	           
	           
	            videoInfo.videoTypeBySpecial = jsonObject.getInt("videoTypeByspecial");
	            videoInfo.videoTypeMain = jsonObject.getInt("videoTypeMain");
	            videoInfo.marvellous = jsonObject.getInt("marvellous")==0?false:true;
	            videoInfo.playCount = jsonObject.getInt("playCount");
//	            videoInfo.price = jsonObject.getInt("price");  //*****hu
	            videoInfo.onlyLook = jsonObject.getInt("onlyLook")==0?false:true;
	            if(null != httpFragmentActivity){
	            	httpFragmentActivity.videoList.add(videoInfo);
	    		}
	         }
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

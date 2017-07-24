package com.zancheng.callphonevideoshow.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

public class NetworkManager {
	public static enum netType {
		NT_NONE,
		NT_WIFI,
		NT_MOBILE,
		NT_ANY,
	}
	
	public final static int USER_INFO = 0;
	public final static int UPDATE_USER_INFO = USER_INFO+1;
	public final static int DELETE_DIY_VIDEO = UPDATE_USER_INFO+1;
	public final static int VIDEO_URL_BY_ID_BY_DIRECT = DELETE_DIY_VIDEO+1;
	public final static int VIDEO_URL_BY_ID_BY_CALL = VIDEO_URL_BY_ID_BY_DIRECT+1;
	public final static int VIDEO_URL_BY_LINKMEN = VIDEO_URL_BY_ID_BY_CALL+1;
    public final static int SPECIAL_VIDEO = VIDEO_URL_BY_LINKMEN+1;//活动专题视频列表请求
	public final static int COMMON_VIDEO_LIST = SPECIAL_VIDEO+1;
	
	//更新用户信息数据交互用到的数据
	public static final String BIND_PHONE = "bindPhone";
	public static final String BUY = "buy";
	public static final String PAY_SUCCESS = "success";
	public static final String UPDATE_CARD = "updateCard";
	public static final String BP_SYNC_USER_DATA = "syncUserData";
	public static final String BP_PHONE_NUM = "phoneNum";
	
	public static String jointParamsOfLoadVideoList(String messageKey, int videoTypeByspecial, int lastVideoServerId){
		return jointParamsOfLoadVideoList(-2, videoTypeByspecial, false, false, messageKey, lastVideoServerId, null);
	}
	
	public static String jointParamsOfLoadVideoList(int videoTypeMain, int videoTypeByspecial, boolean marvellous, boolean ranking, String messageKey, int lastVideoServerId, String parameter1){
        String str = "videoTypeMain="+videoTypeMain
        		+ "&videoTypeByspecial="+videoTypeByspecial
        		+ "&marvellous="+String.valueOf(marvellous)
        		+ "&ranking="+String.valueOf(ranking)
        		+ "&messageKey="+messageKey
        		+ "&lastVideoServerId="+lastVideoServerId
        		+ "&parameter1="+parameter1;
		return str;
	}
	
	public static void getData( int messageType, String url, String params) {
		getData(null, messageType, url, params, null, null, -1);
	}
	public static void getData(Context context, int messageType, String url, String params, String extParameter1) {
		getData(context, messageType, url, params, extParameter1, null, -1);
	}
	public static void getData(Context context, int messageType, String url, String params, String extParameter1, String extParameter2) {
		getData(context, messageType, url, params, extParameter1, extParameter2, -1);
	}

	
	@SuppressLint("NewApi")
	public static void getData(Context context,  int messageType, String url, String params, String extParameter1, String extParameter2, int videoType) {
		switch (messageType) {
		//用户基本信息
		case USER_INFO:
		case DELETE_DIY_VIDEO:
		case UPDATE_USER_INFO:
		case VIDEO_URL_BY_ID_BY_DIRECT:
		{
			HttpMessageThread asyncTask = new HttpMessageThread(context, messageType, url, params);  
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
	        break;
		}
		case VIDEO_URL_BY_LINKMEN:
		{
			HttpMessageThreadGetVideoUrl asyncTask = new HttpMessageThreadGetVideoUrl(context,
					messageType, url, params, extParameter1);  
	        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
	        break;
		}
		case SPECIAL_VIDEO:
		case COMMON_VIDEO_LIST:
		{
			HttpMessageThreadLoadVideoList asyncTask = new HttpMessageThreadLoadVideoList(context, url, params, extParameter1, extParameter2, videoType);  
	        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
	        break;
		}
		default:
			break;
		}
	}
	
}

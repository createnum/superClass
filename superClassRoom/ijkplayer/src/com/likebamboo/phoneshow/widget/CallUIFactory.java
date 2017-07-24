package com.likebamboo.phoneshow.widget;
import java.io.File;

import com.likebamboo.phoneshow.config.ShowPref;
import com.likebamboo.phoneshow.util.Utils;
import com.likebamboo.phoneshow.widget.overlayActivity.OverLayActivity;
import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.SDKMain;
import com.zancheng.callphonevideoshow.http.NetworkManager;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

@SuppressLint("NewApi")
public class CallUIFactory {
	public static int SHOW_NORMAL = 0;
	public static int SHOW_DEMO = 1;
	public static UIType cur_UiAnswerMode = UIType.CALL_LR_SLIDE;
	public static boolean callUiAlreadyOpen = false;
	
    public static enum UIType{
        CALL_CLICK,
        CALL_GLOWPAD,
        CALL_LR_SLIDE,
        CALL_UP_ANSWER,
    } 
    
    /*
     * uiCreateType 通话窗口创建方式  activity 或 悬浮窗
     * uiUseTarget  demo或者正式通话
     * uiAnswerMode 通话接听方式
     */
    public static void showByDirect(int uiUseTarget, Context activity, String number, String videoPath){
    	//防止手机通话完毕后，因为网络的问题而展现出视频播放界面
        if(uiUseTarget == SHOW_NORMAL){
            if(MyAppData.callState == Constants.CS_DEFAULT){
                return;
            }
            if(callUiAlreadyOpen){
                return;
            }else{
                callUiAlreadyOpen = true;
            }
           
        }
    	
		String videoUrl = videoPath;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if(!sdCardExist && !Tool.isHttpPath(videoPath)){
			String videoName = Tool.getFileAllNameFromPath(videoPath);
			String sonPath = Tool.getPhoneSdCardSonPath(Constants.DOWNLOAD_PHONE_SDCARD_VIDEO_SON_PATH);
			if(sonPath == null){
				videoUrl = "android.resource://" + SDKMain.sdkContext.getPackageName() + "/" + SDKMain.sdkContext.getResources().getIdentifier("csx_sdk_zc_defaultring", "drawable", SDKMain.sdkContext.getPackageName());
			}else{
				String path = sonPath+videoName;
				File file = new File(path);
				if(file.exists() && 
						(MyAppData.getInst().isDownloadingVideoPath ==null 
						|| (MyAppData.getInst().isDownloadingVideoPath != null && !MyAppData.getInst().isDownloadingVideoPath.equals(path)))){
					videoUrl = path;
				}else{
					videoUrl = "android.resource://" + SDKMain.sdkContext.getPackageName() + "/" + SDKMain.sdkContext.getResources().getIdentifier("csx_sdk_zc_defaultring", "drawable", SDKMain.sdkContext.getPackageName());
				}
			}
			
		}else{
				//外置sd卡存在的话用外置sd卡中的视频
				String videoName = Tool.getFileAllNameFromPath(videoPath);
				String localExternalSDCardPath = Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH + videoName;
				File outfile = new File(localExternalSDCardPath);
				if(outfile.exists()){
					videoUrl = localExternalSDCardPath;
				}
				
				//内置sd卡存在的话用内置sd卡中的视频
				String sonPath = Tool.getPhoneSdCardSonPath(Constants.DOWNLOAD_PHONE_SDCARD_VIDEO_SON_PATH);
				if(sonPath != null){
					String localPhoneSDCardPath =  sonPath+ videoName;
					File infile = new File(localPhoneSDCardPath);
					if(infile.exists()){
						videoUrl = localPhoneSDCardPath;
					}
				}
				
		}
		
		final int uiCreateType = ShowPref.getMainInstance(activity).loadInt(ShowPref.SHOW_TYPE);
		if(uiCreateType == ShowPref.TYPE_ACTIVITY){
			Intent intent = new Intent(activity, OverLayActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        intent.putExtra(OverLayActivity.EXTRA_PHONE_NUM, number);
	        intent.putExtra(OverLayActivity.EXTRA_VIDEO_NAME, videoUrl);
	        intent.putExtra(OverLayActivity.UI_USE_TARGET, uiUseTarget);
	        if(MyAppData.callState == Constants.CS_CILCK_DIAL_BUTTON){
	        	intent.putExtra(OverLayActivity.IS_OUT_CALL, 1);
	        }
        	activity.startActivity(intent);
	        
		}else{


//			switch (cur_UiAnswerMode) {
//			case CALL_CLICK:
//				CallClickView.show(activity, uiUseTarget, number, videoUrl);
//				break;
//			case CALL_GLOWPAD:
//				CallGlowPadView.show(activity, uiUseTarget, number, videoUrl);
//				break;
//			case CALL_LR_SLIDE:
//				CallLRSlideView.show(activity, uiUseTarget, number, videoUrl);
//				break;
//			case CALL_UP_ANSWER:
//				CallUpView.show(activity, uiUseTarget, number, videoUrl);
//				break;
//			default:
//				break;
//			}
		}
    	
    }
    
    public static void hide(Context activity){
        CallUIFactory.callUiAlreadyOpen = false;
        
    	final int uiCreateType = ShowPref.getMainInstance(activity).loadInt(ShowPref.SHOW_TYPE);
    	if(uiCreateType == ShowPref.TYPE_ACTIVITY){
    		Utils.sendCloseCallUIBroadCast(activity);
		}else{
//	    	switch (cur_UiAnswerMode) {
//			case CALL_CLICK:
//				CallClickView.hide(activity);
//				break;
//			case CALL_GLOWPAD:
//				CallGlowPadView.hide(activity);
//				break;
//			case CALL_LR_SLIDE:
//				CallLRSlideView.hide(activity);
//				break;
//			case CALL_UP_ANSWER:
//				CallUpView.hide(activity);
//				break;
//			default:
//				break;
//			}
		}
    }
    
    
    public static void endCall(int uiUseTarget, Context context) {
    	if(uiUseTarget == SHOW_NORMAL){
    		Utils.endCall(context);
    	}else{
    		hide(context);
    	}
    	
    }

    public static void answerCall(int uiUseTarget, Context context) {
    	if(uiUseTarget == SHOW_NORMAL){
    		if (Utils.hasGingerbread()) {
                Utils.autoAnswerPhone(context);
            } else {
                Utils.answerRingingCall(context);
            }
    	}else{
    		hide(context);
    	}
    	 
    }
    
    public static void tryShowByHttp(int uiUseTarget, UIType uiAnswerMode, final Context context, final String myPhoneNum, final String number, String videoPath) {
    	if(videoPath == null){
    		videoPath = MyAppData.getDefaultVideoPath(context);
    	}
    	if(number.equals(Constants.WEIZHI_MESSAGE)){
    		showByDirect(SHOW_NORMAL, context, number, videoPath);
    		return;
		}
		if (Tool.isNetworkAvailable(context, "any")) {
			int messageType = -1;
			int videoId = 0;
			if(MyAppData.callState == Constants.CS_CILCK_DIAL_BUTTON){
				messageType = NetworkManager.VIDEO_URL_BY_LINKMEN;
			}else{
//		    		messageType = HttpMessageThreadGetVideoUrl.VIDEO_URL_BY_ID;
//		    		int linkmenCount = MyAppData.getInst().user.linkmensRing.size();
//					for(int i = 0; i<linkmenCount;i++){
//						if(number.equals(MyAppData.getInst().user.linkmensRing.get(i).mContactsNumber)){
//							videoId = MyAppData.getInst().user.linkmensRing.get(i).videoId;
//							break;
//						}
//					}
//					messageType = HttpMessageThreadGetVideoUrl.VIDEO_URL_BY_LINKMEN;
				//===========!dong====start========
				showByDirect(SHOW_NORMAL, context, number, videoPath);
				//===========!dong====end========
			}
			switch (messageType) {
			case NetworkManager.VIDEO_URL_BY_ID_BY_CALL:
			{
//				List<NameValuePair> params = new ArrayList<NameValuePair>(); 
//		        params.add(new BasicNameValuePair("id", String.valueOf(videoId))); 
//		        HttpMessageThreadGetVideoUrl asyncTask = new HttpMessageThreadGetVideoUrl(context,
//		        		HttpMessageThreadGetVideoUrl.VIDEO_URL_BY_ID_BY_CALL, Constants.GET_SHOW_VIDEO_URL, params, number);  
//		        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
////		        asyncTask.execute();  
//				break;
				
			}
			case NetworkManager.VIDEO_URL_BY_LINKMEN:
			{
		        String params = "myPhoneNum="+myPhoneNum+"&linkmenPhoneNum="+number+"&id=-1";
		        NetworkManager.getData(context, NetworkManager.VIDEO_URL_BY_LINKMEN, Constants.GET_SHOW_VIDEO_SIMPLE_INFO, params, number);
				break;
			}
			default:
				break;
			}
			
			//网络访问3秒后，调用本地资源播放
			final String localVideoPath = videoPath;
			new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showByDirect(SHOW_NORMAL, context, number, localVideoPath);
                }
            }, Constants.SHOW_NET_CARD_OUTTIME);
		}else{
			if(MyAppData.callState == Constants.CS_CILCK_DIAL_BUTTON){
				showByDirect(SHOW_NORMAL, context, number, videoPath);
			}else{
//	    		int linkmenCount = MyAppData.getInst().user.linkmensRing.size();
//	    		boolean isShowed = false;
//				for(int i = 0; i<linkmenCount;i++){
//					if(number.equals(MyAppData.getInst().user.linkmensRing.get(i).mContactsNumber)){
//						int videoServerId = MyAppData.getInst().user.linkmensRing.get(i).videoId;
//						VideoInfo videoInfo = MyAppData.getInst().mgr.getLocalVideoInfo(videoServerId);
//						if(videoInfo != null){
//							showByDirect(CallUIFactory.SHOW_NORMAL, CallUIFactory.cur_UiAnswerMode, context, number, videoInfo.videoPath);
//							isShowed = true;
//						}
//						break;
//					}
//				}
//				if(!isShowed){
					showByDirect(SHOW_NORMAL, context, number, videoPath);
//				}
			}
			
		}
	}
	
	public static void dataloadingComplete(Context context, int messageType, String str, String number) {
			switch (messageType) {
			case NetworkManager.VIDEO_URL_BY_ID_BY_CALL:
			case NetworkManager.VIDEO_URL_BY_LINKMEN:
			{
				String videoUrl;
				if(str == null || str.equals("")){
					videoUrl = MyAppData.getDefaultVideoPath(context);
				}else{
					if(Tool.isHttpPath(str)){
						videoUrl = str;
					}else{
						videoUrl = Constants.SERVER_IP_URL + str;
					}
//					MyAppData.getInst().needAutoDownVideoUrl = videoUrl;
				}
				showByDirect(SHOW_NORMAL, context, number, videoUrl);
				break;
			}
			default:
				break;
			}

	}
}

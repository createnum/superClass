package com.zancheng.callphonevideoshow.show.commonShow;

import com.likebamboo.phoneshow.phoneStateService;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.show.commonShow.loadVideos.CommonVideoActivityHasRecommend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Receiver extends BroadcastReceiver{  
	public static final String DOWNLOAD_FINISH_OF_VIDEO = "com.zc.video.downFinish";
	public static final String SET_FRIEND_VIDEO_SELECT_FINISH = "com.zc.setFriendVideo.selectFinish";
	public static final String SET_FRIEND_VIDEO_FINISH = "com.zc.setFriendVideo.finish";
	public static final String FRIST_LINKMEN_VIDEO_ID = "fristLinkmenVideoId";
	public static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED"; 
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if(arg1.getAction().equals(DOWNLOAD_FINISH_OF_VIDEO)){
			VideoInfo downloadFinishVideoInfo = MyAppData.getInst().downloadFinish();
//			if (MyAppData.getInst().list_download.isEmpty()) {
//				((MainActivity) MainActivity.context).removeTopDownText();
//			}
			if(CommonVideoActivityHasRecommend.context != null){
			    CommonVideoActivityHasRecommend.context.downLoadFinish();
			}
		}else if(arg1.getAction().equals(SET_FRIEND_VIDEO_SELECT_FINISH)){
		}else if(arg1.getAction().equals(SET_FRIEND_VIDEO_FINISH)){
		}else if(arg1.getAction().equals(BOOT_COMPLETED_ACTION)){
			arg0.startService(new Intent(arg0, phoneStateService.class));
		}
	}  
} 
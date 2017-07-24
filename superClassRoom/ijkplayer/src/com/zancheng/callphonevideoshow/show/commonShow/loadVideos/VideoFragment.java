package com.zancheng.callphonevideoshow.show.commonShow.loadVideos;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.object.VideoInfo;

import android.app.Fragment;
import android.os.Bundle;

public class VideoFragment extends Fragment {
	public static final int VT_OUT_LINE_CACHE = 13;
	   public static final String VT_ALL_TITLE[] = {"主页", "排行榜","动漫","音乐","影视","其他",
	        "自定义","推荐","我的视频","本地","收藏","查询","推荐","离线缓存", "我的上传"};
   public static final String INTENT_PAGE_INDEX = "pageIndex";

	
	public List<VideoInfo> videoList = new ArrayList<VideoInfo>();//后面加泛型hu
	protected int videoType;
	protected int lastVideoServerId;
	protected String parameter1;
	protected Mode pullToRefreshMode;
	
	public static VideoFragment newInstance(int pageIndex, String parameter1) {
    	switch (pageIndex) {
    	
		//离线缓存
		case VT_OUT_LINE_CACHE:
		{
			VideoFragmentHasCommend fragment = new VideoFragmentOutLineCache();
			fragment.init_son_data(pageIndex, parameter1);
	        return fragment;
		}
		
		default:
			break;
		}
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	public void init_data(int videoType, String parameter1){
		init_data(null, videoType, parameter1);
	}
	
	public void init_data(Mode pullToRefreshMode, int videoType, String parameter1){
		this.videoType = videoType;
		this.parameter1 = parameter1;
		this.pullToRefreshMode = pullToRefreshMode;
		loadVideoData(videoType);
	}
	
	protected void loadVideoData(int videoType){
		boolean isLocal = false;
		switch (videoType) {
		case VT_OUT_LINE_CACHE:
			isLocal = true;
			break;
		default:
			break;
		}
		if(isLocal){
		    videoList.addAll(MyAppData.getInst().list_download);
			videoList.addAll(MyAppData.getInst().mgr.queryLocalList());
			dataloadingComplete(true);
		}
			
    }
    
    public void dataloadingComplete(boolean needUpdate) {
        if(needUpdate){
//            if(0 == lastVideoServerId){
//                MyAppData.getInst().mgr.deleteOldVideos(false, videoList.get(0).videoTypeMain, -1);
//                MyAppData.getInst().mgr.addList(false, false, videoList);
//            }
        }
        int listLength = videoList.size();
        if(listLength>0){
            lastVideoServerId = videoList.get(listLength-1).id;
        }
    }
    
	public void videoDownLoadFinish() {

	} 
	public void updateVideoListShow() {
		
	}
	public boolean needClearVideoList() {
		return lastVideoServerId == 0;
	}
}

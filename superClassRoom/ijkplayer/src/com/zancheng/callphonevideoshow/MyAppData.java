package com.zancheng.callphonevideoshow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.likebamboo.phoneshow.phoneStateService;
import com.likebamboo.phoneshow.config.ShowPref;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zancheng.callphonevideoshow.db.DBManager;
import com.zancheng.callphonevideoshow.http.NetworkManager;
import com.zancheng.callphonevideoshow.object.Linkmen;
import com.zancheng.callphonevideoshow.object.ListBean;
import com.zancheng.callphonevideoshow.object.User;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.show.commonShow.Receiver;
import com.zancheng.callphonevideoshow.show.videoShow.MyCard;
import com.zancheng.callphonevideoshow.tools.ToastUtils;
import com.zancheng.callphonevideoshow.tools.Tool;
import com.zc.csx.Sdk;

public class MyAppData {
	public ImageLoader imageLoader = ImageLoader.getInstance();
	protected static MyAppData inst;
    public DBManager mgr;
    private ShowPref pref;
	public VideoInfo curVideoInfo;
	public VideoInfo defaultVideoInfo;
	public DisplayImageOptions options;
	public int localVideoNum;
	public static int originalMusicVolume;
	public static boolean callSetVolume = false;
	public static int callState = Constants.CS_DEFAULT;
	public static String callingNum;
	public String needAutoDownVideoUrl;
	protected boolean isShowVideo = true;//显示来电视频
	public boolean IsShowSet=false;//是否显示播放界面设置按钮
    public boolean IsShowAllTabs = true;//是否显示5个tab
	
	public List<VideoInfo> list_download=new ArrayList<VideoInfo>();
	public List<ListBean> list_cardInfos=new ArrayList<ListBean>();
	
	public User user = new User();
	public Map<Integer, List<String>> setLinkmenRingList = new HashMap<Integer, List<String>>();
	public List<String> phoneSDCardVideosList = new ArrayList<String>();
	public String isDownloadingVideoPath;
	public List<String> selectedPhoneNum = new ArrayList<String>();
	public int bindPhoneNumByFromPlace;
	public int  price;//充值金额
	
	public boolean setFriendRingListContainsVideo(int videoId){
		return setLinkmenRingList.containsKey(videoId);
	}
	
	public void addSetFriendRingList(int videoId, List<String> phoneNums){
		List<String> phoneNumList = new ArrayList<String>();
		phoneNumList.addAll(phoneNums);
		if(videoId == 0 && curVideoInfo != null){
			videoId = curVideoInfo.id;
		}
		if(setLinkmenRingList.get(videoId) == null){
			setLinkmenRingList.put(videoId,  phoneNumList);
		}
	}
	
	public void addSetFriendRingList(int videoId, String phoneNum){
		if(setLinkmenRingList.get(videoId) == null){
			setLinkmenRingList.put(videoId,  new ArrayList<String>());
		}
		setLinkmenRingList.get(videoId).add(phoneNum);
	}
	
	
	public void removeSetFriendRingList(String phoneNum){
		removeSetFriendRingList(curVideoInfo.id, phoneNum);
	}
	
	public void removeSetFriendRingList(int videoId, String phoneNum){
		List<String> linkmenPhoneNum = setLinkmenRingList.get(videoId);
		if(linkmenPhoneNum != null){
			for(int i =0;i<linkmenPhoneNum.size();i++){
				if(linkmenPhoneNum.get(i).equals(phoneNum)){
					linkmenPhoneNum.remove(i);
				}
			}
		}
	}
	
	public boolean setFriendRingListIsExist(int videoId, String phoneNum){
		List<String> linkmenPhoneNum = setLinkmenRingList.get(videoId);
		if(linkmenPhoneNum != null){
			for(int i =0;i<linkmenPhoneNum.size();i++){
				if(linkmenPhoneNum.get(i).equals(phoneNum)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static MyAppData getInst(){
		return inst;
	}
   public static void createInst(Context context){
        inst = new MyAppData();
        inst.init(context);
    }
	
	public void onDestroy() {
		mgr.closeDB();  
	}
	
	public void init(Context context){
        mgr = new DBManager(context); 
        pref = ShowPref.getMainInstance(context);
        options = new DisplayImageOptions.Builder()
		.showImageOnLoading(context.getResources().getIdentifier("ic_stub", "drawable", context.getPackageName()))
		.showImageForEmptyUri(context.getResources().getIdentifier("ic_empty", "drawable", context.getPackageName()))
		.showImageOnFail(context.getResources().getIdentifier("ic_stub", "drawable", context.getPackageName()))
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
        
        if(-1 == pref.loadInt(ShowPref.DEFAULT_VIDEO_ID, -1)){
        	 defaultVideoInfo = new VideoInfo();
        	 defaultVideoInfo.id = -1;
        	 defaultVideoInfo.imgPath = Constants.DOWNLOAD_PIC_ABSOLUTE_PATH + Constants.RING_DEFAULT_PIC;
        	 defaultVideoInfo.videoPath = Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH + Constants.RING_DEFAULT_VIDEO;
        	 defaultVideoInfo.title = "左耳";
        	 if(1 == pref.loadInt(ShowPref.IS_FRIST, 1)){
        		 File file = new File(Constants.ROOT_ABSOLUTE_PATH);
        		 Tool.deleteDir(file);
        		 pref.putInt(ShowPref.IS_FRIST, 0);
        		 pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_FULL_DIALOG);
        		 pref.putInt(ShowPref.IS_SHOW_VIDEO, 1);
        		 pref.putString(ShowPref.DEFAULT_VIDEO_PATH, defaultVideoInfo.videoPath);
             	 pref.putString(ShowPref.DEFAULT_PIC_PATH, defaultVideoInfo.imgPath);
        		 Tool.createDefaultFile(context, Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH, Constants.RING_DEFAULT_VIDEO, context.getResources().getIdentifier("csx_sdk_zc_defaultring", "drawable", context.getPackageName()));
        		 Tool.createDefaultFile(context, Constants.DOWNLOAD_PIC_ABSOLUTE_PATH, Constants.RING_DEFAULT_PIC, context.getResources().getIdentifier("csx_sdk_zc_defaultpic", "drawable", context.getPackageName()));
        		 pref.putString(ShowPref.MY_NAME, "账号_赞成");
        		 pref.putString(ShowPref.MY_SIGN,  "赞成科技");
				 pref.putString(ShowPref.APP_NEW_VERSION, Tool.getVersionName(context));
        	 }
        }else{
        	defaultVideoInfo = new VideoInfo();
        	defaultVideoInfo.id = pref.loadInt(ShowPref.DEFAULT_VIDEO_ID);
        	defaultVideoInfo.imgPath = pref.loadString(ShowPref.DEFAULT_PIC_PATH);
        	defaultVideoInfo.videoPath = pref.loadString(ShowPref.DEFAULT_VIDEO_PATH);
        }
        //========!dongtest==============
        pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_ACTIVITY);
        
        isShowVideo = pref.loadInt(ShowPref.IS_SHOW_VIDEO)==1?true:false;
        localVideoNum = mgr.queryLocalNum();
        user.id = pref.loadString(ShowPref.MY_ID);
        user.name = pref.loadString(ShowPref.MY_NAME);
        user.sign = pref.loadString(ShowPref.MY_SIGN);
        user.phoneNum = pref.loadString(ShowPref.MY_PHONENUMBER);
        user.gold = pref.loadInt(ShowPref.MY_GOLD);
        questGetUserInfo();
        String _dirName = Tool.getPhoneSdCardSonPath(Constants.DOWNLOAD_PHONE_SDCARD_VIDEO_SON_PATH);
		File dirFile = new File(_dirName);
		if (phoneSDCardVideosList.size() != dirFile.list().length) {
			phoneSDCardVideosList.clear();
			for (int i = 0; i < dirFile.list().length; i++) {
				phoneSDCardVideosList.add(_dirName + dirFile.list()[i]);
			}
		}	
	}
	
	
	public void questGetUserInfo() {
		//用户基本信息
		String params = "id="+user.id;
		NetworkManager.getData(NetworkManager.USER_INFO, Constants.GET_USER_INFO_URL, params);
	}
	
	public void syncUserData(String str) {
		try {
			JSONObject jsonObject1 = new JSONObject(str);
			user.id = jsonObject1.getString("id");
			user.name = jsonObject1.getString("userName");
			user.sign = jsonObject1.getString("userSign");
			user.gold = jsonObject1.getInt("gold");
			user.isMan = jsonObject1.getString("sex").equals("0")?false:true;
			
			String phoneNum = jsonObject1.getString("phoneNum");
			user.phoneNum = phoneNum.equals("null")?"":phoneNum;
			user.setLinkmensRing(jsonObject1.getString("linkmenVideo"));
			user.setLinkmensCard(jsonObject1.getString("linkmenCard"));
			
			pref.putString(ShowPref.MY_ID, user.id);
			pref.putString(ShowPref.MY_NAME, user.name);
			pref.putString(ShowPref.MY_SIGN, user.sign);
			pref.putInt(ShowPref.MY_GOLD, user.gold);
			pref.putString(ShowPref.MY_PHONENUMBER, user.phoneNum);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void responseGetUserInfo(String str) {
		syncUserData(str);
	}
	
   
	public synchronized  void dataUpdateComplete(int messageType, String str) {
		switch (messageType) {
		case NetworkManager.UPDATE_USER_INFO:
		{
			String[] splitStr = str.split("\\|");
			if(splitStr.length >= 2){
				String result = splitStr[0];
				if(result.equals(NetworkManager.BIND_PHONE)){
					String action = splitStr[1];
					if(action.equals(NetworkManager.BP_SYNC_USER_DATA)){
						syncUserData(splitStr[2]);
						ToastUtils.showToast(SDKMain.sdkContext, "你已绑定过手机号，数据同步完成");
						Intent intent1 = new Intent(SDKMain.sdkContext, MyCard.class);
	                     SDKMain.sdkContext.startActivity(intent1);
					}else if(action.equals(NetworkManager.BP_PHONE_NUM)){
						user.phoneNum = splitStr[2];
						ShowPref.getMainInstance(SDKMain.sdkContext).putString(ShowPref.MY_PHONENUMBER, user.phoneNum);
						Sdk.sendPhoneStateService(phoneStateService.MSG_SET_PHONENUM, null, user.phoneNum, null);
						ToastUtils.showToast(SDKMain.sdkContext, "手机号绑定成功");
						Intent intent1 = new Intent(SDKMain.sdkContext, MyCard.class);
	                    SDKMain.sdkContext.startActivity(intent1);
					}
					 
				}else if(result.equals(NetworkManager.UPDATE_CARD)){
					user.setLinkmensCard(splitStr[1]);
					ToastUtils.showToast(SDKMain.sdkContext, "默认名片修改成功");
				}
			}else{
				ToastUtils.showToast(SDKMain.sdkContext, "访问数据异常！");
			}
			
			break;
		}
		case NetworkManager.VIDEO_URL_BY_ID_BY_DIRECT:
		{
			if(MyCard.context != null){
				try {
					JSONObject jsonObject1 = new JSONObject(str);
					String title = jsonObject1.getString("title");
					String url = jsonObject1.getString("videoUrl");
					((MyCard)(MyCard.context)).getVideoPathComplete(title,url);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
		default:
			break;
		}
		
	}
	
	public void setCurHttpVideoInfoToDefault(boolean isHttp, VideoInfo videoInfo) {
			
		List<String> linkmensPhoneNum = setLinkmenRingList.get(videoInfo.id);
		if(!setLinkmenRingList.isEmpty() && linkmensPhoneNum != null){
			int linkmensPhoneNumCount = linkmensPhoneNum.size();
				for(int j= 0;j<linkmensPhoneNumCount;j++){
					String phoneNum = linkmensPhoneNum.get(j);
					Linkmen curLinkmen = new Linkmen();
					curLinkmen.videoId = videoInfo.id;
					curLinkmen.videoTitle = videoInfo.title;
					curLinkmen.videoPath = videoInfo.videoPath;
					curLinkmen.mContactsNumber = phoneNum;
					mgr.addAndUpdateLinkmenVideo(curLinkmen);
					if(j == linkmensPhoneNumCount-1){
						Intent intent = new Intent();  
						intent.setAction(Receiver.SET_FRIEND_VIDEO_FINISH); 
						SDKMain.sdkContext.sendBroadcast(intent);
					}
				}
				setLinkmenRingList.remove(videoInfo.id);
		}else{
			defaultVideoInfo.id = videoInfo.id;
			defaultVideoInfo.author = videoInfo.author;
			defaultVideoInfo.title = videoInfo.title;
			defaultVideoInfo.imgPath2 = videoInfo.imgPath;
			defaultVideoInfo.title = videoInfo.title;
			String picPath = Tool.getFileLocalPath(Tool.getFileAllNameFromPath(videoInfo.imgPath), Constants.DOWNLOAD_PIC_ABSOLUTE_PATH, Constants.DIY_PIC_ABSOLUTE_PATH);
			//程序中判断视频相关文件（视频+图片）都是根据视频来判断的，为保证程序正常运行这里判断除视频外的文件
			if(picPath != null){ 
				defaultVideoInfo.imgPath = picPath;
			}
			defaultVideoInfo.videoPath = Tool.getFileLocalPath(Tool.getFileAllNameFromPath(videoInfo.videoPath), Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH, Constants.DIY_VIDEO_ABSOLUTE_PATH);
			if(defaultVideoInfo.videoPath == null){
				defaultVideoInfo.videoPath = Tool.getFileLocalPath(Tool.getFileAllNameFromPath(videoInfo.videoPath), Constants.DIY_DRAFT_VIDEO_ABSOLUTE_PATH, null);
			}
//			if (defaultVideoInfo.videoPath == null) {
//				defaultVideoInfo.videoPath = videoInfo.videoPath;
//			}
			pref.putInt(ShowPref.DEFAULT_VIDEO_ID, defaultVideoInfo.id);
			pref.putString(ShowPref.DEFAULT_VIDEO_PATH, defaultVideoInfo.videoPath);
			pref.putString(ShowPref.DEFAULT_PIC_PATH, defaultVideoInfo.imgPath2);
			Sdk.sendPhoneStateService(phoneStateService.MSG_SET_SHOW_VIDEO_PATH, null, defaultVideoInfo.videoPath, null);
		}
		
		if(isHttp){
			mgr.addLocalVideo(videoInfo);
			localVideoNum++;
		}
	}

	public static String getDefaultVideoPath(Context context){
		String path = "";
		if(inst != null && inst.defaultVideoInfo != null){
			path = getInst().defaultVideoInfo.videoPath;
		}else{
			ShowPref pref = ShowPref.getMainInstance(context);
			path = pref.loadString(ShowPref.DEFAULT_VIDEO_PATH);
		}
		return path;
	}
	
	public static boolean getIsShowVideo(Context context){
		boolean isShowVideo = true;
		if(inst != null){
			isShowVideo = inst.isShowVideo;
		}else{
			ShowPref pref = ShowPref.getMainInstance(context);
			isShowVideo = pref.loadInt(ShowPref.IS_SHOW_VIDEO)==1?true:false;
		}
		return isShowVideo;
	}
	public void setIsShowVideo(boolean isShowVideo){
		this.isShowVideo = isShowVideo;
		int isShowVideoB = isShowVideo?1:0;
		pref.putInt(ShowPref.IS_SHOW_VIDEO, isShowVideoB);
		Sdk.sendPhoneStateService(phoneStateService.MSG_SET_SHOW_VIDEO, isShowVideo, null, null);
	}
	
	public VideoInfo downloadFinish(){
		VideoInfo downloadFinishVideoInfo = list_download.get(0);
		//添加到本地列表
		setCurHttpVideoInfoToDefault(true, downloadFinishVideoInfo);
		//移除第一项
		list_download.remove(0);
		return downloadFinishVideoInfo;
	}
	
	public int checkIsInDownloadList(int videoId){
		List<VideoInfo> videoList = list_download;
		for(int i=0;i<videoList.size();i++){
			if(videoList.get(i).id == videoId){
				return i;
			}
		}
		return -1;
	}
}

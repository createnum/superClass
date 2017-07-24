package com.zancheng.callphonevideoshow.show.commonShow.loadVideos;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.tools.MyDownload;
import com.zancheng.callphonevideoshow.tools.ToastUtils;
import com.zancheng.callphonevideoshow.tools.Tool;
  
/**
 *	离线视频 
 * 
 * 存储下载到本地的视频
 * @author GD01319
 */
@SuppressLint("NewApi")
public class CommonVideoActivityHasRecommend extends Activity {  
    public VideoFragment videoFragment;
	public static CommonVideoActivityHasRecommend context;
	public static final int  FROM_FRIEND_SET_VOICE_ACTIVITY= 1;//通过联系人设置铃声
	private int typeIndex;
	private FragmentManager mFragMgr;
	private int count;
	private long firClick;
	private long secClick;
	
    @SuppressLint("CutPasteId")
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getResources().getIdentifier("csx_sdk_zc_common_video_has_recommend", "layout", getPackageName()));  
        context = this;
        findViewById(getResources().getIdentifier("finish", "id", getPackageName())).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
    	});
    	
    	Intent intent = getIntent(); 
        typeIndex = intent.getIntExtra(VideoFragment.INTENT_PAGE_INDEX, -1);
        TextView typeName = (TextView)findViewById(getResources().getIdentifier("title", "id", getPackageName()));
        typeName.setText(VideoFragment.VT_ALL_TITLE[typeIndex]);
        mFragMgr = getFragmentManager();  
        String parameter1 = intent.getStringExtra("parameter1");
        showFragments("11", false, typeIndex, parameter1);
        
        RelativeLayout title = (RelativeLayout) findViewById(getResources().getIdentifier("title_top", "id", getPackageName()));
        //双击返回顶端
        title.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(MotionEvent.ACTION_DOWN == event.getAction()){  
			            count++;  
			            if(count == 1){  
			                firClick = System.currentTimeMillis();  
			                  
			            } else if (count == 2){  
			                secClick = System.currentTimeMillis();  
			                if(secClick - firClick < 1000){  
			                	((VideoFragmentHasCommend)videoFragment).scrollTo_Top();
			                }  
			                count = 0;  
			                firClick = 0;  
			                secClick = 0;  
			                  
			            }  
			        } 
				return false;
			}
        	
        });
        
    }   
    
//    @SuppressWarnings("deprecation")
	public void  showNoResource() {
    	RelativeLayout rl = (RelativeLayout) findViewById(getResources().getIdentifier("relative", "id", getPackageName()));
    	FrameLayout  frameLayout = (FrameLayout) findViewById(getResources().getIdentifier("door_root_content_ll", "id", getPackageName()));
//    	ImageView relative_icon = (ImageView) findViewById(R.id.relative_icon);
//    	TextView name = (TextView) findViewById(R.id.relative_text_name);
//    	TextView introduce = (TextView) findViewById(R.id.relative_text_introduce);
//    	//离线缓存
//    	if (typeIndex == VideoFragment.VT_OUT_LINE_CACHE) {
//        //我的上传
//		}
//    	
    	frameLayout.setVisibility(View.GONE);
    	rl.setVisibility(View.VISIBLE);
	}
    
    private void showFragments(String tag, boolean needback, int index, String parameter1){  
        FragmentTransaction trans = mFragMgr.beginTransaction();  
        videoFragment = VideoFragment.newInstance(index, parameter1);
        if(needback){  
            trans.add(getResources().getIdentifier("door_root_content_ll", "id", getPackageName()), videoFragment, tag);  
            trans.addToBackStack(tag);  
        }else{  
            trans.replace(getResources().getIdentifier("door_root_content_ll", "id", getPackageName()), videoFragment, tag);  
        }  
        trans.commit();  
    }  
    
    public static void setRingVideoByFromFriend(VideoInfo videoInfo){
		MyAppData.getInst().addSetFriendRingList(videoInfo.id, MyAppData.getInst().selectedPhoneNum);
		MyAppData.getInst().selectedPhoneNum.clear();
		
		String videoName = Tool.getFileAllNameFromPath(videoInfo.videoPath);
		String localPath = Tool.getFileLocalPath(videoName, Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH, Constants.DIY_VIDEO_ABSOLUTE_PATH);
		if(localPath == null){
			if (checkIsInDownloadList(videoInfo.id) != -1) {
	    		ToastUtils.showToast(context, "已在下载队列中");
	    		return;
			}
			MyDownload myDownloadManager = MyDownload.getInst();
			myDownloadManager.addDownloadQueue(videoInfo.imgPath, videoInfo.videoPath);
    		myDownloadManager.downloadStart();
	    	ToastUtils.showToast(context, "加入设置队列");
	    		
    		MyAppData.getInst().list_download.add(videoInfo);
    		
		}else{
			MyAppData.getInst().setCurHttpVideoInfoToDefault(false, videoInfo);
			ToastUtils.showToast(context, "设置成功");
		}
	}
    
    public static int checkIsInDownloadList(int videoId){
		List<VideoInfo> videoList = MyAppData.getInst().list_download;
		for(int i=0;i<videoList.size();i++){
			if(videoList.get(i).id == videoId){
				return i;
			}
		}
		return -1;
	}
    
    public void downLoadFinish() {
        if(videoFragment instanceof VideoFragmentOutLineCache){
            ((VideoFragmentOutLineCache)videoFragment).downLoadFinish();
        }
     }
    
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
    }  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
    }  
    
	protected void onDestroy() {
		super.onDestroy();
		context = null;
	}
	
}

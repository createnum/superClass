package com.zancheng.callphonevideoshow.show.videoShow;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.internal.HeaderGridView;
import com.likebamboo.phoneshow.widget.CallUIFactory;
import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.SDKMain;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.http.NetworkManager;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.show.commonShow.HttpFragmentActivity;
import com.zancheng.callphonevideoshow.show.commonShow.VerticalSeekBar;
import com.zancheng.callphonevideoshow.tools.DeviceUtil;
import com.zancheng.callphonevideoshow.tools.Screen;
import com.zancheng.callphonevideoshow.tools.Tool;
import com.zancheng.ijkplayer.common.PlayerManager;
import com.zancheng.ijkplayer.widget.media.IjkVideoView;
import com.zc.csx.CommonItemAdapter;
import com.zc.csx.CommonShow;

public class MyCard extends CommonShow implements
PlayerManager.PlayerStateListener{
	private boolean isShowVideo;
	public static Context context;
	private CommonItemAdapter mAdapter;
	private IjkVideoView videoView;
	private SeekBar seekbar;
	private RelativeLayout icon_layout;
	Runnable command;
	private Handler myHandler;
	private ProgressBar progressBar1;
	private TextView textView_card,time_start,time_end;
	private ImageView startvideo;
	private int curVideoId = 1;//为保证程序因为异常而导致设置名片的时候名片不存在，所以这个给一个存在视频的id为默认值
	private TextView name;
	private RelativeLayout rl;
	
	private float mPosX;
	private float mPosY;
	private float mCurPosY;
	private float mCurPosX;
	private float screenWidth;
	private AudioManager mAudioManager;
	private VerticalSeekBar verticalSeekBar;
	private VerticalSeekBar verticalSeekBar_screen;
	private ImageView voice_add;
	private ImageView screen_add;
	private ImageView voice_down;
	private ImageView screen_down;
   protected int lastVideoServerId;
   
   private PlayerManager player;
	
	//系统是否自动调节亮度 
    private boolean isAutoBrightness = false;  
    //系统亮度变化值 
    private static final int SYSTEM_BRIGHTNESS_CHANGE_VALUES = 15;  
    // 最大系统音量值 
    private static final int MAX_SYSTEM_BRIGHTNESS_VALUES = 255;  
    private int videoShowHeight;
    private static int videoShowMaxHeight;
	private boolean fullscreen = false;//全屏/窗口播放切换标志
	private int max;
	private RelativeLayout effshowVideoViewLL;
	private LinearLayout tips;
	private ImageView fullScreen;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getResources().getIdentifier("csx_sdk_zc_mycard", "layout", getPackageName()));
		context = this;
		
		isAutoBrightness = DeviceUtil.isAutoBrightness(this);  
        if (isAutoBrightness) {  
            DeviceUtil.stopAutoBrightness(this);  
        }  
        
        videoShowMaxHeight = (int)getResources().getDimension(getResources().getIdentifier("video_show_height", "dimen", getPackageName()));
		videoShowHeight = videoShowMaxHeight;
        
        if (Tool.isNetworkAvailable(context, "any")) {
			NetworkManager.getData(null, NetworkManager.VIDEO_URL_BY_ID_BY_DIRECT, 
	        		Constants.GET_SHOW_VIDEO_SIMPLE_INFO, "id="+MyAppData.getInst().user.defaultCardVideoId,null);
		}else{
			final AlertDialog.Builder normalDialog =  new AlertDialog.Builder(context);
		        normalDialog.setTitle("当前无网络连接");
		        normalDialog.setPositiveButton("确定", 
		            new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            }
		        });
//		        normalDialog.setCancelable(false);
		        normalDialog.show();
		}
		
        effshowVideoViewLL = (RelativeLayout) findViewById(getResources().getIdentifier("effshowVideoViewLL", "id", getPackageName()));
        fullScreen = (ImageView) findViewById(getResources().getIdentifier("fullScreen", "id", getPackageName()));
        tips = (LinearLayout) findViewById(getResources().getIdentifier("tips", "id", getPackageName()));
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		verticalSeekBar = (VerticalSeekBar)findViewById(getResources().getIdentifier("verticalSeekBar", "id", getPackageName()));
		verticalSeekBar_screen = (VerticalSeekBar)findViewById(getResources().getIdentifier("verticalSeekBar_screen", "id", getPackageName()));
		voice_add = (ImageView) findViewById(getResources().getIdentifier("voice_add", "id", getPackageName()));
		screen_add = (ImageView) findViewById(getResources().getIdentifier("screen_add", "id", getPackageName()));
		voice_down = (ImageView) findViewById(getResources().getIdentifier("voice_down", "id", getPackageName()));
		screen_down = (ImageView) findViewById(getResources().getIdentifier("screen_down", "id", getPackageName()));
		max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		screenWidth = Screen.getScreenWidth(context)/2;
		
		findViewById(getResources().getIdentifier("back_img", "id", getPackageName())).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (fullscreen) {
					changeVideoShowType(false);
				}else {
					finish();
				}
			}
		});
		
		fullScreen.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				changeVideoShowType(!fullscreen);
			}
		});
		
		rl = (RelativeLayout) findViewById(getResources().getIdentifier("top_mine", "id", getPackageName()));
		textView_card=(TextView) findViewById(getResources().getIdentifier("txt_save_card", "id", getPackageName()));
		textView_card.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				textView_card.setVisibility(View.GONE);
				String params = "myPhoneNum="+MyAppData.getInst().user.phoneNum
						+"&updateType="+NetworkManager.UPDATE_CARD
						+"&oldLinkmenInfo="+"default,"+MyAppData.getInst().user.defaultCardVideoId
						+"&newLinkmenInfo="+"default,"+curVideoId;
				NetworkManager.getData(NetworkManager.UPDATE_USER_INFO, Constants.UPDATE_USER_INFO, params);
		        
			}
		});
		
		progressBar1=(ProgressBar) findViewById(getResources().getIdentifier("progressBar1", "id", getPackageName()));
		seekbar = (SeekBar) findViewById(getResources().getIdentifier("seekbar", "id", getPackageName()));
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
		
		name = (TextView) findViewById(getResources().getIdentifier("name", "id", getPackageName()));

		// 当前时常
		time_start = (TextView) findViewById(getResources().getIdentifier("time_start", "id", getPackageName()));
		// 视频总长
		time_end = (TextView) findViewById(getResources().getIdentifier("time_end", "id", getPackageName()));
		
		player = new PlayerManager(this);
		player.setPlayerStateListener(this);
		videoView = player.getVideoView();
		uiHandler.sendEmptyMessageDelayed(0, 200);
		
		startvideo=(ImageView) findViewById(getResources().getIdentifier("startvideo", "id", getPackageName()));
		final ImageView  icon=(ImageView) findViewById(getResources().getIdentifier("stop_icon", "id", getPackageName()));
		icon_layout=(RelativeLayout) findViewById(getResources().getIdentifier("icon_layout", "id", getPackageName()));
		videoView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    mPosX = event.getX();
	                    mPosY = event.getY();
	                    break;
	                case MotionEvent.ACTION_MOVE:
	                    mCurPosX = event.getX();
	                    mCurPosY = event.getY();
	                	//滑动
	                    if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 70)) {
	                        //向下滑動
	                    	if (mPosX > screenWidth) {
								minusSystemBrighiness(DeviceUtil.getSystemScreenBrightness(context));
								showScreenIcon();
								verticalSeekBar_screen.setProgress(DeviceUtil.getSystemScreenBrightness(context)
										* 100 / 255);
							}else {
								//降低音量
								mAudioManager.adjustStreamVolume(
							            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
							            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
								changeVoice();
							}
	                        
	                    } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) >70)) {
	                        //向上滑动增加音量
	                    	if (mPosX < screenWidth) {
								mAudioManager.adjustStreamVolume(
							            AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
							            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
								changeVoice();
							}else {
								addSystemBrighiness(DeviceUtil.getSystemScreenBrightness(context));
								showScreenIcon();
								verticalSeekBar_screen.setProgress(DeviceUtil.getSystemScreenBrightness(context)
										* 100 / 255);
							}
	                    }
	                    break;
	                case MotionEvent.ACTION_UP:
	                	mCurPosX = event.getX();
  	                    mCurPosY = event.getY();
	                	//点击
	                	if (mPosX == mCurPosX && mPosY == mCurPosY && videoView.isPlaying()) {
	                		icon_layout.setVisibility(View.VISIBLE);
	                		videoView.pause();
	        				tips.setVisibility(View.VISIBLE);
	        				rl.setVisibility(View.VISIBLE);
	        				icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("csx_sdk_zc_start", "drawable", getPackageName())));
						}
	                    break;
	                }
	                return true;
			}
			
		});
		icon.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!videoView.isPlaying()) {
					icon_layout.setVisibility(View.GONE);
					startvideo.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("csx_sdk_zc_pause", "drawable", getPackageName())));
					videoView.start();
				}
			}
		});
		startvideo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!videoView.isPlaying()) {
					videoView.start();
					icon_layout.setVisibility(View.GONE);
					startvideo.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("csx_sdk_zc_pause", "drawable", getPackageName())));
				}else {
					icon_layout.setVisibility(View.VISIBLE);
					startvideo.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("csx_sdk_zc_start", "drawable", getPackageName())));
					videoView.pause();
				}
			}
		});
		

		/*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// mp.setLooping(true);
				mp.getVideoWidth();
				
				float scale = (float)mp.getVideoWidth()/Screen.getScreenWidth(context);
				if(mp.getVideoHeight() < mp.getVideoWidth()){
					videoShowHeight = (int) (mp.getVideoHeight()/scale);
					videoShowHeight = (videoShowHeight>videoShowMaxHeight)?videoShowMaxHeight:videoShowHeight;
				}
				RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoShowHeight);
				effshowVideoViewLL.setLayoutParams(lp);
				
				VideoThreed videoThreed=new VideoThreed();
	            videoThreed.start();
				
				// 当前时间
	            myHandler = new Handler() {
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						if (videoView.getCurrentPosition() > 0) {
							progressBar1.setVisibility(View.GONE);
						}
						seekbar.setProgress(videoView.getCurrentPosition()*100/videoView.getDuration());
						time_start.setText(String.valueOf(msg.obj));
					}
				};
				// 总时间
				int time = videoView.getDuration();
				SimpleDateFormat formatter2 = new SimpleDateFormat("mm:ss");
				String hms2 = formatter2.format(time);
				time_end.setText(hms2);
			}
		});*/
		
		

		
		
		new Handler().postDelayed(new Runnable() {
			public void run() {
				rl.setVisibility(View.GONE); 
				tips.setVisibility(View.GONE);
			}
		}, 5000);

		videoList = MyAppData.getInst().mgr.queryList(false, 1, -1, false);
		loadData();
		mAdapter = new ItemAdapter();
		initPullRefreshGridView(mAdapter);
		uiHandler.sendEmptyMessageDelayed(0, 200);
	}

	public void loadData(){
        NetworkManager.getData(context, NetworkManager.COMMON_VIDEO_LIST, Constants.GET_VIDEO_INFO_URL, NetworkManager.jointParamsOfLoadVideoList(Constants.SPECIAL_MESSAGE_KEY, 1, lastVideoServerId), Constants.SPECIAL_MESSAGE_KEY, String.valueOf(lastVideoServerId));
    }
    
	private void changeVoice() {
		verticalSeekBar.setVisibility(View.VISIBLE);
		voice_add.setVisibility(View.VISIBLE);
		voice_down.setVisibility(View.VISIBLE);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		verticalSeekBar.setProgress(current * 100 / max);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				verticalSeekBar.setVisibility(View.GONE);
				voice_add.setVisibility(View.GONE);
				voice_down.setVisibility(View.GONE);
			}
		}, 2000);
	}
	
	 //系统亮度加 
    private void addSystemBrighiness(int currSettingBrighiness) {  
        if (currSettingBrighiness < MAX_SYSTEM_BRIGHTNESS_VALUES  
                && (currSettingBrighiness + SYSTEM_BRIGHTNESS_CHANGE_VALUES) < MAX_SYSTEM_BRIGHTNESS_VALUES) {  
            DeviceUtil.setSystemScreenBrightness(this,  currSettingBrighiness  
                    + SYSTEM_BRIGHTNESS_CHANGE_VALUES);  
        }  
    }  
      
     //系统亮度减 
    public void minusSystemBrighiness(int currSettingBrighiness) {  
        if (currSettingBrighiness >= SYSTEM_BRIGHTNESS_CHANGE_VALUES) {  
            DeviceUtil.setSystemScreenBrightness(this, currSettingBrighiness  
                    - SYSTEM_BRIGHTNESS_CHANGE_VALUES);  
        }  
    }  
    
    private void showScreenIcon() {
		verticalSeekBar_screen.setVisibility(View.VISIBLE);
		screen_add.setVisibility(View.VISIBLE);
		screen_down.setVisibility(View.VISIBLE);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				screen_add.setVisibility(View.GONE);
				screen_down.setVisibility(View.GONE);
				verticalSeekBar_screen.setVisibility(View.GONE);
			}
		}, 2000);
		
	}
	
    protected void changeVideoShowType(boolean isFullScreen){
		if(isFullScreen){
			/**
			  * 设置为横屏
			  */
			 RelativeLayout.LayoutParams layoutParams=
						 new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				 layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				 layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			 effshowVideoViewLL.setLayoutParams(layoutParams);
			 RelativeLayout.LayoutParams layoutParams1=
						 new RelativeLayout.LayoutParams(Screen.getNoHasVirtualKeyScreenRealHeight(), Screen.getScreenWidth(context));
			 effshowVideoViewLL.setLayoutParams(layoutParams1);
			 icon_layout.setLayoutParams(layoutParams1);
			 
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			 //隐藏状态栏
			 WindowManager.LayoutParams lp = getWindow().getAttributes();
	         lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
	         getWindow().setAttributes(lp);
	         getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	         tips.setVisibility(View.VISIBLE);
	         fullScreen.setVisibility(View.INVISIBLE);
			
	        new Handler().postDelayed(new Runnable() {
				public void run() {
					tips.setVisibility(View.INVISIBLE);
					rl.setVisibility(View.INVISIBLE); 
				}
			}, 6000);
			
		}else{//设置RelativeLayout的窗口模式
			/**
			  * 设置为竖屏
			  */
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		  		//显示状态栏
		  	WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			
			 RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoShowHeight);
			 effshowVideoViewLL.setLayoutParams(lp);
//			 notVideoRLFrame.setVisibility(View.VISIBLE);
			 fullScreen.setVisibility(View.VISIBLE);
		} 
		fullscreen = isFullScreen;
	}
	
	public void getVideoPathComplete(String title , String url){
		name.setText(title);
//		Uri uri = Uri.parse(url);
		url="http://182.92.149.179/dong/videos/myvideo.flv";
		player.play(url);
//		videoView.setVideoURI(uri);
//		videoView.start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		videoView.start();
		icon_layout.setVisibility(View.GONE);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 播放视频时，点击home健，视频播放界面消失
		videoView.pause();
		if (isShowVideo) {
			CallUIFactory.hide(context);
			isShowVideo = false;
			
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		context = null;
//		DeviceUtil.startAutoBrightness(this);//打开自动调节亮度
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 switch (keyCode) {
		    case KeyEvent.KEYCODE_BACK:
		    	videoView.pause();
		    	if (isShowVideo) {
					CallUIFactory.hide(context);
					isShowVideo = false;
				}
			    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    		changeVideoShowType(false);
				}else{
					  finish();
				}
			    return true;
			// 音量减小
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				mAudioManager.adjustStreamVolume(
			            AudioManager.STREAM_MUSIC,
			            AudioManager.ADJUST_LOWER,
			            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				changeVoice();
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				// 音量增大时应该执行的功能代码
				mAudioManager.adjustStreamVolume(
				            AudioManager.STREAM_MUSIC,
				            AudioManager.ADJUST_RAISE,
				            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				changeVoice();
				return true;
		    }
		    
		    return super.onKeyDown(keyCode, event);
	}


	class OnSeekBarChangeListenerImp implements SeekBar.OnSeekBarChangeListener {

		// 触发操作，拖动
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		// 表示进度条刚开始拖动，开始拖动时候触发的操作
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		// 停止拖动时候
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			if (videoView != null && videoView.isPlaying()) {
				// 设置当前播放的位置
				videoView.seekTo((int) (1.0f * seekBar.getProgress() / 100 * videoView
								.getDuration()));
			}
		}
	}
	
	public void setCard(VideoInfo videoInfo){
		progressBar1.setVisibility(View.VISIBLE);
		videoView.setVideoURI(Uri.parse(videoInfo.videoPath));
		videoView.start();
		curVideoId = videoInfo.id;
		name.setText(videoInfo.title);
	}

	class ItemAdapter extends CommonItemAdapter {
		
		public class ViewHolder {
			public ImageView image1;
		}

		@Override
		public int getCount() {
			return videoList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = ((Activity) context).getLayoutInflater().inflate(
						getResources().getIdentifier("csx_sdk_zc_item_girdview_image", "layout", getPackageName()), parent, false);
				holder = new ViewHolder();
				holder.image1 = (ImageView) view.findViewById(getResources().getIdentifier("img", "id", getPackageName()));
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.image1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					textView_card.setVisibility(View.VISIBLE);
					icon_layout.setVisibility(View.GONE);
					startvideo.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("csx_sdk_zc_pause", "drawable", getPackageName())));
					
					setCard(videoList.get(position));
				}
			});
			MyAppData.getInst().imageLoader.displayImage(videoList.get(position).imgPath,
					holder.image1, MyAppData.getInst().options, null);

			return view;
		}
	}
	
	
	   //视频进度条更新
    class VideoThreed extends Thread{
    	public void run() {
    		while (!Thread.currentThread().isInterrupted())  {    
    				if(videoView.getCurrentPosition()==videoView.getDuration()){
    					return;
    				}
    				
                    SimpleDateFormat formatter1 = new SimpleDateFormat("mm:ss");
    	            String hms1 = formatter1.format(videoView.getCurrentPosition());
    	            Message message1 = Message.obtain();
    	            message1.obj = hms1;
    	            myHandler.sendMessage(message1);
                    try 
                      {   
                           Thread.sleep(500);    
                      } 
                    catch (InterruptedException e) 
                      {   
                           Thread.currentThread().interrupt();   
                      }   
                 }   
     }
    
}
    @Override
    public void dataloadingComplete(boolean needUpdate) { 
        if(needUpdate){
            if(0 == lastVideoServerId){
            MyAppData.getInst().mgr.deleteOldVideos(false, -1, videoList.get(0).videoTypeBySpecial);
            MyAppData.getInst().mgr.addList(false, true, videoList);
            }
            mAdapter.notifyDataSetChanged();
            mPullRefreshGridView.onRefreshComplete();
        }
        int listLength = videoList.size();
        if(listLength>0){
            lastVideoServerId = videoList.get(listLength-1).id;
        }
    }
    
    Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (videoView.getDuration() > 0) {
					seekbar.setMax(videoView.getDuration());
					seekbar.setProgress(videoView.getCurrentPosition());
					updateTextViewWithTimeFormat(time_start,
							videoView.getCurrentPosition() / 1000);
					updateTextViewWithTimeFormat(time_end,
							videoView.getDuration() / 1000);
				}
				if (videoView.getCurrentPosition() > 0) {
					progressBar1.setVisibility(View.GONE);
				}
				uiHandler.sendEmptyMessageDelayed(0, 200);
				break;
			}
		}
		
	};
	private void updateTextViewWithTimeFormat(TextView textView, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String stringTemp = null;
		if (0 != hh) {
			stringTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			stringTemp = String.format("%02d:%02d", mm, ss);
		}
		textView.setText(stringTemp);
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlay() {
		// TODO Auto-generated method stub
		
	}
	

}

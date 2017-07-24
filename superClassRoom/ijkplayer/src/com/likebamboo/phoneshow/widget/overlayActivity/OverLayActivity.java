package com.likebamboo.phoneshow.widget.overlayActivity;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.likebamboo.phoneshow.widget.CallUIFactory;
import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.http.loadImg.NormalLoadFrameAni;
import com.zancheng.callphonevideoshow.http.loadImg.NormalLoadPictrue;
import com.zancheng.callphonevideoshow.object.Linkmen;
import com.zancheng.callphonevideoshow.tools.NetWorkSpeedUtils;
import com.zancheng.callphonevideoshow.tools.Tool;
import com.zancheng.callphonevideoshow.tools.linkmen.LinkmenTool;

/**
 * 来电显示界面
 * 
 */
public class OverLayActivity extends Activity {

    /**
     * 网络操作结果
     */
    public static final int MSG_OK = 0x1000;

    /**
     * 网络操作结果
     */
    public static final int MSG_FAILED = 0x1001;

    /**
     * 来电号码extra
     */
    public static final String EXTRA_PHONE_NUM = "phoneNum";
    
    
    public static final String UI_USE_TARGET = "showType";
    
    /**
     * 来电显示视频名字
     */
    public static final String EXTRA_VIDEO_NAME = "videoName";

    public static final String IS_OUT_CALL = "outCall";
    
    /**
     * 关闭当前activity 电话action
     */
    public static final String ACTION_CLOSE_CALL_UI = "com.likebamboo.phoneshow.ACTION_CLOSE_CALL_UI";
    
    /**
     * 挂电话按钮
     */
    private Button mEndCallBt = null;

    /**
     * 接听电话按钮
     */
    private Button mAnswerCallBt = null;
    /**
     * 发送短信
     */
    private Button endCallSensMessageBt = null;

    /**
     * 来电号码
     */
    private String phoneNumBer = "未知";

    /**
     * 来电显示视频名称
     */
    private String videoName = "";
    
	private TextView phoneNum;
	private TextView phoneName;
	private VideoView videoView;
	private ImageView imageView;
	protected int curUseTarget = CallUIFactory.SHOW_NORMAL;
	private Uri uri;
	NetWorkSpeedUtils speedUtil;
	ListView lv ;
	List<String> list;
	RelativeLayout show_message;//展示短信提示布局
	TextView canclaMessage;//取消显示短信提示
	Button mute;//静音
	Button speaker;//免提
	Button keyboard;//键盘
	
	boolean toggleSpeakerOn = true;
	boolean keyboardOn = true;
	
    /**
     * 电话广播接收器
     */
    private BroadcastReceiver mEndCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent != null && intent.getAction().equals(ACTION_CLOSE_CALL_UI)) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(getResources().getIdentifier("csx_sdk_zc_call_over_layout", "layout", getPackageName()));
        speedUtil = new NetWorkSpeedUtils(OverLayActivity.this, mHnadler);
  		

        // 初始化界面
        initView();
        // 添加监听器
        addListener();
        updateData(getIntent());
        registerReceiver(mEndCallReceiver, new IntentFilter(ACTION_CLOSE_CALL_UI));
    }

	@Override  
	public void onWindowFocusChanged(boolean hasFocus) {  
	    super.onWindowFocusChanged(hasFocus);  
	   //防止手机通话完毕后，因为异步加载的问题而展现出视频播放界面
    	if(MyAppData.callState == Constants.CS_DEFAULT && curUseTarget == CallUIFactory.SHOW_NORMAL){
    		finish();
        }
	  } 
    
    protected void updateData(Intent intent){
    	if (intent.hasExtra(IS_OUT_CALL)) {
        	int isOutCall = intent.getIntExtra(IS_OUT_CALL, 0);
        	if(isOutCall == 1){
        		RelativeLayout.LayoutParams layoutParams=
             		    new RelativeLayout.LayoutParams(mEndCallBt.getLayoutParams());
         		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
         		layoutParams.setMargins(0, Tool.dp2px(this, 500), 0, 0);
         		mEndCallBt.setLayoutParams(layoutParams);
         		mAnswerCallBt.setVisibility(View.GONE);
         		mute.setVisibility(View.GONE);
         		endCallSensMessageBt.setVisibility(View.GONE);
         		speaker.setVisibility(View.VISIBLE);
         		keyboard.setVisibility(View.VISIBLE);
        	}
        }else {
        	if(mAnswerCallBt.getVisibility() == View.GONE){
        		RelativeLayout.LayoutParams layoutParams=
             		    new RelativeLayout.LayoutParams(mEndCallBt.getLayoutParams());
         		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
         		layoutParams.setMargins(Tool.dp2px(this, 20), 0, 0, Tool.dp2px(this, 50));
         		mEndCallBt.setLayoutParams(layoutParams);
        		mAnswerCallBt.setVisibility(View.VISIBLE);
        	}
    	}
    	
        if (intent.hasExtra(EXTRA_VIDEO_NAME)) {
        	videoName = intent.getStringExtra(EXTRA_VIDEO_NAME);
        }
        
        if(Tool.getFileTypeFromPath(videoName).equals("jpg") || Tool.getFileTypeFromPath(videoName).equals("png")){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            if (Tool.isHttpPath(videoName)) {
                final NormalLoadPictrue normalLoadPicBase = new NormalLoadPictrue();
                normalLoadPicBase.getPicture(videoName, imageView);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!normalLoadPicBase.loadComplete){
                            showLocalPic();
                        }
                    }
                }, Constants.SHOW_NET_CARD_OUTTIME+500);
            }else{
                showLocalPic();
            }
        }else if(Tool.getFileTypeFromPath(videoName).equals("zip")){
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            if (Tool.isHttpPath(videoName)) {
                final NormalLoadFrameAni normalLoadPicBase = new NormalLoadFrameAni();
                normalLoadPicBase.getPicture(videoName, imageView);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!normalLoadPicBase.loadComplete){
                            showLocalPic();
                        }
                    }
                }, Constants.SHOW_NET_CARD_OUTTIME+500); 
            }else{
                showLocalPic();
            }
        }else{
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            uri = Uri.parse(videoName);
            videoView.setVideoURI(uri);
            videoView.start(); 
            if (Tool.isHttpPath(videoName)) {
                speedUtil.startShowNetSpeed(); 
            }
        }
        
        if (intent.hasExtra(EXTRA_PHONE_NUM)) {
        	phoneNumBer = intent.getStringExtra(EXTRA_PHONE_NUM);
        }
        
        phoneNum.setText(phoneNumBer);
        Linkmen linkmen = LinkmenTool.getLinkmen(OverLayActivity.this, phoneNumBer);
  		if(linkmen != null){
  			 phoneName.setText(linkmen.getName());
  		}else {
  			phoneName.setVisibility(View.GONE);//hu   陌生联系人不显示未知两个字
		}
  		
  		if (intent.hasExtra(UI_USE_TARGET)) {
  			curUseTarget = intent.getIntExtra(UI_USE_TARGET, CallUIFactory.SHOW_NORMAL);
        }
  		
    }
    
    private Handler mHnadler = new Handler(){  
	     @Override  
	     public void handleMessage(Message msg) {  
	         switch (msg.what) {  
	             case Constants.HANDLER_MESSAGE_ID_NETWORKSPEEK:  
	                 int netSpeed = msg.arg1;
	               //暂定为100kb/s可正常播放视频
	                 //720p250k左右
	         	    if (netSpeed < 160 )  {
	         	    	OverLayActivity.this.showLocalVideo();
	         		}
	         	    speedUtil.stopShowNetSpeed();
	                break;  
	         }  
	         super.handleMessage(msg);  
	     }  
	 }; 
	 
    @Override
    protected void onNewIntent(Intent intent) { 
        super.onNewIntent(intent); 
        updateData(intent);
    }
    
    public void showLocalVideo() {
    	uri = Uri.parse(MyAppData.getDefaultVideoPath(this));
		videoView.setVideoURI(uri);
		videoView.start();
	}
    
    public void showLocalPic() {
//        String myRingPicPath = MyAppData.getInst().defaultVideoInfo.imgPath;
//        if(myRingPicPath != null){
//            File file = new File(myRingPicPath);
//            if(file.exists()){
//                Bitmap bitmap = BitmapFactory.decodeFile(myRingPicPath);
//                imageView.setImageBitmap(bitmap);
//                return;
//            }
//        }
       
        imageView.setImageResource(getResources().getIdentifier("csx_sdk_zc_defaultpic", "drawable", getPackageName()));
    }
    
    /**
     * 初始化界面
     */
    private void initView() {
    	 list = new ArrayList<String>();
         list.add(getResources().getString(getResources().getIdentifier("message_one", "string", getPackageName())));
         list.add(getResources().getString(getResources().getIdentifier("message_two", "string", getPackageName())));
         list.add(getResources().getString(getResources().getIdentifier("message_three", "string", getPackageName())));
         list.add(getResources().getString(getResources().getIdentifier("message_four", "string", getPackageName())));
         lv= (ListView) findViewById(getResources().getIdentifier("lv_show_message", "id", getPackageName()));
         MyAdapter mAdapter = new MyAdapter(OverLayActivity.this, list);
         lv.setAdapter(mAdapter);
         show_message = (RelativeLayout) findViewById(getResources().getIdentifier("show_message", "id", getPackageName()));
         canclaMessage = (TextView) findViewById(getResources().getIdentifier("cancle_message_text", "id", getPackageName()));
         mute = (Button) findViewById(getResources().getIdentifier("overlay_mute", "id", getPackageName()));
        // 接听电话与挂断电话
    	phoneNum = (TextView)findViewById(getResources().getIdentifier("number", "id", getPackageName()));
    	phoneName = (TextView)findViewById(getResources().getIdentifier("name", "id", getPackageName()));
        mEndCallBt = (Button)findViewById(getResources().getIdentifier("overlay_end_call_bt", "id", getPackageName()));
        mAnswerCallBt = (Button)findViewById(getResources().getIdentifier("overlay_answer_call_bt", "id", getPackageName()));
        endCallSensMessageBt = (Button)findViewById(getResources().getIdentifier("overlay_end_call_message_button", "id", getPackageName()));
        speaker = (Button)findViewById(getResources().getIdentifier("overlay_speaker", "id", getPackageName()));
        keyboard = (Button)findViewById(getResources().getIdentifier("overlay_keyboard", "id", getPackageName()));
        videoView = (VideoView)this.findViewById(getResources().getIdentifier("videoView1", "id", getPackageName()));
//		videoView.setMediaController(new MediaController(this));
		videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {  
		@Override  
        public void onPrepared(MediaPlayer mp) {  
            mp.setLooping(true);  
        }  
		});
		
		imageView = (ImageView)this.findViewById(getResources().getIdentifier("imageView", "id", getPackageName()));
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        mEndCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallUIFactory.endCall(curUseTarget, OverLayActivity.this);
            }
        });
        mAnswerCallBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	CallUIFactory.answerCall(curUseTarget, OverLayActivity.this);
            }
        });
        endCallSensMessageBt.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mEndCallBt.setVisibility(View.GONE);
        		mAnswerCallBt.setVisibility(View.GONE);
        		mute.setVisibility(View.GONE);
        		endCallSensMessageBt.setVisibility(View.GONE);
        		show_message.setVisibility(View.VISIBLE);
        	}
        });
        canclaMessage.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mEndCallBt.setVisibility(View.VISIBLE);
        		mAnswerCallBt.setVisibility(View.VISIBLE);
        		mute.setVisibility(View.VISIBLE);
        		endCallSensMessageBt.setVisibility(View.VISIBLE);
        		show_message.setVisibility(View.GONE);
        	}
        });
        mute.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		toggleRingerMute(OverLayActivity.this);
        	}
        });
        speaker.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		toggleSpeaker(OverLayActivity.this,toggleSpeakerOn);
    			toggleSpeakerOn = !toggleSpeakerOn;
        	}
        });
        keyboard.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		keyboard(OverLayActivity.this,keyboardOn);
        		keyboardOn = !keyboardOn;
        	}
        });
    }
    @SuppressLint("InlinedApi")
	private void keyboard(Context context, boolean isOpentoggleSpeaker){
    	 AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	
    }
    @SuppressLint("InlinedApi")
    private void toggleSpeaker(Context context, boolean isOpentoggleSpeaker){
    	AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	try {
    		//播放音频流类型
    		setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    		//获得当前类
    		Class audioSystemClass = Class.forName("android.media.AudioSystem");
    		//得到这个方法
    		Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
    		if (isOpentoggleSpeaker) {
    			audioManager.setMicrophoneMute(false);
    			audioManager.setSpeakerphoneOn(true);
    			audioManager.setMode(AudioManager.MODE_NORMAL);
    			
    			// setForceUse.invoke(null, 1, 1);
    		} else {
    			audioManager.setSpeakerphoneOn(false);
    			audioManager.setMode(AudioManager.MODE_NORMAL);
    			setForceUse.invoke(null, 0, 0);
    			
    			audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    //jingyin
    private void toggleRingerMute(Context context){
    	AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
    	mAudioManager.setStreamMute(android.media.AudioManager.STREAM_MUSIC, true);
    }
    
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try {
            unregisterReceiver(mEndCallReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    
     class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		List<String> list;
		
		public MyAdapter(Context context, List<String> list) {
			this.mInflater = LayoutInflater.from(context);
			this.list = list;
		}

        @Override
        public int getCount() {
            return list.size();    
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
             ViewHolder holder;
            if (convertView == null) {
            	convertView = mInflater.inflate(getResources().getIdentifier("csx_sdk_zc_item_message", "layout", getPackageName()),null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(getResources().getIdentifier("text", "id", getPackageName()));
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();             
            }
            holder.text.setText(list.get(position));
            
            holder.text.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					PendingIntent paIntent = PendingIntent.getBroadcast(OverLayActivity.this, 0, new Intent(), 0
	        				); 
	        		SmsManager.getDefault().sendTextMessage(phoneNumBer, null, list.get(position), paIntent, 
	                         null);
	        		CallUIFactory.endCall(curUseTarget, OverLayActivity.this);
				}
            	
            });
            return convertView;
        }
    
    }
	class ViewHolder{
	   TextView text;
    }
}

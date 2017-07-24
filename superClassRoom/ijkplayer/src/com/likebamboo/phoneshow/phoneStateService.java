package com.likebamboo.phoneshow;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.likebamboo.phoneshow.config.ShowPref;
import com.likebamboo.phoneshow.widget.CallUIFactory;
import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.db.DBManager;
import com.zancheng.callphonevideoshow.http.listenphoneend.HttpMessageThreadPhoneState;
import com.zancheng.callphonevideoshow.object.Linkmen;

public class phoneStateService extends Service {
	
    public static String BUNDLE_KEY_VIDEOPATH = "videoPath";
    public static String BUNDLE_KEY_ISSHOWVIDEO = "isShowVideo";
    public static String BUNDLE_KEY_MYPHONENUM = "myPhoneNum";
	private TelephonyManager telMgr = null;
    private AudioManager aManger = null;
    private static final Object monitor = new Object();
    public DBManager mgr;
	protected static phoneStateService service;
	protected String videoPath;
	protected boolean isShowVideo = true;
	private MyReceiver myReceiver;
    /** 用于Handler里的消息类型 */  
    public static final int MSG_SET_SHOW_VIDEO = 1;  
    public static final int MSG_SET_SHOW_VIDEO_PATH = MSG_SET_SHOW_VIDEO+1;  
    public static final int MSG_SET_ALL = MSG_SET_SHOW_VIDEO_PATH+1;  
    public static final int MSG_SET_PHONENUM = MSG_SET_ALL+1; 
    protected long watchDogDelayMillis = 1000*60*10;
	private String myPhoneNum;  
	HttpMessageThreadPhoneState httpMessageThreadPhoneState;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate() {
    	super.onCreate();
    	
    	service = this;
    	telMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
    	telMgr.listen(new TelListener(), PhoneStateListener.LISTEN_CALL_STATE);  
    	aManger = (AudioManager) service.getSystemService(Service.AUDIO_SERVICE); 
    	
//    	@SuppressWarnings("deprecation")
//		Notification notification = new Notification(R.drawable.icon,  
//			 getString(R.string.app_name), System.currentTimeMillis());  
//		 PendingIntent pendingintent = PendingIntent.getActivity(this, 0, 
//		 new Intent(this, phoneStateService.class), 0);  
//		 notification.setLatestEventInfo(this, getString(R.string.app_name), "保证你的来电视频正常运行",  
//		 pendingintent);
//		 startForeground(0x111, notification);
		 
		 NotificationManager manager = (NotificationManager) 
				 getSystemService(NOTIFICATION_SERVICE);
		 NotificationCompat.Builder builder = new NotificationCompat.Builder(service);//hu
		 Notification notification = builder.setContentTitle(getString(getResources().getIdentifier("app_name", "string", getPackageName())))
				 .setContentText("保证你的来电视频正常运行")
				 .setWhen(System.currentTimeMillis())
				 .setSmallIcon(getResources().getIdentifier("ic_launcher", "drawable", getPackageName()))
				 .setLargeIcon(BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("ic_launcher", "drawable", getPackageName())))
				 .build();
		 
		 //增加点击事件 
//		 Intent notificationIntent = new Intent(Intent.ACTION_MAIN);  
//		    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
//		    notificationIntent.setClass(service, SDKMain.class);  
//		    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  
//		            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  
//		    PendingIntent intent = PendingIntent.getActivity(service, 0,  
//		            notificationIntent, 0);  
//		    notification.setLatestEventInfo(service, getString(R.string.app_name), "保证你的来电视频正常运行", intent);  
		    notification.flags |= Notification.FLAG_NO_CLEAR;  
		 manager.notify(1, notification);
		 
		 mgr = new DBManager(service); 
         
        myReceiver = new MyReceiver();  
        IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);  
        registerReceiver(myReceiver, filter);
        handler.postDelayed(runnable, watchDogDelayMillis);
        ShowPref pref = ShowPref.getServiceInstance(service);
        //========!dongtest==============
        pref.putInt(ShowPref.SHOW_TYPE, ShowPref.TYPE_ACTIVITY);
        
        isShowVideo = pref.loadInt(ShowPref.IS_SHOW_VIDEO)==1?true:false;
    	videoPath = pref.loadString(ShowPref.DEFAULT_VIDEO_PATH);
    	myPhoneNum = pref.loadString(ShowPref.MY_PHONENUMBER);
    	
    }

    Handler handler=new Handler(); 
    Runnable runnable=new Runnable(){  
    	   @Override  
    	   public void run() {  
    	    // TODO Auto-generated method stub  
		    unregisterReceiver(myReceiver);
		    IntentFilter filter = new IntentFilter();  
	        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);  
		    registerReceiver(myReceiver, filter);
    	    handler.postDelayed(this, watchDogDelayMillis);  
    	   }   
    	};
    	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { 
    	 flags = START_STICKY; 
		 return super.onStartCommand(intent, flags, startId); 
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mgr.closeDB();  
        aManger = null;
        telMgr = null;
        stopForeground(true);
        unregisterReceiver(myReceiver);
    }
  
    class IncomingHandler extends Handler {  
        @Override  
        public void handleMessage(Message msg) { 
        	ShowPref pref = ShowPref.getServiceInstance(service);
        	Bundle bundle=(Bundle)msg.obj;
            switch (msg.what) {  
                case MSG_SET_SHOW_VIDEO:  
            		isShowVideo = bundle.getBoolean(BUNDLE_KEY_ISSHOWVIDEO);
            		pref.putInt(ShowPref.IS_SHOW_VIDEO, isShowVideo?1:0);
                    break;  
                case MSG_SET_PHONENUM:
                	myPhoneNum = bundle.getString(BUNDLE_KEY_MYPHONENUM);
                	pref.putString(ShowPref.MY_PHONENUMBER, myPhoneNum);
                    break; 
                case MSG_SET_SHOW_VIDEO_PATH: 
            		videoPath = bundle.getString(BUNDLE_KEY_VIDEOPATH);
            		pref.putString(ShowPref.DEFAULT_VIDEO_PATH, videoPath);
                    break; 
                case MSG_SET_ALL:
                	isShowVideo = bundle.getBoolean(BUNDLE_KEY_ISSHOWVIDEO);
                	videoPath = bundle.getString(BUNDLE_KEY_VIDEOPATH);
                	myPhoneNum = bundle.getString(BUNDLE_KEY_MYPHONENUM);
                	pref.putInt(ShowPref.IS_SHOW_VIDEO, isShowVideo?1:0);
                	pref.putString(ShowPref.DEFAULT_VIDEO_PATH, videoPath);
                	pref.putString(ShowPref.MY_PHONENUMBER, myPhoneNum);
                	break;
                default:  
                    super.handleMessage(msg);  
            }  
        }  
    }  
    final Messenger mMessenger = new Messenger(new IncomingHandler());
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return mMessenger.getBinder();  
    }  
    
    private class TelListener extends PhoneStateListener{ 
    	Timer timer = null;
    	String linkmenNumber;
		@Override  
        public void onCallStateChanged(int state, String getNumber) {
                  switch (state) {
                      case TelephonyManager.CALL_STATE_RINGING:// 来电响铃
                    	  Linkmen linkmen = mgr.queryLinkmenVideoPath(getNumber);
                    	  String curVideoPath = "";
                    	  if(linkmen.videoPath != null){
                    		  curVideoPath = linkmen.videoPath;
                    	  }else{
                    		  curVideoPath = videoPath;
                    	  }
                    	  
//                          System.out.println("....................来电响铃...................."+aaa.videoPath);
                          if(MyAppData.callState != Constants.CS_DEFAULT){
                    		  return;
                    	  }
                          
                          if(getNumber == null){
                        	  getNumber = Constants.WEIZHI_MESSAGE;
                          }
                          linkmenNumber = getNumber;
                          final String finalCurVideoPath = curVideoPath;
                          synchronized (monitor) {
                        	  MyAppData.callState = Constants.CS_RINGING;
                        	  if(changeVolume() == false){
                        		  break;
                        	  }
                        	  timer = new Timer();
                        	  timer.schedule(new TimerTask(){    
                        		     public void run(){    
                        		    	 changeVolume();
                        		     }    
                        		}, 0, 100); 
                              new Handler().postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      // TODO Auto-generated method stub
                                	  if(MyAppData.callState == Constants.CS_RINGING){
                                		  showWindow(service, linkmenNumber, finalCurVideoPath);
                                	  }
                                	  timer.cancel();
                            		  timer = null;
                            		  
                                  }
                              }, 1000);
                              
                              httpMessageThreadPhoneState = HttpMessageThreadPhoneState.sendPhoneState(service, myPhoneNum, linkmenNumber);
                          }
                          break;
                      case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话
                      	synchronized (monitor) {
                  			//下面的逻辑保证了用户在通话中本应用保持正常运行
                  			if(MyAppData.callState == Constants.CS_DIAL){
                  			//外拨处理逻辑
                  				MyAppData.callState = Constants.CS_CALLING;
                  				break;
                  			}else if(MyAppData.callState == Constants.CS_RINGING){
                  			//来电接通逻辑
                  				MyAppData.callState = Constants.CS_ANSWER;
                  				recoverVolume();
                                closeWindow(service);
                  			}
                      	}
                             
                          break;
                      case TelephonyManager.CALL_STATE_IDLE:// 挂断电话
                          synchronized (monitor) {
//                        	  if((MyAppData.callState == Constants.CS_CALLING || MyAppData.callState == Constants.CS_DIAL) 
//                        			  && MyAppData.getInst().needAutoDownVideoUrl != null){
//                        		  final String needAutoDownVideoUrl = MyAppData.getInst().needAutoDownVideoUrl;
//                        		  MyAppData.getInst().needAutoDownVideoUrl = null;
//                        		  new Handler().postDelayed(new Runnable(){    
//                      			    public void run() {    
//                      			    	HttpMessageThreadDownLoad asyncTask = new HttpMessageThreadDownLoad(needAutoDownVideoUrl);  
//                            		  	asyncTask.execute();  
//                      			    }    
//                      			 }, 10000); 
//                        	  }
                        	  MyAppData.callState = Constants.CS_DEFAULT;
                        	  recoverVolume();
                              closeWindow(service);
                              if(httpMessageThreadPhoneState != null){
                            	  httpMessageThreadPhoneState.cancel(true);
                            	  httpMessageThreadPhoneState = null;
                              }
                             
                              System.out.println("....................CALL_STATE_IDLE....................");
                          }
                          break;
                      default:
                    	  synchronized (monitor) {
                    		  MyAppData.callState = Constants.CS_DEFAULT;
                    		  recoverVolume();
                    	  }
                          break;
                  }
              }

        }
    	
    
    private boolean changeVolume() {
    	if(MyAppData.callSetVolume==false || aManger.getStreamVolume(AudioManager.STREAM_RING) != 0){
    		MyAppData.originalMusicVolume = aManger.getStreamVolume(AudioManager.STREAM_MUSIC);
    		float musicToRingVolumescale = (float)aManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/aManger.getStreamMaxVolume(AudioManager.STREAM_RING);
    		int defineRingVolume = (int)(aManger.getStreamVolume(AudioManager.STREAM_RING)*musicToRingVolumescale+0.5);
        	aManger.setStreamVolume(AudioManager.STREAM_MUSIC, defineRingVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        	aManger.setStreamMute(AudioManager.STREAM_RING, true);
        	MyAppData.callSetVolume = true;
        	return true;
    	}
    	return false;
	}
    
    private void recoverVolume() {
    	if(MyAppData.callSetVolume){
    		MyAppData.callSetVolume = false;
   		 	aManger.setStreamMute(AudioManager.STREAM_RING, false); 
          	aManger.setStreamVolume(AudioManager.STREAM_MUSIC, MyAppData.originalMusicVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    	}
       	
    }


	/**
     * 显示来电弹窗
     * 
     * @param ctx 上下文对象
     * @param number 电话号码
     */
    private void showWindow(final Context ctx, String number, String videoPath) {
    	
    	if (!isShowVideo)
		{
			return;
		}
    	CallUIFactory.tryShowByHttp(CallUIFactory.SHOW_NORMAL, CallUIFactory.cur_UiAnswerMode, ctx, myPhoneNum, number, videoPath);
    }

    /**
     * 关闭来电弹窗
     * 
     * @param ctx 上下文对象
     */
    private void closeWindow(Context ctx) {
    	if (!isShowVideo)
		{
			return;
		}
    	CallUIFactory.hide(ctx);
    }
    
	private class MyReceiver extends BroadcastReceiver {  
    	
        @Override
		public void onReceive(final Context context, Intent intent) {
		    if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
	    		
				MyAppData.callState = Constants.CS_CILCK_DIAL_BUTTON;
				CallUIFactory.callUiAlreadyOpen = false;
				String callNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				if(callNumber == null){
					callNumber = Constants.WEIZHI_MESSAGE;
				}
				final String phoneNumber = callNumber;
				
				new Handler().postDelayed(new Runnable() {
		             @Override
		             public void run() {
		            	 if(MyAppData.callState != Constants.CS_DEFAULT){
		            		 service.showWindow(service, phoneNumber, null);
                  	  	}
		             }
		         }, 1000);
				
				new Handler().postDelayed(new Runnable() {
		             @Override
		             public void run() {
		            		 service.closeWindow(service);
		             }
		         }, 15000);
				httpMessageThreadPhoneState = HttpMessageThreadPhoneState.getPhoneState(context, ((phoneStateService)context).myPhoneNum, phoneNumber);
				
		      }
		
		}

    }
    
    
}

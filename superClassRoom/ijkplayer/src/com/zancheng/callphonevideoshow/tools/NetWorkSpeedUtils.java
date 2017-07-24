package com.zancheng.callphonevideoshow.tools;
import android.content.Context;  
import android.net.TrafficStats;  
import android.os.Handler;  
import android.os.Message;  
  

import java.util.Timer;  
import java.util.TimerTask;  

import com.zancheng.callphonevideoshow.Constants;
  
public class NetWorkSpeedUtils {  
    private Context context;  
    private Handler mHandler;  
  
    private long lastTotalRxBytes = 0;  
    private long lastTimeStamp = 0;  
    Timer timer ;
  
    public NetWorkSpeedUtils(Context context, Handler mHandler){  
        this.context = context;  
        this.mHandler = mHandler;  
    }  
  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            showNetSpeed();  
        }  
    };  
  
    public void startShowNetSpeed(){  
    	timer = new Timer();
        lastTotalRxBytes = getTotalRxBytes();  
        lastTimeStamp = System.currentTimeMillis();  
        timer.schedule(task, 1000, 1000); // 1s后启动任务，每1s执行一次  
    }  
  
    private long getTotalRxBytes() {  
        return TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB  
    } 
    
    public void stopShowNetSpeed() {
    	if (timer != null) {
    		timer.cancel();
		}
	}
  
    private void showNetSpeed() {  
        long nowTotalRxBytes = getTotalRxBytes();  
        long nowTimeStamp = System.currentTimeMillis();  
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换  
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换  
  
        lastTimeStamp = nowTimeStamp;  
        lastTotalRxBytes = nowTotalRxBytes;  
  
        Message msg = mHandler.obtainMessage();  
        msg.what = Constants.HANDLER_MESSAGE_ID_NETWORKSPEEK;  
        msg.obj = String.valueOf(speed) + "." + String.valueOf(speed2) + " kb/s";
        msg.arg1 = (int) speed;//直接发送网速数值
        mHandler.sendMessage(msg);//更新界面  
    }  
}  
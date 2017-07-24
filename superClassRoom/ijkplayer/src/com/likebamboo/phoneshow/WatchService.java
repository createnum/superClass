
package com.likebamboo.phoneshow;

import com.zancheng.callphonevideoshow.tools.Tool;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class WatchService extends Service {
    protected long watchDogDelayMillis = 1000*10;

    Handler handler=new Handler(); 
    Runnable runnable=new Runnable(){  
    	   @Override  
    	   public void run() {  
			   if(!Tool.isServiceWork(WatchService.this, phoneStateService.class.getName())){
				   Intent service = new Intent(WatchService.this, phoneStateService.class);
					startService(service); 
			   }
			   handler.postDelayed(this, watchDogDelayMillis);  
    	   }   
    	};
    	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { 
    	 flags = START_STICKY; 
		 return super.onStartCommand(intent, flags, startId); 
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(runnable, watchDogDelayMillis);
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
 
}

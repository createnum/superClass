package com.zancheng.callphonevideoshow.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.SDKMain;
import com.zancheng.callphonevideoshow.show.commonShow.Receiver;
import com.zancheng.callphonevideoshow.assembly.RoundProgressBar;
@SuppressLint("NewApi")
public class MyDownload {
	protected DownloadManager downloadManager;
    protected ScheduledExecutorService scheduledExecutorService;
    protected long downLoadId;
    protected ProgressBar progressBar;
    Queue<String> curQueue = new LinkedList<String>();
    Queue<Queue<String>> remainderQueue = new LinkedList<Queue<String>>();
    Queue<Long> downloadIdqueue = new LinkedList<Long>();
	private boolean isRuning;  
	private int progressValue;
	private RoundProgressBar roundProgressBar;
	private String curDownTempFilePath;
	protected static MyDownload inst;
    
	public static MyDownload getInst(){
		if(null == inst){
			inst = new MyDownload();
		}
		return inst;
	}
    protected MyDownload() {
		downloadManager = (DownloadManager)SDKMain.sdkContext.getSystemService(Context.DOWNLOAD_SERVICE);  
		scheduledExecutorService = Executors.newScheduledThreadPool(2);
	}
    
    
    public static void instSetProgressBar(RoundProgressBar roundProgressBar, ProgressBar progressBar){
    	if(inst != null){
    		inst.setProgressBar(roundProgressBar, progressBar);
    	}
    }
    
    public static void instDownloadDestroy(){
    	if(inst != null){
    		inst.downloadDestroy();
    	}
    }
    
    public void setProgressBar(RoundProgressBar roundProgressBar, ProgressBar progressBar){
    	if (roundProgressBar != null) {
    		this.roundProgressBar = roundProgressBar;
    		this.progressBar = null;
    		return;
		}
    	if(progressBar != null){
    		this.progressBar = progressBar;
    		this.roundProgressBar = null;
    		return;
    	}
		this.roundProgressBar = roundProgressBar;
		this.progressBar = progressBar;
    }
    
    public boolean addDownloadQueue(String picUrl, String videoUrl) {
    	if(picUrl == null || videoUrl == null){
    		return false;
    	}
    	addDownLoadTask(picUrl, videoUrl);
    	return true;
	}
    
    public void downloadStart(){
		if(!isRuning){
			isRuning = true;
			downloadNextFile(curQueue.poll());
	        scheduledExecutorService.scheduleWithFixedDelay(command, 0, 1000, TimeUnit.MILLISECONDS);
		}
	} 
	
    protected boolean downloadDestroy() {
    	if(isRuning){ 
    		Long id = downloadIdqueue.poll();
    		while (id != null) {
    			downloadManager.remove(id);  
    			id = downloadIdqueue.poll();
			}
    		downloadStop();
        	return true;
    	}
    	return true;
	}
    
    protected boolean downloadStop() {
    	if(isRuning){
    		isRuning = false;
        	scheduledExecutorService.shutdownNow();
        	inst = null;
        	return true;
    	}
    	return true;
	}
	
	protected synchronized void addDownLoadTask(String picUrl, String videoUrl){
		if(curQueue.peek() == null && progressValue == 0){
    		curQueue.offer(videoUrl);
    		curQueue.offer(picUrl);
    	}else{
    		Queue<String> queue = new LinkedList<String>();
    		queue.offer(videoUrl);
    		queue.offer(picUrl);
    		remainderQueue.offer(queue);
    	}
	}

	protected synchronized void nextDownLoadTask(){
		if(remainderQueue.peek() != null){
			Queue<String> queue = remainderQueue.poll();
			curQueue.offer(queue.poll());
			curQueue.offer(queue.poll());
			downloadNextFile(curQueue.poll());
		}else{
			downloadStop(); 
		}
	}
	
	protected void downloadNextFile(String url){
		if(url == null){
			return;
		}
        Uri resource = Uri.parse(Tool.encodeUTF(url));   
        DownloadManager.Request request = new DownloadManager.Request(resource);   
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);   
        request.setAllowedOverRoaming(false);   
        //设置文件类型  
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();  
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));  
        request.setMimeType(mimeString);  
        //在通知栏中显示   
        request.setShowRunningNotification(false);  
        request.setVisibleInDownloadsUi(true);  
        //sdcard的目录下的download文件夹  
        String fileAllName = Tool.getFileAllNameFromPath(url);
        curDownTempFilePath = fileAllName+"._temp";
        if(Tool.getFileTypeFromPath(fileAllName).equals("png") || Tool.getFileTypeFromPath(fileAllName).equals("jpg")){
        	request.setDestinationInExternalPublicDir(Constants.DOWNLOAD_PIC_DIR, curDownTempFilePath);  
        }else{
        	request.setDestinationInExternalPublicDir(Constants.DOWNLOAD_VIDEO_DIR, curDownTempFilePath);  
        }
//      request.setTitle("赞成下载测试");   
		downLoadId = downloadManager.enqueue(request);  
        downloadIdqueue.offer(downLoadId);
        //保存id   
	} 
	
	
    private void queryDownloadStatus() {   
    	
        DownloadManager.Query query = new DownloadManager.Query();   
        query.setFilterById(downLoadId);   
        Cursor c = downloadManager.query(query);   
        if(c.moveToFirst()) {   
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));   
            switch(status) {   
            case DownloadManager.STATUS_PAUSED:   
            	break;   
            case DownloadManager.STATUS_PENDING:   
            	break;   
            case DownloadManager.STATUS_RUNNING:   
                //正在下载，不做任何事情  
            	 int fileSizeIdx =c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));        
            	 int bytesDLIdx =c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            	 if(curQueue.peek() != null){
            	   progressValue = (int)(((float)bytesDLIdx/fileSizeIdx)*100);
            	   if(progressValue == 100){
            		   progressValue = 99;
            		   //是为了避免progressValue=100  导致后续文件不能继续下载
            	   }
               }
                break;   
            case DownloadManager.STATUS_SUCCESSFUL:  
            	if(curDownTempFilePath != null){
            		String filePath = curDownTempFilePath.split("._temp")[0];
					if(Tool.getFileTypeFromPath(filePath).equals("png") || Tool.getFileTypeFromPath(filePath).equals("jpg")){
						Tool.renameFile(Constants.DOWNLOAD_PIC_ABSOLUTE_PATH+curDownTempFilePath, Constants.DOWNLOAD_PIC_ABSOLUTE_PATH+filePath);
					}else{
						Tool.renameFile(Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH+curDownTempFilePath, Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH+filePath);
					}
				}
                //完成  
            	if(curQueue.peek() == null){
            		progressValue = 100;
            	}else{
            		downloadNextFile(curQueue.poll());
            	}
                break;   
            case DownloadManager.STATUS_FAILED:   
                //清除已下载的内容，重新下载  
            	downloadDestroy();
                break;   
            }   
        }  
        c.close();
    } 
    	
    Runnable command = new Runnable() {
		@Override
		public void run() {
			queryDownloadStatus();

			if(roundProgressBar != null){
				roundProgressBar.setProgress(progressValue);
			}
			if(progressBar != null){
				progressBar.setProgress(progressValue);
//				System.out.println("11111111"+progressValue);
			}
			if(progressValue == 100){
				Intent intent = new Intent();  
				intent.setAction(Receiver.DOWNLOAD_FINISH_OF_VIDEO); 
				SDKMain.sdkContext.sendBroadcast(intent);
				nextDownLoadTask();
				progressValue = 0;
			}
		}
	};
	

}

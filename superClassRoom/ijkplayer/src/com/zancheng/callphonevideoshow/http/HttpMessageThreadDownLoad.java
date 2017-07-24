package com.zancheng.callphonevideoshow.http;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

class HttpMessageThreadDownLoad extends AsyncTask<Void, Void, Void> {
	
	protected String url;
	protected boolean downFinish;
	protected int downNum;
	
	protected HttpMessageThreadDownLoad() {  
	       super();  
	}  
	 
	protected HttpMessageThreadDownLoad(String getShowVideoUrl) {
		this.url = getShowVideoUrl;
	}
	
	protected HttpMessageThreadDownLoad(String getShowVideoUrl, int downNum) {
		this.url = getShowVideoUrl;
		this.downNum = downNum;
	}

	@Override
	protected Void doInBackground(Void... params) {
        send();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
//		if(!downFinish && downNum < 2 && Tool.isNetworkAvailable(MainTabActivity.myMainActivity, "WIFI")){
//			new Handler().postDelayed(new Runnable(){    
//			    protected void run() {    
//			    	HttpMessageThreadDownLoad asyncTask = new HttpMessageThreadDownLoad(url, downNum);  
//        		  	asyncTask.execute();  
//			    }    
//			 }, 10000); 
//			
//		}
	}
	
	private void send() {
		downFinish = downHttpFileToPhoneSdCard(url, MyAppData.getInst().phoneSDCardVideosList);
		downNum++;
	}
	
	protected boolean downHttpFileToPhoneSdCard(String fromurl, List<String> phoneSDCardVideosList) {
		boolean downFinish = true;
		String newFilename = fromurl.substring(fromurl.lastIndexOf("/") + 1);
		String _dirName = Tool.getPhoneSdCardSonPath(Constants.DOWNLOAD_PHONE_SDCARD_VIDEO_SON_PATH);
		if (_dirName == null) {
			return false;
		}
		newFilename = _dirName + newFilename;
		File file = new File(newFilename);
		// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if (file.exists()) {
			return true;
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			MyAppData.getInst().isDownloadingVideoPath = newFilename;
			// 构造URL
			URL url = new URL(fromurl);
			// 打开连接
			URLConnection con = url.openConnection();
			// //获得文件的长度
			// int contentLength = con.getContentLength();
			// System.out.println("长度 :"+contentLength);
			// 输入流
			is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			os = new FileOutputStream(newFilename);
			// 开始读取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			phoneSDCardVideosList.add(newFilename);
		} catch (Exception e) {
			downFinish = false;
			if (file.exists()) {
				file.delete();
			}
			e.printStackTrace();
		} finally {
			MyAppData.getInst().isDownloadingVideoPath = null;
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		while (phoneSDCardVideosList.size() > 2) {
			File deleteFile = new File(phoneSDCardVideosList.get(0));
			if (deleteFile.exists()) {
				deleteFile.delete();
			}
			phoneSDCardVideosList.remove(0);
		}
		File dirFile = new File(_dirName);
		if (phoneSDCardVideosList.size() != dirFile.list().length) {
			phoneSDCardVideosList.clear();
			for (int i = 0; i < dirFile.list().length; i++) {
				phoneSDCardVideosList.add(_dirName + dirFile.list()[i]);
			}
		}

		return downFinish;
	}
}

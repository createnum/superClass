package com.zancheng.callphonevideoshow.db;
  
import java.util.ArrayList;  
import java.util.List;  
  



import com.zancheng.callphonevideoshow.object.Linkmen;
import com.zancheng.callphonevideoshow.object.VideoInfo;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
  
public class DBManager {  
    private DBHelper helper;  
    private SQLiteDatabase db;  
      
    public DBManager(Context context) {  
        helper = new DBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  

    public void addAndUpdateLinkmenVideo(Linkmen linkmen) { 
    	String myPhoneNumberFormat = Tool.getMyPhoneNumberFormat(linkmen.mContactsNumber);
    	boolean linkmenIsSet = selectLinkmenVideoIsExist(myPhoneNumberFormat);
    	if(linkmenIsSet){
    		ContentValues cv = new ContentValues();  
            cv.put("videoId", linkmen.videoId);  
            cv.put("videoTitle", linkmen.videoTitle);  
            cv.put("videoPath", linkmen.videoPath);  
            db.update("linkmen", cv, "phoneNumber = ?", new String[]{myPhoneNumberFormat}); 
            return;
    	}
        db.beginTransaction();  //开始事务  
        try {  
    		db.execSQL("INSERT INTO linkmen VALUES(null, ?, ?, ?, ?)",
            		new Object[]{
    				myPhoneNumberFormat,
            		linkmen.videoId,
            		linkmen.videoTitle,
            		linkmen.videoPath
            }); 
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }
    
    protected boolean selectLinkmenVideoIsExist(String phoneNumber) {  
    	boolean isExist = false;
        Cursor c = db.rawQuery("SELECT count(1) as num FROM linkmen where phoneNumber=?", new String[]{phoneNumber});
        while (c.moveToNext()) {  
        	int num = c.getInt(c.getColumnIndex("num")); 
        	if(num > 0){
        		isExist = true;
        	}
        }  
        c.close();  
        return isExist;  
    } 
    
    public Linkmen queryLinkmenVideoPath(String phoneNumber) {  
    	phoneNumber = Tool.getMyPhoneNumberFormat(phoneNumber);
    	Linkmen linkmen = new Linkmen();
        Cursor c = db.rawQuery("SELECT * FROM linkmen where phoneNumber=?", new String[]{phoneNumber});  
        while (c.moveToNext()) {  
        	linkmen.videoPath = c.getString(c.getColumnIndex("videoPath"));  
        	linkmen.videoId = c.getInt(c.getColumnIndex("videoId"));  
        	linkmen.videoTitle = c.getString(c.getColumnIndex("videoTitle"));  
        }  
        c.close();  
        return linkmen;  
    }
    
    public void addLocalVideo(VideoInfo videoInfo) {  
        db.beginTransaction();  //开始事务  
        try {  
                db.execSQL("INSERT INTO videoLocal VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                		new Object[]{
                		videoInfo.id,
                		videoInfo.title,
                		videoInfo.author,
                		videoInfo.imgPath,
                		videoInfo.videoPath,
                		videoInfo.videoTypeMain,
                		videoInfo.videoTypeBySpecial,
                		videoInfo.imgType,
                		videoInfo.marvellous,
                		videoInfo.playCount,
                		videoInfo.likeCount,
                		videoInfo.local,
                		videoInfo.onlyLook,
                		videoInfo.price
                		
                });  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }
    
    public int queryLocalNum() {  
    	int num = 0;
        Cursor c = db.rawQuery("SELECT count(1) as num FROM videoLocal", null);  
        while (c.moveToNext()) {  
        	num = c.getInt(c.getColumnIndex("num"));  
        }  
        c.close();  
        return num;  
    }  
    
    public List<VideoInfo> queryLocalList() {  
        ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>();  
        Cursor c = db.rawQuery("SELECT * FROM videoLocal limit 50", null);  
        while (c.moveToNext()) {  
        	VideoInfo videoInfo = new VideoInfo();  
        	videoInfo.id = c.getInt(c.getColumnIndex("id"));  
        	videoInfo.title = c.getString(c.getColumnIndex("title"));  
        	videoInfo.author = c.getString(c.getColumnIndex("author"));  
        	videoInfo.imgPath = c.getString(c.getColumnIndex("imgPath"));  
        	videoInfo.videoPath = c.getString(c.getColumnIndex("videoPath"));  
        	videoInfo.videoTypeMain = c.getInt(c.getColumnIndex("videoTypeMain"));  
        	videoInfo.videoTypeBySpecial = c.getInt(c.getColumnIndex("videoTypeByspecial"));  
        	videoInfo.imgType = c.getInt(c.getColumnIndex("imgType"));  
        	videoInfo.price = c.getInt(c.getColumnIndex("price"));  //*****hu
        	int marvellousInt = c.getInt(c.getColumnIndex("marvellous"));
        	videoInfo.marvellous = (marvellousInt==0)?false:true;
        	videoInfo.playCount = c.getInt(c.getColumnIndex("playCount"));  
        	videoInfo.likeCount = c.getInt(c.getColumnIndex("likeCount"));  
        	videoInfo.local = (c.getInt(c.getColumnIndex("local")) == 0)?false:true; 
        	videoInfo.onlyLook = (c.getInt(c.getColumnIndex("onlyLook")) == 0)?false:true; 
        	videoInfos.add(videoInfo);  
        }  
        c.close();  
        return videoInfos;  
    } 
    
	public VideoInfo getLocalVideoInfo(int serverId) {
		VideoInfo videoInfo = null;
        Cursor c = db.rawQuery("SELECT * FROM videoLocal where id=?",  new String[]{String.valueOf(serverId)});  
        while (c.moveToNext()) {  
        	videoInfo = new VideoInfo();
        	videoInfo.id = c.getInt(c.getColumnIndex("id"));  
        	videoInfo.title = c.getString(c.getColumnIndex("title"));  
        	videoInfo.author = c.getString(c.getColumnIndex("author"));  
        	videoInfo.imgPath = c.getString(c.getColumnIndex("imgPath"));  
        	videoInfo.videoPath = c.getString(c.getColumnIndex("videoPath"));  
        	videoInfo.videoTypeMain = c.getInt(c.getColumnIndex("videoTypeMain"));  
        	videoInfo.videoTypeBySpecial = c.getInt(c.getColumnIndex("videoTypeByspecial"));  
        	videoInfo.imgType = c.getInt(c.getColumnIndex("imgType"));  
        	videoInfo.price = c.getInt(c.getColumnIndex("price"));  //*****hu
        	int marvellousInt = c.getInt(c.getColumnIndex("marvellous"));
        	videoInfo.marvellous = (marvellousInt==0)?false:true;
        	videoInfo.playCount = c.getInt(c.getColumnIndex("playCount"));  
        	videoInfo.likeCount = c.getInt(c.getColumnIndex("likeCount"));  
        	videoInfo.local = (c.getInt(c.getColumnIndex("local")) == 0)?false:true; 
    		videoInfo.onlyLook = (c.getInt(c.getColumnIndex("onlyLook")) == 0)?false:true; 
        }  
        c.close();  
        return videoInfo;  
	}
    
	public void addList(boolean isMarvellous, boolean isSpecial, List<VideoInfo> videoInfos) {  
       if(isMarvellous){
    	   addList("videoMarvellous", videoInfos);
       }else if(isSpecial){
    	   addList("videoSpecial", videoInfos);
       }else{
    	   addList("video", videoInfos);
       }
    } 
	
    /** 
     * add VideoInfo 
     * @param videoInfos 
     */  
	protected void addList(String tableName, List<VideoInfo> videoInfos) {  
        db.beginTransaction();  //开始事务  
        try {  
            for (VideoInfo videoInfo : videoInfos) {  
            	if(queryIsExist(tableName, videoInfo.id)){
            		continue;
            	}
                db.execSQL("INSERT INTO "+tableName+" VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,? , ?)",
                		new Object[]{
                		videoInfo.id,
                		videoInfo.title,
                		videoInfo.author,
                		videoInfo.imgPath,
                		videoInfo.videoPath,
                		videoInfo.videoTypeMain,
                		videoInfo.videoTypeBySpecial,
                		videoInfo.imgType,
                		videoInfo.marvellous,
                		videoInfo.playCount,
                		videoInfo.likeCount,
                		videoInfo.local,
                		videoInfo.onlyLook,
                		videoInfo.price
                });  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    } 
    
    /** 
     * query all videoInfo, return list 
     * @return List<VideoInfo> 
     */  
    public List<VideoInfo> queryList(boolean isMarvellous, int specialType, int videoTypeMain, boolean isRanking) {  
        ArrayList<VideoInfo> videoInfos = new ArrayList<VideoInfo>();  
        Cursor c;
        if(isRanking){
        	c = db.rawQuery("SELECT * FROM video order by playCount desc limit 20", null);  
        }else if(isMarvellous){
        	c = db.rawQuery("SELECT * FROM videoMarvellous where marvellous=? ",
					new String[]{isMarvellous?"1":"0"});  
        }else if(-1 != specialType){
        	c = db.rawQuery("SELECT * FROM videoSpecial where videoTypeByspecial=? ", new String[]{String.valueOf(specialType)}); 
        }else{
        	c = db.rawQuery("SELECT * FROM video where videoTypeMain=? ",
					new String[]{String.valueOf(videoTypeMain)});  
        }
        
        while (c.moveToNext()) {  
        	VideoInfo videoInfo = new VideoInfo();  
        	videoInfo.id = c.getInt(c.getColumnIndex("id"));  
        	videoInfo.title = c.getString(c.getColumnIndex("title"));  
        	videoInfo.author = c.getString(c.getColumnIndex("author"));  
        	videoInfo.imgPath = c.getString(c.getColumnIndex("imgPath"));  
        	videoInfo.videoPath = c.getString(c.getColumnIndex("videoPath"));  
        	videoInfo.videoTypeMain = c.getInt(c.getColumnIndex("videoTypeMain"));  
        	videoInfo.videoTypeBySpecial = c.getInt(c.getColumnIndex("videoTypeByspecial"));  
        	videoInfo.imgType = c.getInt(c.getColumnIndex("imgType"));  
        	int marvellousInt = c.getInt(c.getColumnIndex("marvellous"));
        	videoInfo.marvellous = (marvellousInt==0)?false:true;
        	videoInfo.playCount = c.getInt(c.getColumnIndex("playCount"));  
        	videoInfo.likeCount = c.getInt(c.getColumnIndex("likeCount"));  
        	videoInfo.local = (c.getInt(c.getColumnIndex("local")) == 0)?false:true; 
        	videoInfo.onlyLook = (c.getInt(c.getColumnIndex("onlyLook")) == 0)?false:true; 
        	videoInfo.price = c.getInt(c.getColumnIndex("price"));  //*****hu
        	videoInfos.add(videoInfo);  
        }  
        c.close();  
        return videoInfos;  
    } 
      
    /** 
     * update videoInfo's Local 
     * @param videoInfo 
     */  
    public void updateLocal(VideoInfo videoInfo) {  
        ContentValues cv = new ContentValues();  
        cv.put("local", videoInfo.local);  
        db.update("video", cv, "id = ?", new String[]{String.valueOf(videoInfo.id)});  
    }  
	
    /** 
     * delete old videoInfo 
     * @param videoInfo 
     */  
    public void deleteLocalVideoInfo(int id) {  
        db.delete("videoLocal", "id = ?", new String[]{String.valueOf(id)});  
    }  
      
    public void deleteOldVideos(boolean isMarvellous, int videoTypeMain, int videoTypeByspecial) {  
    	
    	if(isMarvellous){
    		 db.delete("videoMarvellous", "", new String[]{});  
    	}else if(videoTypeMain != -1){
    		 db.delete("video", "videoTypeMain = ?",
    	        		new String[]{String.valueOf(videoTypeMain)}); 
    	}else if(videoTypeByspecial != -1){
    		db.delete("videoSpecial", "videoTypeByspecial = ?",
	        		new String[]{String.valueOf(videoTypeByspecial)}); 
    	}
         
    }  
    
    
    public boolean queryIsExist(String tableName, int serverId) {  
    	boolean isExist = false;
        Cursor c = db.rawQuery("SELECT _id FROM "+tableName+" where id=?", new String[]{String.valueOf(serverId)});  
        while (c.moveToNext()) {  
        	isExist = true;
        }  
        c.close();  
        return isExist;  
    } 
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }

}  
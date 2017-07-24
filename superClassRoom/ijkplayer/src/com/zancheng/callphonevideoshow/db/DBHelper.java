package com.zancheng.callphonevideoshow.db;
  
import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteOpenHelper;  
  
public class DBHelper extends SQLiteOpenHelper {  
  
    private static final String DATABASE_NAME = "ZCCallPhone.db";  
    private static final int DATABASE_VERSION = 1;  
	
    public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //数据库第一次被创建时onCreate会被调用  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS video" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, title VARCHAR, author VARCHAR, "
                + "imgPath VARCHAR, videoPath VARCHAR, videoTypeMain INTEGER, videoTypeByspecial INTEGER"
                + ", imgType INTEGER, marvellous INTEGER, playCount INTEGER, likeCount INTEGER, local INTEGER, onlyLook INTEGER, price INTEGER)");
        
        db.execSQL("CREATE TABLE IF NOT EXISTS videoLocal"+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, title VARCHAR, author VARCHAR, "
                + "imgPath VARCHAR, videoPath VARCHAR, videoTypeMain INTEGER, videoTypeByspecial INTEGER"
                + ", imgType INTEGER, marvellous INTEGER, playCount INTEGER, likeCount INTEGER, local INTEGER, onlyLook INTEGER, price INTEGER)");  
        db.execSQL("CREATE TABLE IF NOT EXISTS videoMarvellous"+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, title VARCHAR, author VARCHAR, "
                + "imgPath VARCHAR, videoPath VARCHAR, videoTypeMain INTEGER, videoTypeByspecial INTEGER"
                + ", imgType INTEGER, marvellous INTEGER, playCount INTEGER, likeCount INTEGER, local INTEGER, onlyLook INTEGER, price INTEGER)"); 
        db.execSQL("CREATE TABLE IF NOT EXISTS videoSpecial"+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, title VARCHAR, author VARCHAR, "
                + "imgPath VARCHAR, videoPath VARCHAR, videoTypeMain INTEGER, videoTypeByspecial INTEGER"
                + ", imgType INTEGER, marvellous INTEGER, playCount INTEGER, likeCount INTEGER, local INTEGER, onlyLook INTEGER, price INTEGER)"); 
        db.execSQL("CREATE TABLE IF NOT EXISTS linkmen"+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, phoneNumber VARCHAR, "
                + "videoId INTEGER, videoTitle VARCHAR, videoPath VARCHAR)"); 
        
    }
  
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }  
}  
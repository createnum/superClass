package com.example.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	// 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 数据库名
    private static final String DATABASE_NAME = "superClassroom";

    //Contact表名
    private static final String TABLE_COLLECT = "collects";

    //Contact表的列名
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ARTICLE_id = "article_id";
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);  
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		   String CREATE_COLLECT_TABLE = "CREATE TABLE " + TABLE_COLLECT + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " VARCHAR,"
	                + KEY_ARTICLE_id + " INTEGER" + ")";
	        db.execSQL(CREATE_COLLECT_TABLE);
	        db.execSQL("CREATE TABLE  user ("
	                +   " id  INTEGER PRIMARY KEY,name VARCHAR,money INTEGER,rank INTEGER,"
	                + "giftMoney INTEGER,time VARCHAR)");
	        db.execSQL("CREATE TABLE  buy_class ("
	                +   " id  INTEGER PRIMARY KEY,name VARCHAR,class_id INTEGER)");
	        db.execSQL("CREATE TABLE  them ("
	                +   " id  INTEGER PRIMARY KEY,appid VARCHAR,them INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	




}

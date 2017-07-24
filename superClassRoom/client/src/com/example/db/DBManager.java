package com.example.db;

import java.util.ArrayList;
import java.util.List;

import com.example.moudle.BuyClass;
import com.example.moudle.Collect;
import com.example.moudle.Them;
import com.example.moudle.User;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class DBManager {
	private DatabaseHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DatabaseHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
	}

	public void addUser(User user) {
		db.beginTransaction(); // 开始事务
		try {
			ContentValues values = new ContentValues();
			values.put("name", user.getName());
			values.put("money", user.getMoney());
			values.put("rank", user.getRank());
			values.put("giftMoney", user.getGiftMoney());
			values.put("time", user.getTime());
			db.insert("user", null, values);
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public List<User> queryUser(String imsi) {
		ArrayList<User> users = new ArrayList<User>();
		Cursor c = db.rawQuery("SELECT * FROM user  where name=?",
				new String[] { imsi });
		while (c.moveToNext()) {
			User user = new User(imsi);
			user.setName(imsi);
			user.setMoney(c.getInt(c.getColumnIndex("money")));
			user.setGiftMoney(c.getInt(c.getColumnIndex("giftMoney")));
			user.setRank(c.getInt(c.getColumnIndex("rank")));
			user.setTime(c.getString(c.getColumnIndex("time")));
			users.add(user);
		}
		c.close();
		return users;
	}

	public void addBuyClass(BuyClass buyClass) {
		db.beginTransaction(); // 开始事务
		try {
			ContentValues values = new ContentValues();
			values.put("name", buyClass.getName());
			values.put("class_id", buyClass.getClass_id());
			db.insert("buy_class", null, values);
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void updateUser(User user) {
		ContentValues cv = new ContentValues();
		if (!"".equals(user.getMoney())) {
			cv.put("money", user.getMoney());
		}
		if (!"".equals(user.getRank())) {
			cv.put("rank", user.getRank());
		}
		if (!"".equals(user.getGiftMoney())) {
			cv.put("giftMoney", user.getGiftMoney());
		}
		if (!"".equals(user.getTime()) && user.getTime() != null) {
			cv.put("time", user.getTime());
		}
		db.update("user", cv, "name = ?",
				new String[] { String.valueOf(user.getName()) });
	}

	public List<Integer> queryBuyClass(String imsi) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Cursor c = db.rawQuery("SELECT * FROM buy_class  where name=?",
				new String[] { imsi });
		while (c.moveToNext()) {
			int class_id = c.getInt(c.getColumnIndex("class_id"));
			list.add(class_id);
		}
		c.close();
		return list;
	}

	public List<Integer> queryCollect(String imsi) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Cursor c = db.rawQuery("SELECT * FROM collects  where name=?",
				new String[] { imsi });
		while (c.moveToNext()) {
			if (!"".equals(c.getColumnIndex("article_id"))) {
				int article_id = c.getInt(c.getColumnIndex("article_id"));
				list.add(article_id);
			}
		}
		c.close();
		return list;
	}

	public void addCollect(Collect collect) {
		db.beginTransaction(); // 开始事务
		try {
			ContentValues values = new ContentValues();
			values.put("name", collect.getName());
			values.put("article_id", collect.getArticle_id());
			db.insert("collects", null, values);
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public void deleteCollect(Collect collect) {
		String[] str = new String[] { collect.getName(),
				String.valueOf(collect.getArticle_id()) };
		db.delete("collects", "name=? and article_id=?", str);
	}

	public void addThem(Them them) {
		db.beginTransaction(); // 开始事务
		try {
			ContentValues values = new ContentValues();
			values.put("appid", them.getAppid());
			values.put("them", them.getThem());
			db.insert("them", null, values);
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	public Them queryThem(String imsi) {
		int them=0;
		Them t1 = null;
		Cursor c = db.rawQuery("SELECT * FROM them  where appid=?",
				new String[] { imsi });
		while (c.moveToNext()) {
			if (!"".equals(c.getColumnIndex("them"))) {
				them = c.getInt(c.getColumnIndex("them"));
			}
			t1=new Them(imsi, them);
		}
		c.close();
		return t1;
	}
	public void updateThem(Them them) {
		ContentValues cv = new ContentValues();
		if (!"".equals(them.getAppid())) {
			cv.put("appid", them.getAppid());
		}
		if (!"".equals(them.getThem())) {
			cv.put("them", them.getThem());
		}
		db.update("them", cv, "appid = ?",
				new String[] { String.valueOf(them.getAppid()) });
	}
	
	
}

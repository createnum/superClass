package com.zancheng.callphonevideoshow.object;

import android.graphics.Bitmap;


public class ListBean {
	 public int type;
	 public String text;
	 public Bitmap bitmap;
	public ListBean(int type, String text, Bitmap bitmap) {
		super();
		this.type = type;
		this.text = text;
		this.bitmap = bitmap;
	}
}

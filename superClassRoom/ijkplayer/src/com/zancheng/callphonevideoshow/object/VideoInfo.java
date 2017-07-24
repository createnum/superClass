package com.zancheng.callphonevideoshow.object;

import java.io.Serializable;

public class VideoInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String title;
	public String author;
	public String imgPath;
	public String videoPath;
	public int videoTypeMain;
	public int videoTypeBySpecial;
	public int imgType;
	public int showType;
	public boolean marvellous;
	public int playCount;
	public int likeCount;
	public boolean onlyLook;
	public boolean local;
	public int price;
	
	//存储消费时间 ----hu
	public String time;
	public String imgPath2;//存储个人中心默认铃声的图片路径
	
	public VideoInfo(){
	}
	public VideoInfo(int id, String title, String author, String imgPath, String videoPath, boolean isLocal){
		this.id = id;
		this.title = title;
		this.author = author;
		this.imgPath = imgPath;
		this.videoPath = videoPath;
		this.local = isLocal;
	}
}

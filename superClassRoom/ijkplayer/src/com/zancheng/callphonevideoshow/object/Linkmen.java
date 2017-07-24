package com.zancheng.callphonevideoshow.object;

import java.io.Serializable;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.zancheng.callphonevideoshow.assembly.org.nsg.util.PinYinKit;

import android.graphics.Bitmap;

public class Linkmen implements Serializable{
	
	public String mContactsNumber;  
	public Bitmap mContactsPhonto;  
	public int videoId;
	public String videoTitle;
	public String videoPath;
	protected String mContactsName; //原来的
	protected String sortLetters;
	
	public String getName()
	{
		return mContactsName;
	}
	public void setName(String name)
	{
		this.mContactsName = name;
		
		//汉字转换成拼音
		try {
			String pinyin = PinYinKit.getPingYin(name);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				setSortLetters(sortString.toUpperCase());
			}else{
				setSortLetters("#");
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getSortLetters()
	{
		return sortLetters;
	}
	protected void setSortLetters(String sortLetters)
	{
		this.sortLetters = sortLetters;
	}
	
	public void init(String contactName, String phoneNumber, Bitmap contactPhoto) {
		this.mContactsName = contactName;
		this.mContactsNumber = phoneNumber;
		this.mContactsPhonto = contactPhoto;
	}
}
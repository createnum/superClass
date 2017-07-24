package com.zancheng.callphonevideoshow.object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	public String id;
	public String name;
	public String sign;
	public String phoneNum;
	public List<Linkmen> linkmensRing = new ArrayList<Linkmen>();
	public List<Linkmen> linkmensCard = new ArrayList<Linkmen>();
	public int defaultCardVideoId = 1;
	public boolean isMan;
	public int gold;
	public boolean isVip;
	public String time;
	
	public void setLinkmensRing(String str){
		if(str == null || str.equals("null")){
			return;
		}
		String[] linkmensStr = str.split(";");
		for(int i= 0;i<linkmensStr.length; i++){
			if(linkmensStr[i].equals("")){
				continue;
			}
			String[] linkmenStr = linkmensStr[i].split(",");
			if(linkmenStr.length == 2){
				Linkmen linkmen = new Linkmen();
		        linkmen.mContactsNumber = linkmenStr[0];
		        linkmen.videoId = Integer.valueOf(linkmenStr[1]);
		        linkmensRing.add(linkmen);
			}
			
		}
	}
	
	public void setLinkmensCard(String str){
		if(str == null || str.equals("null")){
			return;
		}
		String[] linkmensStr = str.split(";");
		for(int i= 0;i<linkmensStr.length; i++){
			if(linkmensStr[i].equals("")){
				continue;
			}
			String[] linkmenStr = linkmensStr[i].split(",");
			if(linkmenStr.length == 2){
				if(linkmenStr[0].equals("default")){
					defaultCardVideoId = Integer.valueOf(linkmenStr[1]);
				}
				Linkmen linkmen = new Linkmen();
		        linkmen.mContactsNumber = linkmenStr[0];
		        linkmen.videoId = Integer.valueOf(linkmenStr[1]);
		        linkmensCard.add(linkmen);
			}
			
		}
		
	}
	
}


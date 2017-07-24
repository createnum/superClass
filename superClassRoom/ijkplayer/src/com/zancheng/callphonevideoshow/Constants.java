/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zancheng.callphonevideoshow;

import android.os.Environment;


/**
 * @author kang
 */
public final class Constants {
	
	public static final String SPECIAL_MESSAGE_KEY = "special";
	public static final String RING_DEFAULT_PIC = "csx_sdk_zc_defaultpic.png";
	public static final String RING_DEFAULT_VIDEO = "csx_sdk_zc_defaultring.mp4";
	
	public static final String WEIZHI_MESSAGE = "未知";
	
	public static final int CS_DEFAULT = 0;  //用户当前处于闲置电话状态
	public static final int CS_CILCK_DIAL_BUTTON = 1;  //点击拨出按钮
	public static final int CS_DIAL = 2;  //点击外拨等待中
	public static final int CS_CALLING = 3;  //用户当前处于拨打通话中状态
	public static final int CS_RINGING = 4;   //用户当前处于响铃状态
	public static final int CS_ANSWER = 5;   //用户当前处于接听电话状态

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static class Extra {
		public static final String IMAGES = "com.zancheng.callphonevideoshow.IMAGES";
		public static final String IMAGE_POSITION = "com.zancheng.callphonevideoshow.IMAGE_POSITION";
	}
	
	
	public static final String DL_ID = "downloadId"; 
	public static final String ROOT_DIR = "/com.zancheng.callphonevideoshow/"; 
	public static final String DOWNLOAD_ROOT_DIR = ROOT_DIR+"download/"; 
	public static final String ROOT_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getPath()+ROOT_DIR; 
	public static final String DOWNLOAD_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getPath()+DOWNLOAD_ROOT_DIR; 
	protected static final String DIY_ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getPath()+ROOT_DIR+"diy/"; 
	public static final String DIY_DRAFT_ABSOLUTE_PATH = DIY_ABSOLUTE_PATH+"draft/"; 
	public static final String DOWNLOAD_PIC_ABSOLUTE_PATH = DOWNLOAD_ABSOLUTE_PATH+"pic/"; 
	public static final String DOWNLOAD_VIDEO_ABSOLUTE_PATH = DOWNLOAD_ABSOLUTE_PATH+"video/"; 
	public static final String DOWNLOAD_VIDEO_DIR = DOWNLOAD_ROOT_DIR+"video/"; 
	public static final String DOWNLOAD_PIC_DIR = DOWNLOAD_ROOT_DIR+"pic/"; 
	public static final String DIY_VIDEO_ABSOLUTE_PATH = DIY_ABSOLUTE_PATH+"video/"; 
	public static final String DIY_PIC_ABSOLUTE_PATH = DIY_ABSOLUTE_PATH+"pic/"; 
	public static final String DIY_DRAFT_VIDEO_ABSOLUTE_PATH = DIY_DRAFT_ABSOLUTE_PATH+"video/"; 
	public static final String DIY_DRAFT_PIC_ABSOLUTE_PATH = DIY_DRAFT_ABSOLUTE_PATH+"pic/"; 
	public static final String DOWNLOAD_PHONE_SDCARD_VIDEO_SON_PATH = "ZCDownload/ZCVideos/";
	
	public static final String SERVER_IP_URL = "http://182.92.149.179/";
	public static final String RES_SERVER_IP_URL = "http://182.92.149.179/";
	public static final String GET_VIDEO_INFO_URL = SERVER_IP_URL + "dong/getVideoInfo.php";
	public static final String GET_USER_INFO_URL = SERVER_IP_URL + "dong/getUserInfo.php";
	public static final String GET_SHOW_VIDEO_SIMPLE_INFO = SERVER_IP_URL + "dong/getVideoSimpleInfo.php";
	public static final String UPDATE_USER_INFO = SERVER_IP_URL + "dong/updateUserInfo.php";
	
	public static final int HANDLER_MESSAGE_ID_NETWORKSPEEK = 100;//网络速度检测的handler消息标示id
    public static final int SHOW_NET_CARD_OUTTIME = 3000; //展示网络名片最多的等待时间
}

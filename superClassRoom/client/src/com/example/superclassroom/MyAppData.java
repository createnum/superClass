package com.example.superclassroom;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;


public class MyAppData {
	
	public ImageLoader imageLoader = ImageLoader.getInstance();
	protected static MyAppData inst;
	public Context context;
	public DisplayImageOptions options;
	public static MyAppData getInst(){
		if(inst == null){
			inst = new MyAppData();
			inst.init();
		}
		return inst;
	}
	
	public void onDestroy() {
		
	}
	
	public void init(){
		context = MainTabActivity.myMainActivity;
        options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	public void datapdata(int messageType,String res,VipStoreFragmentActivity vf){
		boolean flag;
		if(res.equals("ok")){
			flag=false;
		}else{
			flag=true;
		}
		MainTabActivity.flag=flag;
	}
	
	
}

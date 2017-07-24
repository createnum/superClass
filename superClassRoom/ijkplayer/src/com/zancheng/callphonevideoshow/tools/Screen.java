package com.zancheng.callphonevideoshow.tools;

import java.lang.reflect.Method;

import com.zancheng.callphonevideoshow.SDKMain;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Screen {
    public static final int DEFAULT_STANDARD_WIDTH = 750;//组件适配的默认基准屏幕宽度(用于组件在不同屏幕的自适应宽度)
    public static final int DEFAULT_STANDARD_HEIGHT =1334;//组件适配的默认基准屏幕高度(用于组件在不同屏幕的自适应宽度)
    public static final int DEFAULT_CENTER_X = DEFAULT_STANDARD_WIDTH/2;// 屏幕中心点坐标
    public static final int DEFAULT_CENTER_Y = DEFAULT_STANDARD_HEIGHT/2;// 屏幕中心点坐标
       
    
    //包含虚拟功能键高度
    public static  int getScreenRealHeight()
    {   
    	int dpi = 0;
        Display display = ((Activity) SDKMain.sdkContext).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics(); 
        @SuppressWarnings("rawtypes")
                Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
                        Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, dm);
            dpi=dm.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }  
        if(dpi == 0){
        	dpi = ((Activity) SDKMain.sdkContext).getWindowManager().getDefaultDisplay().getHeight();
        }
        return dpi;
    }
    
     //获取屏幕高度，但是不包括虚拟功能高度
    public static int getNoHasVirtualKeyScreenRealHeight() {
        int height = ((Activity) SDKMain.sdkContext).getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }
    
    public static int getScreenWidth(Context context) {  
        WindowManager manager = (WindowManager) context  
                .getSystemService(Context.WINDOW_SERVICE);  
        Display display = manager.getDefaultDisplay();  
        return display.getWidth();  
    }  
}

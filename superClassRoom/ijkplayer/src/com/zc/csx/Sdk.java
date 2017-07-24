package com.zc.csx;

import java.io.File;

import com.likebamboo.phoneshow.phoneStateService;
import com.likebamboo.phoneshow.config.ShowPref;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.SDKMain;
import com.zancheng.callphonevideoshow.tools.MyDownload;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class Sdk {

    private static Sdk inst;
//    public static Context context;
    private Messenger mPhoneStateService;
    private Intent service;
    
    public static void initSdk(Context context){
        if(inst == null){
            inst = new Sdk();
            inst.init(context);
        }
    }
    
    public static void openShow(Context context){
        if(inst != null){
            inst._openShow(context);
        }
    }
    
    public static void sendPhoneStateService(int msgType, Boolean msgB, String msgStr, String msgStr2) {
        if(inst != null){
            inst._sendPhoneStateService(msgType, msgB, msgStr, msgStr2);
        }
    }
    public static void exitSDK() {
        if(inst != null){
            inst._exitSDK();
        }
    }
    public static void unbindService(Context context) {
        if(inst != null){
            inst._unbindService(context);
        }
    }
    
    public static void bindService(Context context) {
        if(inst != null){
            inst._bindService(context);
        }
    }
    
    private void _exitSDK(){
        try {
            MyDownload.instDownloadDestroy();
            MyAppData.getInst().list_download.clear();
            //!!!dong====start===== 是否需要释放DB
//            MyAppData.getInst().onDestroy();
            //!!!dong====end=====
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    private void _openShow(Context context){
        Intent intent = new Intent(context, SDKMain.class);
        context.startActivity(intent);
    }
    
    private void _unbindService(Context context){
        context.unbindService(mConnection);
    }
    private void _bindService(Context context){
        context.bindService(service, mConnection, Context.BIND_ABOVE_CLIENT);
    }
    
    private void init(Context context){
        MyAppData.createInst(context);
        initImageLoaderConfiguration(context);
        service = new Intent(context, phoneStateService.class);
        context.startService(service);
    }
    
    
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            // Activity已经绑定了Service
            // 通过参数service来创建Messenger对象，这个对象可以向Service发送Message，与Service进行通信
            mPhoneStateService = new Messenger(service);
            sendPhoneStateService(phoneStateService.MSG_SET_ALL,
                    MyAppData.getIsShowVideo(SDKMain.sdkContext),
                    MyAppData.getDefaultVideoPath(SDKMain.sdkContext),
                    ShowPref.getMainInstance(SDKMain.sdkContext).loadString(ShowPref.MY_PHONENUMBER));
        }

        public void onServiceDisconnected(ComponentName className) {
            mPhoneStateService = null;
        }
    };
    
    public void _sendPhoneStateService(int msgType, Boolean msgB, String msgStr, String msgStr2) {
        // 向Service发送一个Message
        Bundle bundle = new Bundle();
        if (msgStr != null) {
            if(msgType == phoneStateService.MSG_SET_PHONENUM){
                bundle.putString(phoneStateService.BUNDLE_KEY_MYPHONENUM, msgStr);
            }else{
                bundle.putString(phoneStateService.BUNDLE_KEY_VIDEOPATH, msgStr);
            }
        }
        if (msgB != null) {
            bundle.putBoolean(phoneStateService.BUNDLE_KEY_ISSHOWVIDEO, msgB);
        }
        if (msgStr2 != null) {
            bundle.putString(phoneStateService.BUNDLE_KEY_MYPHONENUM, msgStr2);
        }
        
        Message msg = Message.obtain(null, msgType, bundle);
        try {
            mPhoneStateService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    
    protected void initImageLoaderConfiguration(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(
                context.getApplicationContext(), "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(480, 800)
                // max width, max height，即保存的每个缓存文件的最大长宽
                // .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
                // null) // Can slow ImageLoader, use it carefully (Better don't
                // use it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache
                // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                // 缓存的文件数量
                .discCache(new UnlimitedDiskCache(cacheDir))
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(
                        new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
                                                                            // (5
                                                                            // s),
                                                                            // readTimeout
                                                                            // (30
                                                                            // s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();// 开始构建
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
    }
}

package com.example.superclassroom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class CommonFunc{
    public static String getSdcardRootPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if(sdCardExist){
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return null == sdDir ? null : sdDir.toString();
    }

    public static String getSdcardPath(String relativePath){
        String rootPath = getSdcardRootPath();
        if(null == rootPath){
            return null;
        }
        String path = rootPath + File.separator + relativePath;
        File file = new File(path);
        File parentFile = file.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        return path;
    }
    
    
    public static Element loadDictFile(String filePath){
        InputStream is = null;
        try{
            AssetManager assetManager = MainTabActivity.myMainActivity.getAssets();
            is = assetManager.open(filePath);
            
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            if(null == document){
                return null;
            }
            return document.getRootElement();
        }catch(DocumentException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static OutputStream getNativeFileOutputStream(Context activity, String name){
        try{
            return activity.openFileOutput(name, Context.MODE_PRIVATE);
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeToNativePlace(Activity activity, String name, byte[] bts){
        OutputStream outStream = null;
        try{
            outStream = getNativeFileOutputStream(activity, name);
            if(null != outStream){
                outStream.write(bts);
                return true;
            }else{
                return false;
            }
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }finally{
            if(null != outStream){
                try{
                    outStream.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static InputStream readFileFromNativePlace(Context activity, String name){
        try{
            File file = activity.getFileStreamPath(name);
            if(file.exists()){
                return activity.openFileInput(name);
            }
            return null;            
        }catch(FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }
}

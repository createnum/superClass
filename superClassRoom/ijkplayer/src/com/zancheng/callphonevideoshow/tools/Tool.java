package com.zancheng.callphonevideoshow.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;




import com.zancheng.callphonevideoshow.SDKMain;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Tool {
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
//	public static String postHttpJsonByhttpclient(String fromurl, List<NameValuePair> params) {
//		try {
//			Log.v("callPhoneVideoShow", "使用httget");
//			HttpPost httpPost = new HttpPost(fromurl);
//			DefaultHttpClient httpclient = new DefaultHttpClient();
//			if(params != null){
//				httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
//			}
//			HttpResponse response = httpclient.execute(httpPost);
//			Log.v("callPhoneVideoShow", "响应码" + response.getStatusLine().getStatusCode());
//			if (response.getStatusLine().getStatusCode() == 200) {
//
//				String returnStr = EntityUtils.toString(response.getEntity(),
//						"utf-8");
//				Log.v("callPhoneVideoShow", "返回值" + returnStr);
//				return returnStr;
//			} else {
//				Log.v("callPhoneVideoShow", "访问网络返回数据失败，错误码:"
//						+ response.getStatusLine().getStatusCode());
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
	
    public static String postHttpJsonByurlconnection(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            result = null;
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    

    
    public static String getHttpJsonByurlconnection(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
	
	 /** 
     * 如果服务器不支持中文路径的情况下需要转换url的编码。 
     * @param string 
     * @return 
     */  
    public static String encodeUTF(String string)  
    {  
        //转换中文编码  
        String split[] = string.split("/");  
        for (int i = 1; i < split.length; i++) {  
            try {  
                split[i] = URLEncoder.encode(split[i], "UTF-8");  
            } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
            }  
            split[0] = split[0]+"/"+split[i];  
        }  
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格  
        return split[0];  
    }  
    
    //从路径中提取文件名包含扩展名
    public static String getFileAllNameFromPath(String path){  
        int start=path.lastIndexOf("/");  
        if(start!=-1){  
            return path.substring(start+1);    
        }else{  
            return "";  
        }  
          
    }

    //从路径中提取文件名
    public static String getFileNameFromPath(String path){  
        int start=path.lastIndexOf("/");  
        int end=path.lastIndexOf("."); 
        if(start!=-1 && end!=-1){  
            return path.substring(start+1, end);    
        }else{  
            return "";  
        }  
          
    }
    
    //提取文件名的扩展名
    public static String getFileTypeFromPath(String name){  
        int start=name.lastIndexOf(".");  
        if(start!=-1){  
            return name.substring(start+1);    
        }else{  
            return "";  
        }  
          
    }
    
    public static String getFileLocalPath(String name, String dir1, String dir2){ 
    	
    	String videoPath = dir1 + name;
		File file = new File(videoPath);
		if(file.exists()){
			return videoPath;
		}
		videoPath = dir2 + name;
		file = new File(videoPath);
		if(file.exists()){
			return videoPath;
		}
		return null;
    }
    
	public static boolean isHttpPath(String videoPath) {
		if(videoPath.substring(0, 4).equals("http")){
			return true;
		}
		return false;
	} 
	
	/**
     * 检查当前网络是否可用
     * 
     * @param context
     * @param typeName   "WIFI" ,"mobile","any"
     * @return
     */
    //===============dong typeName 临时全部改成设置成any===============
    public static boolean isNetworkAvailable(Context context, String typeName)
    {
    	typeName = "any";
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED && 
                    		(typeName.equals(networkInfo[i].getTypeName()) ||  typeName.equals("any")) )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void createDefaultFile(Context context, String dirName, String fileName, int fildResourceId){
		try{
            
            String databaseFilename = dirName + fileName;
            File dir = new File(dirName);
           
            if (!dir.exists())
                dir.mkdirs();
          
            if (!(new File(databaseFilename)).exists())
            {
                InputStream is = context.getResources().openRawResource(fildResourceId);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
              
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            }
			catch (Exception e)
			{ 
				new AlertDialog.Builder(context).setTitle("错误报告").setMessage("无法复制！").setPositiveButton("确定",
                    new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialoginterface, int i)
                            {
                            }
                     }).show();
			}
	}
    
    
    public static String getPhoneSdCardPath() {
    	String phoneSdCard = "";
    	String externalSdCard = "";
    	boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
    	if(sdCardExist){
    		externalSdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    	}
        String path = null;
        ArrayList<String> devMountList = getDevMountList();
 
        for (String devMount : devMountList) {
            File file = new File(devMount);
 
            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();
                if(path.equals(externalSdCard)){
                	continue;
                }
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);
 
                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }
 
        if (path != null) {
            phoneSdCard = path;
        }else{
        	phoneSdCard = externalSdCard;
        }
 
        return phoneSdCard;
    }
    
    
	public static String getPhoneSdCardSonPath(String sonDirs) {
		String _dirName = getPhoneSdCardPath();
		if(_dirName.equals("")){
			return null;
		}
		String videoPath = _dirName  + File.separator + sonDirs;
		File file = new File(videoPath);
		if(!file.exists()){
			file.mkdirs();
		}
		return videoPath;
	}
	
    /**
     * 获取扩展SD卡存储目录
     * 
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     * 
     * @return
     */
    public static String getExternalSdCardPath() {
    	boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
    	if(sdCardExist){
    		return Environment.getExternalStorageDirectory().getAbsolutePath();
    	}
        String path = null;
 
        File sdCardFile = null;
 
        ArrayList<String> devMountList = getDevMountList();
 
        for (String devMount : devMountList) {
            File file = new File(devMount);
 
            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();
 
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);
 
                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }
 
        if (path != null) {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }
 
        return null;
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     * 
     * @return
     */
    private static ArrayList<String> getDevMountList() {
//        String[] toSearch;
        ArrayList<String> out = new ArrayList<String>();
//		try {
//			toSearch = readFile("/etc/vold.fstab").split(" ");
//	        for (int i = 0; i < toSearch.length; i++) {
//	            if (toSearch[i].contains("dev_mount")) {
//	                if (new File(toSearch[i + 2]).exists()) {
//	                    out.add(toSearch[i + 2]);
//	                }
//	            }
//	        }
//	       
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        out.add("/mnt/external_sd");
        out.add("/mnt/sdcard0");
        out.add("/mnt/sdcard1");
        out.add("/mnt/sdcard2");
		return out;
    }
    
    //读数据  
    public static String readFile(String fileName) throws IOException{   
      String res="";   
      try{   
             FileInputStream fin = SDKMain.sdkContext.openFileInput(fileName);   
             int length = fin.available();   
             byte [] buffer = new byte[length];   
             fin.read(buffer);       
             res = new String(buffer, "UTF-8");
             fin.close();       
         }   
         catch(Exception e){   
             e.printStackTrace();   
         }   
         return res;   
      
    }
    /**
     * 删除单个文件
     * 
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
    File file = new File(filePath);
        if (file.isFile() && file.exists()) {
        return file.delete();
        }
        return false;
    }
    //删除文件夹
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
          //递归删除目录中的子目录下
            if (children != null && children.length != 0) {
            	for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
			}
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
    /** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
    
    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

	public static String extractPngFromVideo(String saveDirName, String videoFileName) {
		File dir = new File(saveDirName);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String picfileName = getFileNameFromPath(videoFileName)+".png";
		File f = new File(saveDirName, picfileName);
		if (f.exists()) {
			f.delete();
		}
		 try {
			   FileOutputStream out = new FileOutputStream(f);
			   Bitmap bm = getVideoThumbnail(videoFileName, 60, 60, MediaStore.Images.Thumbnails.MICRO_KIND);
			   bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			   out.flush();
			   out.close();
			   return saveDirName+picfileName;
		  } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		 return null;
	}
	
	public static int zipAndCheckMaxSize(String[] _files, String _zipFile, int sizeMB) {
		int BUFFER = 2048;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(_zipFile);

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));

			byte data[] = new byte[BUFFER];
			int fileMaxSize = 0;
			for (int i = 0; i < _files.length; i++) {
				Log.v("Compress", "Adding: " + _files[i]);
				FileInputStream fi = new FileInputStream(_files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(_files[i].substring(_files[i]
						.lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
					fileMaxSize +=  count;
				}
				origin.close();
			}
			
			out.close();
			if(fileMaxSize >= 1024*1024*sizeMB){
				return 1;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}
	
	public static void UnZipFile(String zipFileString, String outPathString) throws Exception {    
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));    
        ZipEntry zipEntry;    
        String szName = "";    
        while ((zipEntry = inZip.getNextEntry()) != null) {    
            szName = zipEntry.getName();    
            if (!zipEntry.isDirectory()) {    
                File file = new File(outPathString + File.separator + szName);    
                file.createNewFile();    
                // get the output stream of the file    
                FileOutputStream out = new FileOutputStream(file);    
                int len;    
                byte[] buffer = new byte[1024];    
                // read (len) bytes into buffer    
                while ((len = inZip.read(buffer)) != -1) {    
                    // write (len) byte from buffer at the position 0    
                    out.write(buffer, 0, len);    
                    out.flush();    
                }    
                out.close();    
            }    
        }   
        inZip.close();    
    } 
	
   public static AnimationDrawable UnAniZipFile(ZipInputStream inZip) throws Exception {    
        ZipEntry zipEntry;    
        AnimationDrawable anim = new AnimationDrawable();
        while ((zipEntry = inZip.getNextEntry()) != null) {    
            if (!zipEntry.isDirectory()) {    
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;    
                byte[] buffer = new byte[1024];    
                while ((len = inZip.read(buffer)) != -1) {    
                    bos.write(buffer, 0, len);    
                }    
                byte[] zipByte = bos.toByteArray();
                bos.close();
                Bitmap bitmap = BitmapFactory.decodeByteArray(zipByte, 0, zipByte.length);
                Drawable drawable =new BitmapDrawable(bitmap);
                anim.addFrame(drawable, 600);
            }    
        }   
        inZip.close();   
        return anim;
        
    } 
	   
	public static boolean renameFile(String srcFileName, String destFileName) {
		 File srcFile = new File(srcFileName);  
		    if(!srcFile.exists() || !srcFile.isFile())   
		        return false;  
		
	    return srcFile.renameTo(new File(destFileName));   
	} 
	
	public static boolean moveFile(String srcFileName, String destDirName) {  
	      
	    File srcFile = new File(srcFileName);  
	    if(!srcFile.exists() || !srcFile.isFile())   
	        return false;  
	      
	    File destDir = new File(destDirName);  
	    if (!destDir.exists())  
	        destDir.mkdirs();  
	      
	    return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));  
	}
	
	public static String getMyPhoneNumberFormat(String sysPhoneNumber) {
		return sysPhoneNumber.replaceAll("\\s","");
	}
	
	public static void hideSoftInput(Context context, View view){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘  
	}
	
    public static boolean isServiceWork(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(100);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    } 
    
    public static String getVersionName(Context context) {
		//getPackageName()是你当前类的包名，0代表是获取版本信息  
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}

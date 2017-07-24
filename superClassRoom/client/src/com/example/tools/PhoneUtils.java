package com.example.tools;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

public class PhoneUtils {
    
    public static enum OperatorName{
        CHINA_MOBILE,
        CHINA_TELECOM,
        CHINA_UNICOM
    }    
    private PhoneUtils(){};
    
    /**
     * 获取网络运营商名称
     * @link #permission:android.permission.READ_PHONE_STATE
     * @param
     * @return
     */
    public static OperatorName getOperatorName(Context context) {
        try{
            String str = getIMSI(context);
            if(str.startsWith("46000") || str.startsWith("46002") || str.startsWith("46007")){
                return OperatorName.CHINA_MOBILE;
            }else if(str.startsWith("46001") || str.startsWith("46006")){
                return OperatorName.CHINA_UNICOM;
            }else if(str.startsWith("46003") || str.startsWith("46005")){
                return OperatorName.CHINA_TELECOM;
            }else{
                TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
                    return OperatorName.CHINA_TELECOM;
                }else{
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    public static OperatorName getOperatorNameWithPhoneNumber(String phonenum) {
    	
    	return null;
    }

    public static String getNetworkOperator(Context context) {
        
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        
        try {
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                return "46003";
            } else {
                return tm.getNetworkOperator();
            }
        } catch (Exception e) {
            return "-1";
        }
    }
    /**
     * 获取IMSI
     * @link #permission:android.permission.READ_PHONE_STATE
     * @param
     * @return
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId() == null ? "" : tm.getSubscriberId();
        } catch (SecurityException e) {
            
        }
        return "";
    }
    
    /**
     * 获取IMEI  
     * @link #permission:android.permission.READ_PHONE_STATE
     * @param
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId() == null ? "" : tm.getDeviceId();
        } catch (SecurityException e) {
            
        }
        return "";
    }
    
    
    public static String getPhoneNumber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = tm.getLine1Number();
            if (TextUtils.isEmpty(phoneNumber)) {
                return "";
            }
            if (phoneNumber.startsWith("+")) {
                phoneNumber = tm.getLine1Number().substring(3);
            }
//          if (StringUtils.checkIsPhoneNumber(phoneNumber)) {
//              return phoneNumber;
//          }
        } catch (SecurityException e) {
            
        } catch (Exception e) {
            
        }
        return "";
    }
    
    /**
     * 判断sim卡是否准备好
     * @param tm
     * @return
     */
    public static boolean isSimReady(Context context) {
        TelephonyManager tm = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE); 
        if (!(tm.getSimState() == TelephonyManager.SIM_STATE_READY)) {
            return false;
        }
        return true;
    }
    

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED
                            || info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 获取IPv4
     * @param useIPv4
     * @return
     */
    public static String getLocalIpAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> niList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : niList) {
                List<InetAddress> iaList = Collections.list(intf.getInetAddresses());
                for (InetAddress ia : iaList) {
                    if (!ia.isLoopbackAddress()) {
                        String sAddr = ia.getHostAddress().toUpperCase(Locale.CHINESE);
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { 
        }
        return "";
    }
    
    
    /**
     * 获取蓝牙地址
     * @param context
     * @return
     */
    public static String getBluetoothAddress(Context context) {
        String address = "";
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null) {
                address = adapter.getAddress() == null ? "" : adapter.getAddress();
            }
        } catch (SecurityException e) {
            
        } catch (Exception e) {
            
        }
        return address;
    }

    /**
     * 获取MAC地址(未申请相应的权限,wifi未打开状态下获取不到)!
     * @param
     * @return
     */
    public static String getMacAddress() {
        String macAddress = "";
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; str != null; ) {
                str = input.readLine();
                if (str != null) {
                    macAddress = str.trim();
                    break;
                }
            }
        } catch (IOException ex) {
            
        }
        return macAddress;
    }
    
    /**
     * 获取MAC地址
     * @param
     * @return
     */
    public static String getMacAddress(Context context) {
        try {
            WifiManager wManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wManager.getConnectionInfo();
            if (info != null) {
                return info.getMacAddress() == null ? "" : info.getMacAddress();
            }
        } catch (SecurityException e) {
            return getMacAddress();
        }
        return "";
    }
    

    /** 
     * 判断当前网络是否是wifi网络
     * @param context
     * @return boolean
     */  
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
    
    
    /** 
     * 判断当前网络是否3G网络
     * @param context
     * @return boolean
     */  
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
            return true;
        }
        return false;
    }

    @TargetApi(9)
    public static String getSerial() {
        try {
            return Build.VERSION.SDK_INT >=9 ? Build.SERIAL : "0";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
    

    /**
     * 获取手机分辨率
     * @param context
     * @return
     */
    public static String getScreenResolution(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (metrics == null) {
            return "0*0";
        }
        return metrics.heightPixels + "*" + metrics.widthPixels;
    }

    public static String getAppVersionName(Context context) {  
        String versionName = "";  
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versionName = pi.versionName;  
            //versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {  
                return "";  
            }  
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versionName;  
    }  
    
    public static int getAppVersionCode(Context context) {  
        int versioncode = 0;  
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versioncode = pi.versionCode;
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versioncode;  
    } 
}

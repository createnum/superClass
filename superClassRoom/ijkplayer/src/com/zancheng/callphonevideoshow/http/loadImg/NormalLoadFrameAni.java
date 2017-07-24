package com.zancheng.callphonevideoshow.http.loadImg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipInputStream;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.tools.Tool;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class NormalLoadFrameAni{
    protected String uri;
    protected ImageView imageView;
    protected byte[] fileByte;
    public boolean loadComplete;
    
    public void getPicture(String uri, ImageView imageView) {
        this.uri = uri;
        this.imageView = imageView;
        new Thread(runnable).start();
    }
     
    @SuppressLint("HandlerLeak")
    Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (fileByte != null) {
                    ZipInputStream inZip = new ZipInputStream(new ByteArrayInputStream(fileByte)); 
                    AnimationDrawable anim = null;
                    try {
                        anim = Tool.UnAniZipFile(inZip);
                        anim.setOneShot(false);
                        imageView.setImageDrawable(anim);
                        anim.start();
                        loadComplete = true;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
            }
        }
    };
 
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(Constants.SHOW_NET_CARD_OUTTIME);
                conn.setReadTimeout(2000);
                if (conn.getResponseCode() == 200) {
                    InputStream fis =  conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = fis.read(bytes)) != -1) {
                        bos.write(bytes, 0, length);
                    }
                    fileByte = bos.toByteArray();
                    bos.close();
                    fis.close();
                    Message message = new Message();
                    message.what = 1;
                    handle.sendMessage(message);
                }
                 
                 
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

     
}
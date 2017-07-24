package com.zancheng.callphonevideoshow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zancheng.callphonevideoshow.show.commonShow.BindPhoneDialog;
import com.zancheng.callphonevideoshow.show.videoShow.MyCard;
import com.zancheng.callphonevideoshow.show.videoShow.MyShow;
import com.zancheng.ijkplayer.MainActivity;
import com.zc.csx.Sdk;

public class SDKMain extends Activity{

    public static SDKMain sdkContext;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(getResources().getIdentifier("csx_sdk_zc_sdk_main", "layout", getPackageName()));  
        sdkContext = this;
        Sdk.bindService(SDKMain.this);
        Button card = (Button) findViewById(getResources().getIdentifier("card", "id", getPackageName()));
        card.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(MyAppData.getInst().user.phoneNum == null || MyAppData.getInst().user.phoneNum.equals("null") || MyAppData.getInst().user.phoneNum.equals("")){
                    //BindPhoneDialog直接显示数字输入
                    BindPhoneDialog bindPhoneDialog = new BindPhoneDialog(SDKMain.this);
                    bindPhoneDialog.setFromPlace(BindPhoneDialog.FP_SetCard);
                    bindPhoneDialog.show();
                    return;
                }
                Intent intent1 = new Intent(SDKMain.this, MyCard.class);
                SDKMain.this.startActivity(intent1);
              
            }
        });
        Button ring = (Button) findViewById(getResources().getIdentifier("ring", "id", getPackageName()));
        ring.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SDKMain.this, MyShow.class);
                SDKMain.this.startActivity(intent);
            }
        });
    } 
	

   @Override
    protected void onDestroy() {
        super.onDestroy();
        Sdk.unbindService(SDKMain.this);
        Sdk.exitSDK();
        sdkContext = null;
    }
}

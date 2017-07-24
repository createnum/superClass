package com.example.tools;


import com.example.superclassroom.VipStoreFragmentActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MyServiceReceiver extends BroadcastReceiver
{

	private Context mContext;
    private static final String TAG = "REC_TEST";


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsMessages = null;
        Object[] pdus = null;


        if (bundle != null) {
            pdus = (Object[]) bundle.get("pdus");
        }
        if (pdus !=null){
            smsMessages = new SmsMessage[pdus.length];
            String sender = null;
            String content = null;


            for (int i=0; i<pdus.length; i++){
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sender = smsMessages[i].getOriginatingAddress();
                content = smsMessages[i].getMessageBody();
                //ShowToast(sender + " ¶ÌÐÅ", R.drawable.ic_dialog_email);
               /* Toast.makeText(context, sender + "," + content, Toast.LENGTH_LONG).show();
                if(sender.equals("10655133") && i!=-1){
                	VipStoreFragmentActivity.addMoney(imsi);
                }*/
            }//for smsMessages
        }//if pdus
    }
}
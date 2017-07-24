package com.zancheng.callphonevideoshow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zc.csx.Sdk;

public class Test extends Activity{

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Sdk.initSdk(this);
        setContentView(getResources().getIdentifier("test", "layout", getPackageName()));  
        Button button = (Button) findViewById(getResources().getIdentifier("start", "id", getPackageName()));
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Sdk.openShow(Test.this);
            }
        });
        
    } 
	

   @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

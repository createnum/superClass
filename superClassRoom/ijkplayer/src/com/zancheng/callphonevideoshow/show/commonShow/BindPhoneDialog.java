package com.zancheng.callphonevideoshow.show.commonShow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.assembly.CustomDialog;
import com.zancheng.callphonevideoshow.http.NetworkManager;
import com.zancheng.callphonevideoshow.tools.ToastUtils;
import com.zancheng.callphonevideoshow.tools.Tool;

public class BindPhoneDialog extends CustomDialog {
	public static final int FP_SetCard = 1;

	public BindPhoneDialog(Context context) {
		super(context);
		init();
		setFromPlace(0);
	}

	public void init(){
		getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		
		setOnNegativeListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tool.hideSoftInput(getContext(), getEditText());
				dismiss();
			}
	    });
		
		setOnPositiveListener(new View.OnClickListener() {
	        @SuppressLint("NewApi")
			@Override
	        public void onClick(View v) {
	        	String phoneNum = getEditText().getText().toString();
	        	if (phoneNum.matches("[1][3578]\\d{9}")) {
	        		//绑定手机号
	        		String params = "updateType="+NetworkManager.BIND_PHONE
		        			+"&myPhoneNum="+phoneNum+"&userId="+MyAppData.getInst().user.id;
	        		NetworkManager.getData(NetworkManager.UPDATE_USER_INFO, Constants.UPDATE_USER_INFO, params);
				}else {
					ToastUtils.showToast(getContext(), "请输入有效手机号");
				}
		        Tool.hideSoftInput(getContext(), getEditText());
	        	dismiss();
	        }

	    });
	}

	public void setFromPlace(int FPSetcard) {
		MyAppData.getInst().bindPhoneNumByFromPlace = FPSetcard;
	}
}

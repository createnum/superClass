package com.example.moudle;

import java.text.DecimalFormat;

import com.example.superclassroom.MainTabActivity;
import com.example.superclassroom.R;
import com.example.superclassroom.VipStoreDict.VipStoreInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 * @author Tom.Cai
 */
public class CustomDialog extends Dialog {
	private EditText editText;
	private Button positiveButton, negativeButton,btn;
	private TextView title;
	private View mView;
	private Boolean flag=true;
	
	public CustomDialog(Context context, int tp) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCustomDialog(tp);
	}

	private void setCustomDialog(int tp) {
		if(tp!=R.layout.vip_buy_dialog){
			flag=false;
		}else{
			flag=true;
		}
		mView = LayoutInflater.from(getContext()).inflate(tp, null);
		positiveButton = (Button) mView.findViewById(R.id.positiveButton);
		negativeButton = (Button) mView.findViewById(R.id.negativeButton);
		editText = (EditText) mView.findViewById(R.id.yzm);
		if (editText != null) {
			editText.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus) {
						editText.setHint(null);
					} else {
						if(flag){
							editText.setHint("请输入验证码");
						}else{
							editText.setHint("请输入手机号");
						}
						
					}
				}
			});
		}
		super.setContentView(mView);
	}

	public EditText getEditText() {
		return editText;
	}

	public void clearEditText() {
		editText.setText("");
		;
	}

	public void setTitle(String titleStr) {
		this.title.setText(titleStr);
	}

	@Override
	public void setContentView(int layoutResID) {
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
	}

	public Button getBtn() {
		return btn;
	}

	@Override
	public void setContentView(View view) {
	}

	/**
	 * 确定键监听器
	 * 
	 * @param listener
	 */
	public void setOnPositiveListener(View.OnClickListener listener) {
			positiveButton.setOnClickListener(listener);
	}

	/**
	 * 取消键监听器
	 * 
	 * @param listener
	 */
	public void setOnNegativeListener(View.OnClickListener listener) {

		negativeButton.setOnClickListener(listener);

	}

	public void tank(VipStoreInfo info) {
		DecimalFormat df = new DecimalFormat("#.00");
		title = (TextView) mView.findViewById(R.id.title);
		title.setText(info.name);
		// title=(TextView)mView.findViewById(R.id.dlhd);
		title.setText(String.valueOf(info.goldNum));
		// title=(TextView)mView.findViewById(R.id.ljhd);
		title.setText(String.valueOf(info.countGoldNum));
		// title=(TextView)mView.findViewById(R.id.oldP);
		title.setText("原价￥" + df.format(info.oldMoney));
		// title=(TextView)mView.findViewById(R.id.newP);
		title.setText("现价￥" + df.format(info.money));
	}

}
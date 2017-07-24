package com.zancheng.callphonevideoshow.assembly;


import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
public class CustomDialog extends Dialog {
	protected EditText editText;
    protected Button positiveButton;
    protected Button negativeButton;
    protected TextView title;
 
    public CustomDialog(Context context) {
        super(context, context.getResources().getIdentifier("dialog", "style", context.getPackageName()));
        setCustomDialog();
    }
 
    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(getContext().getResources().getIdentifier("csx_sdk_zc_custom_dialog", "layout", getContext().getPackageName()), null);
        editText = (EditText) mView.findViewById(getContext().getResources().getIdentifier("videoNameET", "id", getContext().getPackageName()));
        positiveButton = (Button) mView.findViewById(getContext().getResources().getIdentifier("dialog_confirm", "id", getContext().getPackageName()));
        negativeButton = (Button) mView.findViewById(getContext().getResources().getIdentifier("dialog_canel", "id", getContext().getPackageName()));
        super.setContentView(mView);
    }
     
    public EditText getEditText(){
        return editText;
    }
     
    public void clearEditText(){
        editText.setText("");
    }
    
    public void setTitle(String titleStr){
        this.title.setText(titleStr);
    }
 
    /**
     * 确定键监听器
     * @param listener
     */ 
    public void setOnPositiveListener(View.OnClickListener listener){ 
        positiveButton.setOnClickListener(listener); 
    } 
    /**
     * 取消键监听器
     * @param listener
     */ 
    public void setOnNegativeListener(View.OnClickListener listener){ 
        negativeButton.setOnClickListener(listener); 
    }

}
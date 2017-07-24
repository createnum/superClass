package com.example.superclassroom;

import java.util.List;

import com.example.db.DBManager;
import com.example.moudle.User;
import com.example.tools.PhoneUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
  
@SuppressLint("NewApi")
public class ArticleListActivity extends FragmentActivity {  
    private FragmentManager mFragMgr;
    public ArticleFragmentList fragment;
	public ArticleListActivity context;
	private DBManager dbm;
	private TextView tv;
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.common_show);  
        context = this;
        tv=(TextView)findViewById(R.id.goldNum1);
        dbm=new DBManager(context);
        List<User> list=dbm.queryUser(PhoneUtils.getIMSI(context));
        if(list.size()>0){
        	int money=list.get(0).getMoney();
        	tv.setText(money+"");
        }
        //·µ»Ø¼ü
        TextView textView = (TextView)findViewById(R.id.textView1);
        textView.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View arg0) {
    			 context.finish();
    		}
		});
        
        Intent intent = getIntent(); 
        int index = intent.getIntExtra("pageIndex", -1);
        int tp=intent.getIntExtra("tp", -1);
        String typeNameValue = intent.getStringExtra("typeName");
        TextView typeName = (TextView)findViewById(R.id.textView2);
        typeName.setText(typeNameValue);
        mFragMgr = getSupportFragmentManager();  
        showFragments("11", false, index,tp);
    }  
    
      
    private void showFragments(String tag, boolean needback, int index,int tp){  
        FragmentTransaction trans = mFragMgr.beginTransaction();  
        fragment = new ArticleFragmentList();
		fragment.init_data(index,tp);
        if(needback){  
            trans.add(R.id.door_root_content_ll, fragment, tag);  
            trans.addToBackStack(tag);  
        }else{  
            trans.replace(R.id.door_root_content_ll, fragment, tag);  
        }  
        trans.commit();  
    }  
    
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
    }  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
    	List<User> list=dbm.queryUser(PhoneUtils.getIMSI(context));
        if(list.size()>0){
        	int money=list.get(0).getMoney();
        	tv.setText(money+"");
        }
        super.onResume();  
    }  
    
	protected void onDestroy() {
		super.onDestroy();
	}

}

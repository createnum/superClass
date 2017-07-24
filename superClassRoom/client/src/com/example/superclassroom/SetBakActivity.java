package com.example.superclassroom;

import com.example.db.DBManager;
import com.example.moudle.Them;
import com.example.tools.PhoneUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

public class SetBakActivity extends Activity{
	
	private View layout;
	private View ddxg;
	private View fgxg;
	private View hbxg;
	private View hjxg;
	private View rlxg;
	private View ybxg;
	private Context context;
	private Them them;
	private String imsi;
	private DBManager dbm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=MainTabActivity.myMainActivity;
		dbm = new DBManager(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_center);
		imsi=PhoneUtils.getIMSI(context);
		init();
		addListener();
	}

	private void init() {
		// TODO Auto-generated method stub
		ddxg=findViewById(R.id.collectionRL);
		fgxg=findViewById(R.id.boughtRL);
		hbxg=findViewById(R.id.vipRL);
		hjxg=findViewById(R.id.setRL);
		rlxg=findViewById(R.id.aboutRL);
		ybxg=findViewById(R.id.oldRL);
		layout=findViewById(R.id.set_bk);
		Them t1=dbm.queryThem(imsi);
		if(t1!=null){
			layout.setBackgroundResource(t1.getThem());
		}
	}
	
	private void addListener() {
		ddxg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				them=new Them(imsi, R.drawable.ddxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.ddxg);
			}
		});
		fgxg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				them=new Them(imsi, R.drawable.fgxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.fgxg);
			}
		});
		hbxg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				them=new Them(imsi,  R.drawable.hbxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.hbxg);
			}
		});
		hjxg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				them=new Them(imsi,  R.drawable.hjxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.hjxg);
			}
		});
		rlxg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				them=new Them(imsi,  R.drawable.rlxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.rlxg);
			}
		});
		ybxg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				them=new Them(imsi,  R.drawable.ybxg);
				dbm.updateThem(them);
				layout.setBackgroundResource(R.drawable.ybxg);
			}
		});
	}
}

package com.example.superclassroom;

import java.util.List;

import com.example.db.DBManager;
import com.example.superclassroom.ArticleDict.ArticleInfo;
import com.example.tools.PhoneUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecommendFragmentList extends FragmentActivity implements
		OnRefreshListener<ListView> {
	private ItemAdapter mAdapter;
	protected PullToRefreshListView mPullRefreshListView;
	public Activity context;
	private List<ArticleInfo> articleList;
	private DBManager dbm;
	
	 @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_recommend);  
        context = this;
        dbm=new DBManager(context);
        articleList = ArticleDict.getInstance().getRecommendList();
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.recommendRefreshList);

		mAdapter = new ItemAdapter();
		mPullRefreshListView.setAdapter(mAdapter);

		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent , View view, int position,
				long id) {
			Boolean flag = ArticleFragmentList.buy(articleList.get(position).goldNum, dbm, PhoneUtils.getIMSI(context),articleList.get(position).id);
			if(flag || articleList.get(position).goldNum <= 0){
				Intent intent = new Intent(context, ArticleDetailActivity.class);
				intent.putExtra("fileName", articleList.get(position).path);
				intent.putExtra("title", articleList.get(position).title);
				intent.putExtra("id", articleList.get(position).id);
				intent.putExtra("buyMoney", articleList.get(position).goldNum);
				context.startActivity(intent);
					}
			
				}
		});

//		mPullRefreshListView.setOnRefreshListener(RecommendFragmentList.this);
    }  
	 
	 
	
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}



	class ItemAdapter extends BaseAdapter {


			private class ViewHolder {
				public TextView title;
				public TextView goldNum;
				public ImageView gold;
			}

			@Override
			public int getCount() {
				return articleList.size();
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				View view = convertView;
				Boolean flag = false;
				String imsi = PhoneUtils.getIMSI(context);
				final ViewHolder holder;
				if (convertView == null) {
					view = ((Activity) context).getLayoutInflater().inflate(
							R.layout.item_list_article, parent, false);
					holder = new ViewHolder();
					holder.title = (TextView) view.findViewById(R.id.title);
					holder.goldNum = (TextView) view.findViewById(R.id.goldNum);
					holder.gold = (ImageView) view.findViewById(R.id.goldImg);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}
				holder.title.setText(articleList.get(position).title);
				holder.goldNum.setText(String.valueOf(articleList.get(position).goldNum));
				List<Integer> list = dbm.queryBuyClass(imsi);
				for (int i : list) {
					Log.i("myTitle", articleList.get(position).title);
					if (i == articleList.get(position).id) {
						flag = true;
					}
				}
				if(articleList.get(position).goldNum <= 0 || flag){
					holder.gold.setVisibility(View.INVISIBLE);
					holder.goldNum.setVisibility(View.INVISIBLE);
				}else{
					holder.gold.setVisibility(View.VISIBLE);
					holder.goldNum.setVisibility(View.VISIBLE);
					dbm=new DBManager(context);
					
				}
				return view;
			}
		}

	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(
				context.getApplicationContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	public void dataloadingComplete() {
			mAdapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
	}

}

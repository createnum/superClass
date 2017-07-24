package com.example.superclassroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.example.db.DBManager;
import com.example.moudle.BuyClass;
import com.example.moudle.User;
import com.example.superclassroom.ArticleDict.ArticleInfo;
import com.example.superclassroom.VipStoreDict.VipStoreInfo;
import com.example.tools.PhoneUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ArticleFragmentList extends Fragment implements
		OnRefreshListener<ListView> {
	public ItemAdapter mAdapter;
	protected PullToRefreshListView mPullRefreshListView;
	public Activity context;
	private List<ArticleInfo> articleList;
	private int type;
	private DBManager dbm;
	private String imsi;
	private List<Integer>  list;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		mPullRefreshListView = (PullToRefreshListView) LayoutInflater.from(
				context).inflate(R.layout.ac_article_list, container, false);

		mAdapter = new ItemAdapter();
		mPullRefreshListView.setAdapter(mAdapter);
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Boolean flag = buy(articleList.get(position).goldNum, dbm, imsi,articleList.get(position).id);
				if (flag || articleList.get(position).goldNum <= 0) {
					Intent intent = new Intent(context,
							ArticleDetailActivity.class);
					intent.putExtra("fileName", articleList.get(position).path);
					intent.putExtra("title", articleList.get(position).title);
					intent.putExtra("id", articleList.get(position).id);
					intent.putExtra("buyMoney",
							articleList.get(position).goldNum);
					context.startActivity(intent);
				}
			}
		});

//		mPullRefreshListView.setOnRefreshListener(ArticleFragmentList.this);
		return mPullRefreshListView;
	}

	public void init_data(int type, int tp) {
		this.type = type;
		imsi = PhoneUtils.getIMSI(MainTabActivity.myMainActivity);
		dbm=new DBManager(MainTabActivity.myMainActivity);
		if (tp == 0) {
			articleList = ArticleDict.getInstance().articleLists.get(type);
		} else if (tp == 1) {
			list = dbm.queryBuyClass(imsi);
			articleList = ArticleDict.getInstance().getArticleList(list);
		}else if(tp==2){
			list=dbm.queryCollect(imsi);
			articleList = ArticleDict.getInstance().getArticleList(list);
		}

	}

	@Override
	public void onResume() {
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
			final ViewHolder holder;
			String imsi = PhoneUtils.getIMSI(context);
			if (convertView == null) {
				view = ((Activity) context).getLayoutInflater().inflate(
						R.layout.item_list_article, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) view.findViewById(R.id.title);
				holder.goldNum = (TextView) view.findViewById(R.id.goldNum);
				holder.gold = (ImageView) view.findViewById(R.id.goldImg);
				view.setBackgroundColor(ArticleFragmentList.this.getResources()
						.getColor(ArticleTypeFragmentList.TYPE_COLOR[type]));
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.title.setText(articleList.get(position).title);
			holder.goldNum
					.setText(String.valueOf(articleList.get(position).goldNum));
			List<Integer> list = dbm.queryBuyClass(imsi);
			for (int i : list) {
				if (i == articleList.get(position).id) {
					flag = true;
				}
			}
			if (articleList.get(position).goldNum <= 0 || flag) {
				holder.gold.setVisibility(View.INVISIBLE);
				holder.goldNum.setVisibility(View.INVISIBLE);
			}else{
				holder.gold.setVisibility(View.VISIBLE);
				holder.goldNum.setVisibility(View.VISIBLE);
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
	
	public static Boolean buy(int money, DBManager dbm, String imsi,int id) {
		List<User> lists = dbm.queryUser(imsi);
		List<Integer> list = dbm.queryBuyClass(imsi);
		User user = lists.get(0);
		if (user.getMoney() - money > 0 || list.contains(id)) {
			return true;
		} else {
			return false;
		}
	}
}

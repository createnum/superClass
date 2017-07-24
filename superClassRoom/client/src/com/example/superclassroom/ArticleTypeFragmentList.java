package com.example.superclassroom;

import java.util.List;

import com.example.superclassroom.R.color;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ArticleTypeFragmentList extends FragmentActivity implements
		OnRefreshListener<ListView> {
	private ItemAdapter mAdapter;
	protected PullToRefreshListView mPullRefreshListView;
	public Activity context;
	private List<String> typeList;
	public static int[] TYPE_COLOR = {color.orange, color.blue, color.pink,color.green,color.orange};
	
	 @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_type);  
        context = this;
        typeList = ArticleDict.getInstance().typeList;
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.recommendRefreshList);

		mAdapter = new ItemAdapter();
		mPullRefreshListView.setAdapter(mAdapter);

		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent , View view, int position,
				long id) {
							Intent intent = new Intent(context, ArticleListActivity.class);
							intent.putExtra("pageIndex", position);
							intent.putExtra("tp", 0);
							intent.putExtra("typeName", (String) typeList.get(position));
							context.startActivity(intent);
						
				}
		});

		mPullRefreshListView.setOnRefreshListener(ArticleTypeFragmentList.this);
    }  
	 

	 @Override
		public void onBackPressed() {
			new AlertDialog.Builder(this).setTitle("温馨提示")
					.setMessage("确认退出" +getResources().getString(R.string.app_name )+ "吗？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 点击“确认”后的操作
							ArticleTypeFragmentList.this.finish();

						}
					})
					.setNegativeButton("返回", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 点击“返回”后的操作,这里不设置没有任何操作
						}
					}).show();
		}
	class ItemAdapter extends BaseAdapter {


		private class ViewHolder {
			public TextView title;
		}

		@Override
		public int getCount() {
			return typeList.size();
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
			final ViewHolder holder;
			if (convertView == null) {
				view = ((Activity) context).getLayoutInflater().inflate(
						R.layout.item_list_article_type, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) view.findViewById(R.id.title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.title.setText(typeList.get(position));
			view.setBackgroundColor(ArticleTypeFragmentList.this.getResources().getColor(TYPE_COLOR[position]));
			
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

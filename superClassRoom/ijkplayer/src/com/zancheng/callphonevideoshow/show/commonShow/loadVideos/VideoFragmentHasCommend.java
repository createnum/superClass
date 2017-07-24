package com.zancheng.callphonevideoshow.show.commonShow.loadVideos;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.internal.HeaderGridView;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.assembly.RoundProgressBar;
import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.callphonevideoshow.tools.MyDownload;

public abstract class VideoFragmentHasCommend extends VideoFragment {
	protected PullToRefreshGridView mPullRefreshGridView;
	protected ImageAdapter mAdapter;
	protected List<Integer> VideosEachTypeCount = new ArrayList<Integer>();
	protected boolean curVideoTypeAllLoadFinish;
	protected int fromPlace=-1;
	private float xDown;
	private float xMove;
	public int indexDis;
    public RoundProgressBar fristItemProgressbar; //该变量保证，传送给下载工具类中的进度条跟显示的进度条是同一个   （进度条不走的临时解决方案）
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = getBaseView(inflater, container);
    	initCommonView(view);
    	mAdapter = new ImageAdapter();
		mPullRefreshGridView.setAdapter(mAdapter);
        return view;
    }
	
	protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(getResources().getIdentifier("csx_sdk_zc_ac_image_grid_has_recommend", "layout",getActivity().getPackageName()), container, false);  
	}

	
	protected void initCommonView(View view){
		mPullRefreshGridView = (PullToRefreshGridView)view.findViewById(getResources().getIdentifier("pull_refresh_grid", "id", getActivity().getPackageName()));
		
		mPullRefreshGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clickVideoItem(position);
			}
		});
//		mPullRefreshGridView.getRefreshableView().setOnTouchListener(new OnTouchListener(){
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					xDown = event.getX();
//					break;
//				case MotionEvent.ACTION_MOVE:
//					xMove = event.getX();
//					break;
//				case MotionEvent.ACTION_UP:
//					if (xMove - xDown > 250 ) {
//						getActivity().finish();
//						getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//					}
//					break;
//				default:
//					break;
//				}
//				return false;
//			}
//			
//		});

		
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<HeaderGridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				loadVideoData(videoType);
			}

		});
		
	}
	
	public boolean needClearVideoList(){
		if(VideosEachTypeCount.get(0)> 0){
			return false;
		}
		return super.needClearVideoList();
	}
	
	protected void showVideo(int position) {
	}
    
	protected int getVideoCount(){
		int videoOccupyGridCount = 0;
		for(int i=0; i<VideosEachTypeCount.size();i++){
			if(VideosEachTypeCount.get(i)%2 == 0){
				videoOccupyGridCount += VideosEachTypeCount.get(i);
			}else{
				videoOccupyGridCount += (VideosEachTypeCount.get(i)+1);
			}
		}
		return videoOccupyGridCount;
	}
	  
    public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return getVideoCount();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(getResources().getIdentifier("csx_sdk_zc_head_download_message", "layout", getActivity().getPackageName()), parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(getResources().getIdentifier("img", "id", getActivity().getPackageName()));
				holder.progressbar = (RoundProgressBar) view.findViewById(getResources().getIdentifier("roundProgressBar", "id", getActivity().getPackageName()));
	            holder.spot=(ImageView) view.findViewById(getResources().getIdentifier("spot", "id", getActivity().getPackageName()));
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
				
          if(position == 0 && (fristItemProgressbar == null || fristItemProgressbar == holder.progressbar)){
              fristItemProgressbar = holder.progressbar;
              MyDownload.instSetProgressBar(holder.progressbar , null);
          }
			int fristTypeVideoCount = VideosEachTypeCount.get(0);
			if(fristTypeVideoCount%2 == 0){
				view.setVisibility(View.VISIBLE);
				loadImage(position, holder.imageView);
			}else{
				if(position == fristTypeVideoCount ){
					view.setVisibility(View.GONE);
				}else if(position == fristTypeVideoCount+1 ){
					view.setVisibility(View.VISIBLE);
				}else if(position == fristTypeVideoCount+2){
					view.setVisibility(View.VISIBLE);
				}else{
					view.setVisibility(View.VISIBLE);
				}
				if(position> fristTypeVideoCount){
					loadImage(position-1, holder.imageView);
				}else if(position < fristTypeVideoCount){
					loadImage(position, holder.imageView);
				}
			}
//			
			
			return view;
		}

		class ViewHolder {
			ImageView imageView;
	        ImageView spot;
	        RoundProgressBar progressbar;
		}
	}
    
	
    protected abstract void init_son_data(int videoType, String parameter1);
	protected abstract void clickVideoItem(int position);
	
	//双击回头部
	public void scrollTo_Top() {
		mPullRefreshGridView.getRefreshableView().smoothScrollToPosition(1);
	}
	
	protected void commonClickVideoItem(int fristTypeVideoCount, int position) {
		if(fristTypeVideoCount%2 != 0 && position>fristTypeVideoCount){
			showVideo(position-1);
		}else{
			showVideo(position);
		}
	}
	
	public void loadImage(int position, ImageView imageView) {
		MyAppData.getInst().imageLoader.displayImage(
				videoList.get(position).imgPath, imageView,
				MyAppData.getInst().options, null);
	}
}

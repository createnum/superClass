package com.zc.csx;

import android.text.format.DateUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.internal.HeaderGridView;
import com.zancheng.callphonevideoshow.show.commonShow.HttpFragmentActivity;

public class CommonShow extends HttpFragmentActivity {
	public PullToRefreshGridView mPullRefreshGridView;
	
	@Override
	public void dataloadingComplete(boolean needUpdate) {
		// TODO Auto-generated method stub
	}

	public void loadData(){
		
	};
	
	public void initPullRefreshGridView(CommonItemAdapter mAdapter){
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(getResources().getIdentifier("pull_refresh_grid", "id", getPackageName()));
		mPullRefreshGridView.setAdapter(mAdapter);
	    //      当用户拉到底时调用
        mPullRefreshGridView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData();
            }
        }); 
        
   //    Set a listener to be invoked when the list should be refreshed.
        mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<HeaderGridView>() {
   
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
            }
   
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                loadData();
            }
   
        });
	}
}

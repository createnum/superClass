package com.zancheng.callphonevideoshow.show.commonShow.loadVideos;



import java.io.File;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.zancheng.callphonevideoshow.Constants;
import com.zancheng.callphonevideoshow.MyAppData;
import com.zancheng.callphonevideoshow.tools.ToastUtils;
import com.zancheng.callphonevideoshow.tools.Tool;

public class VideoFragmentOutLineCache extends VideoFragmentHasCommend {


    @Override
	public void init_son_data(int videoType, String parameter1) {
		super.init_data(videoType, parameter1);
		if(parameter1 != null){
			fromPlace = Integer.valueOf(parameter1);
		}
		curVideoTypeAllLoadFinish = true;
		//将下载的数据添加到离线缓存中
		VideosEachTypeCount.add(videoList.size());
		if (videoList.isEmpty() && CommonVideoActivityHasRecommend.context != null) {
			CommonVideoActivityHasRecommend.context.showNoResource();
		}
	}
	
	@Override
	protected void clickVideoItem(int position) {
		int fristTypeVideoCount = VideosEachTypeCount.get(0);
		commonClickVideoItem(fristTypeVideoCount, position);
		
	}
	
	@Override
	protected void initCommonView(View view){
		super.initCommonView(view);
		//长按删除视频
		mPullRefreshGridView.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				
				if(position > VideosEachTypeCount.get(0)-1){
					return true;
				}
				if(position < MyAppData.getInst().list_download.size()){
				    ToastUtils.showToast(getActivity(), "视频在下载队列中不可删除！");
				    return true;
				}
				//删除视频文件
				String videoName = Tool.getFileAllNameFromPath(videoList.get(position).videoPath);
				String localPath = Tool.getFileLocalPath(videoName, Constants.DOWNLOAD_VIDEO_ABSOLUTE_PATH, Constants.DIY_VIDEO_ABSOLUTE_PATH);
				if(localPath != null){
					Tool.deleteDir(new File(localPath));
				}
				//删除对应的图片
				String imgName = Tool.getFileAllNameFromPath(videoList.get(position).imgPath);
				String imgPath = Tool.getFileLocalPath(imgName, Constants.DOWNLOAD_PIC_ABSOLUTE_PATH, Constants.DIY_PIC_ABSOLUTE_PATH);
				if(imgPath != null){
					Tool.deleteDir(new File(imgPath));
				}
				MyAppData.getInst().mgr.deleteLocalVideoInfo(videoList.get(position).id);
				videoList.remove(position);
				VideosEachTypeCount.set(0, VideosEachTypeCount.get(0)-1);
				mAdapter.notifyDataSetChanged();
				return true;//否则长按是会执行setOnItemClickListener
			}
    	});
	}
	
	@Override
	public void dataloadingComplete(boolean needUpdate) {
		super.dataloadingComplete(needUpdate);
		switch (videoType) {
		case VT_OUT_LINE_CACHE:
			curVideoTypeAllLoadFinish = true;
		default:
			break;
		}
		if(needUpdate&&mAdapter!=null&&mPullRefreshGridView!=null){
			mAdapter.notifyDataSetChanged();
			mPullRefreshGridView.onRefreshComplete();
		}
	}
	
    public void downLoadFinish() {
        videoList.clear();
        if(MyAppData.getInst().list_download.isEmpty()){
            fristItemProgressbar.setProgress(0);
        }
        loadVideoData(VT_OUT_LINE_CACHE);
//       mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

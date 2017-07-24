package com.zancheng.callphonevideoshow.show.commonShow;

import java.util.ArrayList;
import java.util.List;

import com.zancheng.callphonevideoshow.object.VideoInfo;

import android.app.Activity;

public abstract class HttpFragmentActivity extends Activity{
	public List<VideoInfo> videoList = new ArrayList();
	public abstract void dataloadingComplete(boolean needUpdate);
}

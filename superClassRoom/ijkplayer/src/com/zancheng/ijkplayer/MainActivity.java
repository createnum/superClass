package com.zancheng.ijkplayer;

import java.text.SimpleDateFormat;

import com.zancheng.callphonevideoshow.sdk.R;
import com.zancheng.ijkplayer.common.PlayerManager;
import com.zancheng.ijkplayer.widget.media.IjkVideoView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements
		PlayerManager.PlayerStateListener {
	private String url = "http://skt.kjava.com.cn/videos/May/Six Bomb���������10��baby����о�ӣ�.mp4";
	private String url1 = "http://182.92.149.179/dong/videos/myvideo.flv";
	private String url2 = "http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8";
	private String url3 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
	private String url4 = "http://42.96.249.166/live/24035.m3u8";
	private PlayerManager player;
	private TextView countTime, currentTime;
	private SeekBar seekbar;
	private IjkVideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initPlayer();
		initData();
	}

	private void initPlayer() {
		player = new PlayerManager(this);
		player.setFullScreenOnly(true);
		player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
		player.playInFullScreen(true);
		player.setPlayerStateListener(this);
		player.play(url1);
		videoView = player.getVideoView();
		countTime = (TextView) findViewById(R.id.time_end);
		currentTime = (TextView) findViewById(R.id.time_start);
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
	}

	public void initData(){
		uiHandler.sendEmptyMessageDelayed(0, 200);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (player.gestureDetector.onTouchEvent(event))
			return true;
		return super.onTouchEvent(event);
	}

	@Override
	public void onComplete() {
	}

	@Override
	public void onError() {
	}

	@Override
	public void onLoading() {
	}

	@Override
	public void onPlay() {
	}

	class OnSeekBarChangeListenerImp implements SeekBar.OnSeekBarChangeListener {

		// �����������϶�
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		// ��ʾ�������տ�ʼ�϶�����ʼ�϶�ʱ�򴥷��Ĳ���
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		// ֹͣ�϶�ʱ��
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			if (player != null && player.isPlaying()) {
				// ���õ�ǰ���ŵ�λ��
				videoView
						.seekTo((int) (1.0f * seekBar.getProgress() / 100 * videoView
								.getDuration()));
			}
		}
	}

	Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (videoView.getDuration() > 0) {
					seekbar.setMax(videoView.getDuration());
					seekbar.setProgress(videoView.getCurrentPosition());
				}
				updateTextViewWithTimeFormat(currentTime,
						videoView.getCurrentPosition() / 1000);
				updateTextViewWithTimeFormat(countTime,
						videoView.getDuration() / 1000);
				uiHandler.sendEmptyMessageDelayed(0, 200);
				break;
			}
		}
	};

	private void updateTextViewWithTimeFormat(TextView textView, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String stringTemp = null;
		if (0 != hh) {
			stringTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			stringTemp = String.format("%02d:%02d", mm, ss);
		}
		textView.setText(stringTemp);
	}
	
}
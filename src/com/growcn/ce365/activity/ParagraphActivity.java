package com.growcn.ce365.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.growcn.ce365.R;
import com.growcn.ce365.R.drawable;
import com.growcn.ce365.R.id;
import com.growcn.ce365.R.layout;
import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.adapter.ParagraphAdapter;
import com.growcn.ce365.base.ActivityUtil;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.db.LessonDb;
import com.growcn.ce365.db.ParagraphDb;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.service.PlayerService;
import com.growcn.ce365.util.AppConstant;
import com.growcn.ce365.util.AppConstant.ActivityParams;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.PlayerMsg;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ParagraphActivity extends GrowcnBaseActivity {
	private PlayerService.MyBinder myBinder;

	private ListView mListView;
	private ParagraphAdapter mAdapter;
	private List<Paragraph> mArrayList = new ArrayList<Paragraph>();
	private String lesson_id;
	private String lessonUuid;

	// private MediaPlayer mMediaPlayer; // = new MediaPlayer();

	private ImageView mPlay_control;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		initIntent();
		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserBackButton();
		mActivityUtil.setBrowserSetting();

		if (lessonUuid != null) {
			Lesson mLesson = LessonDb.FindByUuid(lessonUuid);
			if (mLesson != null) {
				mActivityUtil.setTitle(mLesson.name);
			}
		}

		initViewPlay();
		initPlayerServer();
		load_listview();
	}

	// @Override
	public void onResume() {
		super.onResume();

	}

	private void initIntent() {
		Bundle bundle = getIntent().getExtras();
		lessonUuid = bundle.getString(ActivityParams.UUID);
	}

	private void load_listview() {
		mListView = (ListView) findViewById(R.id.list_view_paragraph);
		initAdvListview();
		mAdapter = new ParagraphAdapter(this, mArrayList, mListView,
				mPlay_control);
		mListView.setAdapter(mAdapter);
		// networkReques();
		getLoactionDB();

	}

	// 广告view
	private void initAdvListview() {
		if (Config.isAdvModel) {
			View ad_view = LayoutInflater.from(this).inflate(
					R.layout.adv_admob, null);
			// mListView.addHeaderView(ad_view);
			mListView.addFooterView(ad_view);
			// 建立 adView
			adView = new AdView(this, AdSize.BANNER, Config.AdmobID);
			// 查询 LinearLayout (假设您已经提供)
			// 属性是 android:id="@+id/mainLayout"
			RelativeLayout layout = (RelativeLayout) ad_view
					.findViewById(R.id.ad_Layout);
			// // 在其中加入 adView
			layout.addView(adView);
			// // 启用泛用请求，并随广告一起载入
			adView.loadAd(new AdRequest());
		}
	}

	private void initViewPlay() {
		mPlay_control = (ImageView) findViewById(R.id.play_control);
		mPlay_control.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// play_list();
				play_or_stop();
			}

		});
	}

	private void initPlayerServer() {
		Intent intent = new Intent(this, PlayerService.class);
		startService(intent);
		bindService(intent, MyConn, BIND_AUTO_CREATE);
	}

	private Boolean flag = false;

	private void unBind() {
		if (flag == true) {
			Log.i(Config.TAG, "BindService-->unBind()");
			unbindService(MyConn);
			flag = false;
		}
	}

	private ServiceConnection MyConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myBinder = (PlayerService.MyBinder) service;
			flag = true;
			setPlayBtnStat(myBinder.Status());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	private void setPlayBtnStat(int status) {
		Log.e(Config.TAG, ".....status:" + status);
		// || status == PlayerMsg.INIT_MSG
		if (status == PlayerMsg.PLAY_MSG) {
			mPlay_control.setImageDrawable(getResources().getDrawable(
					R.drawable.pause));

			// mPlay_control.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.pause));
		} else {
			// mPlay_control.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.play));
			mPlay_control.setImageDrawable(getResources().getDrawable(
					R.drawable.play));
		}
	}

	private void play_or_stop() {
		Log.e(Config.TAG, "LessonUuid......." + lessonUuid);
		myBinder.play_or_stop(lessonUuid);
		setPlayBtnStat(myBinder.Status());
		Log.e(Config.TAG, "Status()......." + myBinder.Status());
	}

	@Override
	protected void onDestroy() {
		unBind();
		super.onDestroy();
	}

	// private void play_list() {
	// Log.e(Config.TAG, ".......sfsdfs");
	// Intent intent = new Intent();
	// intent.setClass(this, PlayerService.class);
	// intent.putExtra(ActivityParams.LessonUuid, lessonUuid);
	// intent.putExtra(ActivityParams.MSG, PlayerMsg.PLAY_MSG);
	// startService(intent);
	// }

	public void getLoactionDB() {
		if (lessonUuid != null) {
			List<Paragraph> listParagraphs = ParagraphDb.findAll(lessonUuid);
			mArrayList.addAll(listParagraphs);
		}
	}

	// public void networkReques() {
	// AsyncHttpClient client = new AsyncHttpClient();
	// client = BaseClient.get_client_info(this);
	// client.get(ServerApi.ParagraphAll(lesson_id),
	// new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(String response) {
	// List<Paragraph> mlist = Paragraph.parseJSON(response);
	// if (mlist.size() != 0) {
	// mArrayList.addAll(mlist);
	// mAdapter.notifyDataSetChanged();
	// } else {
	// ToastShow("无新的数据！！");
	// }
	// }
	//
	// @Override
	// public void onFailure(int statusCode, Header[] headers,
	// byte[] responseBody, Throwable error) {
	// ToastShow("网络异常！！");
	// }
	// });
	// }

}

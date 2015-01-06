package com.growcn.ce365;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

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

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ParagraphActivity extends GrowcnBaseActivity {

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

		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserBackButton();
		mActivityUtil.setBrowserSetting();

		initIntent();
		initViewPlay();
		load_listview();
	}

	private void initIntent() {
		Bundle bundle = getIntent().getExtras();
		lessonUuid = bundle.getString(ActivityParams.UUID);
	}

	private void load_listview() {
		mListView = (ListView) findViewById(R.id.list_view_paragraph);
		mAdapter = new ParagraphAdapter(this, mArrayList, mListView,
				mPlay_control);
		mListView.setAdapter(mAdapter);
		// networkReques();
		getLoactionDB();
	}

	private void initViewPlay() {
		mPlay_control = (ImageView) findViewById(R.id.play_control);
		mPlay_control.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play_list();
			}
		});
	}

	private void play_list() {
		Log.e(Config.TAG, ".......sfsdfs");
		Intent intent = new Intent();
		intent.setClass(this, PlayerService.class);
		intent.putExtra(ActivityParams.LessonUuid, lessonUuid);
		intent.putExtra(ActivityParams.MSG, PlayerMsg.PLAY_MSG);
		startService(intent);

	}

	public void getLoactionDB() {
		List<Paragraph> listParagraphs = ParagraphDb.findAll(lessonUuid);
		mArrayList.addAll(listParagraphs);
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

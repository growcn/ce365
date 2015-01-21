package com.growcn.ce365.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.R;
import com.growcn.ce365.R.id;
import com.growcn.ce365.R.layout;
import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.base.ActivityUtil;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.db.LessonDb;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.plugin.upload_apk.RequestUpgradeSoft;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.growcn.ce365.plugin.upload_apk.RequestUpgradeSoft;

public class LessonActivity extends GrowcnBaseActivity {

	private ListView mListView;
	private LessonAdapter mLessonAdapter;
	private List<Lesson> mArrayList = new ArrayList<Lesson>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lesson);

		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserSetting();

		load_listview();
		// 检查升级
		upgrade_app(false);
	}

	private void load_listview() {
		mListView = (ListView) findViewById(R.id.list_view_lesson);
		mLessonAdapter = new LessonAdapter(this, mArrayList, mListView);
		mListView.setAdapter(mLessonAdapter);
		// networkReques();
		getLoactionDB();
	}

	public void getLoactionDB() {
		try {
			List<Lesson> listLesson = LessonDb.findAll();
			mArrayList.addAll(listLesson);
		} catch (Exception e) {

		}
	}

	// public void networkReques() {
	// AsyncHttpClient client = new AsyncHttpClient();
	// client = BaseClient.get_client_info(this);
	// client.get(ServerApi.LessonAll(), new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(String response) {
	// List<Lesson> listLesson = Lesson.parseJSON(response);
	// if (listLesson.size() != 0) {
	// mArrayList.addAll(listLesson);
	// mLessonAdapter.notifyDataSetChanged();
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

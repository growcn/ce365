package com.growcn.ce365;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.plugin.upload_apk.UpdateManager;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView mListView;
	private LessonAdapter mLessonAdapter;
	private List<Lesson> mArrayList = new ArrayList<Lesson>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 检查升级
		load_listview();

		// UpdateManager manager = new UpdateManager(this);
		// // 检查软件更新
		// manager.checkUpdate();

	}

	private void load_listview() {
		mListView = (ListView) findViewById(R.id.list_view_lesson);
		mLessonAdapter = new LessonAdapter(this, mArrayList, mListView);
		mListView.setAdapter(mLessonAdapter);
		networkReques();
	}

	public void networkReques() {
		AsyncHttpClient client = new AsyncHttpClient();
		client = BaseClient.get_client_info(this);
		client.get(ServerApi.LessonAll(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				List<Lesson> listLesson = Lesson.parseJSON(response);
				if (listLesson.size() != 0) {
					mArrayList.addAll(listLesson);
					mLessonAdapter.notifyDataSetChanged();
				} else {
					ToastShow("无新的数据！！");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				ToastShow("网络异常！！");
			}
		});
	}

	private void ToastShow(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

package com.growcn.ce365;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.adapter.ParagraphAdapter;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.util.AppConstant;
import com.growcn.ce365.util.AppConstant.ActivityParams;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class ParagraphActivity extends GrowcnBaseActivity {

	private ListView mListView;
	private ParagraphAdapter mAdapter;
	private List<Paragraph> mArrayList = new ArrayList<Paragraph>();
	private String lesson_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paragraph);

		initIntent();
		load_listview();
	}

	private void initIntent() {
		Bundle bundle = getIntent().getExtras();
		lesson_id = bundle.getString(ActivityParams.ID);
	}

	private void load_listview() {
		mListView = (ListView) findViewById(R.id.list_view_lesson);
		mAdapter = new ParagraphAdapter(this, mArrayList, mListView);
		mListView.setAdapter(mAdapter);
		networkReques();
	}

	public void networkReques() {
		AsyncHttpClient client = new AsyncHttpClient();
		client = BaseClient.get_client_info(this);
		client.get(ServerApi.ParagraphAll(lesson_id),
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						List<Paragraph> mlist = Paragraph.parseJSON(response);
						if (mlist.size() != 0) {
							mArrayList.addAll(mlist);
							mAdapter.notifyDataSetChanged();
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

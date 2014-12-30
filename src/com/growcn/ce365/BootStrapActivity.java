package com.growcn.ce365;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.db.DBBaseHelper;
import com.growcn.ce365.db.LessonDb;
import com.growcn.ce365.db.ParagraphDb;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.BookAll;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.plugin.upload_apk.RequestUpgradeSoft;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.growcn.ce365.util.OpenIntent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.image.SmartImageView;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.growcn.ce365.plugin.upload_apk.RequestUpgradeSoft;

public class BootStrapActivity extends GrowcnBaseActivity {

	private Handler handler;
	private long logotime = 1500;
	private SmartImageView mSimage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(false);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bootstrap);

		RequesBooks();
		auto_index();

		// 数库初始化
		DBBaseHelper.init(this);
	}

	public void auto_index() {
		handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toIndex();
			}
		}, logotime);
	}

	public void toIndex() {
		OpenIntent.index(this);
		finish();
	}

	public void RequesBooks() {
		AsyncHttpClient client = new AsyncHttpClient();
		client = BaseClient.get_client_info(this);
		client.get(ServerApi.books_path(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				// Log.e(Config.TAG, "response:" + response);
				sync_data(response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				ToastShow("网络异常！！");
			}
		});
	}

	// 同步本地DB
	private void sync_data(String response) {
		BookAll mBookAll = BookAll.parseJSON(response);
		List<Lesson> lessons = mBookAll.lessons;

		LessonDb.DeleteRedundant(lessons);
		for (int i = 0; i < lessons.size(); i++) {
			Lesson mLesson = lessons.get(i);
			LessonDb.sync(mLesson);
			// Log.e(Config.TAG, ">>>>>>>>>>" + mLesson.name);

			List<Paragraph> paragraphs = mLesson.paragraphs;

			if (paragraphs != null) {
				for (int j = 0; j < paragraphs.size(); j++) {
					Paragraph mParagraph = paragraphs.get(j);
					ParagraphDb.sync(mLesson.uuid, mParagraph);
					// Log.e(Config.TAG, ">>cgg>>>>>>>>" + mParagraph.name);
				}
			}
			//
			ParagraphDb.DeleteRedundant(mLesson.uuid, paragraphs);
		}

	}
}

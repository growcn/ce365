package com.growcn.ce365.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.growcn.ce365.R;
import com.growcn.ce365.R.id;
import com.growcn.ce365.R.layout;
import com.growcn.ce365.R.string;
import com.growcn.ce365.adapter.DownLoadAdapter;
import com.growcn.ce365.adapter.LessonAdapter;
import com.growcn.ce365.adapter.ParagraphAdapter;
import com.growcn.ce365.base.ActivityUtil;
import com.growcn.ce365.base.GrowcnBaseActivity;
import com.growcn.ce365.db.LessonDb;
import com.growcn.ce365.db.ParagraphDb;
import com.growcn.ce365.internal.BaseClient;
import com.growcn.ce365.model.Lesson;
import com.growcn.ce365.model.LoadInfo;
import com.growcn.ce365.model.Paragraph;
import com.growcn.ce365.model.ResourceEntity;
import com.growcn.ce365.service.PlayerService;
import com.growcn.ce365.thread.Downloader;
import com.growcn.ce365.util.AppConstant;
import com.growcn.ce365.util.AppConstant.ActivityParams;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.AppConstant.Dir;
import com.growcn.ce365.util.AppConstant.PlayerMsg;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.growcn.ce365.util.FileUtils;
import com.growcn.ce365.util.VersionUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.ads.*;

public class DownLoadActivity extends GrowcnBaseActivity {
	private ListView mListView;
	private DownLoadAdapter mDownLoadAdapter;
	private List<ResourceEntity> mArrayList = new ArrayList<ResourceEntity>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);

		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserBackButton();
		mActivityUtil.setTitle(this.getString(R.string.action_download));
		initView();
		initViewAdapter();
		networkReques();
		loadAdMob();
	}

	private void initView() {
//		Button btn_unzip = (Button) findViewById(R.id.btn_unzip);
//		btn_unzip.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startUnzip();
//			}
//		});
	}

	private void initViewAdapter() {
		mListView = (ListView) findViewById(R.id.list_view_download);
		// initAdvListview();
		mDownLoadAdapter = new DownLoadAdapter(this, mArrayList);
		mListView.setAdapter(mDownLoadAdapter);
	}

	// 广告view
	// private void initAdvListview() {
	// if (Config.isAdvModel) {
	// View ad_view = LayoutInflater.from(this).inflate(
	// R.layout.adv_admob, null);
	// // mListView.addHeaderView(ad_view);
	// mListView.addFooterView(ad_view);
	// // 建立 adView
	// adView = new AdView(this, AdSize.BANNER, Config.AdmobID);
	// // 查询 LinearLayout (假设您已经提供)
	// // 属性是 android:id="@+id/mainLayout"
	// RelativeLayout layout = (RelativeLayout) ad_view
	// .findViewById(R.id.ad_Layout);
	// // // 在其中加入 adView
	// layout.addView(adView);
	// // // 启用泛用请求，并随广告一起载入
	// adView.loadAd(new AdRequest());
	// }
	// }

	public void networkReques() {
		AsyncHttpClient client = new AsyncHttpClient();
		client = BaseClient.get_client_info(this);
		String pageage_name = new VersionUtils(this).getPackage();
		client.get(ServerApi.Resources(pageage_name),
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						List<ResourceEntity> ResourceEntityList = ResourceEntity
								.parseJSON(response);
						if (ResourceEntityList.size() != 0) {
							mArrayList.addAll(ResourceEntityList);
							mDownLoadAdapter.notifyDataSetChanged();
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

	
	
	
	
	
}

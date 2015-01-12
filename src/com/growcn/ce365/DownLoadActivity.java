package com.growcn.ce365;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

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
import com.growcn.ce365.service.Downloader;
import com.growcn.ce365.service.PlayerService;
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

	// 固定存放下载的音乐的路径：SD卡目录下
	private static final String SD_PATH = Dir.DLTMP();
	// 存放各个下载器
	private Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
	// 存放与下载器对应的进度条
	private Map<String, ProgressBar> ProgressBars = new HashMap<String, ProgressBar>();

	private String zipFileName = "ce365.zip";

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

	

	/**
	 * 响应开始下载按钮的点击事件
	 */
	public void startDownload(View v) {
		// 得到textView的内容
		LinearLayout layout = (LinearLayout) v.getParent();
		String resouceName = ((TextView) layout
				.findViewById(R.id.tv_resouce_name)).getTag().toString();
		String urlstr = resouceName;

		String localfile = SD_PATH + zipFileName;
		// 设置下载线程数为4，这里是我为了方便随便固定的
		String threadcount = "4";
		DownloadTask downloadTask = new DownloadTask(v);
		downloadTask.execute(urlstr, localfile, threadcount);

	};

	class DownloadTask extends AsyncTask<String, Integer, LoadInfo> {
		Downloader downloader = null;
		View v = null;
		String urlstr = null;

		public DownloadTask(final View v) {
			this.v = v;
		}

		@Override
		protected void onPreExecute() {
			Button btn_start = (Button) ((View) v.getParent())
					.findViewById(R.id.btn_start);
			Button btn_pause = (Button) ((View) v.getParent())
					.findViewById(R.id.btn_pause);
			btn_start.setVisibility(View.GONE);
			btn_pause.setVisibility(View.VISIBLE);
		}

		@Override
		protected LoadInfo doInBackground(String... params) {
			urlstr = params[0];
			String localfile = params[1];
			int threadcount = Integer.parseInt(params[2]);
			// 初始化一个downloader下载器
			downloader = downloaders.get(urlstr);
			if (downloader == null) {
				downloader = new Downloader(urlstr, localfile, threadcount,
						DownLoadActivity.this, mHandler);
				downloaders.put(urlstr, downloader);
			}
			if (downloader.isdownloading())
				return null;
			// 得到下载信息类的个数组成集合
			return downloader.getDownloaderInfors();
		}

		@Override
		protected void onPostExecute(LoadInfo loadInfo) {
			if (loadInfo != null) {
				// 显示进度条
				showProgress(loadInfo, urlstr, v);
				// 调用方法开始下载
				downloader.download();
			}
		}

	};

	/**
	 * 显示进度条
	 */
	private void showProgress(LoadInfo loadInfo, String url, View v) {
		ProgressBar bar = ProgressBars.get(url);
		if (bar == null) {
			bar = new ProgressBar(this, null,
					android.R.attr.progressBarStyleHorizontal);
			bar.setMax(loadInfo.getFileSize());
			bar.setProgress(loadInfo.getComplete());
			ProgressBars.put(url, bar);

			LinearLayout.LayoutParams params = new LayoutParams(
					LayoutParams.FILL_PARENT, 5);
			// ((LinearLayout) ((LinearLayout) v.getParent()).getParent())
			// .addView(bar, params);
			((RelativeLayout) ((LinearLayout) v.getParent()).getParent())
					.addView(bar, params);

		}
	}

	/**
	 * 响应暂停下载按钮的点击事件
	 */
	public void pauseDownload(View v) {
		LinearLayout layout = (LinearLayout) v.getParent();
		String resouceName = ((TextView) layout
				.findViewById(R.id.tv_resouce_name)).getTag().toString();
		String urlstr = resouceName;
		downloaders.get(urlstr).pause();
		Button btn_start = (Button) ((View) v.getParent())
				.findViewById(R.id.btn_start);
		Button btn_pause = (Button) ((View) v.getParent())
				.findViewById(R.id.btn_pause);
		btn_pause.setVisibility(View.GONE);
		btn_start.setVisibility(View.VISIBLE);
	}

	/**
	 * 利用消息处理机制适时更新进度条
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				String url = (String) msg.obj;
				int length = msg.arg1;
				ProgressBar bar = ProgressBars.get(url);
				if (bar != null) {
					// 设置进度条按读取的length长度更新
					bar.incrementProgressBy(length);
					if (bar.getProgress() == bar.getMax()) {
						RelativeLayout layout = (RelativeLayout) bar
								.getParent();
						TextView resouceName = (TextView) layout
								.findViewById(R.id.tv_resouce_name);
						Toast.makeText(DownLoadActivity.this,
								"[" + resouceName.getText() + "]下载完成！",
								Toast.LENGTH_SHORT).show();
						// 下载完成后清除进度条并将map中的数据清空
						layout.removeView(bar);
						ProgressBars.remove(url);
						downloaders.get(url).delete(url);
						downloaders.get(url).reset();
						downloaders.remove(url);

						Button btn_start = (Button) layout
								.findViewById(R.id.btn_start);
						Button btn_pause = (Button) layout
								.findViewById(R.id.btn_pause);
						btn_pause.setVisibility(View.GONE);
						btn_start.setVisibility(View.GONE);
					}
				}
			}
		}
	};
}

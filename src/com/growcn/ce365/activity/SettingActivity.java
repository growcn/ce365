package com.growcn.ce365.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.R;
import com.growcn.ce365.R.id;
import com.growcn.ce365.R.layout;
import com.growcn.ce365.R.string;
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
import com.growcn.ce365.util.AppConstant.Dir;
import com.growcn.ce365.util.AppConstant.PlayerMsg;
import com.growcn.ce365.util.AppConstant.ServerApi;
import com.growcn.ce365.util.FileSizeUtil;
import com.growcn.ce365.util.OpenIntent;
import com.growcn.ce365.util.VersionUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.*;

public class SettingActivity extends GrowcnBaseActivity {
	private Context mContext;
	// 进度条
	public ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mContext = this;
		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserBackButton();
		mActivityUtil.setTitle(this.getString(R.string.action_settings));
		// mActivityUtil.setBrowserSetting();
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
		loadAdMob();
	}

	private void initView() {
		TextView mVers = (TextView) findViewById(R.id.version_value);
		TextView mCacheSize = (TextView) findViewById(R.id.clear_cache_value);
		LinearLayout ToAbout = (LinearLayout) findViewById(R.id.setting_about);
		LinearLayout ToFeedback = (LinearLayout) findViewById(R.id.setting_feedback);
		LinearLayout Upgrade = (LinearLayout) findViewById(R.id.setting_upgrade);
		LinearLayout mClearCache = (LinearLayout) findViewById(R.id.setting_clear_cache);
		LinearLayout mOfflineFile = (LinearLayout) findViewById(R.id.setting_offlinefile);

		//
		final String mp3_path = Dir.DLAudio();
		double filesize = FileSizeUtil.getFileOrFilesSize(mp3_path,
				FileSizeUtil.SIZETYPE_MB);
		mCacheSize.setText(filesize + "MB");
		mVers.setText(new VersionUtils(mContext).getName());

		mClearCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowProgressBarDialog();
				delAllFile(mp3_path);
				CloseProgressBarDialog();
				onResume();
			}
		});

		ToFeedback.setOnClickListener(MyOnClickListener);
		ToAbout.setOnClickListener(MyOnClickListener);
		Upgrade.setOnClickListener(MyOnClickListener);
		mOfflineFile.setOnClickListener(MyOnClickListener);

	}

	View.OnClickListener MyOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_feedback:
				OpenIntent.feedback(SettingActivity.this);
				break;
			case R.id.setting_about:
				OpenIntent.about(mContext);
				break;
			case R.id.setting_upgrade:
				upgrade_app(true);
				break;
			case R.id.setting_offlinefile:
				OpenIntent.offlineDown(mContext);
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	public void ShowProgressBarDialog() {
		dialog = new ProgressDialog(this);
		// dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog.setIcon(R.drawable.ic_launcher);
		// dialog.setTitle("正在加载...");
		dialog.setMessage("正在清空... ...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void CloseProgressBarDialog() {
		dialog.dismiss();
	}

}

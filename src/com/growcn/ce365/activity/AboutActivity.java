package com.growcn.ce365.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.growcn.ce365.R;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.ads.*;

public class AboutActivity extends GrowcnBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setSwipeBackEnable(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mActivityUtil = new ActivityUtil(this);
		mActivityUtil.setBrowserBackButton();
		mActivityUtil.setTitle(this.getString(R.string.action_about));

		initView();
		loadAdMob();
	}

	private void initView() {

	}

}

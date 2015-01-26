package com.growcn.ce365.base;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.growcn.ce365.R;
import com.growcn.ce365.db.DBBaseHelper;
import com.growcn.ce365.plugin.swipeback.SwipeBackActivity;
import com.growcn.ce365.plugin.swipeback.SwipeBackLayout;
import com.growcn.ce365.plugin.upload_apk.RequestUpgradeSoft;
import com.growcn.ce365.util.AppConstant.Config;
import com.growcn.ce365.util.OpenIntent;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GrowcnBaseActivity extends SwipeBackActivity {
	protected ActivityUtil mActivityUtil;
	protected AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// @Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	// @Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mActivityUtil != null) {
			ActivityUtil w = mActivityUtil;
			w = null;
			mActivityUtil = null;
		}

		if (adView != null) {
			AdView w = adView;
			w.removeAllViews();
			w.destroy();
			w = null;
			adView = null;
		}
	}

	// 载入广告
	protected void loadAdMob() {
		if (Config.isAdvModel) {
			// 建立 adView
			adView = new AdView(this, AdSize.BANNER, Config.AdmobID);
			// 查询 LinearLayout (假设您已经提供)
			// 属性是 android:id="@+id/mainLayout"
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.ad_Layout);
			// 在其中加入 adView
			layout.addView(adView);
			// 启用泛用请求，并随广告一起载入
			adView.loadAd(new AdRequest());
		}
	}

	// @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 通过点击了哪个菜单子项来改变Activity的标题
	// @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_index:
			OpenIntent.index(this);
			break;
		case R.id.action_settings:
			OpenIntent.setting(this);
			break;
		case R.id.action_about:
			OpenIntent.about(this);
			break;
		case R.id.action_upgrade:
			upgrade_app(true);
			break;
		case R.id.action_feedback:
			OpenIntent.feedback(this);
			break;
		}
		return true;

	}

	// 检查软件更新
	protected void upgrade_app(Boolean mBoolean) {
		new RequestUpgradeSoft(this, mBoolean).start();
		// UpdateManager manager = new UpdateManager(this);
		// manager.checkUpdate();
	}

	// Toast
	protected void ToastShow(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}

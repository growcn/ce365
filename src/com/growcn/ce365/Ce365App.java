package com.growcn.ce365;

import java.io.IOException;

import com.growcn.ce365.db.DBBaseHelper;
import com.growcn.ce365.util.AppConstant.Config;

import android.app.Application;
import android.util.Log;

public class Ce365App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(Config.TAG, "init app");
		// 数库初始化
		try {
			new DBBaseHelper(this, "create").createDataBase();
		} catch (IOException e) {
			// e.printStackTrace();
			Log.e(Config.TAG, "init db error:" + e.getMessage());
		}
	}

}

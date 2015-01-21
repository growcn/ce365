package com.growcn.ce365;

import com.growcn.ce365.db.DBBaseHelper;

import android.app.Application;

public class Ce365App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// 数库初始化
		DBBaseHelper.init(this);
	}

}

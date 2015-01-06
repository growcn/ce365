package com.growcn.ce365.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtils {
	private String versionName; // 当前应用的版本名称
	private String packageName; // 当前版本的包名
	private int versionCode = 0; // 当前版本的版本号

	public VersionUtils(Context mContext) {
		PackageInfo info;
		try {
			info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			this.versionName = info.versionName; // 当前应用的版本名称
			this.packageName = info.packageName; // 当前版本的包名
			this.versionCode = info.versionCode; // 当前版本的版本号

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// Log.e(Config.TAG, "getVersionCode error :" + e.getMessage());
		}
	}

	public String getName() {
		return versionName;
	}

	public int getCode() {
		return versionCode;
	}

	public String getPackage() {
		return packageName;
	}
}

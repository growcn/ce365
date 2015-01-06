package com.growcn.ce365.util;

import com.growcn.ce365.util.AppConstant.Config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class ReadManifestUtil {
	private String myApiKey;

	public ReadManifestUtil(Context mContext, String Key) {

		try {
			ApplicationInfo ai = mContext.getPackageManager()
					.getApplicationInfo(mContext.getPackageName(),
							PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			myApiKey = bundle.getString(Key);
		} catch (NameNotFoundException e) {
			Log.e(Config.TAG,
					"Failed to load meta-data, NameNotFound: " + e.getMessage());
		} catch (NullPointerException e) {
			Log.e(Config.TAG,
					"Failed to load meta-data, NullPointer: " + e.getMessage());
		}
	}

	public String getVal() {
		return myApiKey;
	}

}

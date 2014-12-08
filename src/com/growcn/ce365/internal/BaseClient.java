package com.growcn.ce365.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

public class BaseClient {

	public static AsyncHttpClient get_client_info(Context ctx) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(50000);
		List<Header> headers = new ArrayList<Header>();
		// client.addHeader("identifier", DeviceInfo.findUDID(ctx));
		client.addHeader("platform", "android");
		return client;
	}

}

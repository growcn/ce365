package com.growcn.ce365.plugin.upload_apk;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
	private static String TAG = "HttpUtils";

	public static boolean urlExist(String url) {
		URLConnection urlCon;
		InputStream inStream; // 只需判断这个变量值
		try {
			urlCon = new URL(url).openConnection();
			inStream = urlCon.getInputStream();
			if (inStream != null)
				return true;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}

	/**
	 * get server response of downloading game from market
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static String getUrlContent(String url) {
		HttpGet request = new HttpGet(url);
		BufferedReader br = null;

		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code >= 200 && code < 300) {
				br = new BufferedReader(new InputStreamReader(httpResponse
						.getEntity().getContent()));
				String line = "";
				StringBuffer strBuf = new StringBuffer("");
				while ((line = br.readLine()) != null) {
					strBuf.append(line);
				}
				br.close();
				// Log.d(TAG, "Response Content: " + strBuf.toString());
				return strBuf.toString();
			} else {
				Log.e(TAG, "http status wrong: " + code);
				return null;
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, "ClientProtocolException:" + e.toString());
		} catch (IOException e) {
			Log.e(TAG, "IOException:" + e.toString());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
		return null;
	}

	public static boolean sendHttpPost(String url, Map<String, String> infoMap,
			String judgement) {
		Log.d(TAG, "send info to: " + url);
		boolean bResponse = false;

		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (String key : infoMap.keySet()) {
			params.add(new BasicNameValuePair(key, infoMap.get(key)));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (200 <= code && code < 300) {
				String strResponse = EntityUtils.toString(httpResponse
						.getEntity());
				Log.d(TAG, "response result: " + strResponse);
				if (strResponse.equals(judgement)) {
					bResponse = true;
				} else {
					bResponse = false;
				}
			} else {
				Log.d(TAG, "Request: " + url + " Error code: " + code);
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		return bResponse;
	}
}

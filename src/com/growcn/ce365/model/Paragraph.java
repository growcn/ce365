package com.growcn.ce365.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.growcn.ce365.util.AppConstant.Config;

public class Paragraph {
	public String name;
	public String translation;
	public String token_name;
	public String audio_url;

	public Paragraph getInstance(String notice) {
		return new Gson().fromJson(notice, Paragraph.class);
	}

	public static List<Paragraph> parseJSON(String response) {
		Log.e(Config.TAG, "json" + response);
		List<Paragraph> mList = new ArrayList<Paragraph>();
		try {
			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				mList.add(new Paragraph().getInstance(json.toString()));
			}
			return mList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}

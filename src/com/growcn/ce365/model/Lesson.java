package com.growcn.ce365.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.growcn.ce365.util.AppConstant.Config;

public class Lesson {
	public String id;
	public String name;

	public Lesson getInstance(String notice) {
		return new Gson().fromJson(notice, Lesson.class);
	}

	public static List<Lesson> parseJSON(String response) {
		Log.e(Config.TAG, "json" + response);
		List<Lesson> mList = new ArrayList<Lesson>();
		try {
			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				mList.add(new Lesson().getInstance(json.toString()));
			}
			return mList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}

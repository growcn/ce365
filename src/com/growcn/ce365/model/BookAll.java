package com.growcn.ce365.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.growcn.ce365.util.AppConstant.Config;

public class BookAll {
	public String name;
	public List<Lesson> lessons;

	public BookAll getInstance(String notice) {
		return new Gson().fromJson(notice, BookAll.class);
	}

	public static BookAll parseJSON(String response) {
		BookAll mBookAll = null;
		try {
			mBookAll = new BookAll();
			JSONObject hash = new JSONObject(response);
			String lessons = hash.getJSONArray("lessons").toString();
			mBookAll.name = hash.getString("name");
			mBookAll.lessons = Lesson.parseJSON(lessons);
			// Log.e(Config.TAG, "......." + mBookAll.name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mBookAll;
	}
}

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
	public int id;
	public String name;
	public String uuid;
	public int sort;
	public int lastmodifytime;
	public List<Paragraph> paragraphs;

	public Lesson getInstance(String notice) {
		return new Gson().fromJson(notice, Lesson.class);
	}

	public static List<Lesson> parseJSON(String response) {
		// Log.e(Config.TAG, "json" + response);
		List<Lesson> mList = new ArrayList<Lesson>();
		try {
			JSONArray array = new JSONArray(response);
			// Lesson mLesson;
			for (int i = 0; i < array.length(); i++) {
				JSONObject lessonJson = array.getJSONObject(i);
				// mLesson = new Lesson();
				//
				//
				// String paragraphsJson = lessonJson.getJSONArray("paragraphs")
				// .toString();
				// mLesson = new Lesson().getInstance(lessonJson.toString());

				// Log.e(Config.TAG, "json:" + paragraphsJson);
				// mLesson.paragraphs = Paragraph.parseJSON(paragraphsJson,
				// mLesson.uuid);
				// Log.e(Config.TAG, "mLesson name " + mLesson.name + "uuid:"
				// + mLesson.uuid);
				// mList.add(mLesson);

				mList.add(new Lesson().getInstance(lessonJson.toString()));
				// Log.e(Config.TAG, ".........." + json.toString());
			}

			return mList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}

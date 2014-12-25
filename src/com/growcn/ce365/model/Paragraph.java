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
	public int id;
	public String name;
	public String translation;
	public String lesson_uuid;
	public String uuid;
	public int sort;
	public String audio_url;
	public int lastmodifytime;

	public Paragraph getInstance(String mParagraph, String LessonUuid) {
		Paragraph msParagraph = new Gson()
				.fromJson(mParagraph, Paragraph.class);
		if (LessonUuid != null) {
			msParagraph.lesson_uuid = LessonUuid;
		}
		return msParagraph;
	}

	public Paragraph getInstance(String mParagraph) {
		return new Gson().fromJson(mParagraph, Paragraph.class);
	}

	public static List<Paragraph> parseJSON(String response, String LessonUuid) {
		// Log.e(Config.TAG, "json" + response);
		List<Paragraph> mList = new ArrayList<Paragraph>();
		try {
			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				mList.add(new Paragraph().getInstance(json.toString(),
						LessonUuid));
			}
			return mList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}

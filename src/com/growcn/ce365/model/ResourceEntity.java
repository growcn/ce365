package com.growcn.ce365.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.growcn.ce365.util.AppConstant.Config;

public class ResourceEntity {
	public String name;
	public String uuid;
	public String file_url;

	public ResourceEntity getInstance(String notice) {
		return new Gson().fromJson(notice, ResourceEntity.class);
	}

	public static List<ResourceEntity> parseJSON(String response) {
		// Log.e(Config.TAG, "json" + response);

		List<ResourceEntity> mList = new ArrayList<ResourceEntity>();
		try {
			JSONObject hash = new JSONObject(response);
			JSONArray array = hash.getJSONArray("resources");
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				mList.add(new ResourceEntity().getInstance(json.toString()));
			}
			return mList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}

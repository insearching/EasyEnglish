package com.tntu.easyenglish.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tntu.easyenglish.entity.Content;

public class JSONUtils {
	
	private static final String SUCCESS = "success";
	public static final String SUCCESS_TRUE = "true";
	public static final String SUCCESS_FALSE = "false";
	
	public static String getResponseStatus(String json) {
		String success = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
			success = jsonObject.getString(SUCCESS);
		} catch (JSONException ex) {

		}
		return success;
	}
	
	public static ArrayList<Content> getContentList(String json) {
		ArrayList<Content> data = new ArrayList<Content>();
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONObject(json).getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject object = jsonArray.getJSONObject(i);
				int id = object.getInt("id");
				int level = object.getInt("lvl");
				String title = object.getString("title");
				String date = transformDate(object.getString("date"));
				String genre = object.getString("genre");
				
				Content content = new Content(id, title, genre, level, date);
				data.add(content);
			}
		} catch (JSONException ex) {

		}
		return data;
	}
	
	private static String transformDate(String date){
		SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM yyyy");
		Date oldDate = null;
		try {
			oldDate = oldDateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String newDate = newDateFormat.format(oldDate);
		return newDate;
	}

	public static String getValueFromJSON(String json, String valueName) {
		String value = null;
		JSONObject jsonObject = null;
		
		try {
			jsonObject = new JSONObject(json).getJSONObject("data");
			value = jsonObject.getString(valueName);
		} catch (JSONException ex) {

		}
		return value;
	}
	

}

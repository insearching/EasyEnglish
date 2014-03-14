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
			jsonArray = new JSONObject(json).getJSONArray(KeyUtils.DATA_KEY);
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject object = jsonArray.getJSONObject(i);
				int id = object.getInt(KeyUtils.ID_KEY);
				String title = object.getString(KeyUtils.TITLE_KEY);
				String genre = object.getString(KeyUtils.GENRE_KEY);
				int type = object.getInt(KeyUtils.TYPE_KEY);
				int level = object.getInt(KeyUtils.LEVEL_KEY);
				String date = transformDate(object.getString(KeyUtils.DATE_KEY));

				Content content = new Content(id, title, genre, type, level,
						date);
				data.add(content);
			}
		} catch (JSONException ex) {

		}
		return data;
	}

	public static Content getContentData(String json) {
		Content content = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY);

			int id = jsonObject.getInt(KeyUtils.ID_KEY);
			String title = jsonObject.getString(KeyUtils.TITLE_KEY);
			int ownerId = jsonObject.getInt(KeyUtils.OWNER_ID_KEY);
			int type = jsonObject.getInt(KeyUtils.TYPE_KEY);
			String genre = jsonObject.getString(KeyUtils.GENRE_KEY);
			String text = jsonObject.getString(KeyUtils.TEXT_KEY);
			int level = jsonObject.getInt(KeyUtils.LEVEL_KEY);
			int pages = jsonObject.getInt(KeyUtils.PAGES_KEY);
			String playerLink = jsonObject.getString(KeyUtils.PLAYER_LINK_KEY);
			String date = transformDate(jsonObject.getString(KeyUtils.DATE_KEY));

			content = new Content(id, title, ownerId, type, genre, text, level,
					pages, playerLink, date);
		} catch (JSONException ex) {

		}
		return content;
	}

	public static int getContentId(String json) {
		int id = 0;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY);
			id = jsonObject.getInt(KeyUtils.ID_KEY);
		} catch (JSONException ex) {

		}
		return id;
	}

	private static String transformDate(String date) {
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

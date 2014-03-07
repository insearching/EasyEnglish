package com.tntu.easyenglish.utils;

import org.json.JSONException;
import org.json.JSONObject;

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

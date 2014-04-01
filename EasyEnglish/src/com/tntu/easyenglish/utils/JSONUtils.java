package com.tntu.easyenglish.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tntu.easyenglish.entity.Content;
import com.tntu.easyenglish.entity.DictionaryWord;
import com.tntu.easyenglish.entity.Translation;
import com.tntu.easyenglish.entity.User;

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
			ex.printStackTrace();
		}
		return success;
	}

	public static String getValue(String json, String key) {
		String value = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
			value = jsonObject.getString(key);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return getNullString(value);
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
			ex.printStackTrace();
		}
		return data;
	}

	public static Content getContentData(String json) {
		Content content = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY);

			int id = jsonObject.getInt(KeyUtils.ID_KEY);
			String title = getNullString(jsonObject
					.getString(KeyUtils.TITLE_KEY));
			int ownerId = jsonObject.getInt(KeyUtils.OWNER_ID_KEY);
			int type = jsonObject.getInt(KeyUtils.TYPE_KEY);
			String genre = getNullString(jsonObject
					.getString(KeyUtils.GENRE_KEY));
			String text = getNullString(jsonObject.getString(KeyUtils.TEXT_KEY));
			int level = jsonObject.getInt(KeyUtils.LEVEL_KEY);
			int pages = jsonObject.getInt(KeyUtils.PAGES_KEY);
			String playerLink = getNullString(jsonObject
					.getString(KeyUtils.PLAYER_LINK_KEY));
			String dateObject = getNullString(jsonObject
					.getString(KeyUtils.DATE_KEY));
			String date = null;
			if (dateObject != null)
				date = transformDate(dateObject);

			content = new Content(id, title, ownerId, type, genre, text, level,
					pages, playerLink, date);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return content;
	}

	private static String getNullString(String s) {
		if (s.equals("null"))
			return null;
		else
			return s;
	}

	public static int getContentId(String json) {
		int id = 0;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY);
			id = jsonObject.getInt(KeyUtils.ID_KEY);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public static User getUserData(String json) {
		JSONObject jsonObject = null;
		User user = null;
		try {
			jsonObject = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY);
			String login = jsonObject.getString(KeyUtils.LOGIN_KEY);
			String email = jsonObject.getString(KeyUtils.EMAIL_KEY);
			String avatar = jsonObject.getString(KeyUtils.AVATAR_KEY);
			String regDate = jsonObject.getString(KeyUtils.REG_DATE);

			user = new User(login, email, avatar, regDate);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return user;
	}

	public String registerNewUser(String json) {
		String apiKey = null;
		try {
			JSONObject jsonObject = new JSONObject(json)
					.getJSONObject(KeyUtils.DATA_KEY);
			apiKey = jsonObject.getString(KeyUtils.API_KEY);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return apiKey;
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

	public static String getValueFromData(String json, String valueName) {
		String value = null;
		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(json).getJSONObject("data");
			value = jsonObject.getString(valueName);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return value;
	}

	/*
	 * !!!!Change translation key!!!!!
	 */
	public static ArrayList<Translation> getTranslation(String json) {
		JSONArray jsonArray = null;
		ArrayList<Translation> translations = new ArrayList<Translation>();
		try {
			jsonArray = new JSONObject(json).getJSONObject(KeyUtils.DATA_KEY)
					.getJSONArray("transltations");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				String text = jsonObject.getString(KeyUtils.TEXT_KEY);
				String imageUrl = jsonObject.getString(KeyUtils.IMAGE_KEY);

				Translation tr = new Translation(text, imageUrl);
				translations.add(tr);
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return translations;
	}

	public static ArrayList<DictionaryWord> getUserDictionary(String json) {
		JSONArray jsonArray = null;
		ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
		try {
			jsonArray = new JSONObject(json).getJSONArray(KeyUtils.DATA_KEY);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				int dictionaryId = jsonObject.getInt(KeyUtils.DICT_ID_KEY);
				int wordId = jsonObject.getInt(KeyUtils.WORD_ID_KEY);
				String word = jsonObject.getString(KeyUtils.WORD_KEY);
				String[] translations = getArray(jsonObject, KeyUtils.TRANSLATION_KEY);
				String[] contexts = getArray(jsonObject, KeyUtils.CONTEXT_KEY);
				String[] images = getArray(jsonObject, KeyUtils.IMAGE_KEY);
				String sound = jsonObject.getString(KeyUtils.SOUND_KEY);
				String date = transformDate(jsonObject.getString(KeyUtils.DATE_KEY));
				
				DictionaryWord tr = new DictionaryWord(dictionaryId, wordId,
						word, translations, contexts, images, sound, date);
				words.add(tr);
			}
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return words;
	}

	private static String[] getArray(JSONObject jsonObj, String key) {
		String[] arr = null;
		try {
			JSONArray jsonArr = jsonObj.getJSONArray(key);
			arr = new String[jsonArr.length()];
			for (int i = 0; i < jsonArr.length(); i++)
				arr[i] = jsonArr.getString(i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arr;
	}
	
	public static void addWordToDictionary(){
		
	}
}

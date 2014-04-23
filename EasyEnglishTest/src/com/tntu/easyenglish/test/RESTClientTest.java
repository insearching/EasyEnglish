package com.tntu.easyenglish.test;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.RESTClient;

public class RESTClientTest extends TestCase {

	private static final String SUCCESS = "success";
	public static final String SUCCESS_TRUE = "true";
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testSuccesStatus() {
		RESTClient client = new RESTClient(
				"http://easy-english.yzi.me/api/getUserDetails?api_key=rakivatake");
		
		String result = client.doInBg();
		assertNotNull(result);
		try {
			JSONObject success = new JSONObject(result).getJSONObject(SUCCESS);
			assertNotNull(success);
			assertEquals(SUCCESS_TRUE, success);
		} catch (JSONException e) {

		}

		assertNotNull(result);
	}

	public void testDictionaryFields() {
		RESTClient client = new RESTClient(
				"http://easy-english.yzi.me/api/getUserDictionary?api_key=rakivatake");
		String result = client.doInBg();
		assertNotNull(result);
		try {
			JSONArray dataArray = new JSONObject(result)
					.getJSONArray(KeyUtils.DATA_KEY);

			JSONObject jsonObject = dataArray.getJSONObject(0);
			int dictionaryId = jsonObject.getInt(KeyUtils.DICT_ID_KEY);
			int wordId = jsonObject.getInt(KeyUtils.WORD_ID_KEY);
			String word = jsonObject.getString(KeyUtils.WORD_KEY);
			JSONArray translations = jsonObject
					.getJSONArray(KeyUtils.TRANSLATIONS_KEY);
			JSONArray contexts = jsonObject.getJSONArray(KeyUtils.CONTEXTS_KEY);
			JSONArray images = jsonObject.getJSONArray(KeyUtils.IMAGES_KEY);
			String sound = jsonObject.getString(KeyUtils.SOUND_KEY);
			String date = jsonObject.getString(KeyUtils.DATE_KEY);

			assertNotNull(dataArray);
			assertNotNull(dictionaryId);
			assertNotNull(wordId);
			assertNotNull(word);
			assertNotNull(translations);
			assertNotNull(contexts);
			assertNotNull(images);
			assertNotNull(sound);
			assertNotNull(date);
		} catch (JSONException e) {

		}
	}
	
	public void testUserFields() {
		RESTClient client = new RESTClient(
				"http://easy-english.yzi.me/api/getUserDetails?api_key=rakivatake");
		String result = client.doInBg();
		assertNotNull(result);
		try {
			JSONObject jsonObject = new JSONObject(result).getJSONObject(KeyUtils.DATA_KEY);
			String login = jsonObject.getString(KeyUtils.LOGIN_KEY);
			String email = jsonObject.getString(KeyUtils.EMAIL_KEY);
			String avatar = jsonObject.getString(KeyUtils.AVATAR_KEY);
			String regDate = jsonObject.getString(KeyUtils.REG_DATE);

			assertNotNull(jsonObject);
			assertNotNull(login);
			assertNotNull(email);
			assertNotNull(avatar);
			assertNotNull(regDate);
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}
}

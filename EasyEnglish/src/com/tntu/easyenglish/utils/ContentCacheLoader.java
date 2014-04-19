package com.tntu.easyenglish.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class ContentCacheLoader {

	private Context context;

//	private String filePath = null;

	public ContentCacheLoader(Context context) {
		this.context = context;
	}

	public void writeToFile(String filename, String data) {
		try {
//			File file = createFile(filename);
			FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
			stream.write(data.getBytes());
			stream.close();
		} catch (IOException e) {
			Log.e("Write Exception", "File write failed: " + e.toString());
		}
	}

	public String readFromFile(String fileName) {
		String info = "";

		try {
			InputStream inputStream = context.openFileInput(fileName);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				info = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("cache loader", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("cache loader", "Can not read file: " + e.toString());
		}

		return info;
	}

	public void deleteFile(String fileName) {
		context.deleteFile(fileName);
	}
	
	public boolean isFileExists(String fileName){
		File f = new File(fileName);
		return f.exists();
	}
}

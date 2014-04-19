package com.tntu.easyenglish.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.tntu.easyenglish.R;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ContentCacheLoader {

	private Context context;

//	private String filePath = null;

	public ContentCacheLoader(Context context) {
		this.context = context;
//		filePath = Environment.getExternalStorageDirectory().getPath() + "/"
//				+ context.getString(R.string.app_name) + "/cache";
	}

	public void writeToFile(String filename, String data) {
		try {
//			File file = createFile(filename);
			FileOutputStream stream = new FileOutputStream(filename);
			stream.write(data.getBytes());
			stream.close();
		} catch (IOException e) {
			Log.e("Write Exception", "File write failed: " + e.toString());
		}
	}

//	private File createFile(String fileName) {
//		String sFolder = filePath + "/";
//		File file = new File(sFolder);
//		if (!file.exists())
//			file.mkdirs();
//
//		file = null;
//		try {
//			// Create file or re-download if needest
//			file = new File(sFolder + fileName);
//
//			if (!file.createNewFile()) {
//				file.delete();
//				if (!file.createNewFile()) {
//					return null;
//				}
//			}
//		} catch (Exception e) {
//			return null;
//		}
//		return file;
//	}

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
}

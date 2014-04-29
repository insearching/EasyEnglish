package com.tntu.easyenglish.entity;

import java.io.Serializable;

public class SoundToWord extends Exercise implements Serializable{

	private static final long serialVersionUID = 1L;

	public SoundToWord(int id, String phrase, String pictureLink,
			String voiceLink) {
		super(id, phrase, voiceLink, pictureLink);
	}
}

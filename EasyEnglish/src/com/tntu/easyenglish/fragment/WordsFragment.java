package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.EntireWordsAdapter;
import com.tntu.easyenglish.utils.Word;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WordsFragment extends Fragment {
	private View convertView;
	private ListView wordsLv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.words_fragment, null);
		
		ArrayList<Word> dictionary = new ArrayList<Word>();
		dictionary.add(new Word(0, "accusative", "���������", Word.Status.UNKNOWN));
		dictionary.add(new Word(1, "active", "��������", Word.Status.UNKNOWN));
		dictionary.add(new Word(2, "adjective", "����������", Word.Status.UNKNOWN));
		dictionary.add(new Word(3, "adverb", "���������", Word.Status.UNKNOWN));
		dictionary.add(new Word(4, "alphabet", "������, ������", Word.Status.UNKNOWN));
		dictionary.add(new Word(5, "animate", "�����", Word.Status.UNKNOWN));
		dictionary.add(new Word(6, "aspect", "���", Word.Status.UNKNOWN));
		dictionary.add(new Word(7, "cardinal", "��������", Word.Status.UNKNOWN));
		
		wordsLv = (ListView) convertView.findViewById(R.id.wordsLv);
		wordsLv.setAdapter(new EntireWordsAdapter(getActivity(), dictionary));
		return convertView;
	}
}

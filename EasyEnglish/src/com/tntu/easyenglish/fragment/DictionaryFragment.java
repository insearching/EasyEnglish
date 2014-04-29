package com.tntu.easyenglish.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.tntu.easyenglish.R;
import com.tntu.easyenglish.adapter.DictionaryAdapter;
import com.tntu.easyenglish.entity.DictionaryWord;
import com.tntu.easyenglish.utils.ContentCacheLoader;
import com.tntu.easyenglish.utils.JSONUtils;
import com.tntu.easyenglish.utils.KeyUtils;
import com.tntu.easyenglish.utils.GETClient;
import com.tntu.easyenglish.utils.GETClient.GETListener;
import com.tntu.easyenglish.utils.SoundCacher;

public class DictionaryFragment extends Fragment implements
		GETListener, OnItemClickListener, OnScrollListener {
	private View convertView;
	private PullAndLoadListView contentLv;
	private DictionaryAdapter mAdapter;
	private ContentCacheLoader loader;
	private static final int COUNT = 10;
	private static int offset = 0;
	private String bufferFileName = "dictionary.txt";

	public static DictionaryFragment newInstance(String apiKey) {
		DictionaryFragment fragment = new DictionaryFragment();
		Bundle args = new Bundle();
		args.putString(KeyUtils.API_KEY, apiKey);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.dictionary_fragment, null);
		setHasOptionsMenu(true);

		contentLv = (PullAndLoadListView) convertView
				.findViewById(R.id.contentLv);
		contentLv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshContentList();
			}
		});

		contentLv.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				loadMoreContent();
			}
		});

		contentLv.setOnItemClickListener(this);
		contentLv.setOnScrollListener(this);

		loader = new ContentCacheLoader(getActivity());
		String json = loader.readFromFile(bufferFileName);
		if (json != null && !json.equals("")) {
			setData(json);
		} else {
			refreshContentList();
		}

		return convertView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public void refreshContentList() {
		if (loader == null)
			return;
		loader.deleteFile(bufferFileName);

		GETClient client = new GETClient(this);
		client.execute(getQuery(getArguments().getString(KeyUtils.API_KEY), 0));

		mAdapter = null;
		offset = COUNT;
	}

	public void loadMoreContent() {
		GETClient client = new GETClient(this);
		client.execute(getQuery(getArguments().getString(KeyUtils.API_KEY),
				offset));
		offset += COUNT;
	}

	private void setData(String json) {
		String status = JSONUtils.getResponseStatus(json);
		if (status.equals(JSONUtils.SUCCESS_TRUE)) {
			ArrayList<DictionaryWord> dictionary = JSONUtils
					.getUserDictionary(json);

			int savedPosition = contentLv.getFirstVisiblePosition();
			View firstVisibleView = contentLv.getChildAt(0);
			int savedListTop = (firstVisibleView == null) ? 0
					: firstVisibleView.getTop();
			if (mAdapter == null) {
				mAdapter = new DictionaryAdapter(getActivity(), dictionary);
				contentLv.setAdapter(mAdapter);
				loader.writeToFile(bufferFileName, json);
			} else if (mAdapter != null && dictionary.size() > 0) {
				for (DictionaryWord word : dictionary) {
					mAdapter.addItem(word);
				}
				mAdapter.notifyDataSetChanged();
				loader.writeToFile(bufferFileName, json);
			}
			if (dictionary.size() == 0) {
				contentLv.setSelectionFromTop(savedPosition - 1, savedListTop);
			}
		}
	}

	private boolean mBusy;

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			mBusy = false;
//			if (mAdapter != null)
//			mAdapter.notifyDataSetChanged();
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			mBusy = true;
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			mBusy = true;
			break;

		default:
			mBusy = false;
		}
		if (mAdapter != null)
			mAdapter.setBusy(mBusy);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	@Override
	public void onRemoteCallComplete(String json) {
		contentLv.onLoadMoreComplete();
		contentLv.onRefreshComplete();
		setData(json);
	}

	private String getQuery(String apiKey, int offset) {
		return "http://easy-english.yzi.me/api/getUserDictionary?api_key="
				+ apiKey + "&count=" + COUNT + "&offset=" + offset;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		position -= 1;
		SoundCacher cacher = new SoundCacher(getActivity(), mAdapter.getItem(
				position).getSound());
		cacher.play();
	}


}

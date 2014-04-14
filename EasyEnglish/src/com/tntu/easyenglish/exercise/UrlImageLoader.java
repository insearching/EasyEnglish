package com.tntu.easyenglish.exercise;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;

public class UrlImageLoader implements UrlImageViewCallback {

	Animation anim;
	public UrlImageLoader(Animation anim){
		this.anim = anim;
	}
	@Override
	public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
			boolean loadedFromCache) {

		if (!loadedFromCache) {
			imageView.setVisibility(View.VISIBLE);
			imageView.startAnimation(anim);
		}
	}
}

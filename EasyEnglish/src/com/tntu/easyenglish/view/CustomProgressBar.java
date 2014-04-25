package com.tntu.easyenglish.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.tntu.easyenglish.R;

public class CustomProgressBar extends ImageView {

	Animation anim;
	public CustomProgressBar(Context context) {
		super(context);
		initView(context);
	}

	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	private void initData(Context context){
		Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(context, android.R.anim.accelerate_decelerate_interpolator);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
	}
	
	private void initView(Context context){
		initData(context);
		startAnimation(anim);
		setImageResource(R.drawable.ic_spinner);
	}
}

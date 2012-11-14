package com.example.scrollimageview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;

public class TagChildView extends Button{
	
	public float mLastMotionX;
	public float mLastMotionY;

	public TagChildView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			TagScrollView tsv = (TagScrollView)getParent();
			if(tsv.getChoosedView() == null){
				tsv.setChoosedView(this);
			}
		}
		return false;
	}

}

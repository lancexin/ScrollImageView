package com.example.scrollimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Scroller;

public class TagScrollView extends AbsoluteLayout{
	
	private TagChildView choosedView;
	
	private float mLastMotionX;
	private float mLastMotionY;
	
	private VelocityTracker mVelocityTracker;
	
	FlingRunnable mFlingRunnable;
	
    public static final int TOUCH_MODE_REST = -1;

    public static final int TOUCH_MODE_DOWN = 0;

    public static final int TOUCH_MODE_SCROLL = 1;

    public static final int TOUCH_MODE_FLING = 2;
    
    public int mTouchMode = TOUCH_MODE_REST;
    
    private int marginBetween = 20;

	public TagScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);		
	}
	
	protected void setChoosedView(TagChildView v){
		this.choosedView = v;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			return false;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		final TagChildView mChoosedView = this.choosedView;
		if(mChoosedView == null){
			return super.onTouchEvent(ev);
		}
		
		final int action = ev.getAction();
	    final float x = ev.getX();
	    final float y = ev.getY();
	    
	    if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
	    mVelocityTracker.addMovement(ev);
	    AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) mChoosedView.getLayoutParams();
	    switch (action) {
	    	case MotionEvent.ACTION_DOWN:
	    		mTouchMode = TOUCH_MODE_DOWN;
	    		mLastMotionX = x;
	            mLastMotionY = y;
	            
	            break;
	    	case MotionEvent.ACTION_MOVE:
	    		mTouchMode = TOUCH_MODE_SCROLL;
	    		
	    		final int deltaX = (int) (mLastMotionX - x);
                final int deltaY = (int) (mLastMotionY - y);                
                mLastMotionX = x;
	            mLastMotionY = y;
	            moveByScroll(-deltaY);
                
                break;
	    	case MotionEvent.ACTION_UP:
	    		final VelocityTracker velocityTracker = mVelocityTracker;
	            velocityTracker.computeCurrentVelocity(1000);
	            int velocityY = (int) velocityTracker.getYVelocity();
	            if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
	            if (mFlingRunnable == null) {
                    mFlingRunnable = new FlingRunnable();
                }
                mFlingRunnable.start(-velocityY);
	            break;
	    }
		return super.onTouchEvent(ev);
	}
	
	private void endFling() {
    	if(mTouchMode == TOUCH_MODE_FLING){
    		choosedView = null;
    		mTouchMode = TOUCH_MODE_REST;
    	}
    }
	
	private class FlingRunnable implements Runnable {
        private Scroller mScroller;

        private int mLastFlingY;

        public FlingRunnable() {
            mScroller = new Scroller(getContext());
        }

        public void start(int initialVelocity) {
            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
  
            mScroller.fling(0, initialY, 0, initialVelocity,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            mTouchMode = TOUCH_MODE_FLING;
            post(this);
        }

        

        public void run() {

            if (getChildCount() == 0) {
                endFling();
                return;
            }

            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int y = scroller.getCurrY();
            
            int delta = mLastFlingY - y;
            final TagChildView mChoosedView = getChoosedView();
            if (more && mTouchMode == TOUCH_MODE_FLING && mChoosedView != null) {
            	moveByFling(delta);
                mLastFlingY = y;
                post(this);
            } else {
                endFling();
            }
        }
    }
	
	private void moveByFling(int moveY){
		final TagChildView mChoosedView = getChoosedView();
		int index = indexOfChild(mChoosedView);
		int count = getChildCount();
		
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) mChoosedView.getLayoutParams();
		lp.y = lp.y+moveY;
		if(moveY > 0){
			if(lp.y > marginBetween*index+mChoosedView.getMeasuredHeight()/3*index){
				lp.y = marginBetween*index+mChoosedView.getMeasuredHeight()/3*index;
			}
		}else{
			if(lp.y < marginBetween*(index+1) ){
				lp.y = marginBetween*(index+1);
			}
		}
		if(index != getChildCount()-1){
			View nextChild = getChildAt(index+1);
			scrollNextView(moveY,lp,nextChild);
		}
		if(index != 0){
			View privChild = getChildAt(index-1);
			scrollPrivView(moveY,lp,privChild);
		}
        requestLayout();
        
	}
	
	private void moveByScroll(int moveY){
		final TagChildView mChoosedView = getChoosedView();
		int index = indexOfChild(mChoosedView);
		int count = getChildCount();
		
		AbsoluteLayout.LayoutParams lp = (android.widget.AbsoluteLayout.LayoutParams) mChoosedView.getLayoutParams();
		lp.y = lp.y+moveY;
		if(moveY > 0){
			if(lp.y > marginBetween*index+mChoosedView.getMeasuredHeight()/3*index){
				lp.y = marginBetween*index+mChoosedView.getMeasuredHeight()/3*index;
			}
		}else{
			if(lp.y < marginBetween*(index+1) ){
				lp.y = marginBetween*(index+1);
			}
		}

		if(index != getChildCount()-1){
			View nextChild = getChildAt(index+1);
			scrollNextView(moveY,lp,nextChild);
		}
		if(index != 0){
			View privChild = getChildAt(index-1);
			scrollPrivView(moveY,lp,privChild);
		}
		requestLayout();
		
	}
	
	private void scrollNextView(int moveY,AbsoluteLayout.LayoutParams lp,View nextChild){
		int index = indexOfChild(nextChild);
		AbsoluteLayout.LayoutParams nlp = (android.widget.AbsoluteLayout.LayoutParams) nextChild.getLayoutParams();
		nlp.y = nlp.y+moveY;
		if(moveY > 0){
			if(nlp.y > marginBetween+nextChild.getMeasuredHeight()/3*index){
				nlp.y = marginBetween+nextChild.getMeasuredHeight()/3*index;
			}
		}else{
			if(nlp.y < marginBetween*(index+1)){
				nlp.y = marginBetween*(index+1);
			}
		}
		
		if(index != getChildCount()-1){
			nextChild = getChildAt(index+1);
			scrollNextView(moveY,nlp,nextChild);
		}
		
	}
	
	private void scrollPrivView(int moveY,AbsoluteLayout.LayoutParams lp,View privChild){
		int index = indexOfChild(privChild);
		AbsoluteLayout.LayoutParams nlp = (android.widget.AbsoluteLayout.LayoutParams) privChild.getLayoutParams();
		
		
		if(moveY > 0){
			if(nlp.y < lp.y-privChild.getMeasuredHeight()/3 ){ 
				nlp.y = nlp.y+moveY;
			}
			
			if(nlp.y > marginBetween+privChild.getMeasuredHeight()/3*index){
				nlp.y = marginBetween+privChild.getMeasuredHeight()/3*index;
			}
		}else{
			if(nlp.y < lp.y- marginBetween){ 
				nlp.y = nlp.y+moveY;
			}
			
			if(nlp.y < marginBetween*(index+1)){
				nlp.y = marginBetween*(index+1);
			}
			
		}
		
		if(index != 0){
			privChild = getChildAt(index-1);
			scrollPrivView(moveY,nlp,privChild);
		}
	}
	
	
	public TagChildView getChoosedView() {
		return choosedView;
	}
}

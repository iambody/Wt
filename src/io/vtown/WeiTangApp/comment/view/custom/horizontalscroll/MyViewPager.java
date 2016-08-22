package io.vtown.WeiTangApp.comment.view.custom.horizontalscroll;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可以禁止滑动的ViewPager
 * 
 */
public class MyViewPager extends ViewPager {

	private boolean mSwiped = true; // 是否可滑动

	public MyViewPager(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setSwiped(boolean swiped) {
		mSwiped = swiped;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mSwiped) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if ((mSwiped)) {
			return super.onTouchEvent(event);
		} else {
			return true;
		}
	}
}

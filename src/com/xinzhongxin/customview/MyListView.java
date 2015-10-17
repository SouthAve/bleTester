package com.xinzhongxin.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class MyListView extends ListView {
	private Context mContext;
	private static final int mMaxOverDistance = 30;
	private int mMaxYOverscrollDistance;

	public MyListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		initView();
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

	private void initView() {
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		float density = metrics.density;
		mMaxYOverscrollDistance = (int) (density * mMaxOverDistance);

	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// TODO Auto-generated method stub
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX,
				mMaxYOverscrollDistance, isTouchEvent);
	}

}

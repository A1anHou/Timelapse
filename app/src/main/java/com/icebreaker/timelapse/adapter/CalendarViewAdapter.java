package com.icebreaker.timelapse.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class CalendarViewAdapter<view extends View> extends PagerAdapter{
	private view[] views;
	
	public CalendarViewAdapter(view[] views) {
		// TODO Auto-generated constructor stub
		super();
		this.views = views;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == (View)arg1;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		if (container.getChildCount() == views.length) {
			container.removeView(views[position % views.length]);
		}
		container.addView(views[position % views.length], 0);
		return views[position % views.length];
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View)container);
	}
	
	public view[] getAllView() {
		return views;
	}
}

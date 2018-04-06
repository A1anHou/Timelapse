package com.icebreaker.timelapse.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.icebreaker.timelapse.adapter.CalendarViewAdapter;
import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.DateUtil;


public class MyCalendar implements CalendarView.OnCellClickListener{
	
	private	ViewPager viewPager;
	private Context context;
	private CalendarViewAdapter<CalendarView> adapter;
	private CalendarView[] showViews;
	private int currentIndex = 498;
	private SildeDirection direction = SildeDirection.NO_SILDE;
	private CustomDate customDate,date;
	private OnDateBack dateBack;
	private int flag;
	
	public interface OnDateBack{
		void getDate(CustomDate date, boolean flag);
		void getChangDate(CustomDate date);
	}
	
	public MyCalendar(ViewPager viewPager,Context context,OnDateBack dateBack,CustomDate nowDate,CustomDate customDate,int flag){
		this.viewPager = viewPager;
		this.context = context;
		this.dateBack = dateBack;
		this.customDate = customDate;
		this.date = nowDate;
		this.flag = flag;
	}
	
	/**
	 * ����viewpagerҪ��ʾ��view
	 */
	public void setViews() {
		int y = DateUtil.getYear();
		int m = DateUtil.getMonth();
		int d = DateUtil.getCurrentMonthDay();
		if (date.getYear()!=0) {
			y = date.year;
			m = date.month;
			d = date.day;
		}
		CalendarView[] views = new CalendarView[3];
		for (int i = 0; i < views.length; i++) {
			views[i] = new CalendarView(context,date,this);
			if (i == 0) {
				views[i].setData(y,m,d,customDate,flag);
			}
		}
		setViewPager(views);
	}
	
	private void setViewPager(CalendarView[] views) {
		adapter = new CalendarViewAdapter<CalendarView>(views);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(498);
		viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			measureDirection(position);
			updateCalendarView(position);
		}}
	
	enum SildeDirection {
		RIGHT, LEFT, NO_SILDE;
	}
	
	/**
	 * ���㻬������
	 * @param arg0
	 */
	private void measureDirection(int arg0) {

		if (arg0 > currentIndex) {
			direction = SildeDirection.RIGHT;

		} else if (arg0 < currentIndex) {
			direction = SildeDirection.LEFT;
		}
		currentIndex = arg0;
	}
	
	/**
	 * ���ݻ������򣬸���������ͼ
	 * @param arg0
	 */
	private void updateCalendarView(int arg0) {
		showViews = adapter.getAllView();
		if (direction == SildeDirection.RIGHT) {
			showViews[arg0 % showViews.length].rightSlide(customDate,flag);
		} else if (direction == SildeDirection.LEFT) {
			showViews[arg0 % showViews.length].leftSlide(customDate,flag);
		}
		direction = SildeDirection.NO_SILDE;
	}
	
	//�ӿڻص�����
	@Override
	public void clickDate(CustomDate date,boolean flag) {
		// TODO Auto-generated method stub
		dateBack.getDate(date,flag);
	}
	//�ӿڻص�����
	@Override
	public void changeDate(CustomDate date) {
		// TODO Auto-generated method stub
		dateBack.getChangDate(date);
	}
}

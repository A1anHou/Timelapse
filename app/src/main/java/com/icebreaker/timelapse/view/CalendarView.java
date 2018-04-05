package com.icebreaker.timelapse.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.DateUtil;


public class CalendarView extends View{
	private Paint textPaint,circlePaint;
	private int touchSlop;
	private static final int TOTAL_COL = 7;	//总列数
	private static final int TOTAL_ROW = 6;	//总行数
	private int viewWidth,viewHeight,cellSpace,cellSpace_w,cellSpace_h;
	private Row rows[] = new Row[TOTAL_ROW];
	private Cell clickCell;
	private CustomDate date;
	private OnCellClickListener calListener;
	private Context context;

	public interface OnCellClickListener {
		void clickDate(CustomDate date,boolean flag);//回调点击的日期

		void changeDate(CustomDate date);//回调滑动viewPager改变的日期
	}

	public CalendarView(Context context,CustomDate date,OnCellClickListener calListener) {
		super(context);
		// TODO Auto-generated constructor stub
		this.date = date;
		this.calListener = calListener;
		this.context = context;
		init(context);
	}

	public CalendarView(Context context,AttributeSet arr) {
		super(context,arr);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CalendarView(Context context,AttributeSet arr,int defStyle) {
		super(context,arr,defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		for (int i = 0; i < TOTAL_ROW; i++) {
			if (rows[i] != null)
				rows[i].drawCells(canvas);
		}
	}

	private void init(Context context) {
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.parseColor("#F24949"));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	int myear = 0;
	int mMonth = 0;
	int mDay = 0;
	/**
	 * 设置日期
	 * @param myear
	 * @param mMonth
	 * @param mDay
	 * @param mCustomDate
	 * @param i
	 */
	public void setData(int myear,int mMonth,int mDay,CustomDate mCustomDate,int i ){
		this.myear = myear;
		this.mMonth = mMonth;
		this.mDay = mDay;
		if (myear!=0) {
			date.setYear(myear);
			date.setMonth(mMonth);
			date.setDay(mDay);
		}
		fillMonthDate(mCustomDate,i);
		calListener.changeDate(date);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		viewWidth = w;
		viewHeight = h;
		cellSpace_w = viewWidth/TOTAL_COL;
		cellSpace_h = viewHeight/TOTAL_ROW;
		cellSpace = Math.max(viewHeight/TOTAL_ROW, viewWidth/TOTAL_COL);
		textPaint.setTextSize(cellSpace/3);
	}

	enum State {
		CURRENT_MONTH, PRE_MONTH, NEXT_MONTH, TODAY, CLICK_DAY,T;
	}

	// 组
	class Row {
		public int j;

		Row(int j) {
			this.j = j;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		public void drawCells(Canvas canvas) {
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null)
					cells[i].drawSelf(canvas);
			}

		}
	}

	// 单元格
	class Cell {
		public CustomDate date;
		public State state;
		public int i;
		public int j;

		public Cell(CustomDate date, State state, int i, int j) {
			super();
			this.date = date;
			this.state = state;
			this.i = i;
			this.j = j;
		}


		// 绘制一个单元格 如果颜色需要自定义可以修改
		public void drawSelf(Canvas canvas) {
			switch (state) {
				case CURRENT_MONTH:
					textPaint.setColor(Color.parseColor("#80000000"));
					break;
				case NEXT_MONTH:
					textPaint.setColor(Color.parseColor("#20000000"));
					break;
				case PRE_MONTH:
					textPaint.setColor(Color.parseColor("#40000000"));
					break;
				case TODAY:
					textPaint.setColor(Color.parseColor("#80000000"));
					break;
				case CLICK_DAY:
					textPaint.setColor(Color.parseColor("#fffffe"));
					canvas.drawCircle((float) (cellSpace_w * (i + 0.5)),
							(float) ((j + 0.4) * cellSpace_h), cellSpace_h * 2/ 5,
							circlePaint);
					break;
				default:
					break;
			}
			// 绘制文字
			String content = date.day+"";
			canvas.drawText(content,
					(float) ((i+0.5) * cellSpace_w - textPaint.measureText(content)/2),
					(float) ((j + 0.7) * cellSpace_h - textPaint.measureText(
							content, 0, 1) / 2), textPaint);
		}
	}

	private float X;
	private float Y;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				X = event.getX();
				Y = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float disX = event.getX() - X;
				float disY = event.getY() - Y;
				//判断操作是否为点击，如果是点击，计算出点击的单元格
				if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
					int col = (int) (X/cellSpace_w);
					int row = (int) (Y/cellSpace_h);
					measureClickCell(col, row);
				}
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * 取出单元格数据，并且判断数据是否有效
	 * @param col
	 * @param row
	 */
	private void measureClickCell(int col, int row) {
		// TODO Auto-generated method stub
		if (col >= TOTAL_COL || row >= TOTAL_ROW)
			return;
		CustomDate date = rows[row].cells[col].date;
		System.out.println(rows[row].cells[col].state);
		if (rows[row].cells[col].state == State.NEXT_MONTH) {
			Toast.makeText(context, "选择的日期不可用，请重新选择", Toast.LENGTH_SHORT).show();
			return;
		}
		if (clickCell!= null) {
			rows[clickCell.j].cells[clickCell.i] = clickCell;
		}
		if (rows[row] != null) {
			clickCell = new Cell(rows[row].cells[col].date,
					rows[row].cells[col].state, rows[row].cells[col].i,
					rows[row].cells[col].j);
			rows[row].cells[col].state = State.CLICK_DAY;
			date.week = col;
			calListener.clickDate(date,true);
			invalidate();
		}
	}

	/**
	 * 初始化日历
	 * @param mCustomDate
	 * @param flag
	 */
	private void fillMonthDate(CustomDate mCustomDate,int flag) {
		int lastMonthDays = DateUtil.getMonthDays(date.year, date.month - 1); //上个月的总天数
		int currentMonthDays = DateUtil.getMonthDays(date.year, date.month); //这个月的总天数
		int firstDayWeek = DateUtil.getWeekDayFromDate(date.year, date.month); //这个月第一天周几
		//因为两个日历会互相影响，所以绘制其中一个日历时，需要知道另外一个日历选择的日期
		//下面一系列判断条件，判断日历状态
		boolean isCurrentMonth = false;
		boolean isPreMonth = false;
		boolean isNextMonth = false;
		boolean isDateMonth = false;
		boolean isDatePreMonth = false;
		if (DateUtil.isDateMonth(date, mCustomDate)) {
			isDateMonth = true;
		}
		if (DateUtil.isDatePreMonth(date, mCustomDate)) {
			isDatePreMonth = true;
		}
		if (DateUtil.isCurrentMonth(date)) {
			isCurrentMonth = true;
		}
		if(DateUtil.isPreMonth(date)){
			isPreMonth = true;
		}
		if (DateUtil.isNextMonth(date)) {
			isNextMonth = true;
		}
		int day = 0;
			/*
			 * 分三块儿画日历，上月填充的日期，当前月，下个月填充到当前月的天数
			 */
		for (int j = 0; j < TOTAL_ROW; j++) {
			rows[j] = new Row(j);
			//绘制该月的日期
			for (int i = 0; i < TOTAL_COL; i++) {
				int postion = i + j * TOTAL_COL;
				if (postion >= firstDayWeek
						&& postion < firstDayWeek + currentMonthDays) {
					day++;
					//标识当前记录日期
					if (getType(day)){
						CustomDate cdate = CustomDate.modifiDayForObject(
								date, day);
						clickCell = new Cell(cdate, State.TODAY, i, j);
						cdate.week = i;
						calListener.clickDate(cdate,false);
						rows[j].cells[i] = new Cell(cdate, State.CLICK_DAY, i, j);
						continue;
					}
						/*下面的逻辑时间久了可能人鬼不分
						 * flag为0表示开始时间，1表示结束时间
						 * State为NEXT_MONTH,表示日期不可用，无法点击
						 * 绘制出来的日期字体颜色较浅
						 */
					//如果是另外一个日历选择日期前面的月份
					if (isDatePreMonth) {
						if (flag == 0) {
							rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
									State.CURRENT_MONTH, i, j);
						}else if (flag == 1) {
							rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
									State.NEXT_MONTH, i, j);
						}
						//如果是另外一个日历选择日期的当月
					}else if(isDateMonth){
						if (day > mCustomDate.day) {
							//开始时间不能在结束时间之后
							if (flag == 0) {
								rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
										State.NEXT_MONTH, i, j);
							}else if (flag == 1 && isCurrentMonth) {
								//当前时间之后的时间，不可用
								if (day > DateUtil.getCurrentMonthDay()) {
									rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
											State.NEXT_MONTH, i, j);
								}else {
									rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
											State.CURRENT_MONTH, i, j);
								}
							}else {
								rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
										State.CURRENT_MONTH, i, j);
							}

						}else{
							if (flag == 0) {
								rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
										State.CURRENT_MONTH, i, j);
							}else if (flag == 1) {
								//结束日期不能在开始日期之前
								rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
										State.NEXT_MONTH, i, j);
							}

						}
						//如果是另外一个日历选择日期之后的月份
					}else{
						if (flag == 0) {
							rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
									State.NEXT_MONTH, i, j);
						}else if(flag == 1 && !isNextMonth){
							//结束日期在当前日之前，都应该为可点击状态
							if (isCurrentMonth) {
								if (day > DateUtil.getCurrentMonthDay()) {
									rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
											State.NEXT_MONTH, i, j);
								}else {
									rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
											State.CURRENT_MONTH, i, j);
								}
							}else {
								rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
										State.CURRENT_MONTH, i, j);
							}
						}else {
							rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(date, day),
									State.NEXT_MONTH, i, j);
						}

					}
					//绘制上个月填充过来的日期
				} else if (postion < firstDayWeek) {
					if(isDateMonth || isDatePreMonth){
						if (flag == 0) {
							rows[j].cells[i] = new Cell(new CustomDate(date.year, date.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.PRE_MONTH, i, j);
						}else if (flag == 1) {
							rows[j].cells[i] = new Cell(new CustomDate(date.year, date.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.NEXT_MONTH, i, j);
						}
					}else{
						if (flag == 0) {
							rows[j].cells[i] = new Cell(new CustomDate(date.year, date.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.NEXT_MONTH, i, j);
						}else if (flag == 1 && !isNextMonth) {
							//只要是当前时间之前的月份，结束日期都可以选择
							rows[j].cells[i] = new Cell(new CustomDate(date.year, date.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.PRE_MONTH, i, j);
						}else {
							rows[j].cells[i] = new Cell(new CustomDate(date.year, date.month-1, lastMonthDays - (firstDayWeek- postion - 1)), State.NEXT_MONTH, i, j);
						}
					}
					//绘制下个月填充过来的日期
				} else if (postion >= firstDayWeek + currentMonthDays) {
					if(isDatePreMonth){
						if (flag == 0) {
							rows[j].cells[i] = new Cell((new CustomDate(date.year, date.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.PRE_MONTH, i, j);
						}else if (flag == 1) {
							rows[j].cells[i] = new Cell((new CustomDate(date.year, date.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH, i, j);
						}

					}else{
						if (flag == 0) {
							rows[j].cells[i] = new Cell((new CustomDate(date.year, date.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH, i, j);
						}else if (flag == 1 && !isPreMonth) {
							//在当前月之前的月份，结束日历下个月填充的日期才是有效的
							rows[j].cells[i] = new Cell((new CustomDate(date.year, date.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.NEXT_MONTH, i, j);
						}else {
							rows[j].cells[i] = new Cell((new CustomDate(date.year, date.month+1, postion - firstDayWeek - currentMonthDays + 1)), State.PRE_MONTH, i, j);
						}
					}
				}
			}
		}
	}
	public boolean getType(int day) {
		if (day == date.day && myear == date.year
				&& mMonth == date.month) {
			return true;
		}
		return false;
	}

	//向右滑动Calendar
	public void rightSlide(CustomDate customDate,int flag) {
		if (date.month == 12) {
			date.month = 1;
			date.year += 1;
		} else {
			date.month += 1;
		}
		update(customDate,flag);
	}

	//向左滑动
	public void leftSlide(CustomDate customDate,int flag) {
		if (date.month == 1) {
			date.month = 12;
			date.year -= 1;
		} else {
			date.month -= 1;
		}
		update(customDate,flag);
	}

	//更新日历数据
	public void update(CustomDate customDate,int flag) {
		fillMonthDate(customDate,flag);
		calListener.changeDate(date);
		invalidate();
	}

}

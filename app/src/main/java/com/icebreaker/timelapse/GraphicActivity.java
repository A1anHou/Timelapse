package com.icebreaker.timelapse;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.Util;
import com.icebreaker.timelapse.view.MyCalendar;
import com.icebreaker.timelapse.view.MyCalendar.OnDateBack;
import java.util.ArrayList;
import java.util.Calendar;


public class GraphicActivity extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout mBarView;
    private TextView graphic_date;
    private MyCalendar myCalendar;
    private CustomDate myDate,startDate;
    private PopupWindow popupWindow;
    private ViewPager calendar;
    private PieChart mPieChart;
    private SQLUtils utils;
    private static final  String DB_NAME = "wimt.db3";
    private ArrayList<Adress> mAddress;
    private int curYear,curMonth,curDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);

        Calendar calendar = Calendar.getInstance();
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH)+1;
        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        utils = new SQLUtils(getApplicationContext(),DB_NAME,1);
        mAddress = utils.getOneDayHistory(utils.getReadableDatabase(),curYear,curMonth,curDay);
        initChart(mAddress);
        setViews();
    }
    /**
     * 设置ActionoBar和状态栏
     * author wangbin
     * Created on 2018/4/4 18:59
     */
    private void setViews(){

        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        mBarView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.actionbar_graphic,null);
        ImageView back = (ImageView)mBarView.findViewById(R.id.back_address);
        back.setOnClickListener(this);
         graphic_date = (TextView)mBarView.findViewById(R.id.graphic_date);
        myDate = new CustomDate();
        startDate = new CustomDate(1979,1,1);
        graphic_date.setText(myDate.year+"-"+myDate.month+"-"+myDate.day);
        graphic_date.setOnClickListener(this);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

    }

    /**
     * 初始化饼状图
     * author wangbin
     * Created on 2018/4/5 16:33 
    */
    private void initChart(ArrayList<Adress> addressList){

        //饼状图
        mPieChart = (PieChart) findViewById(R.id.chart);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);

        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文件

        mPieChart.setDrawHoleEnabled(false);
        mPieChart.setHoleColor(Color.BLACK);

        mPieChart.setTransparentCircleColor(Color.BLACK);
        mPieChart.setTransparentCircleAlpha(110);

        mPieChart.setHoleRadius(58f);
        mPieChart.setTransparentCircleRadius(61f);

        mPieChart.setDrawCenterText(true);

        mPieChart.setRotationAngle(0);
        // 触摸旋转
        mPieChart.setRotationEnabled(true);
        mPieChart.setHighlightPerTapEnabled(true);

        //模拟数据
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        if(addressList != null && addressList.size() >0){

        for(Adress address: addressList){
            if(address.getTime()>=600){

            PieEntry entry = new PieEntry(address.getTime(),address.getAdress());
            entries.add(entry);

            }
        }
        }else{
            entries.add(new PieEntry(30,"尚无记录"));
            entries.add(new PieEntry(30,"赶快去记录吧"));
        }
        //设置数据
        setData(entries);

        mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // 输入标签样式
        mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.setEntryLabelTextSize(12f);
    }

    /**
     * 设置饼图的数据
     * @param entries 饼图上显示的比例名称
     */
    //设置数据
    private void setData(ArrayList<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        //刷新
        mPieChart.invalidate();
    }
    /**
     * 回调函数接口
     * @author Administrator
     *
     */
    class DateBack implements OnDateBack {

        private int i;
        public DateBack(int i) {
            // TODO Auto-generated constructor stub
            this.i = i;
        }

        @Override
        public void getDate(CustomDate date,boolean flag) {
            // TODO Auto-generated method stub
            if (i == 1) {
                myDate = date;
                graphic_date.setText(date.year + "-" + date.month + "-" + date.day);
                mAddress = utils.getOneDayHistory(utils.getReadableDatabase(),date.year,date.month,date.day);
                initChart(mAddress);
                if (flag == true) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                        }
                    }, 500);
                }
            } else {
                return;
            }

        }
        @Override
        public void getChangDate(CustomDate date) {
            // TODO Auto-generated method stub

            graphic_date.setText(date.year+"-"+date.month+"-"+date.day);
        }
    }

    /**
     * 日历显示窗口
     * @param view
     * @param myCalendar
     * @param nowDate
     * @param date
     * @param databack
     * @param i
     */
    private void showPopWindow(View view, MyCalendar myCalendar, CustomDate nowDate, CustomDate date, GraphicActivity.DateBack databack, int i) {
        View contentView = LayoutInflater.from(GraphicActivity.this).inflate(R.layout.view_calendar, null);
        popupWindow = new PopupWindow(contentView,
                ActionBar.LayoutParams.MATCH_PARENT, Util.dip2px(this, 350), true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(
                GraphicActivity.this, R.drawable.backgroud));

        TextView left = (TextView) contentView.findViewById(R.id.btnPreMonth);
        TextView right = (TextView) contentView.findViewById(R.id.btnNextMonth);
        calendar = (ViewPager) contentView.findViewById(R.id.vp_calendar);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        myCalendar = new MyCalendar(calendar, this,databack,nowDate,date,i);
        myCalendar.setViews();
        popupWindow.showAsDropDown(view);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_address:
                startActivity(new Intent(this,AddressActivity.class));
                finish();
                break;
            case R.id.graphic_date:
                showPopWindow(view,myCalendar,myDate,startDate,new DateBack(1),1);
                break;
            case R.id.btnPreMonth:
                calendar.setCurrentItem(calendar.getCurrentItem() - 1);
                break;
            case R.id.btnNextMonth:
                calendar.setCurrentItem(calendar.getCurrentItem() + 1);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this,AddressActivity.class));
        finish();
        super.onBackPressed();
    }
}

package com.icebreaker.timelapse;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
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

import com.githang.statusbar.StatusBarCompat;
import com.github.mikephil.charting.charts.PieChart;
import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.Util;
import com.icebreaker.timelapse.view.MyCalendar;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private PieChartHelper pieChartHelper;
    private AppInfoHelper appInfoHelper;
    private int unlockCount = 1;
    private RelativeLayout mBarView;
    private TextView app_date;
    private PopupWindow popupWindow;
    private CustomDate myDate,startDate;
    private ViewPager calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this,UnlockService.class));
        AlarmUtils.setAlarmServiceTime(this, System.currentTimeMillis(), 5 * 1000);
        Calendar beginCal = Calendar.getInstance();
        initApp(beginCal);
        setViews();


    }
    //初始化界面
    private void initApp(Calendar beginCal){
        //检测用户是否对本app开启了“Apps with usage access”权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }

        //设置起始时间为0时0分0秒
        beginCal.set(Calendar.HOUR_OF_DAY,0);
        beginCal.set(Calendar.MINUTE,0);
        beginCal.set(Calendar.SECOND,0);
        appInfoHelper = new AppInfoHelper(this);
        List<AppInfo> appInfos = appInfoHelper.getInformation(beginCal,0);
        PieChart pieChart = findViewById(R.id.app_pieChart);
        pieChartHelper = new PieChartHelper(pieChart,appInfos);
        TextView totalUseTimeTxt = findViewById(R.id.app_time_txt);
        totalUseTimeTxt.setText(formatSecond(appInfoHelper.getTotalUseTime(appInfos)));
        TextView totalUseCountTxt = findViewById(R.id.app_count_txt);
        totalUseCountTxt.setText(String.valueOf(appInfoHelper.getTotalUseCount(appInfos)));
        TextView unlockCountTxt = findViewById(R.id.unlock_count_txt);
        unlockCountTxt.setText(String.valueOf(unlockCount));
    }



    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public String formatSecond(long second){
        String result;
        long minute = second/60;
        if(minute==0){
            result = "小于1分钟";
        }else{
            result = minute+"分钟";
        }
        return result;
    }

    private void setViews(){

        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        mBarView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.actionbar_main,null);
        ImageView app_list = (ImageView)mBarView.findViewById(R.id.show_list);
        app_list.setOnClickListener(this);
        app_date = (TextView)mBarView.findViewById(R.id.app_graphic_date);
        myDate = new CustomDate();
        startDate = new CustomDate(1979,1,1);
        app_date.setText(myDate.year+"-"+myDate.month+"-"+myDate.day);
        app_date.setOnClickListener(this);
        ImageView map = (ImageView)mBarView.findViewById(R.id.show_map);
        map.setOnClickListener(this);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

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
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_calendar, null);
        popupWindow = new PopupWindow(contentView,
                ActionBar.LayoutParams.MATCH_PARENT, Util.dip2px(this, 350), true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(
                MainActivity.this, R.drawable.backgroud));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show_list:
                startActivity(new Intent(this,AppListActivity.class));
                finish();
                break;
            case R.id.show_map:
                startActivity(new Intent(this,MapActivity.class));
                finish();
                break;
        }
    }
}

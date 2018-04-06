package com.icebreaker.timelapse;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
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
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.github.mikephil.charting.charts.PieChart;
import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.Util;
import com.icebreaker.timelapse.view.MyCalendar;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends CheckPermissionsActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private PieChartHelper pieChartHelper;
    private AppInfoHelper appInfoHelper;
    private int unlockCount = 1;
    private RelativeLayout mBarView;
    private TextView app_date;
    private PopupWindow popupWindow;
    private CustomDate myDate,startDate;
    private ViewPager calendar;
    private MyDBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    private BroadcastReceiver updateViewReceiver;
    private Calendar beginCal;
    private MyCalendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检测用户是否对本app开启了“Apps with usage access”权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }

        startService(new Intent(MainActivity.this,AppService.class));
        AlarmUtils.setAlarmServiceTime(this, System.currentTimeMillis(), 5 * 1000);
        beginCal = Calendar.getInstance();
        initApp(beginCal);
        setViews();
        updateViewReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.icebreaker.timelapse.SCREEN_UNLOCK".equals(action)) {
                    initApp(beginCal);
                }
            }
        };
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("com.icebreaker.timelapse.SCREEN_UNLOCK");
        registerReceiver(updateViewReceiver, itFilter);
    }
    //初始化界面
    private void initApp(Calendar beginCal){


        //设置起始时间为0时0分0秒
        beginCal.set(Calendar.HOUR_OF_DAY,0);
        beginCal.set(Calendar.MINUTE,0);
        beginCal.set(Calendar.SECOND,0);
        obtainDataFromDataBase(beginCal);
        appInfoHelper = new AppInfoHelper(this);
        List<AppInfo> appInfos = appInfoHelper.getInformation(beginCal,MainActivity.this);
        TextView totalUseTimeTxt = findViewById(R.id.app_time_txt);
        totalUseTimeTxt.setText(formatSecond(appInfoHelper.getTotalUseTime(appInfos)));
        TextView totalUseCountTxt = findViewById(R.id.app_count_txt);
        totalUseCountTxt.setText(String.valueOf(appInfoHelper.getTotalUseCount(appInfos)));
        TextView unlockCountTxt = findViewById(R.id.unlock_count_txt);
        unlockCountTxt.setText(String.valueOf(unlockCount));
        if(appInfos.size()==0){
            Toast.makeText(this,"暂无数据",Toast.LENGTH_SHORT).show();
            AppInfo inf1 = new AppInfo();
            inf1.setAppName("暂无数据");
            inf1.setForegroundTime(1);
            AppInfo inf2 = new AppInfo();
            inf2.setAppName("快去记录吧");
            inf2.setForegroundTime(1);
            appInfos.add(inf1);
            appInfos.add(inf2);
        }
        PieChart pieChart = findViewById(R.id.app_pieChart);
        pieChartHelper = new PieChartHelper(pieChart,appInfos);


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
            result = "<1分钟";
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
    private void showPopWindow(View view, MyCalendar myCalendar, CustomDate nowDate, CustomDate date, DateBack databack, int i) {
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
            }else{
                initApp(Calendar.getInstance());
            }
        }
    }

    private void obtainDataFromDataBase(Calendar quaryCal){
        //数据库操作
        myDBOpenHelper = new MyDBOpenHelper(MainActivity.this,"wimt.db",null,1);
        db = myDBOpenHelper.getWritableDatabase();
        String date = String.valueOf(quaryCal.get(Calendar.YEAR)+" "+(quaryCal.get(Calendar.MONTH)+1)+" "+quaryCal.get(Calendar.DAY_OF_MONTH));
        String sql = "SELECT count FROM unlockCount WHERE date = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{date});
        if (cursor.moveToFirst()){
            unlockCount = cursor.getInt(cursor.getColumnIndex("count"));
        }else{
            unlockCount = 1;
        }
        cursor.close();
    }

    /**
     * 回调函数接口
     * @author Administrator
     *
     */
    private class DateBack implements MyCalendar.OnDateBack {

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
                beginCal.set(Calendar.YEAR,date.year);
                beginCal.set(Calendar.MONTH,date.month-1);
                beginCal.set(Calendar.DAY_OF_MONTH,date.day);
                //Toast.makeText(MainActivity.this,"123",Toast.LENGTH_SHORT).show();
                initApp(beginCal);
                app_date.setText(date.year + "-" + date.month + "-" + date.day);

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

            app_date.setText(date.year+"-"+date.month+"-"+date.day);
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
            case R.id.app_graphic_date:
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
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(updateViewReceiver);
        if(myDBOpenHelper!=null){
            myDBOpenHelper.close();
        }
    }
}
package com.icebreaker.timelapse;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private PieChartHelper pieChartHelper;
    private AppInfoHelper appInfoHelper;
    private int unlockCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar beginCal = Calendar.getInstance();
        initApp(beginCal);


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
        totalUseTimeTxt.setText("使用时长："+ formatSecond(appInfoHelper.getTotalUseTime(appInfos)));
        TextView totalUseCountTxt = findViewById(R.id.app_count_txt);
        totalUseCountTxt.setText("使用次数："+String.valueOf(appInfoHelper.getTotalUseCount(appInfos)));
        TextView unlockCountTxt = findViewById(R.id.unlock_count_txt);
        unlockCountTxt.setText("解锁次数："+String.valueOf(unlockCount));
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
}

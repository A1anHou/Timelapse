package com.icebreaker.timelapse;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 小侯同学 on 2018/4/5.
 */

public class AppInfoHelper {
    private Context context;
    public AppInfoHelper(Context context){
        this.context = context;
    }


    public long getTotalUseTime(List<AppInfo> appInfos){
        long totalUseTime = 0;
        for(AppInfo appInfo : appInfos){
            totalUseTime = totalUseTime + appInfo.getForegroundTime();
        }
        return totalUseTime;
    }

    public long getTotalUseCount(List<AppInfo> appInfos){
        return appInfos.size();
    }

    //获取应用信息
    public List<AppInfo> getInformation(Calendar beginCal,Context context){
//        Calendar endCal = Calendar.getInstance();
//        if(isSameDay(beginCal,endCal)){//查询今日
            //什么都不用做
            //Toast.makeText(context,beginCal.get(Calendar.DAY_OF_MONTH)+" ",Toast.LENGTH_SHORT).show();
//        }else{//查询指定日期
//            //Toast.makeText(context,beginCal.get(Calendar.DAY_OF_MONTH)+" ",Toast.LENGTH_SHORT).show();
//            endCal.set(Calendar.YEAR,beginCal.get(Calendar.YEAR));
//            endCal.set(Calendar.MONTH,beginCal.get(Calendar.MONTH));
//            endCal.set(Calendar.DAY_OF_MONTH,beginCal.get(Calendar.DAY_OF_MONTH));
//            endCal.set(Calendar.HOUR_OF_DAY,23);
//            endCal.set(Calendar.MINUTE,59);
//            endCal.set(Calendar.SECOND,59);
//            //Toast.makeText(context,endCal.get(Calendar.HOUR_OF_DAY)+" ",Toast.LENGTH_SHORT).show();
//        }
        List<AppInfo> appInfoList = new ArrayList<AppInfo>();
        /*
        UsageStatsManager manager=(UsageStatsManager)context.getApplicationContext().getSystemService(context.USAGE_STATS_SERVICE);
        List<UsageStats> stats=manager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY,beginCal.getTimeInMillis(),endCal.getTimeInMillis());
        for(UsageStats us:stats){
            try {
                PackageManager pm=context.getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo=pm.getApplicationInfo(us.getPackageName(), PackageManager.GET_META_DATA);
                AppInfo appInfo = new AppInfo();
                if (applicationInfo.loadIcon(pm) != null) {
                    appInfo.setIcon(applicationInfo.loadIcon(pm));
                }
                appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                appInfo.setAppPackage(applicationInfo.packageName);
                //将毫秒转化为秒存储
                appInfo.setForegroundTime(us.getTotalTimeInForeground()/1000);
                appInfo.setLaunchCount(us.getClass().getDeclaredField("mLaunchCount").getInt(us));
                //过滤未启动过的应用
                if(appInfo.getLaunchCount()>0){
                    appInfoList.add(appInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(context,"wimt.db",null,1);
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        String sqlQuery = "SELECT * FROM appUsageStats WHERE date = ?";
        String date = String.valueOf(beginCal.get(Calendar.YEAR)+" "+(beginCal.get(Calendar.MONTH)+1)+" "+beginCal.get(Calendar.DAY_OF_MONTH));
        Cursor cursor = db.rawQuery(sqlQuery,new String[]{date});
        if(cursor.moveToFirst()){
            do {
                AppInfo appInfo = new AppInfo();
                appInfo.setAppPackage(cursor.getString(cursor.getColumnIndex("package")));
                appInfo.setForegroundTime(cursor.getLong(cursor.getColumnIndex("time")));
                appInfo.setLaunchCount(cursor.getInt(cursor.getColumnIndex("count")));
                PackageManager pm = context.getPackageManager();
                try {
                    ApplicationInfo applicationInfo=pm.getApplicationInfo(appInfo.getAppPackage(), PackageManager.GET_META_DATA);
                    appInfo.setIcon(applicationInfo.loadIcon(pm));
                    appInfo.setAppName(applicationInfo.loadLabel(pm).toString());
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo.setAppName("此应用已卸载");
                }
                appInfoList.add(appInfo);
            }while(cursor.moveToNext());
        }
        return appInfoList;
    }

    //判断两个Calendar对象是否为同一天
    public boolean isSameDay(Calendar day1,Calendar day2){
        if(day1.get(Calendar.DAY_OF_MONTH)==day2.get(Calendar.DAY_OF_MONTH)){
            if(day2.get(Calendar.MONTH)==day2.get(Calendar.MONTH)){
                if(day1.get(Calendar.YEAR)==day2.get(Calendar.YEAR)){
                    return true;
                }
            }
        }
        return false;
    }
}

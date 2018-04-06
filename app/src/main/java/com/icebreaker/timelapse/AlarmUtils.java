package com.icebreaker.timelapse;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmUtils {
    public static void setAlarmServiceTime(Context context, long timeInMillis, int time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LocationService.class);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = time;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           Log.e("wsh", "启动服务");
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setWindow(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
        } else {
           // Log.e("wsh", "api小于19");
            am.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, interval,sender);
        }
    }
}

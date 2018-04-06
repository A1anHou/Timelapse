package com.icebreaker.timelapse;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by 小侯同学 on 2018/4/5.
 */

public class UnlockService extends Service {
    public static final String TAG = "UnlockService";
    private BroadcastReceiver screenUnlockReceiver;
    private MyDBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    private int unlockCount = 1;
    final Handler unlockHandler = new Handler()
    {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            if(msg.what == 0x123)
            {
                sendBroadcast(new Intent("com.icebreaker.timelapse.SCREEN_UNLOCK"));
                updateDataBase();
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        listenUnlock();
    }

    private void listenUnlock() {
        //屏幕解锁计数
        screenUnlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    unlockHandler.sendEmptyMessage(0x123);
                }
            }
        };
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenUnlockReceiver, itFilter);
    }
    private void updateDataBase(){
        //数据库操作
        myDBOpenHelper = new MyDBOpenHelper(UnlockService.this,"wimt.db",null,1);
        db = myDBOpenHelper.getWritableDatabase();
        Calendar todayCal = Calendar.getInstance();
        String date = String.valueOf(todayCal.get(Calendar.YEAR)+" "+(todayCal.get(Calendar.MONTH)+1)+" "+todayCal.get(Calendar.DAY_OF_MONTH));
        String sql = "SELECT count FROM unlockCount WHERE date = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{date});
        if (cursor.moveToFirst()){
            unlockCount = cursor.getInt(cursor.getColumnIndex("count"));
            unlockCount++;
            //Toast.makeText(UnlockService.this,"解锁成功"+unlockCount,Toast.LENGTH_SHORT).show();
        }else{
            String createSql = "INSERT INTO  unlockCount(date,count) VALUES(?,?)";
            db.execSQL(createSql,new String[]{date,String.valueOf(unlockCount)});
        }
        String updateSql = "UPDATE unlockCount SET count = ? WHERE date = ?";
        db.execSQL(updateSql,new String[]{String.valueOf(unlockCount),date});
        cursor.close();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myDBOpenHelper!=null){
            myDBOpenHelper.close();
        }
    }
}

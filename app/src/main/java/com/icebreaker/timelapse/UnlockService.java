package com.icebreaker.timelapse;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 小侯同学 on 2018/4/5.
 */

public class UnlockService extends Service {
    public static final String TAG = "UnlockService";
    private MyThread myThread = null;
    private BroadcastReceiver screenUnlockReceiver;

    private class MyThread extends Thread {
        private Context context;
        private boolean isRun = true;

        private MyThread(Context context) {
            this.context = context;
        }
        public void setStop() {
            isRun = false;
        }
        @Override
        public void run() {

            while (isRun) {
                try {
                    TimeUnit.SECONDS.sleep(2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        listenUnlock();
        myThread = new MyThread(this);
        myThread.start();
        Log.i(TAG, "Service is start.");
    }

    private void listenUnlock() {
        //屏幕解锁计数
        screenUnlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    Toast.makeText(context, "解锁成功", Toast.LENGTH_SHORT).show();
                }
            }
        };
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenUnlockReceiver, itFilter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        myThread.setStop();
        Log.i(TAG, "Service is stop.");
    }
}

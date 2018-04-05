package com.icebreaker.timelapse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by 小侯同学 on 2018/4/4.
 */

public class StartActivity extends Activity {
    /*
    private int count = -1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (count < 0 ) {// 跳转
                startActivity(new Intent(StartActivity.this,
                        MainActivity.class));
                finish();
            } else {// 倒计时处理
                count--;
                handler.sendEmptyMessageDelayed(99, 500);
            }
        }
    };
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //handler.sendEmptyMessageDelayed(99,500);
        startService(new Intent(StartActivity.this,UnlockService.class));
        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
    }
}

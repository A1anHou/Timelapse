package com.icebreaker.timelapse;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import com.icebreaker.timelapse.MainActivity;
import com.icebreaker.timelapse.MyDBOpenHelper;
import com.icebreaker.timelapse.R;

/**
 * Created by 小侯同学 on 2018/4/6.
 */

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        String show = "";
        TextView textView = findViewById(R.id.test);
        MyDBOpenHelper dbOpenHelper = new MyDBOpenHelper(this,"wimt.db",null,1);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM appUsageStats",null);
        if(cursor.moveToFirst()){
            do {
                String packageName = cursor.getString(cursor.getColumnIndex("package"));
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                String date = cursor.getColumnName(cursor.getColumnIndex("date"));
                show = show + packageName + " " + count +" " + time + " " + date +"\n";
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dbOpenHelper.close();
        textView.setText(show);
    }
}

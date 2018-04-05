package com.icebreaker.timelapse;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import com.githang.statusbar.StatusBarCompat;

import com.icebreaker.timelapse.R;
import com.icebreaker.timelapse.view.MyAdapter;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener,ExpandableListView.OnGroupClickListener,ExpandableListView.OnChildClickListener{
    private RelativeLayout mBarView;
    private ArrayList<Adress> mAllAddresses = new ArrayList<Adress>();
    private SQLUtils mSqlUtils;
    private static final  String DB_NAME = "wimt.db3";
    private ExpandableListView mListView;
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        setViews(); // 设置ActionBar
        init();// 初始化控件
    }
    /**
     * 设置ActionoBar和状态栏
     * author wangbin
     * Created on 2018/4/4 18:59 
    */
    private void setViews(){

        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        mBarView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.actionbar_address,null);
        ImageView back = (ImageView)mBarView.findViewById(R.id.back_main);
        back.setOnClickListener(this);
        ImageView graphic = (ImageView)mBarView.findViewById(R.id.graphic);
        graphic.setOnClickListener(this);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

    }
    /**
     * 初始化普通控件
     * author wangbin
     * Created on 2018/4/5 16:32
    */
    private void init(){

        mSqlUtils = new SQLUtils(getApplicationContext(),DB_NAME,1);
        mAllAddresses = mSqlUtils.getAllHistory(mSqlUtils.getReadableDatabase());
        if(mAllAddresses != null){
            ArrayList<String> dateList = new ArrayList<String>();
            for(Adress address: mAllAddresses){
                String date = address.getmYear()+"."+address.getmMonth()+"."+address.getmDay();
                if(dateList.indexOf(date) == -1){
                    dateList.add(date);
                }
            }

            mListView = (ExpandableListView)findViewById(R.id.mListView);

            mListView.setGroupIndicator(null);

            mAdapter = new MyAdapter(this,dateList,mAllAddresses);
            mListView.setAdapter(mAdapter);
            for(int i = 0; i < mAdapter.getGroupCount(); i++){
                mListView.expandGroup(i);
            }
            mListView.setOnGroupClickListener(this);
            mListView.setOnChildClickListener(this);
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_main:
                startActivity(new Intent(this,MapActivity.class));
                finish();
                break;
            case R.id.graphic:
                startActivity(new Intent(this,GraphicActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
        mListView.expandGroup(i);
      //  Toast.makeText(this,"点击了一个GroupView",Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this,MapActivity.class));
        finish();
        super.onBackPressed();
    }
}

package com.icebreaker.timelapse.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icebreaker.timelapse.Adress;
import com.icebreaker.timelapse.R;

import java.util.ArrayList;


public class MyAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<String> mDateList;
    private ArrayList<Adress> mAddresses;
    public MyAdapter(Context context,ArrayList<String> mDateList,ArrayList<Adress> mAddresses){
        this.mContext = context;
        this.mDateList = mDateList;
        this.mAddresses = mAddresses;
    }
    @Override
    public int getGroupCount() {
        return mDateList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        int count = 0;
        for(Adress adress: mAddresses){
            String date = adress.getmYear()+"."+adress.getmMonth()+"."+adress.getmDay();
            if(date.equals(mDateList.get(i))){
                count++;
            }
        }
        return count;
    }

    @Override
    public Object getGroup(int i) {
        return mDateList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        ArrayList<Adress> childAddress = new ArrayList<Adress>();
        for(Adress adress: mAddresses){
            String date = adress.getmYear()+"."+adress.getmMonth()+"."+adress.getmDay();
            if(date.equals(mDateList.get(i))){
                childAddress.add(adress);
            }
        }
        return childAddress.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        RelativeLayout ll = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.parent_view,null);
        TextView address_date = (TextView)ll.findViewById(R.id.address_date);
        address_date.setText(mDateList.get(i));
        return ll;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ArrayList<Adress> childAddress = new ArrayList<Adress>();

        for(Adress adress: mAddresses){
            String date = adress.getmYear()+"."+adress.getmMonth()+"."+adress.getmDay();
            if(date.equals(mDateList.get(i))){
                childAddress.add(adress);
            }
        }


        LinearLayout ll = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.child_view_layout,null);
        TextView address_name = (TextView)ll.findViewById(R.id.address_name);
        TextView address_time = (TextView)ll.findViewById(R.id.address_time);

        View divider = ll.findViewById(R.id.divider);

        if(i1 == childAddress.size()-1){

            divider.setBackgroundColor(Color.rgb(245,245,245));
        }

        Adress address;
        if(childAddress != null){
            address = childAddress.get(i1);
            address_name.setText(address.getAdress());
            address_time.setText(getTotalTime(address.getTime()));
        }

        return ll;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    /**
     * 将时间从秒转化为时分秒的格式
     * author wangbin
     * Created on 2018/4/4 15:53
     */
    private String getTotalTime(long time)
    {

        long hour = time/3600;
        long minute = (time - hour*3600)/60;
        long second = time - hour*3600 - minute*60;
        String totalTime="";
        if(hour > 0)
        {
            totalTime += hour+"时";
        }
        if(minute >0)
        {
            totalTime += minute+"分";
        }
        if(second >0)
        {
            totalTime += second+"秒";
        }
        return totalTime;
    }
}

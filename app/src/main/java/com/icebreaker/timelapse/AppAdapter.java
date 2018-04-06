package com.icebreaker.timelapse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小侯同学 on 2018/3/22.
 */

public class AppAdapter extends BaseAdapter{

    private List<AppInfo> appInfos = new ArrayList<AppInfo>();
    private Context context;

    public void initAppAdapter(List<AppInfo> appInfos,Context context){
        this.appInfos = appInfos;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(appInfos!=null&&appInfos.size()>0){
            return appInfos.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if(appInfos!=null&&appInfos.size()>0){
            return appInfos.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if(appInfos!=null&&appInfos.size()>0){
            return i;
        }
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AppInfo appInfo = appInfos.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.item_app_info,null);
        ImageView img_icon = (ImageView)view.findViewById(R.id.app_icon);
        TextView txt_appName = (TextView)view.findViewById(R.id.app_name);
        TextView txt_appTime = (TextView)view.findViewById(R.id.app_foreground_time);
        TextView txt_appCount = (TextView)view.findViewById(R.id.app_launch_count);
        ImageView img_top = (ImageView)view.findViewById(R.id.app_top);
        img_icon.setImageDrawable(appInfo.getIcon());
        txt_appName.setText(appInfo.getAppName());
        txt_appTime.setText("\t"+formatSecond(appInfo.getForegroundTime()));
        txt_appCount.setText("\t开启次数："+appInfo.getLaunchCount());
        if(i<3){
            img_top.setImageResource(R.drawable.top);
        }
        return view;
    }

    private String formatSecond(long second){
        String result;
        long hour = second/(60*60);
        second = second - hour*60*60;
        long minute = second/60;
        if(minute==0){
            result = "小于1分钟";
        }else if(hour==0){
            result = minute + "分钟";
        }else{
            result = hour + "小时" + minute + "分钟";
        }
        return result;
    }
}

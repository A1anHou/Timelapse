package com.icebreaker.timelapse;

import android.graphics.Color;
import android.text.SpannableString;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小侯同学 on 2018/4/5.
 */

public class PieChartHelper {
    private PieChart pieChart;
    private List<AppInfo> appInfos;
    public PieChartHelper(PieChart pieChart,List<AppInfo> appInfos){
        this.pieChart = pieChart;
        this.appInfos = appInfos;
        initPieChart();
    }

    private void initPieChart(){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置中间文字
        //pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLACK);

        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // 触摸旋转
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        setData(appInfos);

        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = pieChart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setFormSize(12f);//比例块字体大小
        //设置比例块换行...
        l.setWordWrapEnabled(true);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        //l.setEnabled(false);//设置禁用比例块

        l.setForm(Legend.LegendForm.CIRCLE);//设置比例块形状，默认为方块
        // 输入标签样式
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

    }
    //设置中间文字
    public SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("今日使用情况");
        return s;
    }
    //设置数据
    public void setData(List<AppInfo> appInfos) {
        ArrayList<PieEntry> entries = changeAppInfosToPieEntries(appInfos);
        PieDataSet dataSet = new PieDataSet(entries,null);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);//比例数字颜色
        pieChart.setData(data);
        pieChart.highlightValues(null);
        //刷新
        pieChart.invalidate();
    }
    //将应用信息转化为饼状图实体
    private ArrayList<PieEntry> changeAppInfosToPieEntries(List<AppInfo> appInfos){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        double totalTime = 0;
        double otherTime = 0;
        for(AppInfo appInfo : appInfos){
            totalTime = totalTime+appInfo.getForegroundTime();
        }
        for(AppInfo appInfo : appInfos){
            if((appInfo.getForegroundTime()/totalTime)>=0.05){
                entries.add(new PieEntry(appInfo.getForegroundTime(), appInfo.getAppName()));
            }else{
                otherTime = otherTime + appInfo.getForegroundTime();
            }
        }
        if(otherTime>0){
            entries.add(new PieEntry((int)otherTime, "其他"));
        }
        return entries;
    }
}

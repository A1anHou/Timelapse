package com.icebreaker.timelapse;


/**
 * Created by wangbin on 2018/3/21.
 */

public class Adress {

        private String adress; // 详细地址名称
        private String poi; // 兴趣点名称
        private long time; // 在该地址停留的总时间，以秒为单位

        private int mWeek; // 一年的第几周
        private int mMonth; // 第几月
        private int mDay; // 哪一天
        private int mYear;
        private double lon; // 经度
        private double lat; // 纬度

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

        public String getPoi()
        {
            return poi;
        }
        public void setPoi(String poi)
        {
            this.poi = poi;
        }
        public long getTime()
        {
            return time;
        }
        public void setTime(long time)
        {
            this.time = time;
        }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }



    public int getmWeek() {
        return mWeek;
    }

    public void setmWeek(int mWeek) {
        this.mWeek = mWeek;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public String getTotalTime()
        {

            long hour = this.time/3600;
            long minute = (this.time - hour*3600)/60;
            long second = this.time - hour*3600 - minute*60;
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

    @Override
    public String toString() {
        return "Adress{" +
                "adress='" + adress + '\'' +
                ", poi='" + poi + '\'' +
                ", time=" + time +
                ", mWeek=" + mWeek +
                ", mMonth=" + mMonth +
                ", mDay=" + mDay +
                ", mYear=" + mYear +
                '}';
    }
}

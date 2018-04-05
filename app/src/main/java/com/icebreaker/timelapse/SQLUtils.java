package com.icebreaker.timelapse;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLUtils extends SQLiteOpenHelper{

    private static final String CREATE_TABLE = "create table path_history (" +

            "address varchar(100),"+
            "poi varchar(100),"+
            "time long,"+
            "year int,"+
            "month int,"+
            "week int,"+
            "day int,"+
            "lon double,"+
            "lat double,"+
            "primary key(year,day,address)"+")";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLUtils(Context context,String name,int version){
        super(context,name,null,version);

    }
    public  void insertItem(Adress adress,SQLiteDatabase mDb){

        String address = adress.getAdress();
        String poi = adress.getPoi();
        long time = adress.getTime();
        int year = adress.getmYear();
        int month = adress.getmMonth();
        int week = adress.getmWeek();
        int day = adress.getmDay();
        double lat = adress.getLat();
        double lon = adress.getLon();
        try {

            mDb.execSQL("insert into path_history (address,poi,time,year,month,week,day,lon,lat) values ( '"+address+"','"+poi+"',"+time+","+year+","+month+","+week+","+day+","+lon+","+lat+")");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public  void deleteItem(Adress adress,SQLiteDatabase mDb ){
        try{
            mDb.execSQL("delete from path_history where year = "+adress.getmYear()+" and day = "+adress.getmDay() +" and address = '"+adress.getAdress()+"'");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public  void updateItem(Adress adress,SQLiteDatabase mDb){

        try{
            mDb.execSQL("update path_history set time ="+adress.getTime()+" where year = "+adress.getmYear()+" and day = "+adress.getmDay()+" and address = '"+adress.getAdress()+"'");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public  ArrayList<Adress> getAllHistory(SQLiteDatabase mDb){
        ArrayList<Adress> mAdressList = new ArrayList<Adress>();
        try{
            Cursor cursor = mDb.rawQuery("select * from path_history where time > 600 order by year desc,month desc,day desc,time desc",null);
            if(cursor != null){
                while(cursor.moveToNext()){
                    Adress adress = new Adress();
                    adress.setAdress(cursor.getString(0));
                    adress.setPoi(cursor.getString(1));
                    adress.setTime(cursor.getLong(2));
                    adress.setmYear(cursor.getInt(3));
                    adress.setmMonth(cursor.getInt(4));
                    adress.setmWeek(cursor.getInt(5));
                    adress.setmDay(cursor.getInt(6));
                    adress.setLon(cursor.getDouble(7));
                    adress.setLat(cursor.getDouble(8));
                    mAdressList.add(adress);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return mAdressList;
    }
    public  ArrayList<Adress> getOneDayHistory(SQLiteDatabase mDb,int year,int month,int day){
        ArrayList<Adress> mAdressList = new ArrayList<Adress>();
        try{
            Cursor cursor = mDb.rawQuery("select * from path_history where year = "+year+" and month = "+month+" and day = "+day,null);
            if(cursor != null){
                while(cursor.moveToNext()){
                    Adress adress = new Adress();
                    adress.setAdress(cursor.getString(0));
                    adress.setPoi(cursor.getString(1));
                    adress.setTime(cursor.getLong(2));
                    adress.setmYear(cursor.getInt(3));
                    adress.setmMonth(cursor.getInt(4));
                    adress.setmWeek(cursor.getInt(5));
                    adress.setmDay(cursor.getInt(6));
                    adress.setLon(cursor.getDouble(7));
                    adress.setLat(cursor.getDouble(8));
                    mAdressList.add(adress);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return mAdressList;
    }
    public  Adress getHistory(String address,SQLiteDatabase mDb){
        ArrayList<Adress> mAdressList = new ArrayList<Adress>();
        try{
            Cursor cursor = mDb.rawQuery("select * from path_history",null);
            while(cursor.moveToNext()){
                Adress adress = new Adress();
                adress.setAdress(cursor.getString(0));
                adress.setPoi(cursor.getString(1));
                adress.setTime(cursor.getLong(2));
                adress.setmYear(cursor.getInt(3));
                adress.setmMonth(cursor.getInt(4));
                adress.setmWeek(cursor.getInt(5));
                adress.setmDay(cursor.getInt(6));
                adress.setLon(cursor.getDouble(7));
                adress.setLat(cursor.getDouble(8));
                mAdressList.add(adress);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(mAdressList != null){
            for(Adress adress: mAdressList){
                if(adress.getAdress().equals(address)){
                    return adress;
                }
            }
        }
        return null;
    }
}

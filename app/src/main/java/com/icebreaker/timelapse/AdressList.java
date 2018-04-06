package com.icebreaker.timelapse;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by wangbin on 2018/3/21.
 */

public class AdressList {
    private ArrayList<Adress> adressList;
    public AdressList(SQLUtils utils)
    {
        adressList = utils.getAllHistory(utils.getReadableDatabase());

    }
    public void addAdress(SQLUtils utils,Adress newOne)
    {
        boolean hasExist = false;

        if(adressList != null){

        for(Adress adress: adressList)
        {
            if(adress.getmYear() == newOne.getmYear() && adress.getmMonth() == newOne.getmMonth() &&adress.getmDay() == newOne.getmDay() && adress.getAdress().equals(newOne.getAdress()))
            {
                Log.e("UPDATE","更新一条数据  "+adress.getTime()+"  "+newOne.getTime());
                adress.setTime(adress.getTime()+newOne.getTime());
                newOne.setTime(adress.getTime()+newOne.getTime());
                utils.updateItem(newOne,utils.getReadableDatabase());
                Log.e("TIME",String.valueOf(newOne.getTime()));
                hasExist = true;
            }
        }

        }
        if(!hasExist)
        {

            adressList.add(newOne);
            utils.insertItem(newOne,utils.getReadableDatabase());
            Log.e("COUNT","插入一行数据");
        }
    }
    public void deleteAdress(String name)
    {
        if(adressList != null)
        {
            for(Adress adress: adressList)
            {
                if(adress.getAdress().equals(name))
                {
                    adressList.remove(adress);
                    break;
                }
            }
        }
    }
    public Adress getAdress(String name)
    {
        if(adressList != null)
        {
            for(Adress adress: adressList)
            {
                if(adress.getAdress().equals(name))
                {
                    return adress;
                }
            }
        }
        return null;
    }

    public ArrayList<Adress> getAdressList() {
        return adressList;
    }

    @Override
    public String toString() {
     StringBuffer sb = new StringBuffer();
     if(adressList != null)
     {
         for(Adress adress: adressList)
         {
             sb.append(adress.getAdress()+": "+adress.getTotalTime()+"\n");
         }
     }
     return sb.toString();
    }
}

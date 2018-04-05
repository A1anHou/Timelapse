package com.icebreaker.timelapse.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static int getCurrentMonthDay() {  
    	int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH); 
        return day;  
    }
	
	public static int getMonthDays(int year, int month) {  
        if (month > 12) {  
            month = 1;  
            year += 1;  
        } else if (month < 1) {  
            month = 12;  
            year -= 1;  
        }  
        int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };  
        int days = 0;  
        
        //���Ϊ���꣬2��Ϊ29��
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {  
            arr[1] = 29; 
        }  
  
        try {  
            days = arr[month - 1];  
        } catch (Exception e) {  
            e.getStackTrace();  
        }  
  
        return days;  
    }
	
	public static int getYear() {  
        return Calendar.getInstance().get(Calendar.YEAR);  
    }  
	
	public static int getMonth() {  
        return Calendar.getInstance().get(Calendar.MONTH) + 1;  
    }
	
	 public static int getWeekDayFromDate(int year, int month) {  
	        Calendar cal = Calendar.getInstance();  
	        cal.setTime(getDateFromString(year, month));  
	        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;  
	        if (week_index < 0) {  
	            week_index = 0;  
	        }  
	        return week_index;  
	    }
	 
	 public static Date getDateFromString(int year, int month) {  
	        String dateString = year + "-" + (month > 9 ? month : ("0" + month))  
	                + "-01";  
	        Date date = null;  
	        try {  
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	            date = sdf.parse(dateString);  
	        } catch (ParseException e) {  
	            System.out.println(e.getMessage());  
	        }  
	        return date;  
	    }
	 
	 public static boolean isCurrentMonth(CustomDate date){  
	        return(date.year == DateUtil.getYear() &&  
	                date.month == DateUtil.getMonth());  
	    }
	 
	 public static boolean isDateMonth(CustomDate nowDate,CustomDate date){
		 return(nowDate.year == date.year &&
				 nowDate.month == date.month);
	 }
	 
	 public static boolean isPreMonth(CustomDate date){
		return (date.year < DateUtil.getYear() || 
				(date.year == DateUtil.getYear() && date.month < DateUtil.getMonth()));
		}
	 
	 public static boolean isNextMonth(CustomDate date){
		 return (date.year > DateUtil.getYear() || 
					(date.year == DateUtil.getYear() && date.month > DateUtil.getMonth()));
	 }
	 
	 public static boolean isDatePreMonth(CustomDate nowDate,CustomDate date){
		 return (nowDate.year < date.year || 
					(nowDate.year == date.year && nowDate.month < date.month));
	 }
	 
	 public static boolean isDateNextMonth(CustomDate nowDate,CustomDate date) {
		 return (nowDate.year > date.year || 
					(nowDate.year == date.year && nowDate.month > date.month));
	}
	 
	 public static boolean isEarly(CustomDate date1,CustomDate date2){
		 return (date1.year < date2.year || 
				 (date1.year == date2.year && date1.month < date2.month) || 
				 (date1.year == date2.year && date1.month == date2.month) && date1.day < date2.day);
	 }
	 
	 public static boolean isNextDay(CustomDate date){
	    	return (date.year > DateUtil.getYear() || 
	    			(date.year == DateUtil.getYear() && date.month > DateUtil.getMonth())||
	    			(date.year == DateUtil.getYear() && date.month == DateUtil.getMonth() && date.getDay() > DateUtil.getCurrentMonthDay()));
	    }
}

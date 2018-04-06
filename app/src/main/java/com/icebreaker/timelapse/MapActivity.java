package com.icebreaker.timelapse;




import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.githang.statusbar.StatusBarCompat;
import com.icebreaker.timelapse.util.CustomDate;
import com.icebreaker.timelapse.util.Util;
import com.icebreaker.timelapse.view.MyCalendar;
import com.icebreaker.timelapse.view.MyCalendar.OnDateBack;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * 高精度定位模式功能演示
 *
 * @创建时间： 2015年11月24日 下午5:22:42
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Hight_Accuracy_Activity.java
 * @类型名称: Hight_Accuracy_Activity
 */
public class MapActivity extends CheckPermissionsActivity
        implements
        OnClickListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener,AMap.OnInfoWindowClickListener,AMap.OnMapTouchListener{

    private Intent mLocService;
    private BroadcastReceiver mReceiver;
    private SQLUtils utils;
    private static final  String DB_NAME = "wimt.db3";
    private RelativeLayout mBarView;
    private MapView mMapView;
    private AMap mMap;
    private UiSettings mUISettings;
    private ArrayList<Adress> mHistory;
    private Marker curShowMarker;
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private TextView duration;
    private TextView pointCount;
    private TextView select_date;
    private MyCalendar myCalendar;
    private CustomDate myDate,startDate;
    private PopupWindow popupWindow;
    private ViewPager calendar;
    private int type = 1;
    private int curYear,curMonth,curDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        StatusBarCompat.setStatusBarColor(this,Color.WHITE);// 设置状态栏的颜色为白色
        setActionBar(); // 设置ActionBar
        setOverflowShowingAlways();// 让ActionBar的Overflow始终显示


        initMapView(savedInstanceState); // 初始化地图组件
        showCurLoc(); // 定位到当前位置

        Calendar calendar = Calendar.getInstance();
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH)+1;
        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        utils = new SQLUtils(getApplicationContext(),DB_NAME,1);
        getCurDayAddresses(); // 获取当天的所有足迹点
        initView(type); // 初始化普通控件

        //注册广播
        mReceiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.wimt.icebreaker");
        registerReceiver(mReceiver, filter);

    }

    /**
     * 初始化地图控件
     * author wangbin
     * Created on 2018/4/4 13:57
    */
    private void initMapView(Bundle instance){

        mMapView = (MapView)findViewById(R.id.map);

        mMapView.onCreate(instance);

        mMap = mMapView.getMap();
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapTouchListener(this);
        mUISettings = mMap.getUiSettings();
        mUISettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    }
    /**
     * 初始化普通控件
     * @param type 选择显示足迹点类别
     * 根据不同类别显示不同时间长度的足迹点
     * author wangbin
     * Created on 2018/4/4 13:58
    */
    private void initView(int type){
        duration = (TextView)findViewById(R.id.duration);
        pointCount = (TextView)findViewById(R.id.pointCount);
        int ten=0,thirty=0,sixty=0;
        for(Adress adress: mHistory){
            if(adress.getTime()>60*60){
                sixty += 1;
                ten += 1;
                thirty += 1;
            }else if(adress.getTime()>30*60){
                thirty += 1;
                ten += 1;
            }else if(adress.getTime()>10*60){
                ten += 1;

            }

        }
        if(type == 1){
            duration.setText("10分钟以上");
            pointCount.setText(String.valueOf(ten)+"个足迹点");
        }else if(type == 2){
            duration.setText("30分钟以上");
            pointCount.setText(String.valueOf(thirty)+"个足迹点");
        }else{
            duration.setText("1小时以上");
            pointCount.setText(String.valueOf(sixty)+"个足迹点");
        }
    }
    /**
     * 获取选中日期内的所有足迹点
     * author wangbin
     * Created on 2018/4/4 14:07 
    */
    private void getCurDayAddresses(){

        mHistory = utils.getOneDayHistory(utils.getReadableDatabase(),curYear,curMonth,curDay);
        if(mHistory != null){
            drawMarkers(mHistory,type);
        }

    }
    /**
     * 绘制要显示的足迹点
     * @param adresses 选择的日期内的所有足迹点
     * @param type 根据不同类别绘制满足相应时间长度的Marker
     * author wangbin
     * Created on 2018/4/4 13:59
    */
    private void drawMarkers(ArrayList<Adress> adresses,int type){
        if(mMarkers != null){
            for(Marker marker: mMarkers){
                marker.remove();
            }
            mMap.invalidate();
        }
        ArrayList<Adress> someAdress = new ArrayList<Adress>();
            for(Adress adress: adresses){
                if(type == 1){
                    if(adress.getTime()>10*60){
                        someAdress.add(adress);
                    }
                }
                if(type == 2){
                    if(adress.getTime()>30*60){
                        someAdress.add(adress);
                    }
                }
                if(type == 3){
                    if(adress.getTime()>60*60){
                        someAdress.add(adress);
                    }
                }

            }
            if(someAdress != null){
                for(Adress adress: someAdress){
                    LatLng latLng = new LatLng(adress.getLat(),adress.getLon());
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);
                    View view = LayoutInflater.from(this).inflate(R.layout.marker_layout,null);
                    options.icon(BitmapDescriptorFactory.fromView(view));
                    options.title(adress.getAdress());
                    final Marker marker = mMap.addMarker(options);
                    mMarkers.add(marker);
                }
            }


    }

    /**
     * 将地图定位到当前位置
     * author wangbin
     * Created on 2018/4/4 14:01
    */
    private void showCurLoc(){
        MyLocationStyle myLocationStyle;
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        View view = LayoutInflater.from(this).inflate(R.layout.curlocation_layout,null);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromView(view));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        mMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    @Override
    public View getInfoWindow(Marker marker) {
        long totalTime = 0;
        for(Adress adress: mHistory){
            if(adress.getAdress().equals(marker.getTitle())){
                totalTime = adress.getTime();
                break;
            }
        }
        View infoWindow = getLayoutInflater().inflate(R.layout.point_layout, null);//display为自定义layout文件
        TextView address = (TextView) infoWindow.findViewById(R.id.address);
        TextView lat = (TextView) infoWindow.findViewById(R.id.lat);
        TextView lon = (TextView) infoWindow.findViewById(R.id.lon);
        TextView time = (TextView) infoWindow.findViewById(R.id.time);
        address.setText("地点名称:" + marker.getTitle());
        LatLng l = marker.getPosition();// 获取标签的位置
        lat.setText("纬度:"+l.latitude);
        lon.setText("经度:"+l.longitude);
        time.setText("停留时间:"+getTotalTime(totalTime));
        //此处省去长篇代码
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

            marker.showInfoWindow();
            curShowMarker = marker;

        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (mMap != null && curShowMarker != null) {
            if (curShowMarker.isInfoWindowShown()){
                curShowMarker.hideInfoWindow();
            }
        }
    }

    /**
     * 设置ActionBar
     * author wangbin
     * Created on 2018/4/4 14:01
    */
   private void setActionBar(){

        mBarView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.actionbar_layout,null);
       select_date = (TextView)mBarView.findViewById(R.id.select_date);
       myDate = new CustomDate();
       startDate = new CustomDate(1979,1,1);
       select_date.setText(myDate.year+"-"+myDate.month+"-"+myDate.day);
       select_date.setOnClickListener(this);
       ImageView back = (ImageView)mBarView.findViewById(R.id.back);
       back.setOnClickListener(this);
       ImageView footprint = (ImageView)mBarView.findViewById(R.id.footprint);
       footprint.setOnClickListener(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(mBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);

    }

    /**
     * 回调函数接口
     * @author Administrator
     *
     */
    class DateBack implements OnDateBack {

        private int i;
        public DateBack(int i) {
            // TODO Auto-generated constructor stub
            this.i = i;
        }

        @Override
        public void getDate(CustomDate date,boolean flag) {
            // TODO Auto-generated method stub
                if (i == 1) {
                    myDate = date;

                    select_date.setText(date.year + "-" + date.month + "-" + date.day);
                    mHistory = utils.getOneDayHistory(utils.getReadableDatabase(),date.year,date.month,date.day);
                    drawMarkers(mHistory,type);
                    initView(type);
                    if (flag == true) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                popupWindow.dismiss();
                            }
                        }, 500);
                    }
                } else {
                    return;
                }

        }
        @Override
        public void getChangDate(CustomDate date) {
            // TODO Auto-generated method stub

            select_date.setText(date.year+"-"+date.month+"-"+date.day);
        }
    }

    /**
     * 日历显示窗口
     * @param view
     * @param myCalendar
     * @param nowDate
     * @param date
     * @param databack
     * @param i
     */
    private void showPopWindow(View view,MyCalendar myCalendar,CustomDate nowDate,CustomDate date,DateBack databack,int i) {
        View contentView = LayoutInflater.from(MapActivity.this).inflate(R.layout.view_calendar, null);
        popupWindow = new PopupWindow(contentView,
                ActionBar.LayoutParams.MATCH_PARENT, Util.dip2px(this, 350), true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(
                MapActivity.this, R.drawable.backgroud));

        TextView left = (TextView) contentView.findViewById(R.id.btnPreMonth);
        TextView right = (TextView) contentView.findViewById(R.id.btnNextMonth);
        calendar = (ViewPager) contentView.findViewById(R.id.vp_calendar);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        myCalendar = new MyCalendar(calendar, this,databack,nowDate,date,i);
        myCalendar.setViews();
        popupWindow.showAsDropDown(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    /**
     * 选择不同的停留时间长度，然后重新绘制Marker和底部文字
     * author wangbin
     * Created on 2018/4/4 15:56 
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.tenmin:
               type = 1;
               drawMarkers(mHistory,type);
               initView(type);
               //Toast.makeText(this,"点击了10分钟",Toast.LENGTH_LONG).show();
               break;
           case R.id.thirtymin:
               type = 2;
               drawMarkers(mHistory,type);
               initView(type);
               //Toast.makeText(this,"点击了30分钟",Toast.LENGTH_LONG).show();
               break;
           case R.id.sixtymin:
               type =  3;
               drawMarkers(mHistory,type);
               initView(type);
               //Toast.makeText(this,"点击了30分钟",Toast.LENGTH_LONG).show();
               break;
       }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 使ActionBar的overflow控件始终显示
     * author wangbin
     * Created on 2018/4/4 15:54 
    */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 点击AcitonBar上的按钮的回调函数
     * author wangbin
     * Created on 2018/4/4 15:56 
    */
        @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_date:
                showPopWindow(v,myCalendar,myDate,startDate,new DateBack(1),1);
                break;
            case R.id.back:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
            case R.id.footprint:
                startActivity(new Intent(this, AddressActivity.class));
                finish();
                break;
            case R.id.btnPreMonth:
                calendar.setCurrentItem(calendar.getCurrentItem() - 1);
                break;
            case R.id.btnNextMonth:
                calendar.setCurrentItem(calendar.getCurrentItem() + 1);
                break;
        }

    }

    /**
     * 接收广播数据
     * author wangbin
     * Created on 2018/4/4 15:54 
    */
    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();

            ArrayList<Adress> mHistory = utils.getAllHistory(utils.getReadableDatabase());

            if(mHistory != null){
              // drawMarkers(mHistory);
            }
        }
    }


    @Override
    protected void onDestroy() {

/*        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LocationService.class);

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
        am.cancel(sender);*/
        if(utils != null){
            utils.close();
        }
        super.onDestroy();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
       // super.onBackPressed();
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

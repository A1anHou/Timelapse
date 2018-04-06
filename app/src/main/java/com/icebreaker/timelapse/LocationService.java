package com.icebreaker.timelapse;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class LocationService extends Service implements GeocodeSearch.OnGeocodeSearchListener{
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private AMapLocationListener locationListener;
    private AdressList mAdressList;
    private SQLUtils utils;
    private GeocodeSearch mSearch;
    private StringBuffer mLocateHistory =new StringBuffer();
    private int mCount = 0;
    private String mLastAdress;
    private long mLastLocateTime;
    private static final  String DB_NAME = "wimt.db3";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // initLocation();
        utils = new SQLUtils(getApplicationContext(),DB_NAME,1);
        final Calendar calendar = Calendar.getInstance();
        mAdressList = new AdressList(utils);
        mSearch = new GeocodeSearch(getApplicationContext());
        mSearch.setOnGeocodeSearchListener(this);
        mLastLocateTime = 0;
        locationListener = new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation location) {
                        if (null != location) {

                            StringBuffer sb = new StringBuffer();
                            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                            if(location.getErrorCode() == 0){

                                sb.append("定位成功" + "\n");
                                sb.append("定位类型: " + location.getLocationType() + "\n");
                                sb.append("经    度    : " + location.getLongitude() + "\n");
                                sb.append("纬    度    : " + location.getLatitude() + "\n");
                                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                                sb.append("提供者    : " + location.getProvider() + "\n");

                                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                                sb.append("角    度    : " + location.getBearing() + "\n");
                                // 获取当前提供定位服务的卫星个数
                                sb.append("星    数    : " + location.getSatellites() + "\n");
                                sb.append("国    家    : " + location.getCountry() + "\n");
                                sb.append("省            : " + location.getProvince() + "\n");
                                sb.append("市            : " + location.getCity() + "\n");
                                sb.append("城市编码 : " + location.getCityCode() + "\n");
                                sb.append("区            : " + location.getDistrict() + "\n");
                                sb.append("区域 码   : " + location.getAdCode() + "\n");
                                sb.append("城区信息:"+location.getDistrict()+"\n");
                                sb.append("街道信息："+location.getStreet()+"\n");
                                sb.append("街道门牌信息:"+location.getStreetNum()+"\n");
                                sb.append("地    址    : " + location.getAddress() + "\n");
                                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                                sb.append("AOI信息    : " + location.getAoiName() + "\n");

                                //定位完成的时间
                                sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                               long now = System.currentTimeMillis();
                                long interval;
                               if(mLastLocateTime > 0){
                                    interval = (int)(now-mLastLocateTime)/1000;
                               }else{
                                    interval = 5;
                               }

                                getAddrByLatLon(new LatLng(location.getLatitude(),location.getLongitude()));
                               Log.e("AddressName",mLastAdress);
                                Adress newOne  = new Adress();

                                    newOne.setAdress(mLastAdress);

                                   // Log.e("mLastAdress",mLastAdress);
                                newOne.setPoi(location.getPoiName());
                                newOne.setTime(interval);
                                newOne.setmYear(calendar.get(Calendar.YEAR));
                                newOne.setmMonth(calendar.get(Calendar.MONTH)+1);
                                newOne.setmWeek(calendar.get(Calendar.WEEK_OF_YEAR));
                                newOne.setmDay(calendar.get(Calendar.DAY_OF_MONTH));
                                newOne.setLon(location.getLongitude());
                                newOne.setLat(location.getLatitude());
                               // Log.e("NEWONE",newOne.toString());
                                mAdressList.addAdress(utils,newOne);
                                mLastLocateTime = now;
                                Log.e("data",newOne.toString());
                                //发送广播
                               /* Intent intent=new Intent();
                                intent.putExtra("data", mAdressList.toString());
                                intent.setAction("com.icebreaker.timelapse");
                                sendBroadcast(intent);*/
                                //定位完成的时间
                                sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                            } else {
                                //定位失败
                                sb.append("定位失败" + "\n");
                                sb.append("错误码:" + location.getErrorCode() + "\n");
                                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                                sb.append("错误描述:" + location.getLocationDetail() + "\n");
                            }
                            sb.append("***定位质量报告***").append("\n");
                            sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                            sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                            sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                            sb.append("****************").append("\n");
                            //定位之后的回调时间
                            sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                            //解析定位结果，
                            String result = sb.toString()+"\n";
                            result += mAdressList.getAdressList().toString();

                        } else {
                            // tvResult.setText("定位失败，loc is null");
                            Log.i("ERROR","定位失败!");
                        }
                    }

                };
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        destroyLocation();
        initLocation();
        startLocation();
       Log.e("RUNNING","我运行了"+formatUTC(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"+"\n"));
        //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           // Log.e("wsh", "onStartCommand KITKAT " + flags);
            AlarmUtils.setAlarmServiceTime(this, System.currentTimeMillis(), 5 * 1000);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyLocation();
        if(utils != null){
            utils.close();
        }
        super.onDestroy();

    }

    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        // 启动定位
        locationClient.startLocation();

    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(5000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }


    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){



    }


    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.stopLocation();
           // locationClient.unRegisterLocationListener(locationListener);
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;

        }
    }
    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }
    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress addr = regeocodeResult.getRegeocodeAddress();
        mLastAdress = addr.getFormatAddress();
        //Log.e("result",mLastAdress);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
    private void getAddrByLatLon(LatLng latLng){
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        mSearch.getFromLocationAsyn(new RegeocodeQuery(latLonPoint,5,GeocodeSearch.AMAP));
    }
}

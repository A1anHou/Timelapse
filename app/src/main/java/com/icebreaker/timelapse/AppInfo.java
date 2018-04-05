package com.icebreaker.timelapse;

import android.graphics.drawable.Drawable;

/**
 * Created by 小侯同学 on 2018/4/5.
 */

public class AppInfo {
    private Drawable icon;
    private String appPackage;
    private String appName;
    private long foregroundTime;
    private int launchCount;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getForegroundTime() {
        return foregroundTime;
    }

    public void setForegroundTime(long foregroundTime) {
        this.foregroundTime = foregroundTime;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppInfo)) return false;

        AppInfo appInfo = (AppInfo) o;

        if (getForegroundTime() != appInfo.getForegroundTime()) return false;
        if (getLaunchCount() != appInfo.getLaunchCount()) return false;
        if (getIcon() != null ? !getIcon().equals(appInfo.getIcon()) : appInfo.getIcon() != null)
            return false;
        if (getAppPackage() != null ? !getAppPackage().equals(appInfo.getAppPackage()) : appInfo.getAppPackage() != null)
            return false;
        return getAppName() != null ? getAppName().equals(appInfo.getAppName()) : appInfo.getAppName() == null;
    }

    @Override
    public int hashCode() {
        int result = getIcon() != null ? getIcon().hashCode() : 0;
        result = 31 * result + (getAppPackage() != null ? getAppPackage().hashCode() : 0);
        result = 31 * result + (getAppName() != null ? getAppName().hashCode() : 0);
        result = 31 * result + (int) (getForegroundTime() ^ (getForegroundTime() >>> 32));
        result = 31 * result + getLaunchCount();
        return result;
    }
}

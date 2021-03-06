package com.codingending.packagefairy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.codingending.packagefairy.bean.SimpleAppInfo;

import java.util.List;

/**
 * 与App相关的工具类
 * Created by CodingEnding on 2018/4/5.
 */

public class AppUtils {
    private AppUtils(){}

    /**
     * 获取手机中已经安装的应用
     */
    public static List<PackageInfo> getInstallAppList(Context context){
        return context.getPackageManager().getInstalledPackages(0);
    }

    /**
     * 根据应用包名获取uid
     * @param context
     * @param packageName 包名
     */
    public static int getUidByPackageName(Context context,String packageName){
        int uid=-1;
        PackageManager packageManager=context.getPackageManager();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(packageName,PackageManager.GET_META_DATA);
            uid = packageInfo.applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return uid;
    }

    /**
     * 获取封装了简单应用信息的Map
     */
    public static SparseArray<SimpleAppInfo> getSimpleAppMap(Context context){
        List<PackageInfo> packageInfoList=getInstallAppList(context);
        SparseArray<SimpleAppInfo> appMap=new SparseArray<>();

        for(PackageInfo packageInfo:packageInfoList){
            SimpleAppInfo simpleAppInfo=new SimpleAppInfo();
            simpleAppInfo.packageName=packageInfo.packageName;
            simpleAppInfo.appName=packageInfo.applicationInfo.loadLabel(
                    context.getPackageManager()).toString();//获取应用名
            simpleAppInfo.uid=getUidByPackageName(context,packageInfo.packageName);
            appMap.put(simpleAppInfo.uid,simpleAppInfo);
//            LogUtils.i("AppUtils","packageName:"+simpleAppInfo.packageName
//                    +" appName:"+simpleAppInfo.appName+" uid:"+simpleAppInfo.uid);
        }
        return appMap;
    }

    /**
     * 获取应用图标
     * @param packageName 包名
     */
    public static Drawable getAppIcon(Context context,String packageName){
        Drawable appIcon=null;
        PackageManager packageManager=context.getPackageManager();
        try {
            ApplicationInfo applicationInfo=packageManager.getApplicationInfo(packageName,0);
            appIcon=applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appIcon;
    }

    /**
     * 获取当前的版本号
     */
    public static int getVersionCode(Context context){
        int versionCode=0;
        String packageName=context.getPackageName();//获取包名
        try {
            versionCode=context.getPackageManager().getPackageInfo(packageName,0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取当前的版本名称
     */
    public static String getVersionName(Context context){
        String versionName="";
        String packageName=context.getPackageName();//获取包名
        try {
            versionName=context.getPackageManager().getPackageInfo(packageName,0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

}

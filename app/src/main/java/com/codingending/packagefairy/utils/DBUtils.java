package com.codingending.packagefairy.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.database.PackageSQLiteOpenHelper;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.entity.UserConsume;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.po.UserConsumePO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 与数据库相关的工具类
 * Created by CodingEnding on 2018/4/6.
 */

public class DBUtils {
    public static final int TOP_APP_COUNT=4;//需要统计的前N个App的数量
    public static final int RANK_APP_MAX_COUNT=50;//流量排行表中App的最大数量
    public static final int RECOMMEND_APP_COUNT=10;//需要作为推荐依据的App的数量

    private DBUtils(){}

    //删除指定日期的数据（用于修正数据）
    public static void deleteTemp(SQLiteDatabase database){
        database.delete(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,"month=? and year=?",new String[]{"6","2018"});
        database.delete(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,"month=? and year=?",new String[]{"6","2018"});
    }

    //获取一个数据库实例类
    public static SQLiteDatabase getDatabase(Context context){
        PackageSQLiteOpenHelper helper=new PackageSQLiteOpenHelper(context,PackageSQLiteOpenHelper.DATABASE_NAME,
                null,PackageSQLiteOpenHelper.DATABASE_VERSION);
        return helper.getWritableDatabase();
    }

    //插入一条UserConsume数据
    public static long insert(SQLiteDatabase database,UserConsumePO userConsumePO){
        ContentValues contentValues=new ContentValues();
        contentValues.put("call_time",userConsumePO.getCallTime());
        contentValues.put("flow_all",userConsumePO.getAllFlow());
        contentValues.put("day",userConsumePO.getDay());
        contentValues.put("month",userConsumePO.getMonth());
        contentValues.put("year",userConsumePO.getYear());
        long id=database.insert(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,null,contentValues);
        LogUtils.i("DBUtils","insert id:"+id);
        return id;
    }

    //批量插入UserConsume数据
    public static void insertUserConsumeList(SQLiteDatabase database, List<UserConsumePO> userConsumeList){
        database.beginTransaction();//开启事务-避免只插入了一部分数据
        for(UserConsumePO userConsumePO:userConsumeList){
            insert(database,userConsumePO);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    //更新一条UserConsume数据
    public static int updateByDate(SQLiteDatabase database,UserConsumePO userConsumePO){
        String dayStr=String.valueOf(userConsumePO.getDay());
        String monthStr=String.valueOf(userConsumePO.getMonth());
        String yearStr=String.valueOf(userConsumePO.getYear());
        ContentValues contentValues=new ContentValues();
        contentValues.put("flow_all",userConsumePO.getAllFlow());
        contentValues.put("call_time",userConsumePO.getCallTime());
        return database.update(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,contentValues,
                "day=? and month=? and year=?",new String[]{dayStr,monthStr,yearStr});
    }

    //获取从本月第一天到今日的通话时长和流量消耗情况
    public static List<UserConsumePO> getFirstToNowUserConsumeList(SQLiteDatabase database){
        List<UserConsumePO> userConsumePOList=new ArrayList<>();
        Calendar calendar=Calendar.getInstance();
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,null,"month=? and year=?",
                new String[]{String.valueOf(month),String.valueOf(year)},null,null,"day ASC");//按照day升序排列
        if(cursor.moveToFirst()){
            do{
                UserConsumePO userConsumePO=new UserConsumePO();
                userConsumePO.setId(cursor.getInt(cursor.getColumnIndex("id")));
                userConsumePO.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                userConsumePO.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                userConsumePO.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                userConsumePO.setAllFlow(cursor.getInt(cursor.getColumnIndex("flow_all")));
                userConsumePO.setCallTime(cursor.getInt(cursor.getColumnIndex("call_time")));
                userConsumePOList.add(userConsumePO);
            }
            while(cursor.moveToNext());
        }
        cursor.close();//关闭资源
        return userConsumePOList;
    }

    //判断指定日期的UserConsume数据是否存在
    public static boolean isUserConsumeExist(SQLiteDatabase database,int day,int month,int year){
        String dayStr=String.valueOf(day);
        String monthStr=String.valueOf(month);
        String yearStr=String.valueOf(year);
        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,
                null,"day=? and month=? and year=?",
                new String[]{dayStr,monthStr,yearStr},null,null,null);
        boolean isExist=cursor.moveToFirst();//存在数据就返回true
        cursor.close();//关闭游标
        return isExist;
    }

    //插入一条FlowConsume数据
    public static long insert(SQLiteDatabase database,FlowConsumePO flowConsumePO){
        ContentValues contentValues=new ContentValues();
        contentValues.put("app_name",flowConsumePO.getAppName());
        contentValues.put("package_name",flowConsumePO.getPackageName());
        contentValues.put("flow_amount",flowConsumePO.getFlowAmount());
        contentValues.put("day",flowConsumePO.getDay());
        contentValues.put("month",flowConsumePO.getMonth());
        contentValues.put("year",flowConsumePO.getYear());
        return database.insert(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,null,contentValues);
    }

    //批量插入FlowConsume数据
    public static void insertFlowConsumeList(SQLiteDatabase database,List<FlowConsumePO> flowConsumeList){
        database.beginTransaction();//开启事务-避免只插入了一部分数据
        for(FlowConsumePO flowConsumePO:flowConsumeList){
            insert(database,flowConsumePO);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    //更新FlowConsume数据
    public static int updateByDate(SQLiteDatabase database,FlowConsumePO flowConsumePO){
        String dayStr=String.valueOf(flowConsumePO.getDay());
        String monthStr=String.valueOf(flowConsumePO.getMonth());
        String yearStr=String.valueOf(flowConsumePO.getYear());
        ContentValues contentValues=new ContentValues();
        contentValues.put("flow_amount",flowConsumePO.getFlowAmount());//更新流量
        return database.update(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,contentValues,
                "day=? and month=? and year=? and app_name=?",new String[]{dayStr,monthStr,yearStr,flowConsumePO.getAppName()});
    }

    //批量更新或插入FlowConsumePO
    public static void updateOrInsertByDate(SQLiteDatabase database,List<FlowConsumePO> flowConsumePOList){
        database.beginTransaction();//开启事务-避免只更新了一部分数据
        for(FlowConsumePO flowConsumePO:flowConsumePOList){
            if(DBUtils.updateByDate(database,flowConsumePO)!=1){//如果还没有数据则插入数据
                DBUtils.insert(database,flowConsumePO);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * 获取从本月第一天到现在的应用流量消耗情况（每种应用本月的流量总消耗）
     * @param limit 数据条数最大值
     */
    public static List<FlowConsumePO> getMonthAppConsumeList(SQLiteDatabase database,int limit){
        List<FlowConsumePO> flowConsumePOList=new ArrayList<>();

        Calendar calendar=Calendar.getInstance();
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,new String[]{"month","year","app_name","sum(flow_amount)","package_name"},
                "month=? and year=?",new String[]{String.valueOf(month),String.valueOf(year)},
                "app_name",null,"sum(flow_amount) DESC",String.valueOf(limit));//按照流量消耗量降序排列
        if(cursor.moveToFirst()){
            do{
                FlowConsumePO flowConsumePO=new FlowConsumePO();
                flowConsumePO.setMonth(cursor.getInt(0));
                flowConsumePO.setYear(cursor.getInt(1));
                flowConsumePO.setAppName(cursor.getString(2));
                flowConsumePO.setFlowAmount(cursor.getInt(3));
                flowConsumePO.setPackageName(cursor.getString(4));
                flowConsumePOList.add(flowConsumePO);
            }
            while(cursor.moveToNext());
        }
        cursor.close();//关闭资源
        return flowConsumePOList;
    }

    /**
     * 获取从本周第一天到现在的应用流量消耗情况（每种应用本周的流量总消耗）
     * @param limit 数据条数最大值
     */
    public static List<FlowConsumePO> getWeekAppConsumeList(SQLiteDatabase database,int limit){
        List<FlowConsumePO> flowConsumePOList=new ArrayList<>();

        Calendar calendar=Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//以星期一作为一周的开始
//        int temp=calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_WEEK,calendar.getFirstDayOfWeek());//将日历设置为本周第一天

        int firstDayOfWeek=calendar.get(Calendar.DAY_OF_MONTH);//获取本周第一天对应在日历中的天数
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段
//        LogUtils.i("DBUTIls","firstDayOfWeek:"+temp);
//        LogUtils.i("DBUTIls","day:"+firstDayOfWeek);

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,new String[]{"month","year","day","app_name","sum(flow_amount)","package_name"},
                "month=? and year=? and day>=?",new String[]{String.valueOf(month),String.valueOf(year),String.valueOf(firstDayOfWeek)},
                "app_name",null,"sum(flow_amount) DESC",String.valueOf(limit));//按照流量消耗量降序排列
        if(cursor.moveToFirst()){
            do{
                FlowConsumePO flowConsumePO=new FlowConsumePO();
                flowConsumePO.setMonth(cursor.getInt(0));
                flowConsumePO.setYear(cursor.getInt(1));
                flowConsumePO.setAppName(cursor.getString(3));
                flowConsumePO.setFlowAmount(cursor.getInt(4));
                flowConsumePO.setPackageName(cursor.getString(5));
                flowConsumePOList.add(flowConsumePO);
            }
            while(cursor.moveToNext());
        }
        cursor.close();//关闭资源
        return flowConsumePOList;
    }

    /**
     * 获取今日的应用流量消耗情况（每种应用本月的流量总消耗）
     * @param limit 数据条数最大值
     */
    public static List<FlowConsumePO> getDayAppConsumeList(SQLiteDatabase database,int limit){
        List<FlowConsumePO> flowConsumePOList=new ArrayList<>();

        Calendar calendar=Calendar.getInstance();
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段
        int day=calendar.get(Calendar.DAY_OF_MONTH);//day字段

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_FLOW_CONSUME,null,
                "day=? and month=? and year=?",new String[]{String.valueOf(day),String.valueOf(month),
                String.valueOf(year)},null,null,"flow_amount DESC",String.valueOf(limit));//按照流量消耗量降序排列
        if(cursor.moveToFirst()){
            do {
                FlowConsumePO flowConsumePO=new FlowConsumePO();
                flowConsumePO.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                flowConsumePO.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                flowConsumePO.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                flowConsumePO.setAppName(cursor.getString(cursor.getColumnIndex("app_name")));
                flowConsumePO.setFlowAmount(cursor.getInt(cursor.getColumnIndex("flow_amount")));
                flowConsumePO.setPackageName(cursor.getString(cursor.getColumnIndex("package_name")));
                flowConsumePOList.add(flowConsumePO);
            }while(cursor.moveToNext());
        }
        cursor.close();//关闭资源
        return flowConsumePOList;
    }

    //获取本月流量/通话总消耗
    public static @Nullable UserConsumePO getMonthTotalUserFlow(SQLiteDatabase database){
        Calendar calendar=Calendar.getInstance();
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,new String[]{"month","year",
                "sum(flow_all)","sum(call_time)","count(day)"},"month=? and year=?",new String[]{String.valueOf(month),String.valueOf(year)},
                null,null,null,null);
        UserConsumePO userConsumePO=null;
        if(cursor.moveToFirst()){
            userConsumePO=new UserConsumePO();
            userConsumePO.setMonth(cursor.getInt(0));
            userConsumePO.setYear(cursor.getInt(1));
            userConsumePO.setAllFlow(cursor.getInt(2));//M
            userConsumePO.setCallTime(cursor.getInt(3));
            userConsumePO.setDay(cursor.getInt(4));//注意：此时的day属性就是本月存在数据的天数
        }
        cursor.close();//关闭资源
        return userConsumePO;
    }

    //获取本周流量/通话总消耗
    public static @Nullable UserConsumePO getWeekTotalUserFlow(SQLiteDatabase database){
        Calendar calendar=Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//以星期一作为一周的开始
        calendar.set(Calendar.DAY_OF_WEEK,calendar.getFirstDayOfWeek());//将日历设置为本周第一天

        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段
        int firstDayOfWeek=calendar.get(Calendar.DAY_OF_MONTH);//本周第一天

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,new String[]{"month","year","day",
                        "sum(flow_all)","sum(call_time)","count(day)"},"month=? and year=? and day>=?",new String[]{String.valueOf(month),String.valueOf(year),String.valueOf(firstDayOfWeek)},
                null,null,null,null);
        UserConsumePO userConsumePO=null;
        if(cursor.moveToFirst()){
            userConsumePO=new UserConsumePO();
            userConsumePO.setMonth(cursor.getInt(0));
            userConsumePO.setYear(cursor.getInt(1));
            userConsumePO.setAllFlow(cursor.getInt(3));//MB
            userConsumePO.setCallTime(cursor.getInt(4));
            userConsumePO.setDay(cursor.getInt(5));//注意：此时的day属性就是本周计入统计的天数
        }
        cursor.close();//关闭资源
        return userConsumePO;
    }

    //获取今日流量/通话总消耗
    public static @Nullable UserConsumePO getTodayTotalUserFlow(SQLiteDatabase database){
        Calendar calendar=Calendar.getInstance();
        int month= calendar.get(Calendar.MONTH)+1;//Month字段为0-11
        int year= calendar.get(Calendar.YEAR);//year字段
        int day=calendar.get(Calendar.DAY_OF_MONTH);//day字段

        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_USER_CONSUME,null,"month=? and year=? and day=?",new String[]{String.valueOf(month),String.valueOf(year),String.valueOf(day)},
                null,null,null,null);
        UserConsumePO userConsumePO=null;
        if(cursor.moveToFirst()){
            userConsumePO=new UserConsumePO();
            userConsumePO.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            userConsumePO.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            userConsumePO.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            userConsumePO.setAllFlow(cursor.getInt(cursor.getColumnIndex("flow_all")));//MB
            userConsumePO.setCallTime(cursor.getInt(cursor.getColumnIndex("call_time")));
        }
        cursor.close();//关闭资源
        return userConsumePO;
    }

    /*****************************与Package相关的方法*************************/
    //插入一条新数据
    public static long insert(SQLiteDatabase database,PackageBean packageBean){
        ContentValues values=new ContentValues();
        values.put("id",packageBean.getId());
        values.put("name",packageBean.getName());
        values.put("partner",packageBean.getPartner());
        values.put("operator",packageBean.getOperator());
        values.put("monthRent",packageBean.getMonthRent());
        values.put("packageCountryFlow",packageBean.getPackageCountryFlow());
        values.put("packageProvinceFlow",packageBean.getPackageProvinceFlow());
        values.put("packageCall",packageBean.getPackageCall());
        values.put("extraPackageCall",packageBean.getExtraPackageCall());
        values.put("extraCountryFlow",packageBean.getExtraCountryFlow());
        values.put("extraProvinceFlow",packageBean.getExtraProvinceFlow());
        values.put("extraProvinceOutFlow",packageBean.getExtraProvinceOutFlow());
        values.put("extraCountryDayRent",packageBean.getExtraCountryDayRent());
        values.put("extraCountryDayFlow",packageBean.getExtraCountryDayFlow());
        values.put("extraProvinceInDayRent",packageBean.getExtraProvinceInDayRent());
        values.put("extraProvinceInDayFlow",packageBean.getExtraProvinceInDayFlow());
        values.put("extraProvinceOutDayRent",packageBean.getExtraProvinceOutDayRent());
        values.put("extraProvinceOutDayFlow",packageBean.getExtraProvinceOutDayFlow());
        values.put("extraFlowType",packageBean.getExtraFlowType());
        values.put("privilegeDescription",packageBean.getPrivilegeDescription());
        values.put("star",packageBean.getStar());
        values.put("url",packageBean.getUrl());
        values.put("remark",packageBean.getRemark());
        values.put("abandon",packageBean.getAbandon());
        values.put("freeFlowType",packageBean.getFreeFlowType());
        values.put("totalConsume",packageBean.getTotalConsume());
        return database.insert(PackageSQLiteOpenHelper.TABLE_NAME_RECOMMEND_PACKAGE,null,values);
    }

    //批量插入数据
    public static void insertRecommendPackageList(SQLiteDatabase database,List<PackageBean> dataList){
        database.beginTransaction();//开启事务
        for(PackageBean packageBean:dataList){
            insert(database,packageBean);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * 删除推荐套餐表中所有的数据
     * @return 受影响的行数
     */
    public static int deleteAllRecommendPackage(SQLiteDatabase database){
        return database.delete(PackageSQLiteOpenHelper.TABLE_NAME_RECOMMEND_PACKAGE,null,null);
    }

    //获取所有推荐套餐
    public static List<PackageBean> getRecommendPackageList(SQLiteDatabase database){
        List<PackageBean> dataList=new ArrayList<>();
        Cursor cursor=database.query(PackageSQLiteOpenHelper.TABLE_NAME_RECOMMEND_PACKAGE,null,
                null,null,null,null,"totalConsume");//根据每月预计消费排序
        if(cursor.moveToFirst()){
            do{//批量获取数据
                PackageBean packageBean=new PackageBean();
                packageBean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                packageBean.setName(cursor.getString(cursor.getColumnIndex("name")));
                packageBean.setPartner(cursor.getString(cursor.getColumnIndex("partner")));
                packageBean.setOperator(cursor.getString(cursor.getColumnIndex("operator")));
                packageBean.setMonthRent(cursor.getInt(cursor.getColumnIndex("monthRent")));
                packageBean.setPackageCountryFlow(cursor.getInt(cursor.getColumnIndex("packageCountryFlow")));
                packageBean.setPackageProvinceFlow(cursor.getInt(cursor.getColumnIndex("packageProvinceFlow")));
                packageBean.setPackageCall(cursor.getInt(cursor.getColumnIndex("packageCall")));
                packageBean.setExtraPackageCall(cursor.getDouble(cursor.getColumnIndex("extraPackageCall")));
                packageBean.setExtraCountryFlow(cursor.getDouble(cursor.getColumnIndex("extraCountryFlow")));
                packageBean.setExtraProvinceFlow(cursor.getDouble(cursor.getColumnIndex("extraProvinceFlow")));
                packageBean.setExtraProvinceOutFlow(cursor.getDouble(cursor.getColumnIndex("extraProvinceOutFlow")));
                packageBean.setExtraCountryDayRent(cursor.getInt(cursor.getColumnIndex("extraCountryDayRent")));
                packageBean.setExtraCountryDayFlow(cursor.getInt(cursor.getColumnIndex("extraCountryDayFlow")));
                packageBean.setExtraProvinceInDayRent(cursor.getInt(cursor.getColumnIndex("extraProvinceInDayRent")));
                packageBean.setExtraProvinceInDayFlow(cursor.getInt(cursor.getColumnIndex("extraProvinceInDayFlow")));
                packageBean.setExtraProvinceOutDayRent(cursor.getInt(cursor.getColumnIndex("extraProvinceOutDayRent")));
                packageBean.setExtraProvinceOutFlow(cursor.getInt(cursor.getColumnIndex("extraProvinceOutDayFlow")));
                packageBean.setExtraFlowType(cursor.getInt(cursor.getColumnIndex("extraFlowType")));
                packageBean.setPrivilegeDescription(cursor.getString(cursor.getColumnIndex("privilegeDescription")));
                packageBean.setStar(cursor.getDouble(cursor.getColumnIndex("star")));
                packageBean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                packageBean.setRemark(cursor.getString(cursor.getColumnIndex("remark")));
                packageBean.setAbandon(cursor.getInt(cursor.getColumnIndex("abandon")));
                packageBean.setFreeFlowType(cursor.getInt(cursor.getColumnIndex("freeFlowType")));
                packageBean.setTotalConsume(cursor.getDouble(cursor.getColumnIndex("totalConsume")));
                dataList.add(packageBean);
            }while(cursor.moveToNext());
        }
        return dataList;
    }

}

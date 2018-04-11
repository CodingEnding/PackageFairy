package com.codingending.packagefairy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库的辅助类
 * Created by CodingEnding on 2018/4/6.
 */

public class PackageSQLiteOpenHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME="PackageFairy";//数据库名称
    public static final int DATABASE_VERSION=1;//数据库版本

    public static final String TABLE_NAME_USER_CONSUME="UserConsume";//设备流量/通话时间消耗表名
    public static final String TABLE_NAME_FLOW_CONSUME="FlowConsume";//应用流量消耗表名

    //创建设备流量/通话消耗表
    public static final String CREATE_USER_CONSUME="create table UserConsume(" +
            "id integer primary key autoincrement," +
            "call_time integer not null," +
            "flow_all integer not null," +
            "day integer not null," +
            "month integer not null," +
            "year integer not null);";
    //创建应用流量消耗表
    public static final String CREATE_FLOW_CONSUME="create table FlowConsume(" +
            "id integer primary key autoincrement," +
            "app_name text not null," +
            "package_name text not null,"+
            "flow_amount integer not null," +
            "day integer not null," +
            "month integer not null," +
            "year integer not null);";

    public PackageSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_CONSUME);
        db.execSQL(CREATE_FLOW_CONSUME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新数据库
    }
}

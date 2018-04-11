package com.codingending.packagefairy.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.SparseArray;

import com.codingending.packagefairy.bean.SimpleAppInfo;
import com.codingending.packagefairy.bean.StartEnd;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.po.UserConsumePO;
import com.codingending.packagefairy.utils.AppUtils;
import com.codingending.packagefairy.utils.ConsumeStatsUtils;
import com.codingending.packagefairy.utils.DBUtils;
import com.codingending.packagefairy.utils.DateUtils;
import com.codingending.packagefairy.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 常驻进程
 * 定时存储每天的流量消耗/通话时长
 * @author CodingEnding
 */
public class ConsumeService extends Service {
    public static final String TAG="ConsumeService";
    public static final int TIMER_INTERNAL=10*60*1000;//定时更新的间隔（10min）
    public static final String ACTION_STATS_UPDATE="com.codingending.packagefairy.STATS_UPDATE";//数据已更新
//    public static final int LEAST_FLOW_BOTTOM=2;//计入统计的最小应用流量消耗（M）（少于这个消耗量的应用就不再统计）

    private SQLiteDatabase database;//SQLite实例
    private boolean needUpdate=true;//是否需要执行定时更新操作
    private boolean needSendBroad=true;//是否需要在数据更新后发送广播（默认需要）
    private LocalBroadcastManager localBroadcastManager;//本地广播管理器

    public ConsumeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG,"onStartCommand");
        initDatabase();//每次服务启动的时候重新获取一个SQLite实例
        localBroadcastManager=LocalBroadcastManager.getInstance(this);//初始化本地广播管理器
        new TaskThread().start();//在新的线程中完成更新数据库中流量消耗记录的任务
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG,"onDestroy");
        if(database!=null){
            database.close();//关闭数据库
        }
        super.onDestroy();
    }

    //初始化数据库
    private void initDatabase(){
        if(database!=null){
            database.close();
        }
        database=DBUtils.getDatabase(this);
    }

    //停止发送数据更新的广播
    public void stopSendBroad(){
        needSendBroad=false;
    }

    //自定义的任务线程
    class TaskThread extends Thread{
        @Override
        public void run() {
            while(needUpdate){//定时执行更新数据的逻辑
                if(ActivityCompat.checkSelfPermission(ConsumeService.this,
                        Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
                    //有权限才执行具体逻辑
                    getConsumeStats();//获取流量使用情况
                    if(needSendBroad){
                        localBroadcastManager.sendBroadcast(new Intent(ACTION_STATS_UPDATE));//发送本地广播
                    }
                    //TODO 演示
//                    LogUtils.i(TAG,"从数据库中读取.........");
//                    List<UserConsumePO> dataList=DBUtils.getFirstToNowUserConsumeList(database);
//                    for(UserConsumePO userConsumePO:dataList){
//                        LogUtils.i(TAG,"第"+userConsumePO.getDay()+"天:"+userConsumePO.getAllFlow()+"M "
//                                +userConsumePO.getCallTime()+" 分钟");
//                    }
                }
                else{
                    LogUtils.w(TAG,"没有权限！");
                }

                try {
                    Thread.sleep(TIMER_INTERNAL);//休眠固定的时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();//跳出循环后就关闭服务
        }
    }

    //停止当前任务
    public void stopTask(){
        needUpdate=false;
    }

    //获取通话时长和流量使用情况
    @SuppressLint("MissingPermission")
    private void getConsumeStats(){
        LogUtils.i(TAG,"getConsumeStats");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//Android6.0及以上可用
            NetworkStatsManager networkStatsManager= (NetworkStatsManager)getSystemService(
                    Context.NETWORK_STATS_SERVICE);
            TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            String subscriberId = tm.getSubscriberId();

            try {
                if(isConsumeDataExist()){//判断数据库中是否已经存储了流量消耗数据
                    LogUtils.i(TAG,"isConsumeDataExist true");
                    getToDayConsumeStats(networkStatsManager,subscriberId);
                    getTodayEachAppFlowStats(networkStatsManager,subscriberId);//记录今天每种应用的消耗量
                }
                else{
                    //如果当月第一天的数据不存在证明之前从未储存过数据，直接计算从当月第一天到今天的设备流量消耗和应用流量消耗
                    LogUtils.i(TAG,"isConsumeDataExist false");
                    getEachDayConsumeStats(networkStatsManager,subscriberId);
                    getMonthEachAppFlowStats(networkStatsManager,subscriberId);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //判断数据库中是否已经存储了消耗数据（决定接下来是更新今日的消耗量还是从本月第一天开始记录每天的消耗量）
    private boolean isConsumeDataExist(){
        Calendar calendar=DateUtils.getFirstMonthDayInCalendar();//获取本月第一天的时间数据
        int day=calendar.get(Calendar.DAY_OF_MONTH);//从1开始
        int month=calendar.get(Calendar.MONTH)+1;//从0开始
        int year=calendar.get(Calendar.YEAR);
        return DBUtils.isUserConsumeExist(database,day,month,year);//判断本月第一天的数据是否已存储
    }

    //获取本月每天的通话时长和整体的流量消耗
    @TargetApi(Build.VERSION_CODES.M)
    private void getEachDayConsumeStats(NetworkStatsManager networkStatsManager,String subscriberId) throws RemoteException {
        LogUtils.i(TAG,"getEachDayConsumeStats");
        List<StartEnd> timeList= DateUtils.getFirstMonthDayToNowTimeList();
        List<UserConsumePO> userConsumePOList=new ArrayList<>();

        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;//当前月份（从0开始）
        int year=calendar.get(Calendar.YEAR);//当前年份

        for(int i=0;i<timeList.size();i++){
            StartEnd startEnd=timeList.get(i);
            userConsumePOList.add(getUserConsume(networkStatsManager,subscriberId,
                    startEnd,i+1,month,year));//逐一统计每天的流量消耗和通话时长
        }
        DBUtils.insertUserConsumeList(database,userConsumePOList);//批量存储
    }

    //获取今日的通话时长和整体流量消耗
    @TargetApi(Build.VERSION_CODES.M)
    private void getToDayConsumeStats(NetworkStatsManager networkStatsManager, String subscriberId) throws RemoteException {
        StartEnd startEnd=DateUtils.getTodayTimePart();//当日的时间段
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);//天数（从1开始）
        int month=calendar.get(Calendar.MONTH)+1;//当前月份（从0开始）
        int year=calendar.get(Calendar.YEAR);//当前年份
        UserConsumePO userConsumePO=getUserConsume(networkStatsManager,
                subscriberId,startEnd,day,month,year);//获取今日的流量消耗

        if(DBUtils.updateByDate(database,userConsumePO)!=1){//将数据更新到数据库中
            DBUtils.insert(database,userConsumePO);//如果还没有数据则插入数据
        }
    }

    /**
     * 获取指定时间段内的流量消耗和通话时长
     * @param startEnd 时间段
     * @param day 天数
     * @param month 所属月份
     * @param year 所属年份
     */
    @TargetApi(Build.VERSION_CODES.M)
    private UserConsumePO getUserConsume(NetworkStatsManager networkStatsManager,
                                         String subscriberId, StartEnd startEnd,
                                         int day, int month, int year) throws RemoteException {
        NetworkStats.Bucket bucket=networkStatsManager.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,subscriberId,startEnd.start,
                startEnd.end);
        float totalByM=getFlowByBucket(bucket);//以M为单位
//        LogUtils.i(TAG,"第"+day+"天:"+totalByM+"M");

        int callTime= ConsumeStatsUtils.getCallTime(this,startEnd)/60;//获得当日的通话时长（分钟）

        LogUtils.i(TAG,"getUserConsume-->"+year+" "+month+" "+day+" totalByM:"+Math.round(totalByM)+" callTime:"+callTime);
        return new UserConsumePO(callTime,Math.round(totalByM),day,month,year);
    }

    /**
     * 获取本月每天每种App的流量消耗
     * 注意：querySummary的结果中包含很多的Bucket
     * （同一个uid对应的所有Bucket加在一起才是这个应用在这段时间内的流量消耗量）
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void getMonthEachAppFlowStats(NetworkStatsManager networkStatsManager,String subscriberId) throws RemoteException {
        LogUtils.i(TAG,"getMonthEachAppFlowStats");
        List<StartEnd> timeList=DateUtils.getFirstMonthDayToNowTimeList();
        List<FlowConsumePO> monthDataList=new ArrayList<>();//从本月第一天到现在的数据列表

        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;//当前月份（从0开始）
        int year=calendar.get(Calendar.YEAR);//当前年份

        for(int i=0;i<timeList.size();i++){//循环获取本月第一天到现在的应用数据消耗
            StartEnd startEnd=timeList.get(i);
            monthDataList.addAll(getAppFlowConsumeList(networkStatsManager,
                    subscriberId,startEnd,i+1,month,year));//逐一统计每天的数据
        }
        DBUtils.insertFlowConsumeList(database,monthDataList);//批量插入数据
    }

    /**
     * 获取今日每种App的流量消耗
     * 注意：querySummary的结果中包含很多的Bucket
     * （同一个uid对应的所有Bucket加在一起才是这个应用在这段时间内的流量消耗量）
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void getTodayEachAppFlowStats(NetworkStatsManager networkStatsManager,String subscriberId) throws RemoteException {
        StartEnd startEnd=DateUtils.getTodayTimePart();//今日的时间段
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);//今天所属的天数（从1开始）
        int month=calendar.get(Calendar.MONTH)+1;//当前月份（从0开始）
        int year=calendar.get(Calendar.YEAR);//当前年份

        List<FlowConsumePO> flowConsumePOList=getAppFlowConsumeList(networkStatsManager,
                subscriberId,startEnd,day,month,year);//获取今日的应用流量数据列表
        DBUtils.updateOrInsertByDate(database,flowConsumePOList);//批量更新或插入数据
    }

    /**
     * 获取指定时间段内的应用流量数据列表
     * @param startEnd 时间段
     * @param day 天数
     * @param month 所属月份
     * @param year 所属年份
     */
    @TargetApi(Build.VERSION_CODES.M)
    private List<FlowConsumePO> getAppFlowConsumeList(NetworkStatsManager networkStatsManager,
                        String subscriberId,StartEnd startEnd,int day,int month,int year) throws RemoteException {
        SparseArray<Long> appFlowMap=new SparseArray<>();//存储应用的流量消耗[uid-流量消耗(字节)]
        NetworkStats networkStats=networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE,
                subscriberId,startEnd.start,startEnd.end);

        NetworkStats.Bucket bucket=new NetworkStats.Bucket();
        do{
            networkStats.getNextBucket(bucket);
            int uid=bucket.getUid();//应用id
            long rxBytes=bucket.getRxBytes();
            long txBytes=bucket.getTxBytes();
            appFlowMap.put(uid,rxBytes+txBytes+appFlowMap.get(uid,0L));//更新已有的流量消耗数据
//            LogUtils.i(TAG,"uid："+uid+" rxBytes："+rxBytes+" txBytes："+txBytes);
        }while(networkStats.hasNextBucket());
        networkStats.close();//关闭资源（否则报错）

        List<FlowConsumePO> flowConsumePOList=new ArrayList<>();//最终要存储到数据库的数据列表
        SparseArray<SimpleAppInfo> appInfoMap=AppUtils.getSimpleAppMap(this);//获取手机中已经安装的应用信息（uid、包名、应用名）

        for(int i=0;i<appFlowMap.size();i++){//递归获取每个应用的流量消耗
            int uid=appFlowMap.keyAt(i);
            long flowConsume=appFlowMap.get(uid);//应用流量消耗（字节）
            int intFlowConsume=Math.round(flowConsume/1024);//单位为KB

            SimpleAppInfo simpleAppInfo=appInfoMap.get(uid);
            if(simpleAppInfo!=null&&intFlowConsume>0){
                FlowConsumePO flowConsumePO=new FlowConsumePO(simpleAppInfo.appName,
                        simpleAppInfo.packageName,intFlowConsume,day,month,year);
                flowConsumePOList.add(flowConsumePO);
//                LogUtils.i(TAG,"name："+simpleAppInfo.appName+" flowConsume："+intFlowConsume+"KB");
            }
        }
        return flowConsumePOList;
    }

    //通过NetworkStats.Bucket获取流量消耗（返回M为单位）
    @TargetApi(Build.VERSION_CODES.M)
    private float getFlowByBucket(NetworkStats.Bucket bucket){
        long rxBytes=bucket.getRxBytes();//接收的
        long txBytes=bucket.getTxBytes();//发送的
        long total=rxBytes+txBytes;
        return total/1024.0f/1024.0f;
    }

}

package com.codingending.packagefairy.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.CallRankActivity;
import com.codingending.packagefairy.activity.FlowRankActivity;
import com.codingending.packagefairy.bean.StartEnd;
import com.codingending.packagefairy.chart.ChartValueFormatter;
import com.codingending.packagefairy.chart.DataFlowWindowView;
import com.codingending.packagefairy.chart.DayValueFormatter;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.po.UserConsumePO;
import com.codingending.packagefairy.service.ConsumeService;
import com.codingending.packagefairy.utils.ConsumeStatsUtils;
import com.codingending.packagefairy.utils.DBUtils;
import com.codingending.packagefairy.utils.DateUtils;
import com.codingending.packagefairy.utils.DeviceUtils;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PermissionUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 统计页面
 * Created by CodingEnding on 2018/4/5.
 */
public class StatisticsFragment extends BaseFragment{
    private static final String TAG="StatisticsFragment";
    public static final int PERMISSION_REQUEST=2;//权限请求码
    public static final int ACTIVITY_REQUEST=3;//启动Activity的请求码

    private ProgressBar progressBar;
    private ScrollView dataLayout;//数据区域
    private LineChart lineChart;//流量走势图
    private PieChart pieChart;//流量消耗分布图
    private TextView flowTextView;//流量消耗
    private TextView callTextView;//已使用的通话时长
    private LinearLayout flowRankView;//流量排行
    private LinearLayout callRankView;//通话统计

    private List<String> permissionList=new ArrayList<>();//储存需要请求的权限
    private SQLiteDatabase database;//SQLite实例
    private LocalBroadcastManager localBroadcastManager;//本地广播管理器
    private DataUpdateReceiver dataUpdateReceiver;//数据更新广播接收器

    /**
     * 返回实例
     */
    public static StatisticsFragment newInstance(){
        return new StatisticsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDeviceInfo();//获取设备信息
        initDatabase();//初始化数据库
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_statistics,container,false);
        initViews(view);
        initLineChar();
        initPieChar();
        checkAndRequestPermissions();//检测并获取权限
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()&&hasPermissions()){//可见且拥有权限就更新数据
            progressBar.setVisibility(View.VISIBLE);
            dataLayout.setVisibility(View.GONE);
            loadDataFromDatabase();
        }
    }

    @Override
    public void onDestroy() {
        if(database!=null){
            database.close();//关闭数据库
        }
        if(localBroadcastManager!=null&&dataUpdateReceiver!=null){
            localBroadcastManager.unregisterReceiver(dataUpdateReceiver);//取消广播接收器
        }
        super.onDestroy();
    }

    //初始化数据库
    private void initDatabase(){
        if(database!=null){
            database.close();
        }
        database=DBUtils.getDatabase(getActivity());
    }

    /**
     * 开启后台服务
     */
    private void startBackService(){
        getActivity().startService(new Intent(getActivity(),ConsumeService.class));//开启后台服务
    }

    /**
     * 加载数据
     */
    private void loadData(){
        LogUtils.i(TAG,"loadData");
        if(isConsumeDataExist()){//数据库中已经存储了消耗数据就直接加载
            loadDataFromDatabase();//从数据库中加载数据
        }
        else{//否则为Fragment注册广播接收器，等待Service更新数据
            localBroadcastManager=LocalBroadcastManager.getInstance(getActivity());
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction(ConsumeService.ACTION_STATS_UPDATE);
            dataUpdateReceiver=new DataUpdateReceiver();
            localBroadcastManager.registerReceiver(dataUpdateReceiver,intentFilter);
        }
    }

    /**
     * 从数据库中加载数据
     */
    private void loadDataFromDatabase(){
        LogUtils.i(TAG,"loadData");
        int monthTotalFlow=0;//本月总流量消耗
        int monthCallTime=0;//本月通话时长
        List<UserConsumePO> userConsumeList=DBUtils.getFirstToNowUserConsumeList(database);

        if(userConsumeList.isEmpty()){//空数据情景
            return;
        }

        //为折线图绑定数据
        List<Entry> chartEntryList=new ArrayList<>();//存储图表需要的数据对
        for(int i=0;i<userConsumeList.size();i++){//循环获取每一天的流量数据
            UserConsumePO userConsumePO=userConsumeList.get(i);
            chartEntryList.add(new Entry(i,userConsumePO.getAllFlow()));
            monthTotalFlow+=userConsumePO.getAllFlow();
            monthCallTime+=userConsumePO.getCallTime();
//            LogUtils.i(TAG,"第"+(i+1)+"天:"+totalByM+"M");
        }
        callTextView.setText(getString(R.string.call_text,monthCallTime+""));
        flowTextView.setText(getString(R.string.flow_text,monthTotalFlow+""));
        LineDataSet lineDataSet=new LineDataSet(chartEntryList,"device");
        initLineDataSet(lineDataSet);//初始化LineDataSet
        LineData lineData=new LineData(lineDataSet);
        lineChart.setData(lineData);//为线性图设置数据
        lineChart.invalidate();//刷新视图

        //为流量消耗饼状图绑定数据
        List<PieEntry> pieEntryList = new ArrayList<>();//饼状图需要的数据源
        List<FlowConsumePO> flowConsumePOList=DBUtils.getMonthAppConsumeList(database,DBUtils.TOP_APP_COUNT);//这里只获取Top-5
        int topAppFlow=0;//TOP-N个应用的流量总消耗（MB）
        for(FlowConsumePO flowConsumePO:flowConsumePOList){
            int flowMB=flowConsumePO.getFlowAmount()/1024;//MB为单位的流量消耗
            PieEntry pieEntry=new PieEntry(flowMB,flowConsumePO.getAppName());
            pieEntryList.add(pieEntry);
            topAppFlow+=flowMB;
        }
        int leaveAppLow=monthTotalFlow-topAppFlow;//剩余App总的流量消耗（MB）
        if(leaveAppLow>=1){//如果剩余App总的流量消耗大于1M就显示在饼状图中
            pieEntryList.add(new PieEntry(leaveAppLow,getString(R.string.statistic_app_other)));
        }

        PieDataSet pieSet = new PieDataSet(pieEntryList,"流量消耗分布图");
        PieData pieData = new PieData(pieSet);
        initPieDataSet(pieSet,pieData);
        pieChart.setData(pieData);
        pieChart.invalidate();

        //隐藏加载进度条（显示内容区域）
        if(dataLayout.getVisibility()!=View.VISIBLE){
            dataLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 接收数据更新广播
     */
    class DataUpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDataFromDatabase();
        }
    }

    //判断数据库中是否已经存储了消耗数据（决定接下来是直接加载数据还是接等待后台service更新数据）
    private boolean isConsumeDataExist(){
        Calendar calendar=DateUtils.getFirstMonthDayInCalendar();//获取本月第一天的时间数据
        int day=calendar.get(Calendar.DAY_OF_MONTH);//从1开始
        int month=calendar.get(Calendar.MONTH)+1;//从0开始
        int year=calendar.get(Calendar.YEAR);
        return DBUtils.isUserConsumeExist(database,day,month,year);//判断本月第一天的数据是否已存储
    }

    //初始化LineChar
    private void initLineChar(){
        lineChart.setScaleEnabled(false);//不允许缩放
        lineChart.getDescription().setEnabled(false);//不提供右下方的说明文字
        lineChart.getLegend().setEnabled(false);//不显示数据集的说明文字
        //配置横轴格式
        XAxis xAxis=lineChart.getXAxis();//横轴
        xAxis.setDrawGridLines(false);//不绘制纵向网格线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置横轴的绘制位置
        xAxis.setTextColor(Color.GRAY);//设置横轴标签的颜色
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new DayValueFormatter());//格式化要显示的横轴标签
        //配置纵轴格式
        lineChart.getAxisRight().setEnabled(false);//禁用右侧纵轴
        YAxis yAxis=lineChart.getAxisLeft();
        yAxis.setTextColor(Color.GRAY);//设置纵轴标签的颜色
        //配置高亮点的弹出窗
        DataFlowWindowView dataFlowWindowView=new DataFlowWindowView(
                getActivity(),R.layout.chart_flow_marker);
        lineChart.setMarker(dataFlowWindowView);
    }

    //初始化LineDataSet
    private void initLineDataSet(LineDataSet lineDataSet){
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);//设置绘制风格
        lineDataSet.setDrawCircles(true);//设置绘制每个值的圆点
        lineDataSet.setCircleRadius(4.5f);//设置圆点外圈半径
        lineDataSet.setCircleHoleRadius(2f);//设置圆点内圈半径
        lineDataSet.setCircleColor(Color.RED);//设置圆点外圈颜色
        lineDataSet.setCircleColorHole(Color.WHITE);//设置圆点内圈颜色
        lineDataSet.setColor(Color.RED);//设置线条的颜色
        lineDataSet.setDrawHighlightIndicators(false);//设置不绘制水平和垂直方向的指示器
        lineDataSet.setDrawFilled(true);//设置是否填充线条下方区域
        lineDataSet.setFillColor(Color.parseColor("#F36C60"));//设置填充色
        lineDataSet.setLineWidth(2f);//设置线条宽度
        lineDataSet.setDrawValues(false);//默认不绘制每个点的值
        lineDataSet.setValueFormatter(new ChartValueFormatter());//格式化每个点的值
    }

    //初始化PieChar
    private void initPieChar(){
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraLeftOffset(60f);
        final Legend legend=pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10f);
        legend.setDrawInside(false);
        legend.setXEntrySpace(3f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(14f);
        legend.setTextColor(Color.GRAY);
        pieChart.setDrawEntryLabels(false);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {//点击某个部分就更新中心的数据
                pieChart.setCenterText(Float.valueOf(e.getY()).intValue()+"M");
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }

    //初始化PieDataSet
    private void initPieDataSet(PieDataSet pieDataSet,PieData pieData){
        pieDataSet.setDrawIcons(false);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setIconsOffset(new MPPointF(0, 40));
        pieDataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getActivity().getResources().getColor(R.color.color_pink_one));
        colors.add(getActivity().getResources().getColor(R.color.color_pink_two));
        colors.add(getActivity().getResources().getColor(R.color.color_pink_three));
        colors.add(getActivity().getResources().getColor(R.color.color_purple_one));
        colors.add(getActivity().getResources().getColor(R.color.color_purple_two));
        colors.add(getActivity().getResources().getColor(R.color.color_purple_three));
        pieDataSet.setColors(colors);//设置颜色集合

        pieData.setValueFormatter(new ChartValueFormatter());
        pieData.setDrawValues(false);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);
    }

    @Override
    protected void initViews(View rootView) {
        lineChart= (LineChart) rootView.findViewById(R.id.line_chart_package);
        pieChart= (PieChart) rootView.findViewById(R.id.pie_chart_package);
        flowTextView= (TextView) rootView.findViewById(R.id.text_view_flow);
        callTextView= (TextView) rootView.findViewById(R.id.text_view_call);
        dataLayout= (ScrollView) rootView.findViewById(R.id.layout_statistics);
        progressBar= (ProgressBar) rootView.findViewById(R.id.progressbar_statistics);
        flowRankView= (LinearLayout) rootView.findViewById(R.id.layout_flow_rank);
        callRankView= (LinearLayout) rootView.findViewById(R.id.layout_call_rank);

        flowRankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FlowRankActivity.class));
            }
        });
        callRankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CallRankActivity.class));
            }
        });
    }

    /**
     * 检查并请求所需的权限
     */
    private void checkAndRequestPermissions(){
        permissionList.clear();//先清空可能有的旧数据
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_PHONE_STATE)
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CALL_LOG)
                !=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_CALL_LOG);
        }

        if(!permissionList.isEmpty()){ //批量获取权限
            requestPermissions(permissionList.toArray(new String[permissionList.size()]),
                    PERMISSION_REQUEST);//注意在Fragment中一定不要使用ActivityCompat获取权限，否则onRequestPermissionsResult方法无法回调
        }
        else{//此时所有运行时权限已授予
            checkAndRequestReadStatsPermission();//检测并请求读取应用使用量的权限
        }
    }

    //判断是否拥有所有权限
    private boolean hasPermissions(){
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            return false;
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            return false;
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_PHONE_STATE)
                !=PackageManager.PERMISSION_GRANTED){
            return false;
        }
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CALL_LOG)
                !=PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }

    //检测并请求读取应用使用量的权限
    private void checkAndRequestReadStatsPermission(){
        if(hasPermissionToReadNetworkStats()){//此时真正拥有了所有的权限
            loadData();//加载数据
            startBackService();//开启后台服务
        }
        else{//此时弹出对话框引导用户进入设置界面授权
            showSpecialPermissionTipDialog();
        }
    }

    /**
     * 检测是否拥有读取应用使用量的权限
     * 如果没有则引导用户打开特殊权限
     */
    private boolean hasPermissionToReadNetworkStats() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),getActivity().getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        return false;
    }

    // 打开[有权查看使用情况的应用]页面
    @TargetApi(Build.VERSION_CODES.M)
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent,ACTIVITY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==ACTIVITY_REQUEST){//此时从授予查看应用使用量权限的Activity返回
            if(hasPermissionToReadNetworkStats()){//此时真正拥有了所有权限
                loadData();//加载数据
                startBackService();//开启后台服务
            }
            else{//再次引导用户授予特殊权限
                showSpecialPermissionDeniedTipDialog();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //获取设备信息并进行存储
    private void initDeviceInfo(){
        String deviceType=DeviceUtils.getDeviceType();//设备厂商+版本
        String systemVersion=DeviceUtils.getSystemVersion();//系统版本
        String deviceFinger=DeviceUtils.getDeviceFinger(getActivity());//设备唯一识别码
        PreferenceUtils.putString(getActivity(),PreferenceUtils.KEY_DEVICE_TYPE,deviceType);
        PreferenceUtils.putString(getActivity(),PreferenceUtils.KEY_SYSTEM_VERSION,systemVersion);
        PreferenceUtils.putString(getActivity(),PreferenceUtils.KEY_DEVICE_FINGER,deviceFinger);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        LogUtils.i(TAG,"requestCode:"+requestCode);
        if(requestCode==PERMISSION_REQUEST){
            if(PermissionUtils.verifyPermissions(grantResults)){//判断是否所有权限都被授予
                checkAndRequestReadStatsPermission();//继续申请特殊权限（读取应用使用量）
            }else{
                showPermissionTipDialog();//在请求权限被拒绝后展示提示对话框
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    /**
     * 在请求权限被拒绝后展示提示对话框
     */
    private void showPermissionTipDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.statistic_permission_title)
                .setCancelable(false)
                .setMessage(R.string.statistic_permission_denied)
                .setPositiveButton(R.string.statistic_dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkAndRequestPermissions();//重新进入授权流程
                    }
                })
                .setNegativeButton(R.string.statistic_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * 在请求应用使用量这一特殊权限前为用户展示提示对话框
     */
    private void showSpecialPermissionTipDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_read_stats_title)
                .setCancelable(false)
                .setMessage(R.string.statistic_read_stats_info)
                .setPositiveButton(R.string.dialog_read_stats_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestReadNetworkStats();//引导用户授权
                    }
                })
                .setNegativeButton(R.string.dialog_read_stats_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * 在请求应用使用量这一特殊权限被拒绝后展示提示对话框
     */
    private void showSpecialPermissionDeniedTipDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.statistic_permission_title)
                .setCancelable(false)
                .setMessage(R.string.statistic_read_stats_denied)
                .setPositiveButton(R.string.statistic_dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestReadNetworkStats();//引导用户授权
                    }
                })
                .setNegativeButton(R.string.statistic_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

}

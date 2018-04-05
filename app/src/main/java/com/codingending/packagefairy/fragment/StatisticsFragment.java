package com.codingending.packagefairy.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.bean.StartEnd;
import com.codingending.packagefairy.chart.ChartValueFormatter;
import com.codingending.packagefairy.chart.DataFlowWindowView;
import com.codingending.packagefairy.chart.DayValueFormatter;
import com.codingending.packagefairy.utils.DateUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 统计页面
 * Created by CodingEnding on 2018/4/5.
 */

public class StatisticsFragment extends BaseFragment{
    public static final String TAG="StatisticsFragment";
    public static final int PERMISSION_REQUEST=2;//权限请求码

    private LineChart lineChart;//流量走势图
    private TextView flowTextView;//流量消耗
    private TextView callTextView;//已使用的通话时长

    /**
     * 返回实例
     */
    public static StatisticsFragment newInstance(){
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_statistics,container,false);
        initViews(view);
        initLineChar();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String requestPermission= Manifest.permission.READ_PHONE_STATE;
        if(ContextCompat.checkSelfPermission(getActivity(),requestPermission)
                !=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),requestPermission)){
                Toast.makeText(getActivity(),"读取设备状态权限是本应用的重要功能，如果不授予权限，程序是无法正常工作的~",
                        Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(getActivity(),new String[]{requestPermission},PERMISSION_REQUEST);
        }else{
            getNetworkStats();
        }
    }

    //获取流量使用情况
    @SuppressLint("MissingPermission")
    private void getNetworkStats(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//Android6.0及以上可用
            NetworkStatsManager networkStatsManager= (NetworkStatsManager) getActivity()
                    .getSystemService(Context.NETWORK_STATS_SERVICE);
            TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(TELEPHONY_SERVICE);
            String subscriberId = tm.getSubscriberId();
            try {
                getTotalFlowStats(networkStatsManager,subscriberId);//获取整体的流量消耗
                getEachDayFlowStats(networkStatsManager,subscriberId);//获取每天的流量消耗
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //获取整体的流量消耗
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private void getTotalFlowStats(NetworkStatsManager networkStatsManager,String subscriberId) throws RemoteException {
        NetworkStats.Bucket bucket=networkStatsManager.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,subscriberId, DateUtils.getFirstMonthDay(),
                System.currentTimeMillis());
        Float totalByM=Float.valueOf(getFlowByBucket(bucket));
        String flowText=getString(R.string.flow_text,String.valueOf(totalByM.intValue()));//格式化一下
        flowTextView.setText(flowText);//取整值部分
    }

    //获取每天的流量消耗
    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private void getEachDayFlowStats(NetworkStatsManager networkStatsManager,String subscriberId) throws RemoteException {
        List<StartEnd> timeList=DateUtils.getFirstMonthDayToNowTimeList();
        List<Entry> chartEntryList=new ArrayList<>();//存储图表需要的数据对
        for(int i=0;i<timeList.size();i++){//循环获取每一天的流量数据
            StartEnd startEnd=timeList.get(i);
            NetworkStats.Bucket bucket=networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,subscriberId,startEnd.start,
                    startEnd.end);
            float totalByM=getFlowByBucket(bucket);
            chartEntryList.add(new Entry(i,totalByM));
//            Log.i(TAG,"第"+(i+1)+"天:"+totalByM+"M");
        }
        LineDataSet lineDataSet=new LineDataSet(chartEntryList,"device");
        initLineDataSet(lineDataSet);//初始化LineDataSet
        LineData lineData=new LineData(lineDataSet);
        lineChart.setData(lineData);//为线性图设置数据
        lineChart.invalidate();//刷新视图
    }

    //通过NetworkStats.Bucket获取流量消耗（返回M为单位）
    @TargetApi(Build.VERSION_CODES.M)
    private float getFlowByBucket(NetworkStats.Bucket bucket){
        long rxBytes=bucket.getRxBytes();//接收的
        long txBytes=bucket.getTxBytes();//发送的
        long total=rxBytes+txBytes;
        return total/1024.0f/1024.0f;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(),"授权成功",Toast.LENGTH_SHORT).show();
                    getNetworkStats();//执行逻辑
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
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

    @Override
    protected void initViews(View rootView) {
        lineChart= (LineChart) rootView.findViewById(R.id.line_chart_package);
        flowTextView= (TextView) rootView.findViewById(R.id.text_view_flow);
        callTextView= (TextView) rootView.findViewById(R.id.text_view_call);
    }
}

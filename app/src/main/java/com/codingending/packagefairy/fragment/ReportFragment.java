package com.codingending.packagefairy.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codingending.packagefairy.MainActivity;
import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.PackageDetailActivity;
import com.codingending.packagefairy.activity.RecommendActivity;
import com.codingending.packagefairy.adapter.ReportRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.FlowConsume;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.entity.UserConsume;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.utils.DBUtils;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 推荐结果页面
 * Created by CodingEnding on 2018/4/5.
 */

public class ReportFragment extends BaseFragment{
    public static final String TAG="ReportFragment";

    private Button getRecommendBtn;
    private TextView titleView;
    private TextView tipsView;

    private RecyclerView reportRecyclerView;//推荐结果列表
    private List<PackageBean> dataList;//数据源
    private ReportRecyclerAdapter reportRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;//布局管理器

    private SQLiteDatabase database;//数据库实例

    /**
     * 返回实例
     */
    public static ReportFragment newInstance(){
        ReportFragment fragment= new ReportFragment();
        fragment.setArguments(new Bundle());//设置默认参数
        return fragment;
    }

    /**
     * 更新数据
     */
    public void update(Bundle bundle){
        LogUtils.i(TAG,"update");
        Bundle args=getArguments();
        args.clear();//先清空数据
        args.putAll(bundle);//添加新数据
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();//初始化 数据库
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_report,container,false);

        initViews(view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onDestroy() {
        if(database!=null){
            database.close();//关闭数据库
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()&&dataList!=null&&dataList.isEmpty()){//如果当前界面对用户可见且无数据就加载数据
            loadRecommendData();
        }
    }

    //这个方法会先于Fragment的生命周期方法调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&dataList!=null&&dataList.isEmpty()){//此时未点击推荐按钮但是当前界面已经处于可见状态（因此如果此时还没有加载数据就对数据进行加载）
            loadRecommendData();
        }
    }

    //初始化数据库
    private void initDatabase(){
        if(database!=null){
            database.close();
        }
        database=DBUtils.getDatabase(getActivity());
    }


    //加载套餐推荐列表数据（数据库或服务器）
    private void loadRecommendData(){
        LogUtils.i(TAG,"loadRecommendData");
        Bundle args=getArguments();//获取参数
        if(args.isEmpty()){//如果参数为空，说明并不需要从服务器获得推荐，直接从数据库加载上次的推荐结果
            getRecommendFromDatabase();
        }
        else{//否则直接向服务器请求数据
            getRecommendFromServer(args);
        }
    }

    //更新顶部提示信息
    private void updateTopTipArea(){
        if(dataList!=null&&!dataList.isEmpty()){
            tipsView.setText(getString(R.string.report_recommend_tips));
            getRecommendBtn.setText(getString(R.string.report_btn_new_recommend));
        }
    }

    //TODO
    //从数据库获得数据
    private void getRecommendFromDatabase(){
        updateTopTipArea(); //更新顶部提示信息
    }

    //从服务器端请求数据
    private void getRecommendFromServer(Bundle args){
        int flow=args.getInt(RecommendActivity.KEY_FLOW,0);//M
        int callTime=args.getInt(RecommendActivity.KEY_CALL_TIME,0);
        int provinceOutDay=args.getInt(RecommendActivity.KEY_PROVINCE_OUT,0);
        int recommendMode=args.getInt(RecommendActivity.KEY_RECOMMEND_MODE,0);
        List<String> operatorList=args.getStringArrayList(RecommendActivity.KEY_OPERATORS);

        String deviceType= PreferenceUtils.getString(getActivity(),PreferenceUtils.KEY_DEVICE_TYPE);
        String systemVersion= PreferenceUtils.getString(getActivity(),PreferenceUtils.KEY_SYSTEM_VERSION);
        String deviceFinger= PreferenceUtils.getString(getActivity(),PreferenceUtils.KEY_DEVICE_FINGER);

        LogUtils.i(TAG,"recommendMode："+recommendMode);

        //获取本月每种应用的流量消耗
        List<FlowConsume> flowConsumeList=null;
        if(recommendMode== UserConsume.RECOMMEND_MODE_ADVANCED){//在精准推荐的情况下才获取本月每种应用的流量消耗
            flowConsumeList=new ArrayList<>();
            List<FlowConsumePO> flowConsumePOList=DBUtils.getMonthAppConsumeList(database,DBUtils.RECOMMEND_APP_COUNT);
            for(FlowConsumePO temp:flowConsumePOList){//将FlowConsumePO转化为FlowConsume
                flowConsumeList.add(FlowConsume.build(temp));
            }
        }

        //构造用户消费数据实例
        UserConsume userConsume=new UserConsume(callTime,flow,provinceOutDay,deviceType,
                systemVersion,deviceFinger,operatorList,recommendMode,null);

        //发起网络请求
        Call<DataResponse<List<PackageBean>>> call=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getRecommendPackage(userConsume);
        call.enqueue(new Callback<DataResponse<List<PackageBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<PackageBean>>> call, Response<DataResponse<List<PackageBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<PackageBean>> body=response.body();
                    if(body!=null){
                        List<PackageBean> packageBeanList=body.getData();
                        if(packageBeanList!=null){
                            dataList.clear();
                            dataList.addAll(packageBeanList);
                            reportRecyclerAdapter.notifyDataSetChanged();//刷新列表
                        }
                        updateTopTipArea(); //更新顶部提示信息
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取套餐推荐结果失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<List<PackageBean>>> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e(TAG,"Retrofit onFailure!");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case MainActivity.RECOMMEND_ACTIVITY_CODE://将基础数据传递到ReportFragment
                if(resultCode== Activity.RESULT_OK){
                    Bundle bundle=new Bundle(data.getBundleExtra(RecommendActivity.KEY_DATA_BUNDLE));
                    update(bundle);//更新参数
                    LogUtils.i(TAG,"onActivityResult");
                }
                break;
            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;
        }
    }

    //初始化RecyclerView
    private void initRecyclerView(){
        dataList=new ArrayList<>();
        reportRecyclerAdapter=new ReportRecyclerAdapter(dataList,getActivity());
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reportRecyclerView.setLayoutManager(linearLayoutManager);
        reportRecyclerView.setAdapter(reportRecyclerAdapter);
        reportRecyclerView.setNestedScrollingEnabled(false);//解决嵌套滑动卡顿的问题

        reportRecyclerAdapter.setOnAdapterItemClickListener(new ReportRecyclerAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(PackageBean packageBean) {
                PackageDetailActivity.actionStart(getActivity(),packageBean);//跳转到详情界面
            }
            @Override
            public void onItemLongClick(PackageBean packageBean) {
            }
        });
    }

    @Override
    protected void initViews(View rootView) {
        getRecommendBtn= (Button) rootView.findViewById(R.id.btn_get_recommend);
        titleView= (TextView) rootView.findViewById(R.id.text_view_report_title);
        tipsView= (TextView) rootView.findViewById(R.id.text_view_report_tips);
        reportRecyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_report);

        getRecommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(),RecommendActivity.class),
                        MainActivity.RECOMMEND_ACTIVITY_CODE);//打开套餐推荐弹窗界面
            }
        });
    }
}

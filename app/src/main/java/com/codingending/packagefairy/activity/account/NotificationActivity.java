package com.codingending.packagefairy.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.adapter.NotificationRecyclerAdapter;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.NotificationBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 通知界面
 * @author CodingEnding
 */
public class NotificationActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    private LinearLayoutManager linearLayoutManager;
    private NotificationRecyclerAdapter adapter;

    private List<NotificationBean> dataList;
    private int page;//页码

    public static final int ANIMATION_DELAY=800;//下拉刷新布局停止动画前的延迟时间

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,NotificationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initData();
        initViews();
        initToolbar();
        initRecyclerView();
        initRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();//自动刷新
    }

    //初始化数据
    private void initData(){
        dataList=new ArrayList<>();
        page=1;
    }

    /**
     * 加载数据
     */
    private void loadData(){
        Call<DataResponse<List<NotificationBean>>> call= RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .getNotification(UserService.NOTIFICATION_COUNT,page);
        call.enqueue(new Callback<DataResponse<List<NotificationBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<NotificationBean>>> call, Response<DataResponse<List<NotificationBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<NotificationBean>> body=response.body();
                    if(body!=null){
                        List<NotificationBean> notificationBeanList=body.getData();
                        int start=dataList.size();//数据发生改变的起始位置
                        dataList.addAll(notificationBeanList);
                        if(page==1){//此时在执行刷新操作
                            smartRefreshLayout.finishRefresh(ANIMATION_DELAY);
                            adapter.notifyDataSetChanged();//刷新列表
                            updateLatestNotificationDate(notificationBeanList);//更新本地最近一次通知的时间
                        }
                        else{//此时在加载更多数据
                            if(notificationBeanList.isEmpty()){
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();//此时已没有更多数据
                            }else{
                                smartRefreshLayout.finishLoadMore(ANIMATION_DELAY);
                                adapter.notifyItemRangeInserted(start,notificationBeanList.size());//局部刷新
                            }
                        }
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<List<NotificationBean>>> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    /**
     * 更新本地最近一次通知的时间（[账户界面]会根据这个时间判断是否需要显示未读消息的小红点）
     */
    private void updateLatestNotificationDate(List<NotificationBean> dataList){
        if(dataList!=null&&!dataList.isEmpty()){
            long latestDate=dataList.get(0).getSendTime().getTime();//获取最近的通知发送时间
            PreferenceUtils.putLong(this,PreferenceUtils.KEY_LATEST_NOTIFICATION,latestDate);
        }
    }

    //初始化下拉刷新布局
    private void initRefreshLayout(){
        smartRefreshLayout.setRefreshHeader(new MaterialHeader(this));
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(dataList!=null){//清除数据
                    dataList.clear();
                }
                page=1;
                refreshLayout.setNoMoreData(false);//取消[无更多数据]状态
                loadData();
            }
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                loadData();
            }
        });
    }

    private void initRecyclerView(){
        adapter=new NotificationRecyclerAdapter(this,dataList);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initViews() {
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_notification);
        smartRefreshLayout= (SmartRefreshLayout) findViewById(R.id.layout_refresh);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

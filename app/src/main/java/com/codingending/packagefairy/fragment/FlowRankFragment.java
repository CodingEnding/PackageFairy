package com.codingending.packagefairy.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.adapter.FlowRankRecyclerAdapter;
import com.codingending.packagefairy.entity.AppBean;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.utils.AppUtils;
import com.codingending.packagefairy.utils.DBUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量排行列表对应的Fragment
 * Created by CodingEnding on 2018/4/25.
 */

public class FlowRankFragment extends BaseFragment{
    public static final String KEY_RANK_MODE="rank_mode";//流量排行模式
    public static final int RANK_TODAY=1;//代表今日流量排行
    public static final int RANK_WEEK=2;//代表本周流量排行
    public static final int RANK_MONTH=3;//代表本月流量排行

    public static final int ANIMATION_DELAY=600;//下拉刷新布局停止动画前的延迟时间

    private List<AppBean> dataList;
    private FlowRankRecyclerAdapter flowRankRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    private SQLiteDatabase database;

    /**
     * 获取一个Fragment实例
     * @param rankMode 需要展示的流量排行模式
     */
    public static FlowRankFragment newInstance(int rankMode){
        FlowRankFragment flowRankFragment=new FlowRankFragment();
        Bundle args=new Bundle();
        args.putInt(KEY_RANK_MODE,rankMode);
        flowRankFragment.setArguments(args);
        return flowRankFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatabase();//初始化数据库
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_flow_rank,container,false);
        initViews(view);
        initRecyclerView();
        initRefreshLayout();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()&&dataList!=null&&dataList.isEmpty()){
            smartRefreshLayout.autoRefresh();//在当前可见且还没有数据的情况下才自动加载数据
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&dataList!=null&&dataList.isEmpty()){
            smartRefreshLayout.autoRefresh();//在当前可见且还没有数据的情况下才自动加载数据
        }
    }

    @Override
    public void onDestroy() {
        if(database!=null){//关闭资源
            database.close();
        }
        super.onDestroy();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        final int rankMode=getArguments().getInt(KEY_RANK_MODE,RANK_TODAY);
        new Thread(new Runnable() { //在后台进程中加载数据（避免阻塞主进程）
            @Override
            public void run() {
                if(rankMode==RANK_TODAY){
                    convert(dataList,DBUtils.getDayAppConsumeList(database,DBUtils.RANK_APP_MAX_COUNT));
                }else if(rankMode==RANK_WEEK){
                    convert(dataList,DBUtils.getWeekAppConsumeList(database,DBUtils.RANK_APP_MAX_COUNT));
                }else{
                    convert(dataList,DBUtils.getMonthAppConsumeList(database,DBUtils.RANK_APP_MAX_COUNT));
                }
                //切换到主进程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        smartRefreshLayout.finishRefresh(ANIMATION_DELAY);//延迟后停止[刷新动画]
                        flowRankRecyclerAdapter.notifyDataSetChanged();//刷新数据
                    }
                });
            }
        }).start();
    }

    /**
     * 将FlowConsumePO批量转化为AppBean并加入数据源
     * 方便展示以及提升RecyclerView的效率
     */
    private void convert(List<AppBean> dataList,List<FlowConsumePO> flowConsumePOList){
        for(FlowConsumePO flowConsumePO:flowConsumePOList){
            Drawable appIcon= AppUtils.getAppIcon(getActivity(),flowConsumePO.getPackageName());//获取应用图标
            dataList.add(AppBean.build(flowConsumePO,appIcon));//快速构造
        }
    }

    //初始化数据库
    private void initDatabase(){
        if(database!=null){
            database.close();
        }
        database=DBUtils.getDatabase(getActivity());//获得数据库实例
    }

    //初始化下拉刷新布局
    private void initRefreshLayout(){
        smartRefreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        smartRefreshLayout.setEnableLoadMore(false);//禁用加载更多的功能（因为每次都直接全部加载了）
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();//加载数据
            }
        });
    }

    //初始化RecyclerView
    private void initRecyclerView(){
        dataList=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        flowRankRecyclerAdapter=new FlowRankRecyclerAdapter(getActivity(),dataList);
        recyclerView.setAdapter(flowRankRecyclerAdapter);
        recyclerView.setNestedScrollingEnabled(false);//防止嵌套卡顿
    }

    @Override
    protected void initViews(View rootView) {
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_flow_rank);
        smartRefreshLayout= (SmartRefreshLayout) rootView.findViewById(R.id.layout_refresh);
    }
}

package com.codingending.packagefairy.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;

import com.codingending.library.FairySearchView;
import com.codingending.packagefairy.R;
import com.codingending.packagefairy.adapter.SearchRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 搜索结果界面
 * @author CodingEnding
 */
public class SearchReportActivity extends BaseActivity {
    private static final String TAG="SearchReportActivity";
    private Toolbar toolbar;
    private FairySearchView fairySearchView;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private List<SimplePackageBean> dataList;
    private SearchRecyclerAdapter adapter;

    public static final String KEY_SEARCH_TEXT="SEARCH_TEXT";
    public static final int ANIMATION_DELAY=800;//下拉刷新布局停止动画前的延迟时间
    private String searchText;
    private int page;//当前页码

    /**
     * 启动Activity
     * @param searchText 搜索内容
     */
    public static void actionStart(Context context,String searchText){
        Intent intent=new Intent(context,SearchReportActivity.class);
        intent.putExtra(KEY_SEARCH_TEXT,searchText);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_report);
        initData();
        initViews();
        initToolbar();
        initRecyclerView();
        initRefreshLayout();
        smartRefreshLayout.autoRefresh();
    }

    private void initData(){
        page=1;
        dataList=new ArrayList<>();
        searchText=getIntent().getStringExtra(KEY_SEARCH_TEXT);
    }

    /**
     * 加载数据
     */
    private void loadData(){
        Call<DataResponse<List<SimplePackageBean>>> call= RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getPackageByKey(searchText,PackageService.SEARCH_PAGE_ITEM_COUNT,page);
        call.enqueue(new Callback<DataResponse<List<SimplePackageBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<SimplePackageBean>>> call, Response<DataResponse<List<SimplePackageBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<SimplePackageBean>> body=response.body();
                    if(body!=null){
                        List<SimplePackageBean> packageBeanList=body.getData();
                        int start=dataList.size();//数据发生改变的起始位置
                        dataList.addAll(packageBeanList);
                        if(page==1){//此时在执行刷新操作
                            smartRefreshLayout.finishRefresh(ANIMATION_DELAY);
                            adapter.notifyDataSetChanged();//刷新界面
                        }else{//此时在执行加载更多操作
                            if(packageBeanList.isEmpty()){
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();//此时已无更多数据
                            }else{
                                smartRefreshLayout.finishLoadMore(ANIMATION_DELAY);
                                adapter.notifyItemRangeInserted(start,packageBeanList.size());//局部刷新
                            }
                        }
                    }else{
                        LogUtils.w(TAG,"数据为空！");
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<List<SimplePackageBean>>> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    //初始化下拉刷新布局
    private void initRefreshLayout(){
        smartRefreshLayout.setRefreshHeader(new MaterialHeader(this));
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(dataList!=null){//清除旧数据
                    dataList.clear();
                }
                refreshLayout.setNoMoreData(false);//取消[无更多数据]状态
                page=1;//恢复页码为1
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
        adapter=new SearchRecyclerAdapter(dataList,this);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnAdapterItemClickListener(new SearchRecyclerAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(SimplePackageBean packageBean) {
                //进入套餐详情界面
                PackageDetailActivity.actionStart(SearchReportActivity.this,packageBean.getId());
            }
            @Override
            public void onItemLongClick(SimplePackageBean packageBean) {}
        });
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        fairySearchView= (FairySearchView) findViewById(R.id.search_view);
        smartRefreshLayout= (SmartRefreshLayout) findViewById(R.id.layout_refresh);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_search);

        fairySearchView.setSearchText(searchText);
        fairySearchView.setOnEnterClickListener(new FairySearchView.OnEnterClickListener() {
            @Override
            public void onEnterClick(String content) {//点击了虚拟键盘中的搜索按钮
                searchText=content;
                toggleSoftInput();//切换软键盘的状态（关闭）
                smartRefreshLayout.autoRefresh();//请求新的数据
            }
        });
        fairySearchView.setOnBackClickListener(new FairySearchView.OnBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 切换软键盘的状态
     */
    private void toggleSoftInput(){
        InputMethodManager inputMethodManager= (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null&&inputMethodManager.isActive()){
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }
}

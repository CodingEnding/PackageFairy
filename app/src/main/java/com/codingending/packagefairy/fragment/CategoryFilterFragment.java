package com.codingending.packagefairy.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.PackageDetailActivity;
import com.codingending.packagefairy.adapter.CategoryFilterRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 按照分类查看套餐
 * Created by CodingEnding on 2018/4/27.
 */

public class CategoryFilterFragment extends BaseFragment{
    private static final String TAG="CategoryFilterFragment";

    public static final String KEY_CATEGORY_NAME="category_name";
    public static final String KEY_CATEGORY_VALUE="category_value";
    public static final int ANIMATION_DELAY=800;//下拉刷新布局停止动画前的延迟时间

    private String categoryName;//分类名称
    private String categoryValue;//分类值
    private int page;//当前页码

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CategoryFilterRecyclerAdapter filterRecyclerAdapter;
    private List<SimplePackageBean> dataList;
    private SmartRefreshLayout smartRefreshLayout;

    /**
     * 获取Fragment实例
     * @param categoryName
     * @param categoryValue
     */
    public static CategoryFilterFragment newInstance(String categoryName,String categoryValue){
        CategoryFilterFragment categoryFilterFragment=new CategoryFilterFragment();
        Bundle args=new Bundle();
        args.putString(KEY_CATEGORY_NAME,categoryName);
        args.putString(KEY_CATEGORY_VALUE,categoryValue);
        categoryFilterFragment.setArguments(args);
        return  categoryFilterFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();//初始化数据
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_category_filter,container,false);
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

    //加载数据
    private void loadData(){
        Call<DataResponse<List<SimplePackageBean>>> call= RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getPackageByCategory(categoryName,categoryValue,PackageService.CATEGORY_PAGE_ITEM_COUNT,page);
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
                            filterRecyclerAdapter.notifyDataSetChanged();//刷新界面
                        }else{//此时在执行加载更多操作
                            if(packageBeanList.isEmpty()){
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();//此时已无更多数据
                            }else{
                                smartRefreshLayout.finishLoadMore(ANIMATION_DELAY);
                                filterRecyclerAdapter.notifyItemRangeInserted(start,packageBeanList.size());//局部刷新
                            }
                        }
                    }else{
                        LogUtils.w(TAG,"数据为空！");
                    }
                }else{
                    LogUtils.w(TAG,"请求失败");
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
        smartRefreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        //设置下拉刷新监听器
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if(dataList!=null){//清除旧数据
                    dataList.clear();
                }
                refreshLayout.setNoMoreData(false);//取消[无更多数据]状态
                page=1;//恢复页码为1
                loadData();
            }
        });
        //设置上拉加载监听器
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                loadData();
            }
        });
    }

    private void initRecyclerView(){
        dataList=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        filterRecyclerAdapter=new CategoryFilterRecyclerAdapter(dataList,getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(filterRecyclerAdapter);

        filterRecyclerAdapter.setOnAdapterItemClickListener(new CategoryFilterRecyclerAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(SimplePackageBean packageBean) {
                //进入套餐详情界面
                PackageDetailActivity.actionStart(getActivity(),packageBean.getId());
            }
            @Override
            public void onItemLongClick(SimplePackageBean packageBean) {
            }
        });
    }

    //初始化数据
    private void initData(){
        page=1;
        Bundle args=getArguments();
        if(args!=null){
            categoryName=args.getString(KEY_CATEGORY_NAME);
            categoryValue=args.getString(KEY_CATEGORY_VALUE);
        }
    }

    @Override
    protected void initViews(View rootView) {
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_filter);
        smartRefreshLayout= (SmartRefreshLayout) rootView.findViewById(R.id.layout_refresh);
    }
}

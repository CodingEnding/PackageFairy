package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.MultiCategoryActivity;
import com.codingending.packagefairy.activity.PackageDetailActivity;
import com.codingending.packagefairy.activity.SingleCategoryActivity;
import com.codingending.packagefairy.adapter.CategoryRecyclerAdapter;
import com.codingending.packagefairy.adapter.FindRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发现页面
 * Created by CodingEnding on 2018/4/5.
 */

public class FindFragment extends BaseFragment{
    private static final String TAG="FindFragment";

    private RecyclerView recyclerView;//热门套餐列表
    private List<SimplePackageBean> dataList;//数据源
    private FindRecyclerAdapter recyclerAdapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView categoryRecyclerView;//分类浏览列表
    private List<String> categoryList;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private GridLayoutManager categoryLayoutManager;

    private SmartRefreshLayout smartRefreshLayout;

    public static final int ANIMATION_DELAY=1000;//下拉刷新布局停止动画前的延迟时间
    private String[] categoryArray;//预置的分类数组
    private int page;//当前页码

    /**
     * 返回实例
     */
    public static FindFragment newInstance(){
        return new FindFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData();//初始化数据
        View view=inflater.inflate(R.layout.fragment_find,container,false);
        initViews(view);
        initRecyclerView();
        initRefreshLayout();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()&&dataList!=null&&dataList.isEmpty()){//当前界面对用户可见且还没有数据时才加载数据
            smartRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&dataList!=null&&dataList.isEmpty()){//当前界面已经处于可见状态（因此如果此时还没有加载数据就对数据进行加载）
            smartRefreshLayout.autoRefresh();
        }
    }

    //初始化数据
    private void initData(){
        page=1;//页码默认为1
        dataList=new ArrayList<>();
        categoryArray=getResources().getStringArray(R.array.package_category);//加载预置类别数组
    }

    /**
     * 加载数据
     */
    private void loadData(){
        Call<DataResponse<List<SimplePackageBean>>> call=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getHotPackage(PackageService.HOT_PAGE_ITEM_COUNT,page);

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
                            recyclerAdapter.notifyDataSetChanged();//刷新列表
                        }
                        else{//此时在加载更多数据
                            if(packageBeanList.isEmpty()){
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();//此时已没有更多数据
                            }else{
                                smartRefreshLayout.finishLoadMore(ANIMATION_DELAY);
                                recyclerAdapter.notifyItemRangeInserted(start,packageBeanList.size());//局部刷新
                            }
                        }
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取套餐推荐结果失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<List<SimplePackageBean>>> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e(TAG,"Retrofit onFailure!");
            }
        });
    }

    //初始化下拉刷新布局
    private void initRefreshLayout(){
        smartRefreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
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

    //初始化RecyclerView
    private void initRecyclerView(){
        //初始化热门套餐列表
        recyclerAdapter=new FindRecyclerAdapter(dataList,getActivity());
        gridLayoutManager=new GridLayoutManager(getActivity(),2);//网格式布局
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);//防止嵌套卡顿

        recyclerAdapter.setOnAdapterItemClickListener(new FindRecyclerAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(SimplePackageBean packageBean) {
                PackageDetailActivity.actionStart(getActivity(),packageBean.getId());//跳转到详情界面
            }
            @Override
            public void onItemLongClick(SimplePackageBean packageBean) {
            }
        });

        //初始化套餐分类列表
        categoryList=new ArrayList<>();
        categoryList.addAll(Arrays.asList(getResources()
                .getStringArray(R.array.package_category)));//读取并添加预置的套餐类别数据
        categoryRecyclerAdapter=new CategoryRecyclerAdapter(categoryList,getActivity());
        categoryLayoutManager =new GridLayoutManager(getActivity(),3);//两列
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryRecyclerView.setAdapter(categoryRecyclerAdapter);
        categoryRecyclerView.setNestedScrollingEnabled(false);//防止嵌套卡顿

        categoryRecyclerAdapter.setOnAdapterItemClickListener(new CategoryRecyclerAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(String category) {
                initCategoryLink(category);//初始化分类浏览Item的跳转逻辑
            }
            @Override
            public void onItemLongClick(String category) {
            }
        });
    }

    /**
     * 初始化分类浏览Item的跳转逻辑
     */
    private void initCategoryLink(String category){
        if(category.equals(categoryArray[0])){
            MultiCategoryActivity.actionStart(getActivity(),
                    PackageService.CATEGORY_NAME_OPERATOR,categoryArray[0]);
        }
        else if(category.equals(categoryArray[1])){
            MultiCategoryActivity.actionStart(getActivity(),
                    PackageService.CATEGORY_NAME_PARTNER,categoryArray[1]);
        }
        else if(category.equals(categoryArray[2])){//日租卡
            SingleCategoryActivity.actionStart(getActivity(),PackageService.CATEGORY_NAME_SINGLE,
                    PackageService.CATEGORY_DAY_RENT,categoryArray[2]);
        }
        else if(category.equals(categoryArray[3])){//免流特权
            SingleCategoryActivity.actionStart(getActivity(),PackageService.CATEGORY_NAME_SINGLE,
                    PackageService.CATEGORY_FREE_FLOW,categoryArray[3]);
        }
        else if(category.equals(categoryArray[4])){
            SingleCategoryActivity.actionStart(getActivity(),PackageService.CATEGORY_NAME_SINGLE,
                    PackageService.CATEGORY_INFINITE_FLOW,categoryArray[4]);
        }
        else if(category.equals(categoryArray[5])){
            SingleCategoryActivity.actionStart(getActivity(),PackageService.CATEGORY_NAME_SINGLE,
                    PackageService.CATEGORY_ALL,categoryArray[5]);
        }
    }

    @Override
    protected void initViews(View rootView) {
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_find);
        categoryRecyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_category);
        smartRefreshLayout= (SmartRefreshLayout) rootView.findViewById(R.id.layout_refresh);
    }

}

package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.PackageDetailActivity;
import com.codingending.packagefairy.adapter.CategoryRecyclerAdapter;
import com.codingending.packagefairy.adapter.FindRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 发现页面
 * Created by CodingEnding on 2018/4/5.
 */

public class FindFragment extends BaseFragment{
    public static final String TAG="FindFragment";

    private RecyclerView recyclerView;//热门套餐列表
    private List<SimplePackageBean> dataList;//数据源
    private FindRecyclerAdapter recyclerAdapter;
    private GridLayoutManager gridLayoutManager;
//    private LinearLayoutManager linearLayoutManager;

    private RecyclerView categoryRecyclerView;//分类浏览列表
    private List<String> categoryList;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private GridLayoutManager categoryLayoutManager;

    /**
     * 返回实例
     */
    public static FindFragment newInstance(){
        return new FindFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_find,container,false);
        initViews(view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()&&dataList!=null&&dataList.isEmpty()){//当前界面对用户可见且还没有数据时才加载数据
            loadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(dataList!=null&&dataList.isEmpty()){//当前界面已经处于可见状态（因此如果此时还没有加载数据就对数据进行加载）
            loadData();
        }
    }

    //加载数据
    private void loadData(){
        Call<DataResponse<List<SimplePackageBean>>> call=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getHotPackage();
        call.enqueue(new Callback<DataResponse<List<SimplePackageBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<SimplePackageBean>>> call, Response<DataResponse<List<SimplePackageBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<SimplePackageBean>> body=response.body();
                    if(body!=null){
                        List<SimplePackageBean> packageBeanList=body.getData();
                        if(packageBeanList!=null){
                            dataList.clear();
                            dataList.addAll(packageBeanList);
                            recyclerAdapter.notifyDataSetChanged();//刷新列表
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

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView(){
        //初始化热门套餐列表
        dataList=new ArrayList<>();
        recyclerAdapter=new FindRecyclerAdapter(dataList,getActivity());
//        linearLayoutManager=new LinearLayoutManager(getActivity());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);//线性布局
        gridLayoutManager=new GridLayoutManager(getActivity(),2);//网格式布局
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(recyclerAdapter);
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
                //TODO 增加跳转代码
            }
            @Override
            public void onItemLongClick(String category) {
            }
        });
    }

    @Override
    protected void initViews(View rootView) {
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_find);
        categoryRecyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_category);
    }
}

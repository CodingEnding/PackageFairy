package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.adapter.FindRecyclerAdapter;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import java.util.ArrayList;
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

    private RecyclerView recyclerView;
    private List<SimplePackageBean> dataList;//数据源
    private FindRecyclerAdapter recyclerAdapter;
    private LinearLayoutManager linearLayoutManager;

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
        if(getUserVisibleHint()){//当前界面对用户可见才加载数据
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
                        List<SimplePackageBean> packageBeanList=response.body().getData();
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

    //初始化RecyclerView
    private void initRecyclerView(){
        dataList=new ArrayList<>();
        recyclerAdapter=new FindRecyclerAdapter(dataList,getActivity());
        linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setNestedScrollingEnabled(false);//防止嵌套卡顿
    }

    @Override
    protected void initViews(View rootView) {
        recyclerView= (RecyclerView) rootView.findViewById(R.id.recycler_view_find);
    }
}

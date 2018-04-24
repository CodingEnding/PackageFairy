package com.codingending.packagefairy.activity.account;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.adapter.DeviceRecyclerAdapter;
import com.codingending.packagefairy.api.DeviceService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.DeviceBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 设备信息
 * @author CodingEnding
 */
public class DeviceActivity extends BaseActivity {
    private static final String TAG="DeviceActivity";

    private Toolbar toolbar;
    private RecyclerView deviceRecyclerView;
    private LinearLayoutManager deviceLayoutManager;
    private DeviceRecyclerAdapter deviceRecyclerAdapter;
    private List<DeviceBean> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initViews();
        initToolbar();
        initRecyclerView();
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        String email= PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_EMAIL);
        String sessionToken=PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_SESSION_TOKEN);
        Call<DataResponse<List<DeviceBean>>> call= RetrofitUtils.getRetrofit()
                .create(DeviceService.class)
                .getDeviceList(email,sessionToken);
        call.enqueue(new Callback<DataResponse<List<DeviceBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<DeviceBean>>> call, Response<DataResponse<List<DeviceBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<DeviceBean>> body=response.body();
                    if(body!=null&&body.isSucceed()){
                        List<DeviceBean> deviceBeanList=body.getData();
                        if(deviceBeanList!=null){
                            dataList.clear();
                            dataList.addAll(deviceBeanList);
                            deviceRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取设备列表失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<List<DeviceBean>>> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure...");
            }
        });
    }

    //初始化RecyclerView
    private void initRecyclerView(){
        dataList=new ArrayList<>();
        deviceLayoutManager=new LinearLayoutManager(this);
        deviceLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deviceRecyclerView.setLayoutManager(deviceLayoutManager);
        deviceRecyclerAdapter=new DeviceRecyclerAdapter(dataList,this);
        deviceRecyclerView.setAdapter(deviceRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        }
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        deviceRecyclerView= (RecyclerView) findViewById(R.id.recycler_view_device);
    }

}

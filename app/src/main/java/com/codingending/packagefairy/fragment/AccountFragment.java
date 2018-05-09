package com.codingending.packagefairy.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.account.AboutActivity;
import com.codingending.packagefairy.activity.account.BackupActivity;
import com.codingending.packagefairy.activity.account.DeviceActivity;
import com.codingending.packagefairy.activity.account.FeedbackActivity;
import com.codingending.packagefairy.activity.account.LibraryActivity;
import com.codingending.packagefairy.activity.account.LoginActivity;
import com.codingending.packagefairy.activity.account.NotificationActivity;
import com.codingending.packagefairy.activity.account.UserActivity;
import com.codingending.packagefairy.api.AppService;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.NotificationBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import java.sql.Timestamp;
import java.util.List;

import ezy.boost.update.UpdateManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 账户页面
 * Created by CodingEnding on 2018/4/5.
 */

public class AccountFragment extends BaseFragment{
    private static final String TAG="AccountFragment";
    private TextView userView;
    private TextView backupView;
    private TextView deviceView;
    private TextView checkUpdateView;
    private TextView libraryView;
    private TextView aboutView;
    private TextView notificationView;
    private TextView feedbackView;
    private TextView logoutView;
    private FrameLayout notificationLayout;


    /**
     * 返回实例
     */
    public static AccountFragment newInstance(){
        return new AccountFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account,container,false);
        initUpdateManager();
        initViews(view);
        updateUserView();//为了优化用户体验提前加载账号数据（这是对首选项的查询所以不会有太大的性能代价）
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){//如果当前界面可见才更新数据
            updateUserView();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            updateUserView();//更新数据
        }
    }

    //初始化版本更新管理工具
    private void initUpdateManager(){
        UpdateManager.setUrl(AppService.URL_CHECK_UPDATE,AppService.CHANNEL_DEFAULT);
        UpdateManager.setWifiOnly(false);//在WIFI和数据流量的状态下都可以检查更新
    }

    /**
     * 判断是否存在未读的系统通知
     */
    private void checkNewNotification(){
        Call<DataResponse<List<NotificationBean>>> call= RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .getNotification(1,1);//仅仅获取一条最新的通知
        call.enqueue(new Callback<DataResponse<List<NotificationBean>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<NotificationBean>>> call, Response<DataResponse<List<NotificationBean>>> response) {
                if(response.isSuccessful()){
                    DataResponse<List<NotificationBean>> body=response.body();
                    if(body!=null){
                        List<NotificationBean> notificationBeanList=body.getData();
                        if(!notificationBeanList.isEmpty()){//判断是否显示新消息小红点
                            showOrHideNotificationHotDot(notificationBeanList.get(0));
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
     * 显示/隐藏系统通知的小红点标识
     * @param notificationBean 服务器最新的一条通知
     */
    private void showOrHideNotificationHotDot(NotificationBean notificationBean){
        long serverNewTime=notificationBean.getSendTime().getTime();
        long localNewTime=PreferenceUtils.getLong(getActivity(),PreferenceUtils.KEY_LATEST_NOTIFICATION);
        if(serverNewTime>localNewTime){//此时存在新通知
            notificationView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bell,0,R.drawable.ic_dot,0);
        }else{
            notificationView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bell,0,0,0);
        }
        LogUtils.i(TAG,"本地最近通知时间："+new Timestamp(localNewTime).toString());
    }

    @Override
    protected void initViews(View rootView) {
        userView= (TextView) rootView.findViewById(R.id.text_view_user);
        backupView= (TextView) rootView.findViewById(R.id.text_view_backup);
        deviceView= (TextView) rootView.findViewById(R.id.text_view_device);
        checkUpdateView= (TextView) rootView.findViewById(R.id.text_view_check_update);
        libraryView= (TextView) rootView.findViewById(R.id.text_view_library);
        aboutView= (TextView) rootView.findViewById(R.id.text_view_about);
        logoutView= (TextView) rootView.findViewById(R.id.text_view_logout);
        notificationView= (TextView) rootView.findViewById(R.id.text_view_notification);
        notificationLayout= (FrameLayout) rootView.findViewById(R.id.layout_notification);
        feedbackView= (TextView) rootView.findViewById(R.id.text_view_feedback);

        userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin()){
                    UserActivity.actionStart(getActivity());//跳转到用户信息界面
                }else{
                    LoginActivity.actionStart(getActivity());//跳转到登录/注册界面
                }
            }
        });
        backupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),BackupActivity.class));
            }
        });
        deviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DeviceActivity.class));
            }
        });
        checkUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),R.string.update_checking_tips,Toast.LENGTH_SHORT).show();
                UpdateManager.checkManual(getActivity());//检查更新
            }
        });
        libraryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LibraryActivity.class));
            }
        });
        aboutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AboutActivity.class));
            }
        });
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationActivity.actionStart(getActivity());
            }
        });
        feedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin()){//在登录状态下才能使用反馈功能
                    FeedbackActivity.actionStart(getActivity());
                }else{
                    showLongToast(R.string.feedback_need_login);
                }
            }
        });
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    //提示用户是否要退出登录
    private void showLogoutDialog(){
       new AlertDialog.Builder(getActivity())
                .setTitle(R.string.logout_tips)
                .setCancelable(false)
                .setPositiveButton(R.string.logout_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        updateUserView();
                        Toast.makeText(getActivity(),R.string.logout_succeed,Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
               .show();
    }

    //退出登录（清除数据）
    private void logout(){
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_USER_ID);
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_USER_NAME);
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_USER_EMAIL);
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_EMAIL_VERIFIED);
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_USER_SESSION_TOKEN);
        PreferenceUtils.remove(getActivity(),PreferenceUtils.KEY_USER_PHONE);
    }

    //更新用户名的状态（如果登录了就显示用户名、或者提示登录/注册）
    private void updateUserView(){
        if(isLogin()){
            userView.setText(PreferenceUtils.getString(getActivity(),PreferenceUtils.KEY_USER_NAME));
            logoutView.setVisibility(View.VISIBLE);
        }else{
            userView.setText(getString(R.string.account_item_login));
            logoutView.setVisibility(View.GONE);
        }
        checkNewNotification();//判断是否存在未读的系统通知
    }

    //判断用户是否已经登录
    private boolean isLogin(){
        //如果已经存在用户数据证明已登录
        return !TextUtils.isEmpty(PreferenceUtils.getString(getActivity(),
                PreferenceUtils.KEY_USER_NAME));
    }

}

package com.codingending.packagefairy.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.LoginActivity;
import com.codingending.packagefairy.activity.UserActivity;
import com.codingending.packagefairy.utils.PreferenceUtils;

/**
 * 账户页面
 * Created by CodingEnding on 2018/4/5.
 */

public class AccountFragment extends BaseFragment{
    public static final String TAG="AccountFragment";
    private TextView userView;
    private TextView backupView;
    private TextView deviceView;
    private TextView checkUpdateView;
    private TextView libraryView;
    private TextView aboutView;
    private TextView logoutView;

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
        initViews(view);
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

    @Override
    protected void initViews(View rootView) {
        userView= (TextView) rootView.findViewById(R.id.text_view_user);
        backupView= (TextView) rootView.findViewById(R.id.text_view_backup);
        deviceView= (TextView) rootView.findViewById(R.id.text_view_device);
        checkUpdateView= (TextView) rootView.findViewById(R.id.text_view_check_update);
        libraryView= (TextView) rootView.findViewById(R.id.text_view_library);
        aboutView= (TextView) rootView.findViewById(R.id.text_view_about);
        logoutView= (TextView) rootView.findViewById(R.id.text_view_logout);

        userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin()){
                    startActivity(new Intent(getActivity(),UserActivity.class));//跳转到用户信息界面
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));//跳转到登录/注册界面
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
    }

    //判断用户是否已经登录
    private boolean isLogin(){
        //如果已经存在用户数据证明已登录
        return !TextUtils.isEmpty(PreferenceUtils.getString(getActivity(),
                PreferenceUtils.KEY_USER_NAME));
    }

}

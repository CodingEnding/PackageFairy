package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codingending.packagefairy.utils.LogUtils;

/**
 * Fragment的基类
 * Created by CodingEnding on 2018/4/5.
 */

public abstract class BaseFragment extends Fragment{
    /**
     * 初始化View
     */
    protected abstract void initViews(View rootView);

    /**
     * 显示Toast（默认显示时间为short）
     * @param msg 内容
     */
    protected void showToast(@StringRes int msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast（显示时间为Long）
     * @param msg 内容
     */
    protected void showLongToast(@StringRes int msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.i(getClass().getSimpleName(),"onHiddenChanged->"+"hidden:"+hidden);
    }

    //判断当前Fragment是否对用户可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(getClass().getSimpleName(),"setUserVisibleHint->"+"isVisibleToUser:"+isVisibleToUser);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(getClass().getSimpleName(),"onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.i(getClass().getSimpleName(),"onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i(getClass().getSimpleName(),"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(getClass().getSimpleName(),"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i(getClass().getSimpleName(),"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i(getClass().getSimpleName(),"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i(getClass().getSimpleName(),"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(getClass().getSimpleName(),"onDestroy");
    }
}

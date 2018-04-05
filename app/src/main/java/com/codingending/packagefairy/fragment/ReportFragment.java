package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;

/**
 * 推荐结果页面
 * Created by CodingEnding on 2018/4/5.
 */

public class ReportFragment extends BaseFragment{

    /**
     * 返回实例
     */
    public static ReportFragment newInstance(){
        return new ReportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_report,container,false);
        return view;
    }

    @Override
    protected void initViews(View rootView) {

    }
}

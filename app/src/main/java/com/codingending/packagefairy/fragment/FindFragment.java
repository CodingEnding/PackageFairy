package com.codingending.packagefairy.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codingending.packagefairy.R;

/**
 * 发现页面
 * Created by CodingEnding on 2018/4/5.
 */

public class FindFragment extends BaseFragment{

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
        return view;
    }

    @Override
    protected void initViews(View rootView) {

    }
}

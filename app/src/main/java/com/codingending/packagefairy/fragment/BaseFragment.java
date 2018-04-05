package com.codingending.packagefairy.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Fragment的基类
 * Created by CodingEnding on 2018/4/5.
 */

public abstract class BaseFragment extends Fragment{
    /**
     * 初始化View
     */
    protected abstract void initViews(View rootView);
}

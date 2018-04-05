package com.codingending.packagefairy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codingending.packagefairy.fragment.AccountFragment;
import com.codingending.packagefairy.fragment.FindFragment;
import com.codingending.packagefairy.fragment.ReportFragment;
import com.codingending.packagefairy.fragment.StatisticsFragment;

/**
 * 首页导航ViewPager的适配器
 * Created by CodingEnding on 2018/4/5.
 */

public class NavigationFragmentAdapter extends FragmentPagerAdapter{
    public static final String TAG="NavigationFragmentAdapter";
    public static final int PAGE_COUNT=4;//首页导航页数为4

    public NavigationFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=StatisticsFragment.newInstance();
                break;
            case 1:
                fragment=FindFragment.newInstance();
                break;
            case 2:
                fragment=ReportFragment.newInstance();
                break;
            case 3:
                fragment= AccountFragment.newInstance();
                break;
            default:break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}

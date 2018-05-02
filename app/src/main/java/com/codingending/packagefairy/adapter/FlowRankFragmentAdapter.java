package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.fragment.FlowRankFragment;

/**
 * Created by CodingEnding on 2018/4/25.
 */

public class FlowRankFragmentAdapter extends FragmentStatePagerAdapter{
    private static final String TAG="FlowRankFragmentAdapter";
    private static final int PAGE_COUNT=3;//流量排行页数为3

    private Context context;

    public FlowRankFragmentAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tabName="";
        switch (position){
            case 0:
                tabName=context.getString(R.string.tab_today);
                break;
            case 1:
                tabName=context.getString(R.string.tab_week);
                break;
            case 2:
                tabName=context.getString(R.string.tab_month);
                break;
            default:break;
        }
        return tabName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=FlowRankFragment.newInstance(FlowRankFragment.RANK_TODAY);
                break;
            case 1:
                fragment=FlowRankFragment.newInstance(FlowRankFragment.RANK_WEEK);
                break;
            case 2:
                fragment=FlowRankFragment.newInstance(FlowRankFragment.RANK_MONTH);
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

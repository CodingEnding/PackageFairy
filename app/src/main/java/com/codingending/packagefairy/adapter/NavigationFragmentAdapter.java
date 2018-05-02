package com.codingending.packagefairy.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.codingending.packagefairy.fragment.AccountFragment;
import com.codingending.packagefairy.fragment.FindFragment;
import com.codingending.packagefairy.fragment.ReportFragment;
import com.codingending.packagefairy.fragment.StatisticsFragment;
import com.codingending.packagefairy.utils.LogUtils;

import java.util.List;

/**
 * 首页导航ViewPager的适配器
 * Created by CodingEnding on 2018/4/5.
 */

public class NavigationFragmentAdapter extends FragmentPagerAdapter{
    private static final String TAG="NavigationAdapter";
    private static final int PAGE_COUNT=4;//首页导航页数为4

    private FragmentManager fragmentManager;
    private SparseArray<String> tagMap=new SparseArray<>();//存储每个Fragment的标签，刷新页面的依据

    public NavigationFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager=fm;
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment= (Fragment) super.instantiateItem(container, position);
        String tag=fragment.getTag();
        tagMap.put(position,tag);//存储每个Fragment的tag（这个TAG是系统默认给的）
        LogUtils.i(TAG,"position"+position+" "+tag);
        return fragment;
    }

    //获取指定位置的Fragment
    public Fragment getFragmentByPosition(int position) {
        return fragmentManager.findFragmentByTag(tagMap.get(position));
    }

    //刷新指定位置的Fragment
    public void notifyFragmentByPosition(int position, Bundle bundle) {
        if(position==2){//更新推荐结果页面
            ReportFragment reportFragment= (ReportFragment) getFragmentByPosition(position);
            reportFragment.update(bundle);
        }
//        tagMap.removeAt(position);
//        notifyDataSetChanged();
    }

//    @Override
//    public int getItemPosition(Object object) {
//        Fragment fragment= (Fragment) object;
//        //如果Item对应的Tag存在，则不进行刷新
//        if (tagMap.indexOfValue(fragment.getTag()) > -1) {
//            return super.getItemPosition(object);
//        }
//        return POSITION_NONE;//这会导致相应Fragment的onCreateView再次调用
//    }

}

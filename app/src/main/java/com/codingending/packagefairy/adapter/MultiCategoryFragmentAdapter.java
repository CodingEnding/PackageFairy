package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codingending.packagefairy.fragment.CategoryFilterFragment;
import com.codingending.packagefairy.utils.FormatUtils;

import java.util.List;

/**
 * 查看多个分类套餐界面中ViewPager的适配器
 * Created by CodingEnding on 2018/4/27.
 */

public class MultiCategoryFragmentAdapter extends FragmentPagerAdapter{
    private String categoryName;//分类的名称
    private List<String> dataList;//数据源（存储分类的值）

    public MultiCategoryFragmentAdapter(FragmentManager fm,String categoryName,
                                        List<String> dataList) {
        super(fm);
        this.categoryName=categoryName;
        this.dataList=dataList;
    }

    @Override
    public Fragment getItem(int position) {
        String categoryValue=dataList.get(position);//当前分类的值
        return CategoryFilterFragment.newInstance(categoryName,categoryValue);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //如果分类名称包含空格就默认只使用空格后的后缀（如“阿里巴巴 钉钉”就返回“钉钉”）
        String categoryValue=dataList.get(position);
        return FormatUtils.getSpacePostfix(categoryValue);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
}

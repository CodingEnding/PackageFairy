package com.codingending.packagefairy.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.adapter.MultiCategoryFragmentAdapter;
import com.codingending.packagefairy.api.PackageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 单类别的套餐列表
 * 如：运营商、合作方
 * @author CodingEnding
 */
public class MultiCategoryActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MultiCategoryFragmentAdapter adapter;
    private List<String> dataList;//分类值数据源

    public static final String KEY_CATEGORY_NAME="category_name";
    public static final String KEY_CATEGORY_TITLE="category_title";
    public static final int PAGE_OFFSET=2;//ViewPager屏幕外最大页面数

    private String categoryName;//分类名
    private String categoryTitle;//分类标题（用于显示在Toolbar）


    /**
     * 启动当前Activity
     * @param categoryName 分类名
     * @param categoryTitle 分类标题
     */
    public static void actionStart(Context context,String categoryName,String categoryTitle){
        Intent intent=new Intent(context,MultiCategoryActivity.class);
        intent.putExtra(KEY_CATEGORY_NAME,categoryName);
        intent.putExtra(KEY_CATEGORY_TITLE,categoryTitle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_category);
        initData();
        initViews();
        initToolbar();
        initViewPager();
        initTabLayout();
    }

    //初始化数据
    private void initData(){
        categoryName=getIntent().getStringExtra(KEY_CATEGORY_NAME);
        categoryTitle=getIntent().getStringExtra(KEY_CATEGORY_TITLE);
        dataList=new ArrayList<>();
        if(categoryName.equals(PackageService.CATEGORY_NAME_OPERATOR)){//根据分类名获取对应的分类值
            dataList.addAll(Arrays.asList(getResources().getStringArray(R.array.package_operator)));
        }else{
            dataList.addAll(Arrays.asList(getResources().getStringArray(R.array.package_partner)));
        }
    }

    private void initViewPager(){
       adapter=new MultiCategoryFragmentAdapter(getSupportFragmentManager(),categoryName,dataList);
       viewPager.setAdapter(adapter);
       viewPager.setOffscreenPageLimit(PAGE_OFFSET);//保证至少会有3个页面常驻（避免用户等待）
       viewPager.setCurrentItem(0);
    }

    //绑定ViewPager和TabLayout
    private void initTabLayout(){
        if(categoryName.equals(PackageService.CATEGORY_NAME_OPERATOR)){//根据类别名设置TabLayout的滑动模式
            tabLayout.setTabMode(TabLayout.MODE_FIXED);//不允许左右滑动
        }else{
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//允许左右滑动
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initViews() {
        tabLayout= (TabLayout) findViewById(R.id.tablayout_multi_category);
        viewPager= (ViewPager) findViewById(R.id.viewpager_multi_category);
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        setTitle(categoryTitle);//修改标题栏
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

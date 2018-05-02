package com.codingending.packagefairy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.fragment.CategoryFilterFragment;

/**
 * 单类别的套餐列表
 * 如：日租卡、免流特权、无限流量、全部
 * @author CodingEnding
 */
public class SingleCategoryActivity extends BaseActivity {
    private static final String TAG="SingleCategoryActivity";

    public static final String KEY_CATEGORY_NAME="category_name";
    public static final String KEY_CATEGORY_VALUE="category_value";
    public static final String KEY_CATEGORY_TITLE="category_title";
    private String categoryName;//分类名称
    private String categoryValue;//分类值
    private String categoryTitle;//用于显示的分类标题

    /**
     * 启动本Activity
     * @param categoryName 类别名
     * @param categoryValue 类别值
     */
    public static void actionStart(Context context,String categoryName,String categoryValue,String categoryTitle){
        Intent intent=new Intent(context,SingleCategoryActivity.class);
        intent.putExtra(KEY_CATEGORY_NAME,categoryName);
        intent.putExtra(KEY_CATEGORY_VALUE,categoryValue);
        intent.putExtra(KEY_CATEGORY_TITLE,categoryTitle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category);
        initData();
        initViews();
        initToolbar();
    }

    //获取出入的数据
    private void initData(){
        categoryName=getIntent().getStringExtra(KEY_CATEGORY_NAME);
        categoryValue=getIntent().getStringExtra(KEY_CATEGORY_VALUE);
        categoryTitle=getIntent().getStringExtra(KEY_CATEGORY_TITLE);
    }

    @Override
    protected void initViews() {
        CategoryFilterFragment fragment=CategoryFilterFragment.newInstance(categoryName,categoryValue);
        FragmentManager fragmentManager=getSupportFragmentManager();
        //将Fragment添加到容器中
        fragmentManager.beginTransaction()
                .add(R.id.container,fragment)
                .commit();
    }

    @Override
    protected void initToolbar() {
        super.initToolbar();
        setTitle(categoryTitle);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

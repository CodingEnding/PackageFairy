package com.codingending.packagefairy.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.adapter.FlowRankFragmentAdapter;
import com.codingending.packagefairy.adapter.FlowRankRecyclerAdapter;

/**
 * 数据流量排行
 * @author CodingEnding
 */
public class FlowRankActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FlowRankFragmentAdapter flowRankFragmentAdapter;

    public static final int PAGE_OFFSET=2;//ViewPager屏幕外最大页面数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_rank);
        initViews();
        initToolbar();
        initViewPager();
        initTabLayout();
    }

    private void initTabLayout(){
        tabLayout.setupWithViewPager(viewPager);//绑定ViewPager和TabLayout
    }

    private void initViewPager(){
        flowRankFragmentAdapter=new FlowRankFragmentAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(flowRankFragmentAdapter);
        viewPager.setOffscreenPageLimit(PAGE_OFFSET);//保证至少会有3个页面常驻（避免用户等待）
        viewPager.setCurrentItem(0);
    }

    @Override
    protected void initViews() {
        tabLayout= (TabLayout) findViewById(R.id.tablayout_flow_rank);
        viewPager= (ViewPager) findViewById(R.id.viewpager_flow_rank);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

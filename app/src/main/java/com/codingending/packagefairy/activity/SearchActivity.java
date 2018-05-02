package com.codingending.packagefairy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.codingending.library.FairySearchView;
import com.codingending.packagefairy.R;

/**
 * 搜索主界面
 * @author CodingEnding
 */
public class SearchActivity extends BaseActivity {
    private Toolbar toolbar;
    private FairySearchView fairySearchView;

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
        initToolbar();
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        fairySearchView= (FairySearchView) findViewById(R.id.search_view);

        fairySearchView.setOnEnterClickListener(new FairySearchView.OnEnterClickListener() {
            @Override
            public void onEnterClick(String content) {
                SearchReportActivity.actionStart(SearchActivity.this,content);
            }
        });
        fairySearchView.setOnCancelClickListener(new FairySearchView.OnCancelClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    @Override
    protected void initToolbar() {//在本Activity中手动初始化Toolbar
        setSupportActionBar(toolbar);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;//在这个Activity中用不到这个方法
    }

}

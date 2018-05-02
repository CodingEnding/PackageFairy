package com.codingending.packagefairy.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.utils.LogUtils;

/**
 * 基础Activity
 * Created by CodingEnding on 2018/4/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG="BaseActivity";

    /**
     * 初始化View
     */
    protected abstract void initViews();

    /**
     * 获取Toolbar
     * 在子Activity中实现
     */
    protected abstract Toolbar getToolbar();

    /**
     * 初始化Toolbar
     * 默认实现（显示左侧返回按钮）
     */
    protected void initToolbar(){
        Toolbar toolbar=getToolbar();
        if(toolbar!=null){//在获得的Toolbar不为空时才设置Toolbar（因为某些Activity并没有Toolbar）
            setSupportActionBar(toolbar);
            ActionBar actionBar=getSupportActionBar();
            if(actionBar!=null){
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){//为具有返回按钮的Activity提供公有逻辑
            case android.R.id.home:
                LogUtils.i(TAG,"onOptionsItemSelected");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

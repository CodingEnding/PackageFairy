package com.codingending.packagefairy.activity.account;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.utils.AppUtils;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    private TextView versionView;
    private LinearLayout authorView;
    private LinearLayout blogView;
    private LinearLayout githubView;
    private LinearLayout protocolView;//用户协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
        initToolbar();
        loadData();
    }

    //加载数据
    private void loadData(){
        String versionName= AppUtils.getVersionName(this);//当前版本名称
        versionView.setText(getString(R.string.about_version,versionName));
    }

    @Override
    protected void initViews() {
        versionView= (TextView) findViewById(R.id.text_view_version);
        authorView= (LinearLayout) findViewById(R.id.layout_author);
        blogView= (LinearLayout) findViewById(R.id.layout_blog);
        githubView= (LinearLayout) findViewById(R.id.layout_github);
        protocolView= (LinearLayout) findViewById(R.id.layout_user_protocol);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

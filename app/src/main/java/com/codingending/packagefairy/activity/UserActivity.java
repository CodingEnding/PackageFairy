package com.codingending.packagefairy.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.utils.PreferenceUtils;

public class UserActivity extends BaseActivity {
    private Toolbar toolbar;
    private TextView usernameView;
    private TextView emailView;
    private TextView phoneView;
    private TextView changePasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        initToolbar();
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);//显示左侧返回按钮
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);//设置图标
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){//如果左上角的按钮被点击
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        usernameView= (TextView) findViewById(R.id.text_view_username);
        emailView= (TextView) findViewById(R.id.text_view_email);
        phoneView= (TextView) findViewById(R.id.text_view_phone);
        changePasswordView= (TextView) findViewById(R.id.text_view_change_password);

        //从SharedPreferences中获取值（并设置了默认值）
        usernameView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_NAME,getString(R.string.user_name_tips)));
        emailView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_EMAIL,getString(R.string.user_email_tips)));
        phoneView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_PHONE,getString(R.string.user_phone_tips)));

        changePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 修改密码
            }
        });
    }
}

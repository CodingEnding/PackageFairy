package com.codingending.packagefairy.activity.account;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.utils.PreferenceUtils;

public class UserActivity extends BaseActivity {
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
    protected void initViews() {
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

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

package com.codingending.packagefairy.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.UserBean;
import com.codingending.packagefairy.utils.EncryptUtils;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录或注册
 * @author CodingEnding
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG="LoginActivity";
    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginBtn;
    private Button registerBtn;
    private TextView forgetView;
    private TextView protocolView;
    private LinearLayout registerActionLayout;//包含注册按钮+用户协议提示

    private boolean isLoginMode=true;//是否处于登录状态（默认处于登录状态）

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login,menu);//解析菜单
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isLoginMode){
            menu.findItem(R.id.menu_login).setVisible(false);
            menu.findItem(R.id.menu_register).setVisible(true);
        }else{
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_register).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_register:
                isLoginMode=false;
                supportInvalidateOptionsMenu();//刷新菜单
                invalidateView();
                return true;
            case R.id.menu_login:
                isLoginMode=true;
                supportInvalidateOptionsMenu();//刷新菜单
                invalidateView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //显示部分view以及隐藏部分View
    private void invalidateView(){
        if(isLoginMode){
            userNameEditText.setVisibility(View.GONE);
            registerActionLayout.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
            forgetView.setVisibility(View.VISIBLE);
        }else{
            userNameEditText.setVisibility(View.VISIBLE);
            registerActionLayout.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            forgetView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initViews() {
        userNameEditText= (EditText) findViewById(R.id.edit_text_username);
        emailEditText= (EditText) findViewById(R.id.edit_text_email);
        passwordEditText= (EditText) findViewById(R.id.edit_text_password);
        registerBtn= (Button) findViewById(R.id.btn_register);
        loginBtn= (Button) findViewById(R.id.btn_login);
        forgetView= (TextView) findViewById(R.id.text_view_forget);
        registerActionLayout= (LinearLayout) findViewById(R.id.layout_register_action);
        protocolView= (TextView) findViewById(R.id.text_view_protocol);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        forgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetActivity.actionStart(LoginActivity.this);//跳转
            }
        });
        protocolView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 用户协议
            }
        });
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    //注册
    private void register(){
        String username=userNameEditText.getText().toString();
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        password= EncryptUtils.sha256String(password);//对密码进行SHA-256加密

        UserBean userBean=new UserBean();
        userBean.setUsername(username);
        userBean.setPassword(password);
        userBean.setEmail(email);

        Call<DataResponse<Integer>> call=RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .register(userBean);
        call.enqueue(new Callback<DataResponse<Integer>>() {
            @Override
            public void onResponse(Call<DataResponse<Integer>> call, Response<DataResponse<Integer>> response) {
                if(response.isSuccessful()){
                    DataResponse<Integer> body=response.body();
                    if(body.getCode()==UserService.CODE_MAIL_EXIST){//邮箱已存在
                        Toast.makeText(LoginActivity.this,getString(
                                R.string.register_email_exist),Toast.LENGTH_LONG).show();
                    }
                    else if(body.getCode()==UserService.CODE_REGISTER_ERROR){//注册失败
                        Toast.makeText(LoginActivity.this,getString(
                                R.string.register_error),Toast.LENGTH_LONG).show();
                    }
                    else{//注册成功
                        Toast.makeText(LoginActivity.this,getString(
                                R.string.register_succeed),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<DataResponse<Integer>> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    //登录
    private void login(){
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        password=EncryptUtils.sha256String(password);//对密码进行SHA-256加密
        String deviceType= PreferenceUtils.getString(this,PreferenceUtils.KEY_DEVICE_TYPE);
        String systemVersion= PreferenceUtils.getString(this,PreferenceUtils.KEY_SYSTEM_VERSION);
        String deviceFinger=PreferenceUtils.getString(this,PreferenceUtils.KEY_DEVICE_FINGER);

        Call<DataResponse<UserBean>> call=RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .login(email,password,deviceType,systemVersion,deviceFinger);
        call.enqueue(new Callback<DataResponse<UserBean>>() {
            @Override
            public void onResponse(Call<DataResponse<UserBean>> call, Response<DataResponse<UserBean>> response) {
                if(response.isSuccessful()){
                    DataResponse<UserBean> body=response.body();
                    if(body.isSucceed()){
                        Toast.makeText(LoginActivity.this,getString(
                                R.string.login_succeed),Toast.LENGTH_SHORT).show();
                        saveUserInfo(body.getData());//储存用户信息
                        finish();//关闭当前界面
                    }else{
                        Toast.makeText(LoginActivity.this,getString(
                                R.string.login_error),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<DataResponse<UserBean>> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    //储存用户信息
    private void saveUserInfo(UserBean userBean){
        saveString(PreferenceUtils.KEY_USER_NAME,userBean.getUsername());
        saveString(PreferenceUtils.KEY_USER_EMAIL,userBean.getEmail());
        saveString(PreferenceUtils.KEY_USER_PHONE,userBean.getPhone());
        saveString(PreferenceUtils.KEY_USER_SESSION_TOKEN,userBean.getSessionToken());
        PreferenceUtils.putInt(this,PreferenceUtils.KEY_USER_ID,userBean.getId());
        PreferenceUtils.putInt(this,PreferenceUtils.KEY_EMAIL_VERIFIED,userBean.getEmailVerified());
    }

    //避免存储空值
    private void saveString(String key,String value){
        if(!TextUtils.isEmpty(value)){
            PreferenceUtils.putString(this,key,value);
        }
    }

}

package com.codingending.packagefairy.activity.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.utils.EncryptUtils;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends BaseActivity {
    private static final String TAG="UserActivity";
    private TextView usernameView;
    private TextView emailView;
    private TextView phoneView;
    private TextView changePasswordView;

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        initToolbar();
        loadData();
    }

    //加载数据
    private void loadData(){
        //从SharedPreferences中获取值（并设置了默认值）
        usernameView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_NAME,getString(R.string.user_name_tips)));
        emailView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_EMAIL,getString(R.string.user_email_tips)));
        phoneView.setText(PreferenceUtils.getString(this,
                PreferenceUtils.KEY_USER_PHONE,getString(R.string.user_phone_tips)));
    }

    @Override
    protected void initViews() {
        usernameView= (TextView) findViewById(R.id.text_view_username);
        emailView= (TextView) findViewById(R.id.text_view_email);
        phoneView= (TextView) findViewById(R.id.text_view_phone);
        changePasswordView= (TextView) findViewById(R.id.text_view_change_password);

        usernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=View.inflate(UserActivity.this,R.layout.dialog_username,null);
                final EditText nameEditText= (EditText) view.findViewById(R.id.edit_text_dialog_name);
                loadAndSelect(nameEditText,PreferenceUtils.getString(UserActivity.this,
                        PreferenceUtils.KEY_USER_NAME));//加载已有的用户名

                AlertDialog.Builder builder=createBaseDialogBuilder(R.string.modify_username);
                builder.setView(view)
                        .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                modifyUsername(nameEditText.getText().toString());
                            }
                        })
                        .show();
            }
        });
        phoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=View.inflate(UserActivity.this,R.layout.dialog_phone,null);
                final EditText phoneEditText= (EditText) view.findViewById(R.id.edit_text_dialog_phone);
                loadAndSelect(phoneEditText,PreferenceUtils.getString(UserActivity.this,
                        PreferenceUtils.KEY_USER_PHONE));//加载已有的手机号码

                AlertDialog.Builder builder=createBaseDialogBuilder(R.string.modify_phone);
                builder.setView(view)
                        .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                modifyPhone(phoneEditText.getText().toString());
                            }
                        })
                        .show();
            }
        });
        changePasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view= View.inflate(UserActivity.this,R.layout.dialog_password,null);;
                final EditText oldPwdEditText= (EditText) view.findViewById(R.id.edit_text_old_pwd);
                final EditText newPwdEditText= (EditText) view.findViewById(R.id.edit_text_new_pwd);
                final EditText againNewPwdEditText= (EditText) view.findViewById(R.id.edit_text_new_pwd_again);
                AlertDialog.Builder builder=createBaseDialogBuilder(R.string.modify_password);
                builder.setView(view)
                        .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                modifyPassword(oldPwdEditText.getText().toString(),newPwdEditText.getText().toString(),
                                        againNewPwdEditText.getText().toString());
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 修改密码
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param againNewPwd 再次输入的新密码
     */
    private void modifyPassword(String oldPwd,String newPwd,String againNewPwd){
        if(TextUtils.isEmpty(oldPwd)||TextUtils.isEmpty(newPwd)||TextUtils.isEmpty(againNewPwd)){
            showToast(R.string.modify_content_none);
            return;
        }
        if(!newPwd.equals(againNewPwd)){//两次密码不一致
            showLongToast(R.string.modify_pwds_not_equals);
            return;
        }

        String email=PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_EMAIL);
        oldPwd= EncryptUtils.sha256String(oldPwd);
        newPwd=EncryptUtils.sha256String(newPwd);

        Call<BaseResponse> call=RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .modifyPassword(email,oldPwd,newPwd);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        showLongToast(R.string.modify_pwd_succeed_tips);
                        logout();//退出登录（清除数据并跳转到登录界面）
                    }else{
                        showToast(R.string.modify_error_tips);
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    //退出登录（清除数据并跳转到登录界面）
    private void logout(){
        PreferenceUtils.remove(this,PreferenceUtils.KEY_USER_ID);
        PreferenceUtils.remove(this,PreferenceUtils.KEY_USER_NAME);
        PreferenceUtils.remove(this,PreferenceUtils.KEY_USER_EMAIL);
        PreferenceUtils.remove(this,PreferenceUtils.KEY_EMAIL_VERIFIED);
        PreferenceUtils.remove(this,PreferenceUtils.KEY_USER_SESSION_TOKEN);
        PreferenceUtils.remove(this,PreferenceUtils.KEY_USER_PHONE);
        LoginActivity.actionStart(this);
        finish();//销毁当前Activity
    }

    /**
     * 修改用户信息
     * @param type 类型
     * @param content 新的内容
     */
    private void modifyInfo(final int type, final String content){
        String email=PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_EMAIL);
        String sessionToken=PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_SESSION_TOKEN);

        Call<BaseResponse> call= RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .modifyInfo(type,email,content,sessionToken);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        if(type==UserService.TYPE_USERNAME){//更新数据
                            updateAndRefresh(PreferenceUtils.KEY_USER_NAME,content);
                        }else{
                            updateAndRefresh(PreferenceUtils.KEY_USER_PHONE,content);
                        }
                        showToast(R.string.modify_succeed_tips);
                    }else{
                        showToast(R.string.modify_error_tips);
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure");
            }
        });
    }

    //修改电话号码
    private void modifyPhone(String newPhone){
        if(TextUtils.isEmpty(newPhone)){
            showToast(R.string.modify_content_none);
        }else{
            modifyInfo(UserService.TYPE_PHONE,newPhone);
        }
    }

    //修改用户名
    private void modifyUsername(String newName){
        if(TextUtils.isEmpty(newName)){
            showToast(R.string.modify_content_none);
        }else{
            modifyInfo(UserService.TYPE_USERNAME,newName);
        }
    }

    /**
     * 更新数据并加载新的数据
     */
    private void updateAndRefresh(String key,String content){
        PreferenceUtils.putString(this,key,content);
        loadData();//重新加载页面数据
    }

    /**
     * 为EditText加载内容并移动光标到末尾
     */
    private void loadAndSelect(EditText editText,String content){
        if(!TextUtils.isEmpty(content)){
            editText.setText(content);
            editText.setSelection(content.length());
        }
    }

    //创建基本的对话框构造器
    private AlertDialog.Builder createBaseDialogBuilder(@StringRes int title){
        return new AlertDialog.Builder(UserActivity.this)
                .setTitle(title)
                .setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

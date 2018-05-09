package com.codingending.packagefairy.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.utils.EncryptUtils;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;
import com.codingending.packagefairy.utils.VerificationUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 忘记密码
 */
public class ForgetActivity extends BaseActivity {
    private static final String TAG="ForgetActivity";
    private TextView codeView;
    private EditText emailEditText;
    private EditText codeEditText;
    private Button nextBtn;
    private TextView emailTipsView;
    private LinearLayout passwordLayout;//密码区域
    private EditText pwdEditText;
    private EditText againPwdEditText;
    private Button modifyBtn;

    private static final long CODE_PERIOD=60*1000;//验证码倒计时的刷新间隔（60s）
    private static final long CODE_INTERNAL=1000;//验证码倒计时的刷新间隔（1s）
    private boolean isCountDown=false;//验证码按钮是否处于倒计时状态

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,ForgetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initViews();
        initToolbar();
    }

    /**
     * 实现验证码倒计时的定时器
     */
    private CountDownTimer countDownTimer=new CountDownTimer(CODE_PERIOD,CODE_INTERNAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            isCountDown=true;
            codeView.setTextColor(getResources().getColor(R.color.forget_btn_code_disabled));
            codeView.setText(getString(R.string.forget_code_tips,millisUntilFinished/1000));
            codeView.setEnabled(false);
        }
        @Override
        public void onFinish() {
            isCountDown=false;
            codeView.setText(R.string.forget_get_code);
            codeView.setTextColor(getResources().getColor(R.color.forget_btn_code));
            codeView.setEnabled(true);
        }
    };

    /**
     * 监听邮箱/验证码两个输入框的内容
     */
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            String email=emailEditText.getText().toString();
            String code=codeEditText.getText().toString();
            if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(code)&&code.length()==6){
                nextBtn.setEnabled(true);
            }else{
                nextBtn.setEnabled(false);
            }
        }
    };

    /**
     * 监听密码输入框的内容
     */
    private TextWatcher pwdTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            String email=emailEditText.getText().toString();
            String code=codeEditText.getText().toString();
            String pwd=pwdEditText.getText().toString();
            String againPwd=againPwdEditText.getText().toString();
            if(!TextUtils.isEmpty(pwd)&&!TextUtils.isEmpty(againPwd)&&
                    !TextUtils.isEmpty(email)&&!TextUtils.isEmpty(code)){
                modifyBtn.setEnabled(true);
            }else{
                modifyBtn.setEnabled(false);
            }
        }
    };

    @Override
    protected void initViews() {
        emailEditText= (EditText) findViewById(R.id.edit_text_forget_email);
        codeEditText= (EditText) findViewById(R.id.edit_text_forget_code);
        codeView= (TextView) findViewById(R.id.text_view_code);
        nextBtn= (Button) findViewById(R.id.btn_forget_next);
        emailTipsView= (TextView) findViewById(R.id.text_view_email_tips);
        passwordLayout= (LinearLayout) findViewById(R.id.layout_forget_pwd);
        pwdEditText= (EditText) findViewById(R.id.edit_text_forget_pwd);
        againPwdEditText= (EditText) findViewById(R.id.edit_text_forget_again_pwd);
        modifyBtn= (Button) findViewById(R.id.btn_forget_modify);

        emailEditText.addTextChangedListener(textWatcher);
        codeEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(pwdTextWatcher);
        codeEditText.addTextChangedListener(pwdTextWatcher);
        pwdEditText.addTextChangedListener(pwdTextWatcher);
        againPwdEditText.addTextChangedListener(pwdTextWatcher);
        codeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCountDown){//此时并未处于倒计时状态
                    getCodeEmail();//发送验证码邮件
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn.setVisibility(View.GONE);
                passwordLayout.setVisibility(View.VISIBLE);
            }
        });
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();//重置密码
            }
        });
    }

    /**
     * 请求发送验证码邮件
     */
    private void getCodeEmail(){
        String email=emailEditText.getText().toString();
        if(TextUtils.isEmpty(email)){
            showToast(R.string.forget_email_none);
            return;
        }
        if(!VerificationUtils.matcherEmail(email)){//判断邮件格式是否合理
            showToast(R.string.forget_email_invalid);
            return;
        }

        Call<BaseResponse> call= RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .getForgetCode(email);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        countDownTimer.start();//开启验证码倒计时
                        emailTipsView.setVisibility(View.VISIBLE);//显示提示语
                        showLongToast(R.string.forget_email_send_succeed);
                    }else{
                        showLongToast(R.string.forget_email_send_error);
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

    /**
     * 重置密码
     */
    private void resetPassword(){
        String code=codeEditText.getText().toString();
        String email=emailEditText.getText().toString();
        String pwd=pwdEditText.getText().toString();
        String againPwd=againPwdEditText.getText().toString();

        if(!pwd.equals(againPwd)){//检查两次密码是否一致
            showLongToast(R.string.forget_pwds_not_equals);
            return;
        }

        pwd= EncryptUtils.sha256String(pwd);//对密码进行加密
        Call<BaseResponse> call=RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .forget(email,pwd,code);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        showLongToast(R.string.forget_reset_pwd_succeed);
                        finish();//销毁当前页面（重新回到登录页面）
                    }else{
                        showToast(R.string.forget_reset_pwd_error);
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

    @Override
    protected void onDestroy() {
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

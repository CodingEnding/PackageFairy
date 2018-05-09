package com.codingending.packagefairy.activity.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.api.UserService;
import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 反馈界面
 * @author CodinEnding
 */
public class FeedbackActivity extends BaseActivity {
    private static final String TAG="FeedbackActivity";

    private EditText feedbackEditText;
    private Button feedbackBtn;
    private TextView errorTipsView;

    /**
     * 启动Activity
     */
    public static void actionStart(Context context){
        Intent intent=new Intent(context,FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initViews();
        initToolbar();
        loadData();
    }

    @Override
    protected void initViews() {
        feedbackEditText= (EditText) findViewById(R.id.edit_text_feedback);
        feedbackBtn= (Button) findViewById(R.id.btn_feedback);
        errorTipsView= (TextView) findViewById(R.id.text_view_feedback_error_tips);

        feedbackEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s)){
                    feedbackBtn.setEnabled(true);
                }else{
                    feedbackBtn.setEnabled(false);
                }
            }
        });
        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubmitDialog();//显示提交提示框
            }
        });
    }

    //显示提示框
    private void showSubmitDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.feedback_dialog_title)
                .setMessage(R.string.feedback_dialog_content)
                .setPositiveButton(R.string.dialog_sure,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitFeedback(feedbackEditText.getText().toString());//提交反馈
                    }
                })
                .setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //提交反馈内容
    private void submitFeedback(String feedback){
        String email= PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_EMAIL);
        String deviceFinger=PreferenceUtils.getString(this,PreferenceUtils.KEY_DEVICE_FINGER);

        Call<BaseResponse> call= RetrofitUtils.getRetrofit()
                .create(UserService.class)
                .postFeedback(email,feedback,deviceFinger);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        showLongToast(R.string.feedback_submit_succeed);
                        clearFeedback();//清除本地的反馈信息
                        finish();//销毁当前页面
                    }else{
                        handleError();
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                    handleError();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure");
                handleError();
            }
        });
    }

    //处理错误情形
    private void handleError(){
        showLongToast(R.string.feedback_submit_error);
        saveFeedback();//在本地存储反馈信息
        errorTipsView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveFeedback();//在Activity被意外销毁前存储反馈信息
    }

    @Override
    public void onBackPressed() {
        saveFeedback();//在点击返回键时保存反馈信息（避免用户误操作）
        super.onBackPressed();
    }

    //加载本地数据
    private void loadData(){
        String feedback=PreferenceUtils.getString(this,PreferenceUtils.KEY_FEEDBACK);
        if(!TextUtils.isEmpty(feedback)){//恢复临时存储的反馈信息
            feedbackEditText.setText(feedback);
        }
    }

    //将反馈信息储存在本地
    private void saveFeedback(){
        String feedback=feedbackEditText.getText().toString();
        if(!TextUtils.isEmpty(feedback)){
            PreferenceUtils.putString(this,PreferenceUtils.KEY_FEEDBACK,feedback);
        }
    }

    //清除本地反馈信息
    private void clearFeedback(){
        PreferenceUtils.remove(this,PreferenceUtils.KEY_FEEDBACK);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

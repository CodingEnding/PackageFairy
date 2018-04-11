package com.codingending.packagefairy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.codingending.packagefairy.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 获取套餐推荐
 * 对话框风格的Activity
 * @author CodingEnding
 */
public class RecommendActivity extends BaseActivity {
    private EditText flowEditText;
    private EditText callTimeEditText;
    private EditText provinceOutEditText;
    private RadioGroup modeRadioGroup;//决定推荐方式
    private RadioButton quickBtn;//快速推荐
    private RadioButton advanceBtn;//精准推荐
    private CheckBox unicomCheckBox;//联通
    private CheckBox telecomCheckBox;//电信
    private CheckBox mobileCheckBox;//移动
    private Button startRecommendBtn;//开始推荐

    private int recommendMode=0;//推荐方式（默认为极速推荐）
    private String[] operatorArray={"中国联通","中国电信","中国移动"};//运营商在数据库内的名称
    private Set<String> operatorSet=new HashSet<>();//运营商列表（使用Set避免重复添加）

    public static final String KEY_DATA_BUNDLE="com.codingending.packagefairy.bundle";//存储整体数据
    public static final String KEY_FLOW="flow_amount";
    public static final String KEY_CALL_TIME="call_time";
    public static final String KEY_PROVINCE_OUT="province_out";
    public static final String KEY_RECOMMEND_MODE="recommend_mode";
    public static final String KEY_OPERATORS="operators";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        initData();
        initViews();
    }

    //初始化数据
    private void initData(){
        operatorSet.add(operatorArray[0]);//添加默认运营商-保证在不点击复选框的情况下也有运营商数据
    }

    @Override
    protected void initViews() {
        flowEditText= (EditText) findViewById(R.id.edit_text_flow);
        callTimeEditText= (EditText) findViewById(R.id.edit_text_call_time);
        provinceOutEditText= (EditText) findViewById(R.id.edit_text_province_out);
        modeRadioGroup= (RadioGroup) findViewById(R.id.radio_group_mode);
        quickBtn= (RadioButton) findViewById(R.id.radio_btn_quick);
        advanceBtn= (RadioButton) findViewById(R.id.radio_btn_advance);
        unicomCheckBox= (CheckBox) findViewById(R.id.checkbox_unicom);
        telecomCheckBox= (CheckBox) findViewById(R.id.checkbox_telecom);
        mobileCheckBox= (CheckBox) findViewById(R.id.checkbox_mobile);
        startRecommendBtn= (Button) findViewById(R.id.btn_start_recommend);

        //切换推荐模式
        modeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_btn_quick){
                    recommendMode=0;
                }else{
                    recommendMode=1;
                }
            }
        });
        //更新需要获取的运营商列表
        unicomCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateOperatorSet(isChecked,operatorArray[0]);
            }
        });
        telecomCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateOperatorSet(isChecked,operatorArray[1]);
            }
        });
        mobileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateOperatorSet(isChecked,operatorArray[2]);
            }
        });
        //开始推荐
        startRecommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecommend();
            }
        });
    }

    //更新运营商列表
    private void updateOperatorSet(boolean isChecked,String operator){
        if(isChecked){
            operatorSet.add(operator);
        }else{
            operatorSet.remove(operator);
        }
    }

    //开始推荐（将相关数据传递回主Activity-方便在Fragment中展示）
    private void startRecommend(){
        Double flow=Double.valueOf(flowEditText.getText().toString())*1000;
        int callTime=Integer.valueOf(callTimeEditText.getText().toString());
        int provinceOutDay=Integer.valueOf(provinceOutEditText.getText().toString());
        ArrayList<String> operatorList=new ArrayList<>(operatorSet);//将HashSet转化为ArrayList

        Intent dataIntent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt(KEY_FLOW,flow.intValue());
        bundle.putInt(KEY_CALL_TIME,callTime);
        bundle.putInt(KEY_PROVINCE_OUT,provinceOutDay);
        bundle.putInt(KEY_RECOMMEND_MODE,recommendMode);
        bundle.putStringArrayList(KEY_OPERATORS,operatorList);
        dataIntent.putExtra(KEY_DATA_BUNDLE,bundle);

        setResult(RESULT_OK,dataIntent);//传递数据
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }
}

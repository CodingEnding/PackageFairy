package com.codingending.packagefairy.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.po.UserConsumePO;
import com.codingending.packagefairy.utils.DBUtils;
import com.codingending.packagefairy.utils.LogUtils;

import java.text.DecimalFormat;
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

    private SQLiteDatabase database;//SQLiteDatabase实例
    public static final int MONTH_DAY_COUNT=30;//默认每个月是30天（方便计算）
    public static final double DEFAULT_FLOW=0.8;//默认本月流量消耗（0.8G）
    public static final int DEFAULT_CALL_TIME=60;//默认本月通话时长（60min）
    public static final int DEFAULT_PROVINCE_OUT=0;//默认每月在省外的天数（0天）
    public static final double MAX_FLOW=15;//本月预测流量的最大值（15G）
    public static final int MAX_CALL_TIME=500;//本月预测通话时长的最大值（500min）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        initDatabase();
        initData();
        initViews();
        loadData();
    }

    @Override
    protected void onDestroy() {
        if(database!=null){
            database.close();//关闭数据库
        }
        super.onDestroy();
    }

    //初始化数据库
    private void initDatabase(){
        if(database!=null){
            database.close();
        }
        database= DBUtils.getDatabase(this);
    }

    //初始化数据
    private void initData(){
        operatorSet.add(operatorArray[0]);//添加默认运营商-保证在不点击复选框的情况下也有运营商数据
    }

    //从数据库中加载历史消耗数据
    private void loadData(){
        UserConsumePO userConsumePO=DBUtils.getMonthTotalUserFlow(database);
        double totalFlow=DEFAULT_FLOW;
        int totalCallTime=DEFAULT_CALL_TIME;
//        LogUtils.i(TAG,userConsumePO.getDay()+" "+userConsumePO.getAllFlow()+" "+userConsumePO.getCallTime());
        if(userConsumePO!=null&&userConsumePO.getDay()<MONTH_DAY_COUNT){//如果当前本月统计的天数还不足一个月，就简单预测出本月的消耗
            int dayCount=userConsumePO.getDay();//本月具有数据统计的天数
            totalCallTime=userConsumePO.getCallTime()*MONTH_DAY_COUNT/dayCount;//根据平均值预测本月的通话时长
            totalCallTime=Math.min(totalCallTime,MAX_CALL_TIME);//MAX_CALL_TIME限制预测值的最大值
            totalFlow=userConsumePO.getAllFlow()*MONTH_DAY_COUNT/dayCount/1024.0;//根据平均值预测本月的流量消耗（G）
            totalFlow=Math.min(totalFlow,MAX_FLOW);//MAX_FLOW限制预测值的最大值
        }
        //为控件设置值
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//小数保留两位
        flowEditText.setText(String.valueOf(decimalFormat.format(totalFlow)));
        callTimeEditText.setText(String.valueOf(totalCallTime));
        provinceOutEditText.setText(String.valueOf(DEFAULT_PROVINCE_OUT));
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

package com.codingending.packagefairy.activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.po.UserConsumePO;
import com.codingending.packagefairy.utils.DBUtils;

/**
 * 通话时长统计
 * @author CodingEnding
 */
public class CallRankActivity extends BaseActivity {
    private TextView todayCallView;
    private TextView weekCallView;
    private TextView monthCallView;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_rank);
        initDatabase();
        initViews();
        initToolbar();
        loadData();
    }

    //加载数据
    private void loadData(){
        UserConsumePO todayUserConsume=DBUtils.getTodayTotalUserFlow(database);
        UserConsumePO weekUserConsume=DBUtils.getWeekTotalUserFlow(database);
        UserConsumePO monthUserConsume=DBUtils.getMonthTotalUserFlow(database);
        bindData(todayUserConsume,todayCallView);
        bindData(weekUserConsume,weekCallView);
        bindData(monthUserConsume,monthCallView);
    }

    //绑定数据
    private void bindData(UserConsumePO userConsumePO,TextView targetView){
        if(userConsumePO!=null){
            targetView.setText(getString(R.string.call_text,userConsumePO.getCallTime()));
        }else{
            targetView.setText(getString(R.string.call_rank_no_data));
        }
    }

    @Override
    protected void onDestroy() {
        if(database!=null){
            database.close();
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

    @Override
    protected void initViews() {
        todayCallView= (TextView) findViewById(R.id.text_view_call_today);
        weekCallView= (TextView) findViewById(R.id.text_view_call_week);
        monthCallView= (TextView) findViewById(R.id.text_view_call_month);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }
}

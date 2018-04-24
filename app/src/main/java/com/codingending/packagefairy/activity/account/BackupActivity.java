package com.codingending.packagefairy.activity.account;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;

/**
 * 同步设置
 */
public class BackupActivity extends BaseActivity {
    private static final String TAG="BackupActivity";
    private Toolbar toolbar;
    private SwitchCompat autoBackupSwitch;
//    private LinearLayout backupInternalView;//同步间隔[暂时弃用]
    private LinearLayout startBackupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        initViews();
        initToolbar();
        loadData();
    }

    //加载数据
    private void loadData(){
        boolean autoBackup=PreferenceUtils.getBoolean(this,PreferenceUtils.KEY_AUTO_BACKUP);
        autoBackupSwitch.setChecked(autoBackup);//根据配置值修改选中状态
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        }
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        autoBackupSwitch= (SwitchCompat) findViewById(R.id.switch_auto_backup);
//        backupInternalView= (LinearLayout) findViewById(R.id.layout_backup_internal);
        startBackupView= (LinearLayout) findViewById(R.id.layout_start_backup);

        autoBackupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                LogUtils.i(TAG,"onCheckedChanged:"+isChecked);
                //更新配置数据
                PreferenceUtils.putBoolean(BackupActivity.this,PreferenceUtils.KEY_AUTO_BACKUP,isChecked);
            }
        });
        startBackupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 开始同步
            }
        });
    }
}

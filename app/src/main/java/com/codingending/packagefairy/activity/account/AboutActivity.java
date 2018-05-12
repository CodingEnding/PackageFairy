package com.codingending.packagefairy.activity.account;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.utils.AppUtils;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    private TextView versionView;
    private LinearLayout authorView;
    private LinearLayout blogView;
    private LinearLayout githubView;
    private LinearLayout protocolView;//用户协议
    private LinearLayout acknowledgeView;//鸣谢

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
        initToolbar();
        loadData();
    }

    //加载数据
    private void loadData(){
        String versionName= AppUtils.getVersionName(this);//当前版本名称
        versionView.setText(getString(R.string.about_version,versionName));
    }

    @Override
    protected void initViews() {
        versionView= (TextView) findViewById(R.id.text_view_version);
        authorView= (LinearLayout) findViewById(R.id.layout_author);
        blogView= (LinearLayout) findViewById(R.id.layout_blog);
        githubView= (LinearLayout) findViewById(R.id.layout_github);
        protocolView= (LinearLayout) findViewById(R.id.layout_user_protocol);
        acknowledgeView= (LinearLayout) findViewById(R.id.layout_acknowledge);

        authorView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyTextToClipboard(getString(R.string.about_author));
                return true;
            }
        });
        blogView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyTextToClipboard(getString(R.string.about_blog));
                return true;
            }
        });
        githubView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyTextToClipboard(getString(R.string.about_github));
                return true;
            }
        });
        protocolView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//加载用户协议
                UserProtocolActivity.actionStart(AboutActivity.this);
            }
        });
        acknowledgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//加载鸣谢
                AcknowledgeActivity.actionStart(AboutActivity.this);
            }
        });
    }

    //将指定内容复制到剪切板
    private void copyTextToClipboard(String content){
        ClipboardManager clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if(clipboardManager!=null){
            clipboardManager.setPrimaryClip(ClipData.newPlainText("PackageFairy",content));
            showToast(R.string.about_copy_succeed);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_share:
                showShareActivity();//利用系统原生功能发起分享操作
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //利用系统原生功能发起分享操作
    private void showShareActivity(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TITLE,getString(R.string.about_share_title));
        intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.about_share_content));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent,getString(R.string.about_share_title)));
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

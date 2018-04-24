package com.codingending.packagefairy.activity.account;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.BaseActivity;

/**
 * 开源协议
 */
public class LibraryActivity extends BaseActivity {
    private Toolbar toolbar;
    private FrameLayout container;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        initViews();
        initToolbar();
        loadData();
    }

    /**
     * 从本地html文件中加载数据
     */
    private void loadData(){
        if(webView!=null){
            webView.loadUrl("file:///android_asset/library.html");
        }else{
            Toast.makeText(this,R.string.library_load_error,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if(webView!=null){//安全地销毁WebView（防止内存泄漏）
            webView.stopLoading();
            webView.loadDataWithBaseURL(null,"",null,null,null);
            webView.clearHistory();

            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.destroy();
            webView=null;
        }
        super.onDestroy();
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

    //初始化WebView[动态创建]
    private void initWebView(){
        webView=new WebView(getApplicationContext());
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(webView,params);//将WebView加入容器
    }

    @Override
    protected void initViews() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        container= (FrameLayout) findViewById(R.id.container);
        initWebView(); //初始化WebView
    }

}

package com.codingending.packagefairy.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.codingending.packagefairy.utils.LogUtils;

/**
 * 基础Activity
 * Created by CodingEnding on 2018/4/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG="BaseActivity";

    /**
     * 初始化View
     */
    protected abstract void initViews();

    /**
     * 初始化Toolbar
     * 默认空实现
     */
    protected void initToolbar(){
    }

}

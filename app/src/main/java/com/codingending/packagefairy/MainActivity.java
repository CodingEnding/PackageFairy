package com.codingending.packagefairy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.codingending.packagefairy.adapter.NavigationFragmentAdapter;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

/**
 * 主页面布局
 * @author CodingEnding
 */
public class MainActivity extends BaseActivity {
    public static final String TAG="MainActivity";
    public static final int INDEX_STATISTICS=0;//底部导航的统计按钮
    public static final int INDEX_FIND=1;//底部导航的发现按钮
    public static final int INDEX_REPORT=2;//底部导航的推荐报告按钮
    public static final int INDEX_ACCOUNT=3;//底部导航的账户按钮

    public static final int PAGE_OFFSET=3;//ViewPager屏幕外最大页面数（避免导航页中的任何一页被销毁）
    public static final int PERMISSION_REQUEST=1;//权限请求码

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SpaceNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();
        hasPermissionToReadNetworkStats();
        initViews();
        initToolbar();
        initViewPager();
        initBottomNavigation(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomNavigationView.onSaveInstanceState(outState);//保存导航菜单的选中状态
    }

    //初始化Toolbar
    private void initToolbar(){
        setSupportActionBar(toolbar);
    }

    //初始化ViewPager
    private void initViewPager(){
        FragmentManager fragmentManager=getSupportFragmentManager();
        NavigationFragmentAdapter navigationAdapter=new NavigationFragmentAdapter(fragmentManager);
        viewPager.setAdapter(navigationAdapter);
        viewPager.setOffscreenPageLimit(PAGE_OFFSET);//配置屏幕外最大页面数（避免导航页中的任何一页被销毁）
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {//在ViewPager页面发生变化时同步更改底部导航状态
                bottomNavigationView.changeCurrentItem(position);
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    //初始化底部导航
    private void initBottomNavigation(Bundle savedInstanceState){
        bottomNavigationView.initWithSaveInstanceState(savedInstanceState);//保存导航状态
        bottomNavigationView.addSpaceItem(new SpaceItem(getString(R.string.navigation_item_statistics),R.drawable.statistics));
        bottomNavigationView.addSpaceItem(new SpaceItem(getString(R.string.navigation_item_find),R.drawable.magnify));
        bottomNavigationView.addSpaceItem(new SpaceItem(getString(R.string.navigation_item_report),R.drawable.report));
        bottomNavigationView.addSpaceItem(new SpaceItem(getString(R.string.navigation_item_account),R.drawable.account));
        bottomNavigationView.shouldShowFullBadgeText(false);
        bottomNavigationView.showIconOnly();//仅展示图标
        bottomNavigationView.setCentreButtonIconColorFilterEnabled(false);//去掉主按钮前的默认灰色遮罩

        bottomNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(MainActivity.this,"中间按钮被点击",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this,"按钮索引："+itemIndex+"按钮名字："+itemName,Toast.LENGTH_LONG).show();
                switch (itemIndex){//根据索引切换页面
                    case INDEX_STATISTICS:
                        viewPager.setCurrentItem(INDEX_STATISTICS,false);
                        break;
                    case INDEX_FIND:
                        viewPager.setCurrentItem(INDEX_FIND,false);
                        break;
                    case INDEX_REPORT:
                        viewPager.setCurrentItem(INDEX_REPORT,false);
                        break;
                    case INDEX_ACCOUNT:
                        viewPager.setCurrentItem(INDEX_ACCOUNT,false);
                        break;
                    default:break;
                }
            }
            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //菜单项再次被选中
            }
        });
    }

    @Override
    protected void initViews() {
        bottomNavigationView= (SpaceNavigationView) findViewById(R.id.navigation_bottom);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
    }

    /**
     * 检测并请求权限
     */
    private void requestPermissions(){
        String requestPermission=Manifest.permission.READ_PHONE_STATE;
        if(ContextCompat.checkSelfPermission(this,requestPermission)
                != PackageManager.PERMISSION_GRANTED){//读取设备状态的权限
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,requestPermission)){
                Toast.makeText(this,"读取设备状态权限是本应用的重要功能，如果不授予权限，程序是无法正常工作的~",
                        Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this,new String[]{requestPermission},PERMISSION_REQUEST);
        }
    }

    /**
     * 检测并引导用户打开特殊权限
     */
    private boolean hasPermissionToReadNetworkStats() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }

        requestReadNetworkStats();
        return false;
    }

    // 打开[有权查看使用情况的应用]页面
    @TargetApi(23)
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"授权成功",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                break;
        }
    }
}

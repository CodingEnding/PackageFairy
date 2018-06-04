package com.codingending.packagefairy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codingending.packagefairy.activity.BaseActivity;
import com.codingending.packagefairy.activity.RecommendActivity;
import com.codingending.packagefairy.activity.SearchActivity;
import com.codingending.packagefairy.adapter.NavigationFragmentAdapter;
import com.codingending.packagefairy.api.AppService;
import com.codingending.packagefairy.utils.LogUtils;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import ezy.boost.update.UpdateManager;

/**
 * 主页面布局
 * @author CodingEnding
 */
public class MainActivity extends BaseActivity {
    private static final String TAG="MainActivity";
    public static final int INDEX_STATISTICS=0;//底部导航的统计按钮
    public static final int INDEX_FIND=1;//底部导航的发现按钮
    public static final int INDEX_REPORT=2;//底部导航的推荐报告按钮
    public static final int INDEX_ACCOUNT=3;//底部导航的账户按钮

    public static final int PAGE_OFFSET=3;//ViewPager屏幕外最大页面数（避免导航页中的任何一页被销毁）

    public static final int RECOMMEND_ACTIVITY_CODE=1;//前往套餐推荐Activity的推荐码

    private Toolbar toolbar;
    private ViewPager viewPager;
    private NavigationFragmentAdapter navigationAdapter;//ViewPager适配器
    private SpaceNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //LogUtils.w(TAG,"数据库地址:"+DebugDB.getAddressLog());//测试数据库的地址
        initViews();
        initToolbar();
        initViewPager();
        initBottomNavigation(savedInstanceState);
        checkUpdate();
    }

    //自动检查是否有新的应用版本
    private void checkUpdate(){
        UpdateManager.setUrl(AppService.URL_CHECK_UPDATE,AppService.CHANNEL_DEFAULT);
        UpdateManager.setWifiOnly(false);
        UpdateManager.check(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomNavigationView.onSaveInstanceState(outState);//保存导航菜单的选中状态
    }

    @Override
    protected void initToolbar() {
        setSupportActionBar(toolbar);//由于主Activity并没有返回图标，所以重写该方法
    }

    //初始化ViewPager
    private void initViewPager(){
        FragmentManager fragmentManager=getSupportFragmentManager();
        navigationAdapter=new NavigationFragmentAdapter(fragmentManager);
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
                startActivityForResult(new Intent(MainActivity.this,
                        RecommendActivity.class),RECOMMEND_ACTIVITY_CODE);//打开套餐推荐弹窗界面
            }
            @Override
            public void onItemClick(int itemIndex, String itemName) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_search:
                SearchActivity.actionStart(MainActivity.this);//进入搜索界面
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initViews() {
        bottomNavigationView= (SpaceNavigationView) findViewById(R.id.navigation_bottom);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    protected Toolbar getToolbar() {
        return null;//在主Activity中用不到这个方法
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        switch (requestCode){
            case RECOMMEND_ACTIVITY_CODE://将基础数据传递到ReportFragment
                if(resultCode==RESULT_OK){
                    Bundle bundle=new Bundle(data.getBundleExtra(RecommendActivity.KEY_DATA_BUNDLE));
                    navigationAdapter.notifyFragmentByPosition(INDEX_REPORT,bundle);//刷新推荐结果界面
                    viewPager.setCurrentItem(INDEX_REPORT);//跳转到推荐结果界面
                    LogUtils.i(TAG,"onActivityResult");
                }
                break;
            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;
        }
    }

}

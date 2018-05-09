package com.codingending.packagefairy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.ExtraFlowType;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.RetrofitUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackageDetailActivity extends BaseActivity {
    private static final String TAG="PackageDetailActivity";
    public static final String KEY_PACKAGE="PackageDetailActivity_PACKAGE";//存储PackageBean的键
    public static final String KEY_PACKAGE_ID="PackageDetailActivity_PACKAGE_ID";//存储套餐Id的键
    public static final String KEY_START_SOURCE="PackageDetailActivity_START_SOURCE";//存储启动来源的键

    public static final int SOURCE_FIND=1;//从FindFragment启动
    public static final int SOURCE_REPORT=2;//从ReportFragment启动

    public static final int PACKAGE_CALL_INFINITE=-1;//在套餐内通话时长等于该值时，说明该套餐的套餐内通话时长是不限量的

    private TextView packageNameView;
    private TextView partnerView;
    private TextView operatorView;
    private TextView monthRentView;
    private TextView packageCallTimeView;
    private TextView extraPackageCallView;
    private TextView packageFlowView;
    private TextView extraPackageFlowView;
    private TextView freeFlowView;
    private TextView startView;//套餐的评分
    private RatingBar starRatingBar;
    private RatingBar myScoreRatingBar;
    private TextView myScoreView;
    private TextView monthConsumeView;
    private TextView extraPackageInfoView;
    private Button linkBtn;
    private Button scoreBtn;
    private LinearLayout extraPackageInfoLayout;
    private LinearLayout monthConsumeLayout;

    /**
     * 提供给ReportFragment的启动方法
     * @param packageBean 套餐实体类
     */
    public static void actionStart(Context context,PackageBean packageBean){
        Intent intent=new Intent(context,PackageDetailActivity.class);
        intent.putExtra(KEY_PACKAGE,packageBean);
        intent.putExtra(KEY_START_SOURCE,SOURCE_REPORT);
        context.startActivity(intent);
    }

    /**
     * 提供给FindFragment的启动方法
     * @param packageId 套餐Id
     */
    public static void actionStart(Context context,int packageId){
        Intent intent=new Intent(context,PackageDetailActivity.class);
        intent.putExtra(KEY_PACKAGE_ID,packageId);
        intent.putExtra(KEY_START_SOURCE,SOURCE_FIND);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);
        initViews();
        initToolbar();
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData(){
        int startSource=getIntent().getIntExtra(KEY_START_SOURCE,SOURCE_FIND);
        if(startSource==SOURCE_FIND){
            loadDataFromServer();
        }else{
            loadDataFromLocal();
        }
    }

    //从服务器获取数据（根据套餐Id）
    private void loadDataFromServer(){
        int id=getIntent().getIntExtra(KEY_PACKAGE_ID,0);
        Call<DataResponse<PackageBean>> call= RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getPackageById(id);
        call.enqueue(new Callback<DataResponse<PackageBean>>() {
            @Override
            public void onResponse(Call<DataResponse<PackageBean>> call, Response<DataResponse<PackageBean>> response) {
                if(response.isSuccessful()){
                    DataResponse<PackageBean> body=response.body();
                    if(body!=null){
                        PackageBean packageBean=response.body().getData();
                        if(packageBean!=null){
                            bindDataToView(packageBean);//绑定数据
                        }
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取指定套餐失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<PackageBean>> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e(TAG,"Retrofit onFailure!");
            }
        });
    }

    //从本地获得数据（根据传递进来的PackageBean）
    private void loadDataFromLocal(){
        PackageBean packageBean=getIntent().getParcelableExtra(KEY_PACKAGE);
        bindDataToView(packageBean);
    }

    /**
     * 将数据绑定到对应的View上
     * @param packageBean 套餐实体对象
     */
    private void bindDataToView(PackageBean packageBean){
        packageNameView.setText(packageBean.getName());
        partnerView.setText(packageBean.getPartner());
        operatorView.setText(packageBean.getOperator());
        startView.setText(getString(R.string.package_detail_star,packageBean.getStar()));
        formatMonthRentString(monthRentView,packageBean.getMonthRent());//格式化月租字符串（部分彩色、加粗）

        //判断套餐内通话时长是否不限量
        if(packageBean.getPackageCall()==PACKAGE_CALL_INFINITE){
            packageCallTimeView.setText(getString(R.string.package_detail_call_infinite));
        }else{
            packageCallTimeView.setText(getString(R.string.package_detail_call_time,packageBean.getPackageCall()));

        }
        extraPackageCallView.setText(getString(R.string.package_detail_extra_call,packageBean.getExtraPackageCall()));

        //设置套餐外流量的计费方式说明
        int extraFlowType=packageBean.getExtraFlowType();//套餐外流量的计费方式
        extraPackageFlowView.setText(getExtraFlowDescription(extraFlowType,packageBean));

        //判断套餐内流量是否不限量
        if(extraFlowType==ExtraFlowType.EXTRA_FLOW_TYPE_FIVE){
            packageFlowView.setText(getString(R.string.package_detail_flow_infinite));
        }else{
            int packageProvinceFlow=packageBean.getPackageProvinceFlow();//省内流量
            int packageCountryFlow=packageBean.getPackageCountryFlow();//全国流量
            String packageFlowStr=getString(R.string.package_detail_flow_two,packageProvinceFlow,packageCountryFlow);
            packageFlowView.setText(packageFlowStr);
        }

        //判断是否存在特权并设置说明文字
        if(TextUtils.isEmpty(packageBean.getPrivilegeDescription())){
            freeFlowView.setText(getString(R.string.package_detail_no_privilege));
        }else{
            freeFlowView.setText(packageBean.getPrivilegeDescription());
        }
    }

    //格式化月租字符串（部分彩色、加粗）
    private void formatMonthRentString(TextView monthRentView,int monthRent){
        String consumeStr=getString(R.string.package_detail_month_rent,monthRent);//预计消费的原始文字

        int consumeColor=getResources().getColor(R.color.package_detail_month_rent);
        Spannable spannableStr=new SpannableString(consumeStr);
        String monthRentStr=String.valueOf(monthRent);//将消费金额转化为字符串
        int start=consumeStr.indexOf(monthRentStr);
        int end=start+monthRentStr.length();
        spannableStr.setSpan(new ForegroundColorSpan(consumeColor),start,end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStr.setSpan(new RelativeSizeSpan(1.2f),start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        monthRentView.setText(spannableStr);//格式化字符串
    }

    //根据套餐外流量计费类型生成相应的描述语句
    private String getExtraFlowDescription(int extraFlowType,PackageBean packageBean){
        switch(extraFlowType){
            case ExtraFlowType.EXTRA_FLOW_TYPE_ONE:
                int countryConsume=Double.valueOf(packageBean.getExtraCountryFlow()*1000).intValue();//套餐外全国1G流量的金额（取整数部分）
                return getString(R.string.package_detail_type_one,countryConsume);
            case ExtraFlowType.EXTRA_FLOW_TYPE_TWO:
                return getString(R.string.package_detail_type_two,packageBean.getExtraCountryDayRent(),
                        packageBean.getExtraCountryDayFlow());
            case ExtraFlowType.EXTRA_FLOW_TYPE_THREE:
                return getString(R.string.package_detail_type_three,packageBean.getExtraProvinceInDayRent(),
                        packageBean.getExtraProvinceInDayFlow(),packageBean.getExtraProvinceOutDayRent(),
                        packageBean.getExtraProvinceOutDayFlow());
            case ExtraFlowType.EXTRA_FLOW_TYPE_FOUR:
                int provinceOutConsume=Double.valueOf(packageBean.getExtraProvinceOutFlow()*1000).intValue();//套餐外省外1G流量的金额（取整数部分）
                return getString(R.string.package_detail_type_four,packageBean.getExtraProvinceInDayRent(),
                        packageBean.getExtraProvinceInDayFlow(),provinceOutConsume);
            case ExtraFlowType.EXTRA_FLOW_TYPE_FIVE:
                return getString(R.string.package_detail_type_five);
            case ExtraFlowType.EXTRA_FLOW_TYPE_SIX:
                return getString(R.string.package_detail_type_six,packageBean.getExtraCountryDayRent());
            case ExtraFlowType.EXTRA_FLOW_TYPE_SEVEN:
                return getString(R.string.package_detail_type_seven,packageBean.getExtraProvinceInDayRent(),
                        packageBean.getExtraProvinceOutDayRent(),packageBean.getExtraProvinceOutDayFlow());
            default:break;
        }
        return "";
    }

    @Override
    protected void initViews() {
        packageNameView= (TextView) findViewById(R.id.text_view_package_name);
        partnerView= (TextView) findViewById(R.id.text_view_partner);
        operatorView= (TextView) findViewById(R.id.text_view_operator);
        monthRentView= (TextView) findViewById(R.id.text_view_month_rent);
        packageCallTimeView= (TextView) findViewById(R.id.text_view_package_calltime);
        extraPackageCallView= (TextView) findViewById(R.id.text_view_extra_package_call);
        packageFlowView= (TextView) findViewById(R.id.text_view_package_flow);
        extraPackageFlowView= (TextView) findViewById(R.id.text_view_extra_package_flow);
        freeFlowView= (TextView) findViewById(R.id.text_view_package_free_flow);
        startView= (TextView) findViewById(R.id.text_view_star);
        starRatingBar= (RatingBar) findViewById(R.id.rating_bar_star);
        myScoreRatingBar= (RatingBar) findViewById(R.id.rating_bar_my_score);
        myScoreView= (TextView) findViewById(R.id.text_view_my_score);
        monthConsumeView= (TextView) findViewById(R.id.text_view_month_consume);
        extraPackageInfoView = (TextView) findViewById(R.id.text_view_package_extra_info);
        linkBtn= (Button) findViewById(R.id.btn_package_link);
        scoreBtn= (Button) findViewById(R.id.btn_package_score);
        monthConsumeLayout= (LinearLayout) findViewById(R.id.layout_month_consume);
        extraPackageInfoLayout= (LinearLayout) findViewById(R.id.layout_package_extra_info);
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

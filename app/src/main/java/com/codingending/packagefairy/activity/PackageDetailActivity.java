package com.codingending.packagefairy.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.api.PackageService;
import com.codingending.packagefairy.entity.BaseResponse;
import com.codingending.packagefairy.entity.DataResponse;
import com.codingending.packagefairy.entity.ExtraFlowType;
import com.codingending.packagefairy.entity.PackageBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;
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

    private static final int MAX_SCORE_COUNT=999;//需要显示出来的最大评分人数（后续评分显示为999+）

    private int userId;
    private int packageId;
    private int startSource;//启动来源
    private String packageUrl;//套餐的官方说明链接

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
    private TextView starTitleView;//综合评分的标题（包含评分人数信息）
    private RatingBar myScoreRatingBar;
    private TextView myScoreView;
    private TextView monthConsumeView;
    private Button linkBtn;
    private Button scoreBtn;
    private TextView remarkView;
    private LinearLayout remarkLayout;
    private LinearLayout monthConsumeLayout;

    /**
     * 提供给ReportFragment的启动方法
     * @param packageBean 套餐实体类
     */
    public static void actionStart(Context context,PackageBean packageBean){
        Intent intent=new Intent(context,PackageDetailActivity.class);
        intent.putExtra(KEY_PACKAGE,packageBean);
        intent.putExtra(KEY_PACKAGE_ID,packageBean.getId());
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
        initData();
        initViews();
        initToolbar();
        loadData();
    }

    //初始化数据
    private void initData(){
        userId=PreferenceUtils.getInt(this,PreferenceUtils.KEY_USER_ID);
        packageId=getIntent().getIntExtra(KEY_PACKAGE_ID,0);
        startSource=getIntent().getIntExtra(KEY_START_SOURCE,SOURCE_FIND);
    }

    /**
     * 加载数据
     */
    private void loadData(){
        if(startSource==SOURCE_FIND){
            loadDataFromServer();
        }else{
            loadDataFromLocal();
        }
        loadScoreData();
    }

    //从服务器获取数据（根据套餐Id）
    private void loadDataFromServer(){
        monthConsumeLayout.setVisibility(View.GONE);//隐藏每月消费
        Call<DataResponse<PackageBean>> call= RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getPackageById(packageId);
        call.enqueue(new Callback<DataResponse<PackageBean>>() {
            @Override
            public void onResponse(Call<DataResponse<PackageBean>> call,Response<DataResponse<PackageBean>> response) {
                if(response.isSuccessful()){
                    DataResponse<PackageBean> body=response.body();
                    if(body!=null){
                        PackageBean packageBean=body.getData();
                        if(packageBean!=null){
                            packageUrl=packageBean.getUrl();//获取套餐链接
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
        packageUrl=packageBean.getUrl();//获取套餐链接
        bindDataToView(packageBean);

        monthConsumeLayout.setVisibility(View.VISIBLE);//显示每月消费
        monthConsumeView.setText(getString(R.string.package_detail_month_consume_content,packageBean.getTotalConsume()));
    }

    //加载与评分相关的数据
    private void loadScoreData(){
        //加载评分人数
        Call<DataResponse<Integer>> scoreCountCall=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getScoreCount(packageId);
        scoreCountCall.enqueue(new Callback<DataResponse<Integer>>() {
            @Override
            public void onResponse(Call<DataResponse<Integer>> call, Response<DataResponse<Integer>> response) {
                if(response.isSuccessful()){
                    DataResponse<Integer> body=response.body();
                    if(body!=null&&body.isSucceed()){
                        int count=body.getData();
                        if(count>MAX_SCORE_COUNT){//如果评论数过多就显示为999+
                            starTitleView.setText(R.string.package_detail_title_total_score_so_many);
                        }else{
                            starTitleView.setText(getString(R.string.package_detail_title_total_score,count));
                        }
                    }else{
                        starTitleView.setText(getString(R.string.package_detail_total_score_no_count));
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取指定套餐失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<Integer>> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure!");
            }
        });

        if(!isLogin()){//尚未登录
            myScoreView.setVisibility(View.VISIBLE);
            myScoreRatingBar.setVisibility(View.GONE);
            myScoreView.setText(getString(R.string.package_detail_not_login));
            return;
        }

        //获取我的评分数据
        Call<DataResponse<Integer>> call=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .getMyScore(packageId,userId);
        call.enqueue(new Callback<DataResponse<Integer>>() {
            @Override
            public void onResponse(Call<DataResponse<Integer>> call, Response<DataResponse<Integer>> response) {
                if(response.isSuccessful()){
                    DataResponse<Integer> body=response.body();
                    if(body!=null&&body.isSucceed()){
                        myScoreView.setVisibility(View.GONE);
                        myScoreRatingBar.setVisibility(View.VISIBLE);
                        myScoreRatingBar.setRating(body.getData()/2.0f);//将评分的一半作为进度（5颗星星对应10分）
                    }else{
                        myScoreView.setVisibility(View.VISIBLE);
                        myScoreView.setText(getString(R.string.package_detail_no_my_score));
                        myScoreRatingBar.setVisibility(View.GONE);
                    }
                }else{
                    LogUtils.w(TAG,"onResponse->获取指定套餐失败！");
                }
            }
            @Override
            public void onFailure(Call<DataResponse<Integer>> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure!");
            }
        });
    }

    /**
     * 将数据绑定到对应的View上
     * @param packageBean 套餐实体对象
     */
    private void bindDataToView(PackageBean packageBean){
        packageNameView.setText(packageBean.getName());
        partnerView.setText(packageBean.getPartner());
        operatorView.setText(packageBean.getOperator());
        formatMonthRentString(monthRentView,packageBean.getMonthRent());//格式化月租字符串（部分彩色、加粗）

        //设置评分数据
        if(Double.compare(packageBean.getStar(),0.0)==0){
            startView.setText(getString(R.string.package_detail_no_star));
            starRatingBar.setRating(0);
        }else{
            startView.setText(getString(R.string.package_detail_star,packageBean.getStar()));
            starRatingBar.setRating(Double.valueOf(packageBean.getStar()/2).floatValue());//将评分的一半作为进度（5颗星星对应10分）
        }

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

        //判断是否存在备注并设置说明文字
        if(TextUtils.isEmpty(packageBean.getRemark())){
            remarkLayout.setVisibility(View.GONE);//隐藏备注区域
        }else{
            remarkLayout.setVisibility(View.VISIBLE);//显示备注区域
            remarkView.setText(packageBean.getRemark());
        }

        //判断是否需要显示[官方链接]按钮
        if(TextUtils.isEmpty(packageBean.getUrl())){
            linkBtn.setVisibility(View.GONE);
        }else{
            linkBtn.setVisibility(View.VISIBLE);
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

    //判断用户是否已经登录
    private boolean isLogin(){
        //如果已经存在用户数据证明已登录
        return !TextUtils.isEmpty(PreferenceUtils.getString(this,PreferenceUtils.KEY_USER_NAME));
    }

    //显示评分对话框
    private void showScoreDialog(){
        if(!isLogin()){//尚未登录
            showToast(R.string.package_detail_need_login);
            return;
        }
        View view=View.inflate(this,R.layout.dialog_score,null);
        final RatingBar scoreRatingBar= (RatingBar) view.findViewById(R.id.rating_bar_score);
        new AlertDialog.Builder(this)
                .setTitle(R.string.package_detail_dialog_title_score)
                .setView(view)
                .setPositiveButton(R.string.dialog_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        int score=Float.valueOf(2*scoreRatingBar.getRating()).intValue();//将进度的两倍作为评分（5颗星星对应10分）
                        if(score>0){
                            doScore(score);//提交评分信息
                        }else{
                            showToast(R.string.package_detail_dialog_score_not_zero);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    //处理评分逻辑
    private void doScore(int score){
        Call<BaseResponse> call=RetrofitUtils.getRetrofit()
                .create(PackageService.class)
                .score(packageId,userId,score);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful()){
                    BaseResponse body=response.body();
                    if(body!=null&&body.isSucceed()){
                        showToast(R.string.package_detail_score_succeed);
                    }else{
                        showLongToast(R.string.package_detail_score_error);
                    }
                }else{
                    LogUtils.w(TAG,"请求失败...");
                }
                loadScoreData();//重新加载评分相关的数据
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                LogUtils.e(TAG,"Retrofit onFailure!");
                loadScoreData();//重新加载评分相关的数据
            }
        });
    }

    //在浏览器中显示套餐官方说明
    private void showPackageUrl(){
        if(!TextUtils.isEmpty(packageUrl)){
            Uri uri=Uri.parse(packageUrl);
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
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
        remarkView = (TextView) findViewById(R.id.text_view_package_remark);
        linkBtn= (Button) findViewById(R.id.btn_package_link);
        scoreBtn= (Button) findViewById(R.id.btn_package_score);
        monthConsumeLayout= (LinearLayout) findViewById(R.id.layout_month_consume);
        remarkLayout = (LinearLayout) findViewById(R.id.layout_package_remark);
        starTitleView= (TextView) findViewById(R.id.text_view_package_title_total_score);

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//跳转到官方链接
                showPackageUrl();
            }
        });
        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScoreDialog();//显示评分对话框
            }
        });
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}

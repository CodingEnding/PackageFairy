package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.activity.PackageDetailActivity;
import com.codingending.packagefairy.entity.ExtraFlowType;
import com.codingending.packagefairy.entity.PackageBean;

import java.util.List;

/**
 * 套餐推荐结果列表的Adapter
 * Created by CodingEnding on 2018/4/7.
 */

public class ReportRecyclerAdapter extends RecyclerView.Adapter<ReportRecyclerAdapter.ViewHolder>{
    private List<PackageBean> dataList;//数据源
    private Context context;
    private LayoutInflater inflater;//布局解析器
    private OnAdapterItemClickListener itemClickListener;//点击监听器

    public ReportRecyclerAdapter(List<PackageBean> dataList,Context context) {
        this.context=context;
        this.dataList = dataList;
        inflater=LayoutInflater.from(context);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.itemClickListener = onAdapterItemClickListener;
    }

    @Override
    public ReportRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_item_report,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportRecyclerAdapter.ViewHolder holder, int position) {
        final PackageBean packageBean=dataList.get(position);
        holder.packageNameView.setText(packageBean.getName());
        holder.packagePartnerView.setText(packageBean.getPartner());
        holder.packageOperatorView.setText(packageBean.getOperator());

        //格式化预计消费文字串（部分彩色、加粗）
        formatConsumeString(holder.packageConsumeView,packageBean.getTotalConsume());

        if(packageBean.getFreeFlowType()==1){//存在免流应用信息
            holder.freeFlowView.setVisibility(View.VISIBLE);
            holder.freeFlowView.setText(context.getString(R.string.report_free_flow));
        }else{
            holder.freeFlowView.setVisibility(View.GONE);
        }

        //处理套餐外流量计费方式的显示数据
        bindExtraTypeView(holder.extraFlowTypeView,packageBean);

        bindListener(holder.itemView,packageBean);//设置监听器
    }

    //设置监听器
    private void bindListener(View itemView,final PackageBean packageBean){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(packageBean);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemLongClick(packageBean);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //格式化预计消费的字符串（部分彩色、加粗）
    private void formatConsumeString(TextView consumeView,double consume){
        int totalConsume=Double.valueOf(consume).intValue();//只取预计消费的整数部分
        String consumeStr=context.getString(R.string.report_consume_text,totalConsume);//预计消费的原始文字

        int consumeColor=context.getResources().getColor(R.color.report_consume_text);
        Spannable spannableStr=new SpannableString(consumeStr);
        String totalConsumeStr=String.valueOf(totalConsume);//将消费金额转化为字符串
        int start=consumeStr.indexOf(totalConsumeStr);
        int end=start+totalConsumeStr.length();
        spannableStr.setSpan(new ForegroundColorSpan(consumeColor),start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStr.setSpan(new RelativeSizeSpan(1.1f),start,end,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        consumeView.setText(spannableStr);//格式化字符串
    }

    //处理套餐外流量计费方式的显示数据
    private void bindExtraTypeView(TextView extraFlowTypeView,PackageBean packageBean){
        switch (packageBean.getExtraFlowType()){
            case ExtraFlowType.EXTRA_FLOW_TYPE_ONE:
                int consumePerGB=Double.valueOf(packageBean.getExtraCountryFlow()*1000).intValue();//套餐外1G流量的金额（取整数部分）
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_one,consumePerGB));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_TWO:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_two));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_THREE:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_three));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_FOUR:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_four));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_FIVE:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_five));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_SIX:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_six));
                break;
            case ExtraFlowType.EXTRA_FLOW_TYPE_SEVEN:
                extraFlowTypeView.setText(context.getString(R.string.report_extra_type_seven));
                break;
            default:break;
        }
    }

    //提供给Adapter使用的监听器接口
    public interface OnAdapterItemClickListener {
        void onItemClick(PackageBean packageBean);
        void onItemLongClick(PackageBean packageBean);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView packageNameView;
        TextView packagePartnerView;
        TextView packageOperatorView;
        TextView packageConsumeView;//预计消费
        TextView extraFlowTypeView;//套餐外流量计费方式
        TextView freeFlowView;//免流应用说明

        public ViewHolder(View itemView) {
            super(itemView);
            packageNameView= (TextView) itemView.findViewById(R.id.text_view_package_name);
            packagePartnerView= (TextView) itemView.findViewById(R.id.text_view_package_partner);
            packageOperatorView= (TextView) itemView.findViewById(R.id.text_view_package_operator);
            packageConsumeView= (TextView) itemView.findViewById(R.id.text_view_package_consume);
            extraFlowTypeView= (TextView) itemView.findViewById(R.id.text_view_package_extra_type);
            freeFlowView= (TextView) itemView.findViewById(R.id.text_view_package_free_flow);
        }
    }

}

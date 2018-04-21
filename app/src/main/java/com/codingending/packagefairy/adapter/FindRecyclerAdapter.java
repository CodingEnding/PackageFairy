package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.entity.SimplePackageBean;
import com.codingending.packagefairy.utils.FormatUtils;

import java.util.List;
import java.util.Random;

/**
 * 发现界面列表的Adapter
 * Created by CodingEnding on 2018/4/7.
 */

public class FindRecyclerAdapter extends RecyclerView.Adapter<FindRecyclerAdapter.ViewHolder>{
    private List<SimplePackageBean> dataList;//数据源
    private Context context;
    private LayoutInflater inflater;//布局解析器
    private OnAdapterItemClickListener itemClickListener;//点击监听器
    private int[] colorIdArray={R.color.find_card_background_one,R.color.find_card_background_two,
            R.color.find_card_background_three,R.color.find_card_background_four,R.color.find_card_background_five,
            R.color.find_card_background_six,R.color.find_card_background_seven};//存储预设的颜色Id

    public FindRecyclerAdapter(List<SimplePackageBean> dataList, Context context) {
        this.context=context;
        this.dataList = dataList;
        inflater=LayoutInflater.from(context);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.itemClickListener = onAdapterItemClickListener;
    }

    @Override
    public FindRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view=inflater.inflate(R.layout.list_item_find,parent,false);
        View view=inflater.inflate(R.layout.list_item_find_grid,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FindRecyclerAdapter.ViewHolder holder, int position) {
        SimplePackageBean packageBean=dataList.get(position);
        holder.packageNameView.setText(packageBean.getName());

        //如果合作方包含空格就默认只使用空格后的后缀（如“阿里巴巴 钉钉”就返回“钉钉”）
        String partner=packageBean.getPartner();
        holder.packagePartnerView.setText(FormatUtils.getSpacePostfix(partner));

        if(Double.compare(packageBean.getStar(),0.0)==0){//如果评分为0就显示特殊的提示信息
            holder.packageStarView.setText(context.getString(R.string.find_package_no_star));
        }else{
            holder.packageStarView.setText(context.getString(
                    R.string.find_package_star,packageBean.getStar()));//评分数据
        }

        holder.monthRentView.setText(context.getString(R.string.find_package_month_rent,
                packageBean.getMonthRent()));//设置月租

        bindListener(holder.itemView,packageBean);//设置监听器

        //随机设置顶部区域的背景色
        int randNum=new Random().nextInt(50);
        holder.topLayout.setBackgroundResource(colorIdArray[randNum%colorIdArray.length]);

//        if(packageBean.getFreeFlowType()==1){//存在免流应用信息
//            holder.freeFlowView.setVisibility(View.VISIBLE);
//            holder.freeFlowView.setText(context.getString(R.string.report_free_flow));
//        }else{
//            holder.freeFlowView.setVisibility(View.GONE);
//        }
    }

    //设置监听器
    private void bindListener(View itemView,final SimplePackageBean packageBean){
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

    //提供给Adapter使用的监听器接口
    public interface OnAdapterItemClickListener {
        void onItemClick(SimplePackageBean packageBean);
        void onItemLongClick(SimplePackageBean packageBean);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout topLayout;
        TextView packageNameView;
        TextView packagePartnerView;
//        TextView freeFlowView;//免流应用说明
        TextView monthRentView;//月租
        TextView packageStarView;//套餐评分

        public ViewHolder(View itemView) {
            super(itemView);
            topLayout= (LinearLayout) itemView.findViewById(R.id.layout_top_area);
            packageNameView= (TextView) itemView.findViewById(R.id.text_view_package_name);
            packagePartnerView= (TextView) itemView.findViewById(R.id.text_view_package_partner);
//            freeFlowView= (TextView) itemView.findViewById(R.id.text_view_package_free_flow);
            packageStarView= (TextView) itemView.findViewById(R.id.text_view_package_star);
            monthRentView= (TextView) itemView.findViewById(R.id.text_view_package_month_rent);
        }
    }

}

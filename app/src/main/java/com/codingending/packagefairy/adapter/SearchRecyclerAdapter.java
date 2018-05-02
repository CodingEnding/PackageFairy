package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.entity.SimplePackageBean;

import java.util.List;

/**
 * 搜索结果列表的Adapter
 * Created by CodingEnding on 2018/4/30.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>{
    private List<SimplePackageBean> dataList;
    private LayoutInflater inflater;
    private Context context;
    private OnAdapterItemClickListener itemClickListener;

    public SearchRecyclerAdapter(List<SimplePackageBean> dataList,Context context) {
        this.dataList = dataList;
        this.context = context;
        this.inflater=LayoutInflater.from(context);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.itemClickListener = onAdapterItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view=inflater.inflate(R.layout.list_item_filter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimplePackageBean packageBean=dataList.get(position);
        holder.packageNameView.setText(packageBean.getName());
        holder.partnerView.setText(packageBean.getPartner());

        if(Double.compare(packageBean.getStar(),0.0)==0){//如果评分为0就显示特殊的提示信息
            holder.starView.setText(context.getString(R.string.find_package_no_star));
        }else{
            holder.starView.setText(context.getString(
                    R.string.find_package_star,packageBean.getStar()));//评分数据
        }
        holder.monthRentView.setText(context.getString(R.string.find_package_month_rent,
                packageBean.getMonthRent()));//设置月租

        //绑定监听器
        bindListener(holder.itemView,packageBean);
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
        TextView packageNameView;
        TextView partnerView;
        TextView starView;//评分
        TextView monthRentView;//月租

        public ViewHolder(View itemView) {
            super(itemView);
            packageNameView= (TextView) itemView.findViewById(R.id.text_view_package_name);
            partnerView= (TextView) itemView.findViewById(R.id.text_view_package_partner);
            starView= (TextView) itemView.findViewById(R.id.text_view_package_star);
            monthRentView= (TextView) itemView.findViewById(R.id.text_view_package_month_rent);
        }
    }
}

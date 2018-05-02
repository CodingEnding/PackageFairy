package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.entity.AppBean;
import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.utils.AppUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 流量排行列表的适配器
 * Created by CodingEnding on 2018/4/25.
 */

public class FlowRankRecyclerAdapter extends RecyclerView.Adapter<FlowRankRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<AppBean> dataList;
    private LayoutInflater inflater;

    public FlowRankRecyclerAdapter(Context context,List<AppBean> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_item_flow_rank,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppBean appBean=dataList.get(position);
        holder.appNameView.setText(appBean.getAppName());
        holder.appIconView.setImageDrawable(appBean.getAppIcon());//设置应用图标

        //格式化数据流量消耗（KB或MB）
        int flowAmount=appBean.getFlowAmount();
        String flowText="";
        if(flowAmount>=1024){//1024kb以上就用MB
            DecimalFormat decimalFormat=new DecimalFormat("0.00");//保留两位小数
            double flowMBAmount=appBean.getFlowAmount()/1024.0;//MB
            flowText=context.getString(R.string.rank_flow_mb_text,
                    decimalFormat.format(flowMBAmount));
        }else{
            flowText=context.getString(R.string.rank_flow_kb_text,flowAmount);
        }
        holder.flowView.setText(flowText);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView appIconView;
        private TextView appNameView;
        private TextView flowView;

        public ViewHolder(View itemView) {
            super(itemView);
            appIconView= (ImageView) itemView.findViewById(R.id.image_view_icon);
            appNameView= (TextView) itemView.findViewById(R.id.text_view_app);
            flowView= (TextView) itemView.findViewById(R.id.text_view_flow);
        }
    }

}

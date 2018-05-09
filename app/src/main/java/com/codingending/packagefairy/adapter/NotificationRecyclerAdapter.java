package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.entity.NotificationBean;
import com.codingending.packagefairy.utils.FormatUtils;

import java.util.List;

/**
 * 通知列表的适配器
 * Created by CodingEnding on 2018/5/7.
 */

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<NotificationBean> dataList;
    private LayoutInflater inflater;

    public NotificationRecyclerAdapter(Context context, List<NotificationBean> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_view_notification,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationBean notificationBean=dataList.get(position);

        holder.titleView.setText(notificationBean.getTitle());
        holder.contentView.setText(notificationBean.getContent());
        holder.dateView.setText(FormatUtils.formatDate(notificationBean.getSendTime()));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleView;
        TextView contentView;
        TextView dateView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView= (TextView) itemView.findViewById(R.id.text_view_title);
            contentView= (TextView) itemView.findViewById(R.id.text_view_content);
            dateView= (TextView) itemView.findViewById(R.id.text_view_date);
        }
    }

}

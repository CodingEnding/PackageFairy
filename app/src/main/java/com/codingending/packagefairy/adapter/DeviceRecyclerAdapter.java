package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.codingending.packagefairy.entity.DeviceBean;
import com.codingending.packagefairy.utils.LogUtils;
import com.codingending.packagefairy.utils.PreferenceUtils;

import java.util.List;

/**
 * 设备列表的Adapter
 * Created by CodingEnding on 2018/4/23.
 */

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.ViewHolder>{
    private List<DeviceBean> dataList;
    private LayoutInflater inflater;
    private Context context;

    public DeviceRecyclerAdapter(List<DeviceBean> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_item_device,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeviceBean deviceBean=dataList.get(position);
        holder.deviceTypeView.setText(deviceBean.getDeviceType());
        holder.systemVersionView.setText(deviceBean.getSystemVersion());

        //如果识别码和当前设备的识别码一致，就显示右侧[本机]标识
        String deviceFinger= PreferenceUtils.getString(context,PreferenceUtils.KEY_DEVICE_FINGER,"");
        if(deviceFinger.equals(deviceBean.getDeviceFinger())){
            holder.nowDeviceView.setVisibility(View.VISIBLE);
            LogUtils.i("DeviceAdapter",""+(holder.nowDeviceView.getVisibility()==View.VISIBLE));
        }else{
            holder.nowDeviceView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView deviceTypeView;
        TextView systemVersionView;
        TextView nowDeviceView;//显示是否为本机

        public ViewHolder(View itemView) {
            super(itemView);
            deviceTypeView= (TextView) itemView.findViewById(R.id.text_view_device_type);
            systemVersionView= (TextView) itemView.findViewById(R.id.text_view_system_version);
            nowDeviceView= (TextView) itemView.findViewById(R.id.text_view_device_now);
        }
    }

}

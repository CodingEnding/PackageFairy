package com.codingending.packagefairy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codingending.packagefairy.R;

import java.util.List;
import java.util.Random;

/**
 * 套餐分类列表的Adapter
 * Created by CodingEnding on 2018/4/21.
 */

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder>{
    private List<String> dataList;
    private Context context;
    private LayoutInflater inflater;
    private OnAdapterItemClickListener itemClickListener;
    private Random random;

    private int[] backgroundIdArray={R.drawable.category_drawable_one,R.drawable.category_drawable_two,
            R.drawable.category_drawable_three,R.drawable.category_drawable_four};//预置的背景drawable的Id数组

    public CategoryRecyclerAdapter(List<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        this.inflater=LayoutInflater.from(context);
        this.random=new Random();
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.itemClickListener = onAdapterItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_item_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {
        String category=dataList.get(position);
        holder.categoryView.setText(category);

        //随机设置一个预置背景
        int randNum=random.nextInt(50);
        holder.categoryView.setBackgroundResource(backgroundIdArray[randNum%backgroundIdArray.length]);

        bindListener(holder.itemView,category);//设置监听器
    }

    //设置监听器
    private void bindListener(View itemView,final String category){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(category);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemLongClick(category);
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
        void onItemClick(String category);
        void onItemLongClick(String category);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView categoryView;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryView= (TextView) itemView;
        }
    }
}

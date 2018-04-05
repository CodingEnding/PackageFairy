package com.codingending.packagefairy.chart;

import android.content.Context;
import android.widget.TextView;

import com.codingending.packagefairy.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * 数据流量的点击弹窗
 * Created by CodingEnding on 2018/4/5.
 */

public class DataFlowWindowView extends MarkerView{
    private TextView flowTextView;//流量文本区域

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public DataFlowWindowView(Context context, int layoutResource) {
        super(context, layoutResource);
        flowTextView= (TextView) findViewById(R.id.text_view_flow);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String content=Float.valueOf(e.getY()).intValue()+"M";//格式化处理
        flowTextView.setText(content);
        super.refreshContent(e, highlight);
    }

    private MPPointF offset;

    @Override
    public MPPointF getOffset() {
        if(offset==null){
            offset=new MPPointF(-(getWidth()/2),-(getWidth()/2));//水平/垂直居中
        }
        return offset;
    }
}

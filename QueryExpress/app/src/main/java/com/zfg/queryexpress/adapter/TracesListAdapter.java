package com.zfg.queryexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zfg.queryexpress.R;
import com.zfg.queryexpress.beans.QueryResultBean;

import java.util.Collections;
import java.util.List;

/**
 * Created by zfg on 2016/12/22.
 */
public class TracesListAdapter extends BaseAdapter {
    private List<QueryResultBean.TracesBean> mTracesList;
    private Context mContext;
    LayoutInflater mInflater;


    public TracesListAdapter(List<QueryResultBean.TracesBean> tracesList, Context context) {
        Collections.reverse(tracesList);
        mTracesList = tracesList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mTracesList == null ? 0 : mTracesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTracesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_traces, null);
            viewHolder = new ViewHolder();
             viewHolder.mAcceptTimeTextView = (TextView) convertView.findViewById(R.id.accept_time);
            viewHolder.mAcceptStationTextView = (TextView) convertView.findViewById(R.id.accept_station);
            convertView.setTag(viewHolder);
        } else {
            //说明开始上下滑动，后面的所有行布局采用第一次绘制时的缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mAcceptTimeTextView.setText(mTracesList.get(position).getAcceptTime());
        viewHolder.mAcceptStationTextView.setText(mTracesList.get(position).getAcceptStation());
        return convertView;
    }

    class ViewHolder {
        //item布局控件
        TextView mAcceptTimeTextView;
        TextView mAcceptStationTextView;
    }
}

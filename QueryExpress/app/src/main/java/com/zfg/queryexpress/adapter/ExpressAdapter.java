package com.zfg.queryexpress.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.beans.ExpressGson;

import java.util.List;

/**
 * Created by ZFG on 2016/12/14.
 */
public class ExpressAdapter extends BaseAdapter {
    private List<ExpressGson> mResultBeanList;
    private Context mContext;
    LayoutInflater mInflater;

    public ExpressAdapter(List<ExpressGson> ResultBeanList, Context context) {
        mResultBeanList = ResultBeanList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mResultBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mResultBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_express, null);
            viewHolder = new ViewHolder();
            //express
            viewHolder.itemCompanyImg = (ImageView) convertView.findViewById(R.id.item_company_img);
            viewHolder.itemExpressNo = (TextView) convertView.findViewById(R.id.item_express_no);
            viewHolder.itemExpressAsk = (TextView) convertView.findViewById(R.id.item_express_ask);
            viewHolder.itemExpressTime = (TextView) convertView.findViewById(R.id.item_express_time);
            convertView.setTag(viewHolder);
        } else {
            //说明开始上下滑动，后面的所有行布局采用第一次绘制时的缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ExpressGson express = mResultBeanList.get(position);

        Glide.with(mContext)
                .load(express.getCompany_img())
//                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(viewHolder.itemCompanyImg);
       // Log.e("img_url", express.getCompany_img());
        viewHolder.itemExpressNo.setText(express.getCompany_name() + " " + express.getExpress_no());
        viewHolder.itemExpressAsk.setText(express.getExpress_remark());
        viewHolder.itemExpressTime.setText(express.getExpress_time());
        return convertView;
    }

    class ViewHolder {
        //item布局控件
        ImageView itemCompanyImg;
        TextView itemExpressNo;
        TextView itemExpressAsk;
        TextView itemExpressTime;
    }
}

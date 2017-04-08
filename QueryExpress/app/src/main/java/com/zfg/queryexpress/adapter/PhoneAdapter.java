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
import com.zfg.queryexpress.beans.Company;

import java.util.List;

/**
 * Created by zfg on 2016/12/23.
 */
public class PhoneAdapter extends BaseAdapter {
    private List<Company> mCompanyList;
    private Context mContext;
    LayoutInflater mInflater;


    public PhoneAdapter(List<Company> companyList, Context context) {
        mCompanyList = companyList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mCompanyList == null ? 0 : mCompanyList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCompanyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_phone, null);
            viewHolder = new ViewHolder();
            viewHolder.mCompanyImgImageView = (ImageView) convertView.findViewById(R.id.item_company_img);
            viewHolder.mCompanyNameTextView = (TextView) convertView.findViewById(R.id.item_company_name);
            viewHolder.mCompanyPhoneTextView = (TextView) convertView.findViewById(R.id.item_phone);
            convertView.setTag(viewHolder);
        } else {
            //说明开始上下滑动，后面的所有行布局采用第一次绘制时的缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(mCompanyList.get(position).getCompany_img())
//                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(viewHolder.mCompanyImgImageView);
        viewHolder.mCompanyNameTextView.setText(mCompanyList.get(position).getCompany_name());
        viewHolder.mCompanyPhoneTextView.setText(mCompanyList.get(position).getCompany_phone());
        return convertView;
    }

    class ViewHolder {
        //item布局控件
        ImageView mCompanyImgImageView;
        TextView mCompanyNameTextView;
        TextView mCompanyPhoneTextView;
    }
}

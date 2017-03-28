package com.zfg.queryexpress.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.beans.ExpressGson;

import java.util.List;

/**
 * Created by ZFG on 2016/12/14.
 */
public class ExpressListAdapter extends SwipeMenuAdapter<ExpressListAdapter.DefaultViewHolder> {
    private List<ExpressGson> mResultBeanList;
    private Context mContext;
    LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public ExpressListAdapter(List<ExpressGson> ResultBeanList,Context context){
        mResultBeanList = ResultBeanList;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express, parent, false);
    }

    @Override
    public ExpressListAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ExpressListAdapter.DefaultViewHolder holder, int position) {
       /* Glide.with(mContext)
                .load(mMusicList.get(position).getMusic_photo())
                .into(holder.mItemCompanyImageView);*/
        //快递公司
        holder.mItemExpressNoTextView.setText(mResultBeanList.get(position).getCompany_name()+" "+mResultBeanList.get(position).getExpress_no());
        holder.mItemExpressRemarkTextView.setText("" + mResultBeanList.get(position).getExpress_remark());
        holder.mItemExpressTiemTextView.setText("" + mResultBeanList.get(position).getExpress_time());
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mResultBeanList == null ? 0 : mResultBeanList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mItemCompanyImageView;
        TextView mItemExpressNoTextView;
        TextView mItemExpressRemarkTextView;
        TextView mItemExpressTiemTextView;
        OnItemClickListener mOnItemClickListener;
        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemCompanyImageView= (ImageView) itemView.findViewById(R.id.item_company_img);
            mItemExpressNoTextView= (TextView) itemView.findViewById(R.id.item_express_no);
            mItemExpressRemarkTextView= (TextView) itemView.findViewById(R.id.item_express_ask);
            mItemExpressTiemTextView= (TextView) itemView.findViewById(R.id.item_express_time);
        }
        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}

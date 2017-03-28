package com.zfg.queryexpress.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.adapter.PhoneAdapter;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.beans.Company;
import com.zfg.queryexpress.common.WaitDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/10/26.
 */
public class PhoneFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final int NOHTTP_WHAT_GETALL = 1;
    View mView;
    private ListView mListView;
    private PhoneAdapter mPhoneListAdapter;
    private List<Company> mCompanyList;

    String mPath;
    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_phone, null);
        initViews();
        initData();
        sendRequest();
        initListener();
        return mView;
    }

    private void initListener() {
        mListView.setOnItemClickListener(this);

    }

    private void sendRequest() {
        mDialog = new WaitDialog(getActivity());//提示框
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        mPath = myApplication.url;
        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath + "/expressservlet", RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "getCompany");/*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_GETALL, request, onResponseListener);
    }

    private void initData() {
        mCompanyList=new ArrayList<>();
    }

    private void initViews() {
        mListView= (ListView) mView.findViewById(R.id.phone_listview);
    }
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        public void onSucceed(int what, Response<String> response) {
            if (what == NOHTTP_WHAT_GETALL) {
                String result = response.get();// 响应结果
                Log.e("result",result);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Company>>() {
                }.getType();
                List<Company> resultList=new ArrayList<>();
                resultList = gson.fromJson(result, type);
                mCompanyList.addAll(resultList);
                mPhoneListAdapter=new PhoneAdapter(mCompanyList,getActivity());
                mListView.setAdapter(mPhoneListAdapter);
                mPhoneListAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            //请求失败
            if (exception instanceof ServerError) {// 服务器错误
                Toast.makeText(getActivity(), "服务器发生错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NetworkError) {// 网络不好
                Toast.makeText(getActivity(), "请检查网络", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof TimeoutError) {// 请求超时
                Toast.makeText(getActivity(), "请求超时，网络不好或者服务器不稳定", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                Toast.makeText(getActivity(), "未发现指定服务器", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof URLError) {// URL是错的
                Toast.makeText(getActivity(), "URL错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NotFoundCacheError) {
                Toast.makeText(getActivity(), "没有发现缓存", Toast.LENGTH_SHORT).show();
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            } else {
                Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(int what) {
            //请求完成
            mDialog.dismiss();
        }

        @Override
        public void onStart(int what) {
            // 请求开始，这里可以显示一个dialog
            mDialog.show();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" +mCompanyList.get(position).getCompany_phone());
        intent.setData(data);
        startActivity(intent);
    }
}

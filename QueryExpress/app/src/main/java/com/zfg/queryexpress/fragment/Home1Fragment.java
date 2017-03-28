package com.zfg.queryexpress.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import com.zfg.queryexpress.activity.ExpressActivity;
import com.zfg.queryexpress.adapter.ExpressAdapter;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.beans.ExpressGson;
import com.zfg.queryexpress.common.WaitDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zfg on 2016/12/22.
 */
public class Home1Fragment extends Fragment {
    public static final int NOHTTP_WHAT_GETALL = 1;
    private String mPageNo;
    View mView;
    /**
     * 状态类型常量
     */
    public static final String STATUS_TYPE = "pagerType";
    private LinearLayout mLinearLayout;
    private PullToRefreshListView mListView;
    private ExpressAdapter mExpressAdapter;
    private List<ExpressGson> mQueryResultList;

    private Bundle bundle;
    private String expressNo;
    private String Code;

    private String mPath;
    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home1, null);
        initViews();
        //设置刷新模式为BOTH才可以上拉和下拉都能起作用,必须写在前面
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        initRefreshListView();
        initData();
        initListener();
        requestData();
        getData();
        return mView;
    }

    private void initListener() {
        //设置上拉和下拉时候的监听器
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //cur = 1;
//                Log.e("cur", "" + cur);
                mQueryResultList.clear();
                getData();
            }

            //上拉时
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //页码加一
                //cur++;
//                Log.e("cur", "" + cur);
                getData();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("position", "" + position);
                Intent intent = new Intent();
                intent.putExtra("company_img",mQueryResultList.get(position-1).getCompany_img());
                intent.putExtra("company_name",mQueryResultList.get(position-1).getCompany_name());
                intent.putExtra("express_no",mQueryResultList.get(position-1).getExpress_no());
                intent.putExtra("remark",mQueryResultList.get(position-1).getExpress_remark());
               /* Bundle bundle = new Bundle();
                bundle.putSerializable("expressgson", mQueryResultList.get(position-1));
                intent.putExtras(bundle);*/
                intent.setClass(getActivity(), ExpressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        mDialog = new WaitDialog(getActivity());//提示框
        mListView = (PullToRefreshListView) mView.findViewById(R.id.express_listview);
        mListView.setEmptyView(mView.findViewById(R.id.empty));
    }

    private void initRefreshListView() {
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("正在刷新");
        startLabels.setReleaseLabel("放开刷新");
        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在载入...");
        endLabels.setReleaseLabel("放开加载...");
    }

    private void initData() {
        //初始化数据
        mQueryResultList = new ArrayList<>();
        //接收数据
        bundle = getArguments();
        if (bundle != null) {
            expressNo = bundle.getString("expressNo");
            Code = bundle.getString("CompanyCode");
            Log.e("expressNo", "" + expressNo);
            Log.e("Code", "" + Code);
            ExpressGson expressGson = new ExpressGson();
            expressGson.setCompany_name(Code);
            expressGson.setExpress_no(expressNo);
           /* mQueryResultList.add(expressGson);
            Log.e("size",mQueryResultList.get(0).toString());
            mExpressAdapter = new ExpressAdapter(mQueryResultList, getActivity());
       */
        }
        mExpressAdapter = new ExpressAdapter(mQueryResultList, getActivity());

    }

    private void requestData() {
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        mPath = myApplication.url;
        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath + "/expressservlet", RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "getall");
        request.add("cur", 1);
        request.add("user_id", 1);
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_GETALL, request, onResponseListener);
    }

    private void getData() {
        //接收数据
        bundle = getArguments();
        if (bundle != null) {
            expressNo = bundle.getString("expressNo");
            Code = bundle.getString("CompanyCode");
            Log.e("expressNo", "" + expressNo);
            Log.e("Code", "" + Code);
            ExpressGson expressGson=new ExpressGson();
            expressGson.setExpress_no(expressNo);
            expressGson.setCompany_name(Code);
            mQueryResultList.add(expressGson);
            mListView.setAdapter(mExpressAdapter);
            Log.e("size", "list大小" + mQueryResultList.size());
                   /* // 通知UI刷新页面
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);*/
            Log.e("zfg", "list" + mQueryResultList.size());
            //通知刷新
            mExpressAdapter.notifyDataSetChanged();
            //表示刷新完成
            mListView.onRefreshComplete();
        }
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            // 请求开始，这里可以显示一个dialog
            mDialog.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case NOHTTP_WHAT_GETALL:
                    //得到数据
                    // 请求成功
                    String result = response.get();// 响应结果
                    Log.e("zfg", result);
                    //把JSON格式的字符串改为Student对象
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ExpressGson>>() {
                    }.getType();
                    List<ExpressGson> expressGsonList = new ArrayList<>();
                    expressGsonList = gson.fromJson(result, type);
                    mQueryResultList.addAll(expressGsonList);
                    mListView.setAdapter(mExpressAdapter);
                    Log.e("size", "list大小" + mQueryResultList.size());
                   /* // 通知UI刷新页面
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);*/
                    Log.e("zfg", "list" + mQueryResultList.size());
                    //通知刷新
                    mExpressAdapter.notifyDataSetChanged();
                    //表示刷新完成
                    mListView.onRefreshComplete();
                    break;
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
    };
}

package com.zfg.queryexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.zfg.queryexpress.adapter.TracesListAdapter;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.beans.QueryResultBean;
import com.zfg.queryexpress.common.WaitDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



public class ExpressActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int NOHTTP_WHAT_GETTRACES = 5;
    private String Tag = "ExpressActivity";

    private ImageView mBackImageView, mCompanyImageView;
    private TextView mExpressNoTextView, mRemarkTextView;
    private ListView mListView;

    private TracesListAdapter mTracesListAdapter;

    private String mPath;
    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;
    private List<QueryResultBean.TracesBean> mTracesBeenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express);
        initViews();
        initData();
        initListener();
        getData();
    }

    private void initListener() {
        mBackImageView.setOnClickListener(this);
    }

    private void initData() {
        mTracesBeenList = new ArrayList<>();
    }

    private void initViews() {
        mDialog = new WaitDialog(this);//提示框
        mBackImageView = (ImageView) findViewById(R.id.back_image);
        mCompanyImageView = (ImageView) findViewById(R.id.express_img);
        mExpressNoTextView = (TextView) findViewById(R.id.express_no);
        mRemarkTextView = (TextView) findViewById(R.id.express_remark);
        mListView = (ListView) findViewById(R.id.express_listView1);
    }

    private void getData() {
        Intent intent = getIntent();
       /* ExpressGson expressGson = (ExpressGson) intent.getSerializableExtra("expressgson");
        Log.e(Tag, expressGson.toString());*/
        String company_img = intent.getStringExtra("company_img");
        String company_name = intent.getStringExtra("company_name");
        String express_no = intent.getStringExtra("express_no");
        String remark = intent.getStringExtra("remark");
        Glide.with(this)
                .load(company_img)
//                .bitmapTransform(new CropCircleTransformation(this))
                .into(mCompanyImageView);
        mExpressNoTextView.setText(company_name + " " + express_no);
        mRemarkTextView.setText(remark);
        MyApplication myApplication = (MyApplication) this.getApplication();
        mPath = myApplication.url;
        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath + "/expressservlet", RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "getOne");
        request.add("express_no", express_no);
        request.add("express_companycode", company_name);
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_GETTRACES, request, onResponseListener);
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
                case NOHTTP_WHAT_GETTRACES:
                    mTracesBeenList.clear();
                    //得到数据
                    // 请求成功
                    String result = response.get();// 响应结果
                    Log.e("ExpressActivity", result);
                    Gson gson = new Gson();
                    Type type = new TypeToken<QueryResultBean>() {
                    }.getType();
                    QueryResultBean queryResultBean = new QueryResultBean();
                    queryResultBean = gson.fromJson(result, type);
                    mTracesBeenList.addAll(queryResultBean.getTraces());
                    Log.e("size", "list大小" + mTracesBeenList.size());
                    mTracesListAdapter = new TracesListAdapter(mTracesBeenList, ExpressActivity.this);

                    mListView.setAdapter(mTracesListAdapter);
                    //通知刷新
                    mTracesListAdapter.notifyDataSetChanged();
                   /* Log.e("zfg", result);
                    //把JSON格式的字符串改为Student对象
                    List<ExpressGson> expressGsonList = new ArrayList<>();

                   */
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            //请求失败
            if (exception instanceof ServerError) {// 服务器错误
                Toast.makeText(ExpressActivity.this, "服务器发生错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NetworkError) {// 网络不好
                Toast.makeText(ExpressActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof TimeoutError) {// 请求超时
                Toast.makeText(ExpressActivity.this, "请求超时，网络不好或者服务器不稳定", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                Toast.makeText(ExpressActivity.this, "未发现指定服务器", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof URLError) {// URL是错的
                Toast.makeText(ExpressActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NotFoundCacheError) {
                Toast.makeText(ExpressActivity.this, "没有发现缓存", Toast.LENGTH_SHORT).show();
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            } else {
                Toast.makeText(ExpressActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(int what) {
            //请求完成
            mDialog.dismiss();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_image:
                finish();
                break;
        }
    }
}

package com.zfg.queryexpress.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.common.WaitDialog;
import com.zfg.queryexpress.utils.ToastUtils;
import com.znq.zbarcode.CaptureActivity;

/**
 * Created by admin on 2016/10/26.
 */
public class ExpressFragment extends Fragment implements View.OnClickListener {
    public static final int NOHTTP_WHAT_LOAD = 1;
    public static final int QR_CODE = 10;
    View mView;
    private EditText mOrdernoEditText, mCompanyEditText;
    private Button mButton;
    private ImageView mScanImageView;
    String mPath;
    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;

    private RelativeLayout mCompanyRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_express, null);
        initViews();
        initListener();
        initRequest();
        return mView;
    }

    private void initRequest() {
        mDialog = new WaitDialog(getActivity());//提示框
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        mPath = myApplication.url;
    }

    private void initListener() {
        mCompanyRelativeLayout.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mScanImageView.setOnClickListener(this);
    }

    private void initViews() {
        mCompanyRelativeLayout = (RelativeLayout) mView.findViewById(R.id.company_rl);
        mOrdernoEditText = (EditText) mView.findViewById(R.id.order_edit);
        mCompanyEditText = (EditText) mView.findViewById(R.id.company_edit);
        mScanImageView = (ImageView) mView.findViewById(R.id.scan_img);
        mButton = (Button) mView.findViewById(R.id.save_btn);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_rl:
                selectCompanyDialog();
                break;
            case R.id.save_btn:
                Log.e("orderNo", "11" + mOrdernoEditText.getText().toString());
                Log.e("company", "22" + mCompanyEditText.getText().toString());
                if (mOrdernoEditText.getText().toString().equals("")) {
                    ToastUtils.showToast(getActivity(), "订单号不能为空");
                } else if (mCompanyEditText.getText().toString().equals("")) {
                    ToastUtils.showToast(getActivity(), "快递公司不能为空");
                } else {
                    sendRequest();
                }
                break;
            case R.id.scan_img:
                ToastUtils.showToast(getActivity(), "扫一扫把");
                Intent intent1 = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent1,  QR_CODE);
                break;
        }
    }

    private void sendRequest() {
        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath + "/expressservlet", RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "getCompanyImg");
        request.add("express_companycode", mCompanyEditText.getText().toString());
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_LOAD, request, onResponseListener);
    }

    private void selectCompanyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(R.array.company, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sexStr = getResources().getStringArray(R.array.company)[which];
                if (!sexStr.equals("选择")) {
                    mCompanyEditText.setText(sexStr);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        public void onSucceed(int what, Response<String> response) {
            if (what == NOHTTP_WHAT_LOAD) {
                String result = response.get();// 响应结果
                ToastUtils.showToast(getActivity(), "跳转到详情");
                Log.e("result", result);
                Intent intent = new Intent();
                intent.putExtra("company_img", result);
                intent.putExtra("company_name", mCompanyEditText.getText().toString());
                intent.putExtra("express_no", mOrdernoEditText.getText().toString());
                intent.putExtra("remark", "");
                intent.setClass(getActivity(), ExpressActivity.class);
                startActivity(intent);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_CODE) {
            String result = data.getStringExtra(CaptureActivity.EXTRA_STRING);
            mOrdernoEditText.setText(result);
            Toast.makeText(getActivity(), result + "", Toast.LENGTH_SHORT).show();
        }
    }
}

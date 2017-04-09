package com.zfg.queryexpress.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.zfg.queryexpress.MainActivity;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.chat.base.DemoHelper;
import com.zfg.queryexpress.chat.db.DemoDBManager;
import com.zfg.queryexpress.common.WaitDialog;
import com.zfg.queryexpress.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "loginActivity";
    public static final int LOGIN_WHAT = 1;
    public static final int LOGIN_WHAT_MESSAGE_ = 5;
    public static final int UPDATE_NICKNAME = 6;
    private EditText mPhoneEditText, mPwdEditText;
    private Button mLoginButton;
    private TextView mForgetTextView, mRegisterTextView;
    /**
     * 请求的时候等待框。
     */
    private WaitDialog mWaitDialog;

    private boolean autoLogin = false;

    /**
     * 请求队列。
     */
    private RequestQueue mQueue;
    private String username;
    private String userid;
    private String nickname;
    private String headimg;
    private String realname;//真实姓名
    private String linktel;//联系电话
    private String resplace;//常住地
    private String company;//所在公司
    private String jiashao;//介绍
    private String isopen;//联系方式是否公开

    //偏好设置
    public static final String TYPE = "user_info";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enter the main activity if already logged in
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }
        setContentView(R.layout.activity_login);
        mQueue = NoHttp.newRequestQueue();
        initView();
        addListener();
    }

    private void initView() {
        mPhoneEditText = (EditText) findViewById(R.id.login_phone_edit);
        mPwdEditText = (EditText) findViewById(R.id.login_pwd_edit);
        mLoginButton = (Button) findViewById(R.id.login_btn);
        mForgetTextView = (TextView) findViewById(R.id.forget_text);
        mRegisterTextView = (TextView) findViewById(R.id.register_text);
        mWaitDialog = new WaitDialog(this);
    }

    private void addListener() {
        mLoginButton.setOnClickListener(this);
        mForgetTextView.setOnClickListener(this);
        mRegisterTextView.setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_WHAT_MESSAGE_:
                    DemoDBManager.getInstance().closeDB();
                    // reset current user name before login
                    DemoHelper.getInstance().setCurrentUserName(mPhoneEditText.getText().toString());
                    loginUser();
                    break;
                case UPDATE_NICKNAME:

                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.login_btn:
                if (!EaseCommonUtils.isNetWorkConnected(this)) {
                    Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (judgePhone() && judgePwd()) {
//                        网络请求数据
                    sendLogin(mPhoneEditText.getText().toString(), mPwdEditText.getText().toString());
                   /* intent.setClass(this, MainActivity.class);
                    startActivity(intent);*/
                }

                break;
            case R.id.forget_text:

                break;
            case R.id.register_text:
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loginUser() {
        EMClient.getInstance().login(mPhoneEditText.getText().toString().trim(), mPwdEditText.getText().toString(), new EMCallBack() {
            @Override
            public void onSuccess() {
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                        nickname);
                Log.e("nickname", "昵称" + nickname);
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                } else {
                    Log.e("LoginActivity", "更新用户昵称成功");
                }

              /*  if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }*/
                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
//                设置当前登录的用户头像
//                DemoHelper.getInstance().getUserProfileManager().uploadUserAvatar("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491035497472&di=605266afc5fbaecdce77b4a7a6fddba4&imgtype=0&src=http%3A%2F%2Fwww.asiafinance.cn%2Fr%2Fcms%2Fwww%2Faf%2Fimg%2Fuserimg.png".getBytes());
                Log.e(TAG, "登录成功");
            }

            @Override
            public void onError(int i, final String s) {
                Log.e(TAG, "登录失败" + i + "," + s);
//                pd.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (s) {
                                    case "User is already login":
                                        Toast.makeText(LoginActivity.this, "登录失败" + "用户已经登录", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });

                    }
                }).start();
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void sendLogin(String phone, String pwd) {

        Request<String> request = NoHttp.createStringRequest(MyApplication.url, RequestMethod.POST);
        request.add("methods", "login");
        request.add("phone", phone);
        request.add("pwd", pwd);
        mQueue.add(LOGIN_WHAT, request, onResponseListener);

    }


    private boolean judgePhone() {
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mPhoneEditText.getText())) {
            ToastUtils.showToast(this, "手机号不能为空");
            return false;
        } else if (mPhoneEditText.getText().toString().matches(telRegex)) {
            return mPhoneEditText.getText().toString().matches(telRegex);
        } else {
            ToastUtils.showToast(this, "手机号输入有误，请重新输入");
            return mPhoneEditText.getText().toString().matches(telRegex);
        }
    }

    private boolean judgePwd() {
        boolean flog = false;
        if (!TextUtils.isEmpty(mPwdEditText.getText().toString())) {
            flog = true;
        } else {
            ToastUtils.showToast(this, "请输入密码");
        }
        return flog;
    }

    /*  *
     * 回调对象，接受请求结果.
     */
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int i) {
            // 请求开始，这里可以显示一个dialog
            if (mWaitDialog != null && !mWaitDialog.isShowing())
                mWaitDialog.show();
        }

        @Override
        public void onSucceed(int i, Response<String> response) {
            //请求成功 对数据的操作
            String Msg = null;
            int Result = 0;
            JSONObject jsonObject1 = null;
            //请求成功 对数据的操作
            if (i == LOGIN_WHAT) {
                Log.e(TAG, response.get());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    Result = jsonObject.getInt("result");
                    Msg = jsonObject.getString("msg");
                    jsonObject1 = jsonObject.getJSONObject("user");
                    userid = jsonObject1.getString("user_id");
                    username = jsonObject1.getString("user_phone");
                    nickname = jsonObject1.getString("user_name");
                    //第一个参数：偏好设置文件的名称；第二个参数：文件访问模式
                    mSharedPreferences = getSharedPreferences(TYPE, MODE_PRIVATE);
                    //向偏好设置文件中保存数据
                    mEditor = mSharedPreferences.edit();
                    mEditor.putString("userid", userid);
                    mEditor.putString("username", username);
                    mEditor.putString("nickname", nickname);
                    //提交保存结果
                    mEditor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (Result == 0) {
                    Log.e(TAG, "登录结果" + Result + ":" + Msg);
                    Log.e("获取昵称", "昵称" + nickname);
                    mHandler.sendEmptyMessage(LOGIN_WHAT_MESSAGE_);
                }
//                ToastUtils.showToast(getApplicationContext(), Msg);
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            // 请求失败
            if (exception instanceof NetworkError) {// 网络不好
                ToastUtils.showToast(LoginActivity.this, R.string.error_please_check_network);
            } else if (exception instanceof TimeoutError) {// 请求超时
                ToastUtils.showToast(LoginActivity.this, R.string.error_timeout);
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                ToastUtils.showToast(LoginActivity.this, R.string.error_not_found_server);
            } else if (exception instanceof URLError) {// URL是错的
                ToastUtils.showToast(LoginActivity.this, R.string.error_url_error);
            } else if (exception instanceof NotFoundCacheError) {
                ToastUtils.showToast(LoginActivity.this, R.string.error_not_found_cache);
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            } else {
                ToastUtils.showToast(LoginActivity.this, R.string.error_unknow);
            }
            Logger.e("错误：" + exception.getMessage());
        }


        @Override
        public void onFinish(int i) {
            // 请求结束，这里关闭dialog
            if (mWaitDialog != null && mWaitDialog.isShowing())
                mWaitDialog.dismiss();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}

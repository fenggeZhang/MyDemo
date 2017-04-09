package com.zfg.queryexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.chat.base.DemoHelper;
import com.zfg.queryexpress.utils.ToastUtils;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "RegisterActivity";
    public static final int ChangeButtonWHAT = 1;
    public static final int ResetButtonWHAT = 2;
    private ImageView mBackImageView;
    private TextView mTitleTextView, mgetCodeTextView;
    private EditText mPhoneEditText, mPwdEditText, mPwd2EditText, mCodeEditText;
    private CheckBox mCheckBox;
    private Button mRegisterButton;
    //相隔几秒获取验证码
    int shortTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        addListener();
    }

    //初始化控件
    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.back);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mgetCodeTextView = (TextView) findViewById(R.id.getcode_text);
        mPhoneEditText = (EditText) findViewById(R.id.phone_edit);
        mPwdEditText = (EditText) findViewById(R.id.pwd_edit);
        mPwd2EditText = (EditText) findViewById(R.id.pwd2_edit);
        mCodeEditText = (EditText) findViewById(R.id.code_edit);
        mRegisterButton = (Button) findViewById(R.id.register_btn);
        mCheckBox = (CheckBox) findViewById(R.id.my_checkbox);
        mTitleTextView.setText("注册");
    }

    private void addListener() {
        mBackImageView.setOnClickListener(this);
        mgetCodeTextView.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.getcode_text:
                ToastUtils.showToast(this, "获取验证码");
                //电话号码不为空 并且合法
                if (judgePhone()) {
                    //手机号没有被注册过时 执行下面的操作
                    delay();
                }
                break;
            case R.id.register_btn:
                /*判断电话号码格式是否正确,然后 判断不为空两次密码一致*/
                if (judgePhone() && judgePwd() && judgeCode() && judgeCheckBox()) {
                    ToastUtils.showToast(this, "注册");
//                    register();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void register() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(mPhoneEditText.getText().toString(), mPwdEditText.getText().toString());
                    runOnUiThread(new Runnable() {
                        public void run() {

                            // save current user
                            DemoHelper.getInstance().setCurrentUserName(mPhoneEditText.getText().toString());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            int errorCode=e.getErrorCode();
                            if(errorCode== EMError.NETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
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
//        密码必须是字母数字混合 并且在6到16位之间
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        boolean flog = false;
        if (!TextUtils.isEmpty(mPwdEditText.getText())) {
            if (!TextUtils.isEmpty(mPwd2EditText.getText())) {
                //
                if (mPwd2EditText.getText().toString().equals(mPwdEditText.getText().toString())) {
                    //密码一致
                    if (mPwdEditText.getText().toString().matches(regex)) {
                        flog = true;
                    } else {
                        ToastUtils.showToast(this, "密码必须是6到16位的数字或字母组成");
                    }
                } else {
                    ToastUtils.showToast(this, "两次密码不一致");
                }
            } else {
                ToastUtils.showToast(this, "确认密码不能为空");
            }
        } else {
            ToastUtils.showToast(this, "密码不能为空");
        }
        return flog;
    }

    private boolean judgeCode() {
        boolean flog = false;
        //验证码为空
        if (!TextUtils.isEmpty(mCodeEditText.getText())) {
            flog = true;
        } else {
            ToastUtils.showToast(this, "验证码不能为空");
        }
        return flog;
    }

    private boolean judgeCheckBox() {
        boolean flog = false;
        //验证码为空
        //复选框没有选
        if (mCheckBox.isChecked()) {
            flog = true;
        } else {
            ToastUtils.showToast(this, "请同意协议");
        }
        return flog;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ChangeButtonWHAT:
                    mgetCodeTextView.setClickable(false);
                    mgetCodeTextView.setText(shortTime + "s后重新获取");
                    break;
                case ResetButtonWHAT:
                    mgetCodeTextView.setClickable(true);
                    mgetCodeTextView.setText("重新获取");
//                    mTextErificationCode.setText("");
                    break;
               /* case VerificationError:
                    show("验证失败，请重新获取验证码！");
                    break;
                case VerificationSuccess:
                    show("验证成功！");
                    break;*/
            }
        }
    };

    //用于更改获取验证码显示
    public void delay() {
        //每隔60s发送一次
        shortTime = 60;
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    while (true) {
                        if (shortTime > 0) {
                            mHandler.sendEmptyMessage(ChangeButtonWHAT);
                            sleep(1000);
                            shortTime--;
                        } else {
                            mHandler.sendEmptyMessage(ResetButtonWHAT);
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}

package com.zfg.queryexpress.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zfg.queryexpress.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "SettingActivity";
    private ImageView mBackImageView;
    private TextView mTitleTextView, mSignOutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        addlistener();
    }

    private void addlistener() {
        mBackImageView.setOnClickListener(this);
        mSignOutTextView.setOnClickListener(this);
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.back);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mTitleTextView.setText("设置");
        mSignOutTextView = (TextView) findViewById(R.id.signout_tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.signout_tv:
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                finish();
               /* EMClient.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "退出成功");
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "退出失败" + i + "," + s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });*/
                break;
        }
    }
}

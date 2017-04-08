package com.zfg.queryexpress.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.util.EasyUtils;
import com.zfg.queryexpress.MainActivity;
import com.zfg.queryexpress.R;
import com.zfg.queryexpress.chat.runtimepermissions.PermissionsManager;

public class ChatActivity extends AppCompatActivity {
    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;
    public static ChatActivity activityInstance;
    String toChatUsername;
    private ChatFragment easeChatFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activityInstance=this;
        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        initView();
    }

    private void initView() {
//        使用easreUi封装好的聊天页面
        easeChatFragment = new ChatFragment();
         //将参数传递给聊天页面
        easeChatFragment.setArguments(getIntent().getExtras());
//        加载EaseUi封装的聊天页面
        getSupportFragmentManager().beginTransaction().add(R.id.ec_layout_content, easeChatFragment).commit();

       /* final String toChatUsername = "zhang";
        mTextView = (TextView) findViewById(R.id.ec_text_content);
        mEditText = (EditText) findViewById(R.id.msg_edit);
        mButton = (Button) findViewById(R.id.msg_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {*/
               /* String content = mEditText.getText().toString();
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
                message.setChatType(EMMessage.ChatType.Chat);
                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);
                mTextView.setText(mTextView.getText() + "\n" + content);

                message.setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.e("ChatActivity", "消息发送成功");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("ChatActivity", "消息发送失败" + i + "," + s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });*/
          /*  }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance=null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        easeChatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}

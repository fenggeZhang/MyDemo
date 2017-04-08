package com.zfg.queryexpress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.zfg.queryexpress.activity.MyInfoActivity;
import com.zfg.queryexpress.activity.SettingActivity;
import com.zfg.queryexpress.adapter.FragmentAdapter;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.common.MyViewPager;
import com.zfg.queryexpress.common.SlidingMenu;
import com.zfg.queryexpress.common.WaitDialog;
import com.zfg.queryexpress.fragment.ExpressFragment;
import com.zfg.queryexpress.fragment.Home1Fragment;
import com.zfg.queryexpress.fragment.PhoneFragment;
import com.znq.zbarcode.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import static com.zfg.queryexpress.fragment.ExpressFragment.QR_CODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int NOHTTP_WHAT_LOAD = 1;
    private MyViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private List<Fragment> mFragmentList;
    Fragment mHomeFragment, mExpressFragment, mPhoneFragment;
    FragmentManager mFragmentManager;
    FragmentAdapter mFragmentAdapter;
    private RadioButton mHomeRadio;
    private RadioButton mExpressRadio;
    private RadioButton mPhoneRadio;

    private LinearLayout mLinearLayout1, mLinearLayout2, mLinearLayout3, mLinearLayout4;
    private ImageView mMyImageView, mUserPhotoImageView;
    private TextView mUserNameTextView;
    private SlidingMenu mMenu;

    private ImageView mAddImageView;
    private Dialog dialog;
    private Button mCancelButton, mSaveButton;
    private ImageView mImageView;
    private RelativeLayout mCompanyRelativeLayout;
    private EditText mOrdernoEditText, mCompanyEditText, mRemarkEditText;
    private String mOrderNoString, mCompanyString, mRemarkString;

    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initdialog();
        addListeners();
        initData();
    }

    private void initViews() {
        mDialog = new WaitDialog(this);//提示框
        mViewPager = (MyViewPager) findViewById(R.id.middle_viewpager);
        mViewPager.setScrollble(false);
        mRadioGroup = (RadioGroup) findViewById(R.id.express_radiogroup);
        mHomeRadio = (RadioButton) findViewById(R.id.home_radio);
        mExpressRadio = (RadioButton) findViewById(R.id.express_radio);
        mPhoneRadio = (RadioButton) findViewById(R.id.phone_radio);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        mMyImageView = (ImageView) findViewById(R.id.img_my);
        mUserPhotoImageView = (ImageView) findViewById(R.id.user_photo);
        mUserNameTextView = (TextView) findViewById(R.id.user_name);
        mLinearLayout1 = (LinearLayout) findViewById(R.id.item_1);
        mLinearLayout2 = (LinearLayout) findViewById(R.id.item_2);
        mLinearLayout3 = (LinearLayout) findViewById(R.id.item_3);
        mLinearLayout4 = (LinearLayout) findViewById(R.id.item_4);

        mAddImageView = (ImageView) findViewById(R.id.home_add_img);

        // (Button)findViewById(R.id.cancel_btn);
        // mSaveButton= (Button)findViewById(R.id.save_btn);
    }

    private void initdialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        Window dialogWindow = dialog.getWindow();
        mCancelButton = (Button) dialogWindow.findViewById(R.id.cancel_btn);
        mSaveButton = (Button) dialogWindow.findViewById(R.id.save_btn);
        mCompanyRelativeLayout = (RelativeLayout) dialogWindow.findViewById(R.id.company_rl);
        mImageView = (ImageView) dialogWindow.findViewById(R.id.sacn_img_dialog);
        mOrdernoEditText = (EditText) dialogWindow.findViewById(R.id.order_edit);
        mCompanyEditText = (EditText) dialogWindow.findViewById(R.id.company_edit);
        mRemarkEditText = (EditText) dialogWindow.findViewById(R.id.remark_edit);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
        //解决dialog圆角之后背景是白色的问题
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         /*lp.x = 20; // 新位置X坐标*/
        lp.y = 300; // 新位置Y坐标
        lp.width = 650; // 宽度
        lp.height = 480; // 高度
        lp.alpha = 0.8f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
    }

    /* public void toggleMenu(View view)
     {
         mMenu.toggle();
     }*/
    private void addListeners() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetViewPager(checkedId);
            }
        });
        //滑动viewPager的时候及时修改底部导航栏的字体颜色
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //根据当前位置设置选中的单选按钮
                resetRadioButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mMyImageView.setOnClickListener(this);
        mUserPhotoImageView.setOnClickListener(this);
        mUserNameTextView.setOnClickListener(this);
        mLinearLayout1.setOnClickListener(this);
        mLinearLayout2.setOnClickListener(this);
        mLinearLayout3.setOnClickListener(this);
        mLinearLayout4.setOnClickListener(this);
        mAddImageView.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        mCompanyRelativeLayout.setOnClickListener(this);
    }

    //重置radio
    private void resetRadioButton(int position) {
        //获取position位置处对应的单选按钮
        RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(position);
        //设置当前单选按钮默认选中
        radioButton.setChecked(true);
    }

    //重置viewpager
    private void resetViewPager(int checkedId) {
        switch (checkedId) {
            case R.id.home_radio:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.express_radio:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.phone_radio:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    private void initData() {
        mFragmentList = new ArrayList<>();
        mHomeFragment = new Home1Fragment();
        mExpressFragment = new ExpressFragment();
        mPhoneFragment = new PhoneFragment();
        mFragmentList.add(mHomeFragment);
        mFragmentList.add(mExpressFragment);
        mFragmentList.add(mPhoneFragment);

        //初始化适配器
        mFragmentManager = getSupportFragmentManager();
        mFragmentAdapter = new FragmentAdapter(mFragmentManager, mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.user_photo:
                Toast.makeText(this, "我是头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_name:
                Toast.makeText(this, "我是昵称", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_1:
                Toast.makeText(this, "我是item1", Toast.LENGTH_SHORT).show();
                intent.setClass(this, MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.item_2:
                Toast.makeText(this, "我是item2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_3:
                Toast.makeText(this, "我是item3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_4:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_my:
                mMenu.toggle();
                break;
            case R.id.home_add_img:
                dialog.show();
                break;
            case R.id.cancel_btn:
                // Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                break;
            case R.id.save_btn:
                // Toast.makeText(this, "保存", Toast.LENGTH_SHORT).show();
                setData();
                dialog.dismiss();
                break;
            case R.id.company_rl:
                selectCompanyDialog();
                break;
            case R.id.sacn_img_dialog:
                Intent intent1 = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent1, QR_CODE);
                break;
        }
    }

    private void selectCompanyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void setData() {
        MyApplication myApplication = (MyApplication) this.getApplication();
        mPath = myApplication.url;
        Log.e("url", mPath);
        mOrderNoString = mOrdernoEditText.getText().toString();
        mCompanyString = mCompanyEditText.getText().toString();
        mRemarkString = mRemarkEditText.getText().toString();

        // 创建请求队列, 默认并发3个请求,传入你想要的数字可以改变默认并发数, 例如NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
        // 创建请求对象
        request = NoHttp.createStringRequest(mPath + "/expressservlet", RequestMethod.POST);
        // 添加请求参数
        request.add("methods", "add");
        request.add("express_no", mOrderNoString);
        request.add("express_companycode", mCompanyString);
        request.add("express_remark", mRemarkString);
        /*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
		 * request: 请求对象
		 * onResponseListener 回调对象，接受请求结果
		 */
        requestQueue.add(NOHTTP_WHAT_LOAD, request, onResponseListener);
        //传值到fragment
        //创建一个Bundle并将要发送的数据放进去
        Bundle bundle = new Bundle();
        bundle.putString("expressNo", mOrderNoString);
        bundle.putString("CompanyCode", mCompanyString);

        mHomeFragment = new Home1Fragment();
        //将bundle绑定到MyFragment的对象上
        mHomeFragment.setArguments(bundle);
        FragmentTransaction shiwu = mFragmentManager.beginTransaction();
        shiwu.replace(R.id.main_top, mHomeFragment);
        shiwu.commit();

        Log.e("信息", mOrderNoString + ":" + mCompanyString + ":" + mRemarkString);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @SuppressWarnings("unused")
        public void onSucceed(int what, Response<String> response) {
            if (what == NOHTTP_WHAT_LOAD) {
                //Log.e("ok","ok");
                /*// 请求成功
                String result = response.get();// 响应结果
                Log.e("666", "666");
                //Log.e("zfg", result);
                Gson gson = new Gson();
                Type type = new TypeToken<QueryResultBean>() {
                }.getType();
                QueryResultBean queryResultBean=new QueryResultBean();*/
//                queryResultBean=gson.fromJson(result,type);
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            //请求失败
            if (exception instanceof ServerError) {// 服务器错误
                Toast.makeText(MainActivity.this, "服务器发生错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NetworkError) {// 网络不好
                Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof TimeoutError) {// 请求超时
                Toast.makeText(MainActivity.this, "请求超时，网络不好或者服务器不稳定", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof UnKnownHostError) {// 找不到服务器
                Toast.makeText(MainActivity.this, "未发现指定服务器", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof URLError) {// URL是错的
                Toast.makeText(MainActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
            } else if (exception instanceof NotFoundCacheError) {
                Toast.makeText(MainActivity.this, "没有发现缓存", Toast.LENGTH_SHORT).show();
                // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            } else {
                Toast.makeText(MainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
        }
    }
}

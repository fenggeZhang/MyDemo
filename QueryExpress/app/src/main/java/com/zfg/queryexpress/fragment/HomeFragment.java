package com.zfg.queryexpress.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
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
import com.zfg.queryexpress.adapter.ExpressListAdapter;
import com.zfg.queryexpress.adapter.OnItemClickListener;
import com.zfg.queryexpress.application.MyApplication;
import com.zfg.queryexpress.beans.ExpressGson;
import com.zfg.queryexpress.common.ListViewDecoration;
import com.zfg.queryexpress.common.WaitDialog;
import com.zfg.queryexpress.utils.ToastUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/10/26.
 */
public class HomeFragment extends Fragment {
    public static final int NOHTTP_WHAT_GETALL = 1;
    private String mPageNo;
    View mView;
    /**
     * 状态类型常量
     */
    public static final String STATUS_TYPE = "pagerType";
    private LinearLayout mLinearLayout;
    private SwipeMenuRecyclerView mRecyclerView;
    private ExpressListAdapter mExpressAdapter;
    private List<ExpressGson> mQueryResultList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Bundle bundle;
    private String expressNo;
    private String Code;

    private String mPath;
    //自定义一个dialog
    private WaitDialog mDialog;
    //请求队列
    private RequestQueue requestQueue;
    Request<String> request;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                   // mQueryResultList.clear();
                    mExpressAdapter.notifyDataSetChanged();
                    /*//表示刷新完成
                    mAudioListview.onRefreshComplete();
                    //通知刷新
                    mAudioListAdapter.notifyDataSetChanged();*/
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, null);
        //接收数据
        bundle = getArguments();
        if (bundle != null) {
            expressNo = bundle.getString("expressNo");
            Code = bundle.getString("CompanyCode");
            Log.e("expressNo", "" + expressNo);
            Log.e("Code", "" + Code);
        }
        initViews();
        initData();
        showRecyclerView();
        requestData();
        getData();
        return mView;
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
        if (expressNo != null && Code != null) {
            ExpressGson expressGson = new ExpressGson(3, expressNo, Code, "12-12", "wwww", "备注");
            //mQueryResultList.add(0, expressGson);
            // 通知UI刷新页面
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
            Log.e("zfg", "list" + mQueryResultList.size());
        }
    }

    private void initData() {
        mQueryResultList = new ArrayList<>();
       /* ExpressGson expressGson = new ExpressGson(1, "71948047915", "中通", "12-12", "www.baidu.com", "备注");
        ExpressGson expressGson1 = new ExpressGson(2, "719504046744", "中通", "12-13", "www.baidu.com", "备注");
        mQueryResultList.add(expressGson);
        mQueryResultList.add(expressGson1);
        mQueryResultList.add(expressGson);*/
    }

    private void showRecyclerView() {
        // mRecyclerView.setItemViewSwipeEnabled(true);// 开启滑动删除
        //RecyclerView的设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));// 布局管理器。
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。
        // 添加滚动监听。
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        mRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mRecyclerView.openLeftMenu(1);
        mRecyclerView.openRightMenu(0);
        mExpressAdapter = new ExpressListAdapter(mQueryResultList, getActivity());
        mRecyclerView.setAdapter(mExpressAdapter);
        mExpressAdapter.setOnItemClickListener(onItemClickListener);
        mExpressAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        mDialog = new WaitDialog(getActivity());//提示框
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mRecyclerView = (SwipeMenuRecyclerView) mView.findViewById(R.id.recycler_view);
    }

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int size = 150;
            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            /*{
                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawic_action_addable.selector_green)// 点击的背景。
                        .setImage(R.mipmap.) // 图标。
                        .setWidth(size) // 宽度。
                        .setHeight(size); // 高度。
                swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

                SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_close)
                        .setWidth(size)
                        .setHeight(size);

                swipeLeftMenu.addMenuItem(closeItem); // 添加一个按钮到左侧菜单。
            }*/

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackgroundDrawable(R.color.delete_red)
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(size)
                        .setHeight(size);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

               /* SwipeMenuItem closeItem = new SwipeMenuItem(getActivity())
                        .setBackgroundDrawable(R.drawable.selector_purple)
                        .setImage(R.mipmap.ic_action_close)
                        .setWidth(size)
                        .setHeight(size);
                swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

                SwipeMenuItem addItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_green)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(size)
                        .setHeight(size);
                swipeRightMenu.addMenuItem(addItem); // 添加一个按钮到右侧菜单。*/
            }
        }
    };
    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            /*if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }*/
          /*  ToastUtils.showToast(getActivity(),"点击了删除第"+adapterPosition+"条");
            mMusciTableOperate.deleteMusicByMusicid(mp3Infos.get(adapterPosition).getMusic_id());
            mp3Infos.remove(adapterPosition);*/
            if (menuPosition == 0) {// 删除按钮被点击
                //
                ToastUtils.showToast(getActivity(), "点击了" + adapterPosition + "条");
                mQueryResultList.remove(adapterPosition);
                mExpressAdapter.notifyDataSetChanged();
            }
        }
    };
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            //ToastUtils.showToast(getActivity(), "点击了" + position + "条");
            if(position==-1){
                mExpressAdapter.notifyDataSetChanged();
            }
            if(position!=-1){
                Log.e("position位置", mQueryResultList.get(position).toString());
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("expressgson", mQueryResultList.get(position));
                intent.putExtras(bundle);
                intent.setClass(getActivity(), ExpressActivity.class);
                startActivity(intent);
            }
        }
    };
    /**
     * 刷新监听。
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 2000);
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                Toast.makeText(getActivity(), "滑到最底部了，去加载更多吧！", Toast.LENGTH_SHORT).show();
                /*size += 50;
                for (int i = size - 50; i < size; i++) {
                    mStrings.add("我是第" + i + "个。");
                }*/
                mExpressAdapter.notifyDataSetChanged();
            }
        }
    };
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
                    Log.e("传过来了呀", result);
                    //把JSON格式的字符串改为Student对象
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ExpressGson>>() {
                    }.getType();
                    List<ExpressGson> expressGsonList=new ArrayList<>();
                    expressGsonList = gson.fromJson(result, type);
                    mQueryResultList.addAll(expressGsonList);
                    //通知刷新
                    mExpressAdapter.notifyDataSetChanged();
                    /*//表示刷新完成
                    mRecyclerView.onRefreshComplete();*/
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

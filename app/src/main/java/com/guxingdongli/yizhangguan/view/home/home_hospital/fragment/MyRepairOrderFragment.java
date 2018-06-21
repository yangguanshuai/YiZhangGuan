package com.guxingdongli.yizhangguan.view.home.home_hospital.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.util.AbDialogUtil;
import com.alibaba.fastjson.JSON;
import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.controller.adapter.MyRepairOrderFragmentAdapter;
import com.guxingdongli.yizhangguan.model.MyRepairOrderBase;
import com.guxingdongli.yizhangguan.util.Constant;
import com.guxingdongli.yizhangguan.util.HttpUtile;
import com.guxingdongli.yizhangguan.util.HttpUtileCallBack;
import com.guxingdongli.yizhangguan.util.LogUtils;
import com.guxingdongli.yizhangguan.view.home.home_hospital.MyRepairOrderActivity;
import com.guxingdongli.yizhangguan.view.home.home_hospital.MyRepairOrderDetailsActivity;
import com.guxingdongli.yizhangguan.view.home.home_hospital.fragment.callback.StorageHospitalOnClickCallBack;
import com.yuxiaolong.yuxiandelibrary.YuXianDeActivity;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshBase;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by jackmask on 2018/3/3.
 */

public class MyRepairOrderFragment extends Fragment {


    @Bind(R.id.content_list)
    PullToRefreshListView contentList;
    private YuXianDeActivity activity;
    private View view;
    private MyRepairOrderFragmentAdapter adapter;
    private List<MyRepairOrderBase.DataBeanX.DataBean> dataList = new ArrayList<>();

    private int page = 1;
    private float allPage = 1;
    private boolean sortOrder = false;
    private String keyword = "";
    private String guid = "";
    private boolean webOne = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            HttpUtile httpUtile;
            switch (what) {
                case 0:
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    if (contentList != null)
                        contentList.onRefreshComplete();

                    if (!TextUtils.isEmpty(getArguments().getString("DATA")) && getArguments().getString("DATA").equals("all")) {

                        ((MyRepairOrderActivity) activity).setTabText((Integer) msg.obj);

                    }
                    break;
                case 1:
                    if (contentList != null)
                        contentList.onRefreshComplete();
                    dataList.clear();
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    break;
            }

        }
    };


    /**
     * 控件是否初始化完成
     */
    private boolean isViewCreated;
    /**
     * 数据是否已加载完毕
     */
    private boolean isLoadDataCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (YuXianDeActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_storage_hospital, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        String type = this.getArguments().getString("DATA");
        setView();
        isViewCreated = true;
        return view;
    }



    //懒加载


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&isViewCreated&&!isLoadDataCompleted){
            //可见
            isLoadDataCompleted = true;
            webData(keyword,sortOrder);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()){
            isLoadDataCompleted =  true;
            webData(keyword,sortOrder);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newData(String s) {
        if (s.equals("newpair")) {
            LogUtils.debug("......................");
            webData(keyword, sortOrder);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public void setPage() {
        page = 1;
    }

    public void setSortOrder(String keyword, boolean sortOrder) {
        this.keyword = keyword;
        this.sortOrder = sortOrder;
        webData(keyword,sortOrder);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void setView() {
        adapter = new MyRepairOrderFragmentAdapter(dataList);
        contentList.setMode(PullToRefreshBase.Mode.BOTH);
        contentList.setAdapter(adapter);
        contentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, MyRepairOrderDetailsActivity.class);
                intent.putExtra("guid", dataList.get(i - 1).getGid());
                activity.startActivity(intent);
            }
        });
        //添加展示的回调
        adapter.setClickCallBack(new StorageHospitalOnClickCallBack() {
            @Override
            public void clickNo(int position, boolean type) {
                for (int i = 0; i < dataList.size(); i++) {
                    dataList.get(i).setType(false);
                }
                dataList.get(position).setType(type);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void clickTime(String hospitalGuid, String number) {
                RequestBody formBody = new FormBody.Builder()
                        .add("number", number)
                        .add("hospitalGuid", hospitalGuid)
                        .build();
                HttpUtile httpUtile = new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.SCANCODETIMING, formBody, new HttpUtileCallBack() {
                    @Override
                    public void getReturnStr(String returnStr) {
                        page = 1;
                        webData(keyword, sortOrder);
                    }

                    @Override
                    public void getReturnStrFailure(String returnStr) {
                        Looper.prepare();
                        Toast.makeText(getContext(), returnStr, Toast.LENGTH_LONG).show();
                        Looper.loop();

                        // AbToastUtil.showToast(LoginActivity.this,returnStr);
                    }

                    @Override
                    public void getErrorStr(String errorStr) {
                        System.out.println("errorStr = " + errorStr);
                    }
                }, false);


            }
        });
        contentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //刷新
                page = 1;
                webData(keyword, sortOrder);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //加载
                page++;
                webData(keyword, sortOrder);

            }
        });
//        webData(keyword, sortOrder);
    }


    public void refresh() {
        page = 1;
        webData(keyword,sortOrder);
    }



    private void webData(String keyword, boolean sortOrder) {
            String type = this.getArguments().getString("DATA");
            LogUtils.debug("type:" + type);
            final RequestBody formBody;
            if (type.equals("all")) {//全部
                System.out.println("all");
                formBody = new FormBody.Builder()
                        .add("pageIndex", page + "")
                        .add("pageSize", "10")
                        .add("sortField", "Id")
                        .add("sortOrder", sortOrder ? "asc" : "desc")
                        .add("filter_group", "{\"Rules\":[],Groups:[{\"Rules\":[{\"Field\":\"BusinessNumber\",\"Value\":\"" + keyword + "\",\"Operate\":\"contains\"}],\"Operate\":\"or\"}],\"Operate\":\"and\"}")
                        .build();
            } else if (type.equals("not")) {//待维修

                System.out.println("not");
                formBody = new FormBody.Builder()
                        .add("pageIndex", page + "")
                        .add("pageSize", "10")
                        .add("sortField", "Id")
                        .add("sortOrder", sortOrder ? "asc" : "desc")
                        .add("filter_group", "{\"Rules\":[{\"Field\":\"MaintenanceStage\",\"Value\":\"1\",\"Operate\":\"equal\"}],Groups:[{\"Rules\":[{\"Field\":\"BusinessNumber\",\"Value\":\"" + keyword + "\",\"Operate\":\"contains\"}],\"Operate\":\"or\"}],\"Operate\":\"and\"}")
                        .build();
            } else {//已维修
                System.out.println("else");
                formBody = new FormBody.Builder()
                        .add("pageIndex", page + "")
                        .add("pageSize", "10")
                        .add("sortField", "Id")
                        .add("sortOrder", sortOrder ? "asc" : "desc")
                        .add("filter_group", "{\"Rules\":[{\"Field\":\"MaintenanceStage\",\"Value\":\"8\",\"Operate\":\"equal\"}],Groups:[{\"Rules\":[{\"Field\":\"BusinessNumber\",\"Value\":\"" + keyword + "\",\"Operate\":\"contains\"}],\"Operate\":\"or\"}],\"Operate\":\"and\"}")
                        .build();
            }

            HttpUtile httpUtile = new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.SEARCHREPAIRORDERLIST, formBody, new HttpUtileCallBack() {
                @Override
                public void getReturnStr(String returnStr) {
                    LogUtils.debug("结果：" + returnStr);
                    MyRepairOrderBase data = JSON.parseObject(returnStr, MyRepairOrderBase.class);
                    if (page == 1) {
                        dataList.clear();
                        allPage = data.getData().getTotal();
                    }
                    if (Math.ceil(allPage / 10) >= page) {
                        for (MyRepairOrderBase.DataBeanX.DataBean dataItem : data.getData().getData()) {
                            dataItem.setType(false);
                            dataList.add(dataItem);
                        }
                    }
                    Message message = new Message();
                    message.what = 0;
                    message.obj = data.getData().getTotal();
                    mHandler.sendMessage(message);
                }

                @Override
                public void getReturnStrFailure(String returnStr) {
                    Looper.prepare();
                    dataList.clear();
                    mHandler.sendEmptyMessage(1);
                    if (webOne) {
                        AbDialogUtil.removeDialog(getContext());
                        Toast.makeText(getContext(), returnStr, Toast.LENGTH_LONG).show();
                    } else {
                        webOne = true;
                    }

                    Looper.loop();

                }

                @Override
                public void getErrorStr(String errorStr) {

                }
            }, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

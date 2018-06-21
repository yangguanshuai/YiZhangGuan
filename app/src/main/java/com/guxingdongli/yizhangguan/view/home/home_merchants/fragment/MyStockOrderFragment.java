package com.guxingdongli.yizhangguan.view.home.home_merchants.fragment;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.util.AbDialogUtil;
import com.alibaba.fastjson.JSON;
import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.controller.adapter.MyOrderNewAdapter;
import com.guxingdongli.yizhangguan.controller.adapter.MyStockOrderAdapter;
import com.guxingdongli.yizhangguan.controller.adapter.PreparationOrderAdapter;
import com.guxingdongli.yizhangguan.controller.adapter.callback.MyOrderNewAdapterCallBack;
import com.guxingdongli.yizhangguan.model.MyStockOrderListBase;
import com.guxingdongli.yizhangguan.model.StorageHospitalBase;
import com.guxingdongli.yizhangguan.util.Constant;
import com.guxingdongli.yizhangguan.util.HttpUtile;
import com.guxingdongli.yizhangguan.util.HttpUtileCallBack;
import com.guxingdongli.yizhangguan.view.home.dialog.InputDialog;
import com.guxingdongli.yizhangguan.view.home.dialog.PromptDialog;
import com.guxingdongli.yizhangguan.view.home.home_hospital.fragment.callback.StorageHospitalOnClickCallBack;
import com.yuxiaolong.yuxiandelibrary.YuXianDeActivity;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshBase;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 我的备货单
 * @author 余先德
 * @data 2018/3/25
 */

public class MyStockOrderFragment extends Fragment {

    @Bind(R.id.content_list)
    PullToRefreshListView contentList;
    private YuXianDeActivity activity;
    private View view;
    private MyStockOrderAdapter adapter;
    private boolean type = true;
    private List<MyStockOrderListBase.DataBeanX.DataBean> dataList = new ArrayList<>();
    private int page = 1;
    private float allPage = 1;
    private boolean sortOrder = false;
    private String keyword = "";
    private String pageType;
    private String cancelGuid;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            HttpUtile httpUtile;
            switch (what){
                case 0:
                    if (pageType.equals("all")) {
                        activity.setAllNum(dataList.size());
                    }
                    if (adapter!=null)
                        adapter.notifyDataSetChanged();
                    if (contentList!=null)
                        contentList.onRefreshComplete();
                    if (contentList!=null)
                        contentList.onRefreshComplete();

                    break;

            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (YuXianDeActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_storage_hospital, container, false);
        ButterKnife.bind(this, view);
        setView();
        return view;
    }

    private void setView(){
        //测试数据

        if (!TextUtils.isEmpty(getArguments().getString("type")))
            type = false;
        adapter = new MyStockOrderAdapter(dataList);
        adapter.setType(type);
        contentList.setMode(PullToRefreshBase.Mode.BOTH);
        contentList.setAdapter(adapter);
        adapter.setCallBack(new MyOrderNewAdapterCallBack() {
            @Override
            public void adapterCallBack(String guid, int position, boolean type) {
                cancelGuid = guid;
                if (type) {
                    //提示dialog
                    Intent intent = new Intent(getActivity(), PromptDialog.class);
                    getActivity().startActivityForResult(intent,0);
                }else{
                    //可输入的，提示dialog
                    Intent intent = new Intent(getActivity(), InputDialog.class);
                    intent.putExtra("title","请填写理由");
                    getActivity().startActivityForResult(intent,0);
                }
            }
        });
        //添加展示的回调
        adapter.setClickCallBack(new StorageHospitalOnClickCallBack() {
            @Override
            public void clickNo(int position, boolean type) {
                for (int i = 0 ; i < dataList.size();i++){
                    dataList.get(i).setType(false);
                }
                dataList.get(position).setType(type);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void clickTime(String hospitalGuid, String number) {

            }
        });
        adapter.notifyDataSetChanged();
        contentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //刷新
                page = 1;
                webData(keyword,sortOrder);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //加载
                page ++;
                webData(keyword,sortOrder);

            }
        });
        webData(keyword,sortOrder);
    }

    public void cancelOrder(){
        RequestBody formBody = new  FormBody.Builder()
                .add("guid", cancelGuid+"")
                .build();
        HttpUtile httpUtile = new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.CANCELPURCHASEORDER, formBody, new HttpUtileCallBack() {
            @Override
            public void getReturnStr(String returnStr) {
                Looper.prepare();
                Toast.makeText(getActivity(),"取消成功",Toast.LENGTH_LONG).show();
                webData(keyword,sortOrder);
                Looper.loop();

            }

            @Override
            public void getReturnStrFailure(String returnStr) {
                Looper.prepare();
                AbDialogUtil.removeDialog(getContext());
                Toast.makeText(getActivity(),returnStr,Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void getErrorStr(String errorStr) {
                AbDialogUtil.removeDialog(getContext());
            }
        },true);

    }


    public void setSortOrder(String keyword,boolean sortOrder){
        this.keyword  = keyword;
        this.sortOrder = sortOrder;
        webData(keyword,sortOrder);
    }
    public void setPage(){
        page = 1;
    }

    private void webData(String keyword,boolean sortOrder){
        pageType = getArguments().getString("DATA");
        RequestBody formBody;
        if (pageType.equals("all")){
            formBody = new  FormBody.Builder()
                    .add("pageIndex", page+"")
                    .add("pageSize", "10")
                    .add("sortField", "Id")
                    .add("sortOrder", sortOrder?"asc":"desc")
                    .add("filter_group", "{\"Rules\":[{\"Field\" : \"OrderStage\",\"Value\":\"0\",\"Operate\":\"equal\"},{\"Field\":\"businessNumber\",\"Value\":\""+keyword+"\",\"Operate\":\"contains\" }],\"Operate\" : \"and\"}")
                    .build();
        }else
        if (pageType.equals("no")){
            formBody = new  FormBody.Builder()
                    .add("pageIndex", page+"")
                    .add("pageSize", "10")
                    .add("sortField", "Id")
                    .add("sortOrder", sortOrder?"asc":"desc")
                    .add("filter_group", "{\"Rules\":[{\"Field\" : \"OrderStage\",\"Value\":\"1\",\"Operate\":\"equal\"},{\"Field\":\"businessNumber\",\"Value\":\"" + keyword + "\",\"Operate\":\"contains\" }],\"Operate\" : \"and\"}")
                    .build();
        } else{
            formBody = new  FormBody.Builder()
                    .add("pageIndex", page+"")
                    .add("pageSize", "10")
                    .add("sortField", "Id")
                    .add("sortOrder", sortOrder?"asc":"desc")
                    .add("filter_group", "{\"Rules\":[{\"Field\" : \"OrderStage\",\"Value\":\"3\",\"Operate\":\"equal\"},{\"Field\":\"businessNumber\",\"Value\":\"" + keyword + "\",\"Operate\":\"contains\" }],\"Operate\" : \"and\"}")
                    .build();
        }

        HttpUtile httpUtile = new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.SEARCHSPAREORDERLIST, formBody, new HttpUtileCallBack() {
            @Override
            public void getReturnStr(String returnStr) {
                MyStockOrderListBase data = JSON.parseObject(returnStr,MyStockOrderListBase.class);
                if (page == 1){
                    dataList.clear();

                    allPage = data.getData().getTotal();
                }
                if (Math.ceil(allPage/10) >= page) {
                    for (MyStockOrderListBase.DataBeanX.DataBean dataItem : data.getData().getData()) {
                        dataList.add(dataItem);
                    }
                }

                mHandler.sendEmptyMessage(0);

            }

            @Override
            public void getReturnStrFailure(String returnStr) {


                Looper.prepare();
                dataList.clear();
                mHandler.sendEmptyMessage(0);
                Toast.makeText(getActivity(),returnStr,Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void getErrorStr(String errorStr) {

            }
        },false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
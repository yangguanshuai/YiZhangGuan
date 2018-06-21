package com.guxingdongli.yizhangguan.view.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.alibaba.fastjson.JSON;
import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.controller.adapter.MessageAdapter;
import com.guxingdongli.yizhangguan.controller.adapter.callback.MessageAdapterCallBack;
import com.guxingdongli.yizhangguan.model.MessageBase;
import com.guxingdongli.yizhangguan.util.Constant;
import com.guxingdongli.yizhangguan.util.HttpUtile;
import com.guxingdongli.yizhangguan.util.HttpUtileCallBack;
import com.guxingdongli.yizhangguan.util.LogUtils;
import com.guxingdongli.yizhangguan.view.home.HomeActivity;
import com.guxingdongli.yizhangguan.view.message.MessageDetailsActivity;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshBase;
import com.yuxiaolong.yuxiandelibrary.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by jackmask on 2018/3/1.
 */

public class MessageFragment extends Fragment {

    @Bind(R.id.return_btn)
    ImageView returnBtn;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.content_list)
    PullToRefreshListView contentList;

    private HomeActivity abActivity;
    private View view;
    private MessageAdapter adapter;
    private List<MessageBase.MessageDataBase.dataBase> dataList = new ArrayList<>();
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if(what == 0){
                //在主线程中需要执行的操作，一般是UI操作
                if (adapter!=null)
                    adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        abActivity = (HomeActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        setView();
        return view;
    }

    private void setView(){
        returnBtn.setVisibility(View.INVISIBLE);
        titleText.setText("消息管理");

        adapter = new MessageAdapter(dataList);
        contentList.setMode(PullToRefreshBase.Mode.DISABLED);
        contentList.setAdapter(adapter);
        adapter.setCallBack(new MessageAdapterCallBack() {
            @Override
            public void selectItem(int position, MessageBase.MessageDataBase.dataBase data) {

                Intent intent = new Intent(getContext(), MessageDetailsActivity.class);
                intent.putExtra("titleStr",data.getMessageType()+"/"+"的标题");
                intent.putExtra("jumpType",data.getJumpType()+"");
                intent.putExtra("content",data.getMessageContent());
                intent.putExtra("msgUrl",data.getMsgUrl());
                intent.putExtra("guid",data.getGid());
                startActivityForResult(intent,2);
            }

            @Override
            public void delItem(int position, String guid) {
                delMessage(guid);
            }
        });
//        adapter.notifyDataSetChanged();
        getMessage();
    }
    public void delMessage(String guid){
        RequestBody body  = new FormBody.Builder()
                .add("guid", guid)
                .build();
        HttpUtile httpUtile =new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.DELETEMESSAGE, body, new HttpUtileCallBack() {

            @Override
            public void getReturnStr(String returnStr) {
                LogUtils.debug(returnStr);
//                getMessage();
                AbDialogUtil.removeDialog(getContext());
                getMessage();
            }

            @Override
            public void getReturnStrFailure(String returnStr) {
                LogUtils.debug(returnStr);
                AbDialogUtil.removeDialog(getContext());
            }

            @Override
            public void getErrorStr(String errorStr) {
                LogUtils.debug(errorStr);
                AbDialogUtil.removeDialog(getContext());
            }
        },true);
    }
    public void getMessage(){
        LogUtils.debug("获取消息列表");
        RequestBody body = new  FormBody.Builder().build();
        HttpUtile httpUtile =new HttpUtile(getActivity(), Constant.DOMAIN_NAME + Constant.SEARCHMESSAGE, body, new HttpUtileCallBack() {
            @Override
            public void getReturnStr(String returnStr) {
                LogUtils.debug("发送来的消息"+returnStr);
                MessageBase data = JSON.parseObject(returnStr,MessageBase.class);
                dataList.clear();
//                int noRead = 0 ;
                for (MessageBase.MessageDataBase.dataBase dataBase : data.getData().getData()){
                    dataList.add(dataBase);
//                    if (dataBase.getReadState().equals("未读取")){
//                        LogUtils.debug("第"+noRead+"个"+dataBase.getId());
//                        noRead++;
//                    }
                }
//                abActivity.setMessageNum(noRead);
                mHandler.sendEmptyMessage(0);
            }
            @Override
            public void getReturnStrFailure(String returnStr) {
                LogUtils.debug("发送来的消息失败"+returnStr);
            }
            @Override
            public void getErrorStr(String errorStr) {
                LogUtils.debug("发送来的消息失败111"+errorStr);

            }
        },false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

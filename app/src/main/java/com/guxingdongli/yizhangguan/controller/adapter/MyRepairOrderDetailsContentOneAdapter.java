package com.guxingdongli.yizhangguan.controller.adapter;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.model.MyRepairOrderDetailsContentBasicInfoBase;
import com.guxingdongli.yizhangguan.model.MyRepairOrderDetailsTabBase;
import com.yuxiaolong.yuxiandelibrary.universalAdapter.BaseCommAdapter;
import com.yuxiaolong.yuxiandelibrary.universalAdapter.ViewHolder;

import java.util.List;

/**
 * 维修单详情几个选项卡的其中一个页面
 * Created by jackmask on 2018/3/5.
 */

public class MyRepairOrderDetailsContentOneAdapter   extends BaseCommAdapter<MyRepairOrderDetailsContentBasicInfoBase> {

    public MyRepairOrderDetailsContentOneAdapter(List<MyRepairOrderDetailsContentBasicInfoBase> datas) {
        super(datas);
    }

    @Override
    protected void setUI(ViewHolder holder, int position, Context context) {
        MyRepairOrderDetailsContentBasicInfoBase data = getItem(position);
        TextView title_tv = holder.getItemView(R.id.title_tv);
        LinearLayout  score_layout = holder.getItemView(R.id.score_layout);
        RatingBar rc_rate = holder.getItemView(R.id.rc_rate);
        TextView content_tv = holder.getItemView(R.id.content_tv);
        title_tv.setText(data.getName());
        content_tv.setText(data.getContent());
        if (data.getName().equals("服务评价")){
            content_tv.setVisibility(View.GONE);
            score_layout.setVisibility(View.VISIBLE);
            try {
                rc_rate.setRating(Float.valueOf(data.getContent()));
            }catch (Exception e){
                rc_rate.setRating(0);
            }

        }else{
            content_tv.setVisibility(View.VISIBLE);
            score_layout.setVisibility(View.GONE);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_my_repair_order_details_basic_info;
    }
}
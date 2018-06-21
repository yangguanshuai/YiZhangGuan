package com.guxingdongli.yizhangguan.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.guxingdongli.yizhangguan.R;
import com.guxingdongli.yizhangguan.controller.adapter.callback.MakeUpInfoCallBack;
import com.guxingdongli.yizhangguan.controller.adapter.callback.StoraeHospitalDetailsCallBack;
import com.guxingdongli.yizhangguan.model.PreparationOrderDetailsBase;
import com.guxingdongli.yizhangguan.model.StoraeHospitalDetailsBase;
import com.guxingdongli.yizhangguan.model.TestHospitalBean;
import com.guxingdongli.yizhangguan.model.passvalue.MyStockOrderInput;
import com.guxingdongli.yizhangguan.view.home.dialog.PromptDialog;
import com.guxingdongli.yizhangguan.view.home.home_hospital.fragment.callback.StorageHospitalOnClickCallBack;
import com.guxingdongli.yizhangguan.view.home.home_merchants.MakeUpInfoActivity;
import com.guxingdongli.yizhangguan.view.home.home_merchants.QRExamineActivity;
import com.guxingdongli.yizhangguan.view.home.home_merchants.StockingInfoActivity;
import com.yuxiaolong.yuxiandelibrary.NoSlideListView;
import com.yuxiaolong.yuxiandelibrary.universalAdapter.BaseCommAdapter;
import com.yuxiaolong.yuxiandelibrary.universalAdapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackmask on 2018/3/8.
 */

public class MakeUpInfoAdapter extends BaseAdapter {

    private MakeUpInfoCallBack callBack;
    private boolean type = true;
    private AbActivity abActivity;
    private LayoutInflater inflater;
    boolean editextCon = false;
    private List<PreparationOrderDetailsBase.DataBean.DetailsListBean> dataList;

    public MakeUpInfoAdapter (AbActivity constant,List<PreparationOrderDetailsBase.DataBean.DetailsListBean> dataList){
        this.abActivity = constant;
        this.dataList  = dataList;
        this.inflater = ((LayoutInflater) abActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE));
    }
    public void setType(boolean type) {
        this.type = type;
    }

    public void setCallBack(MakeUpInfoCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_make_up_info, null);
            viewHolder = new ViewHolder(convertView,position);
            viewHolder.stocking_info_layout.setTag(position);
            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.stocking_info_layout.setTag(position);
        }
        final PreparationOrderDetailsBase.DataBean.DetailsListBean data = dataList.get(position);
        viewHolder.batch_number.setText(data.getBatchNumber());
        viewHolder.serial_number.setText(data.getSerialNum());
        viewHolder.coding.setText(data.getProductNum());
        viewHolder.title_name.setText(data.getName());
        viewHolder.num_until_tv.setText(data.getQuantity()+" / " + data.getUnit());
        viewHolder.specification_tv.setText(data.getPacking());
        viewHolder.num_tv.setText(data.getPrice());

        viewHolder.make_up_info_layout.setVisibility(View.GONE);
        viewHolder.stocking_info_layout.setVisibility(View.VISIBLE);
        if (type) {
            for (TextView textView:viewHolder.marks){
                textView.setVisibility(View.VISIBLE);
            }
            viewHolder.stocking_select_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack != null) {
                        callBack.selectItem(position,viewHolder.stocking_data_tv);
                    }
                }
            });
            if (viewHolder.batch_number.getTag() instanceof TextWatcher) {
                viewHolder.batch_number.removeTextChangedListener((TextWatcher) viewHolder.batch_number.getTag());
            }
            if (viewHolder.serial_number.getTag() instanceof TextWatcher) {
                viewHolder.serial_number.removeTextChangedListener((TextWatcher) viewHolder.serial_number.getTag());
            }
            if (viewHolder.coding.getTag() instanceof TextWatcher) {
                viewHolder.coding.removeTextChangedListener((TextWatcher) viewHolder.coding.getTag());
            }
            TextWatcher batchNumber = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (callBack != null) {
                        data.setBatchNumber(viewHolder.batch_number.getText().toString());
                        callBack.inputBatchNumber((int)viewHolder.stocking_info_layout.getTag(),viewHolder.batch_number.getText().toString());
                    }
                }
            };
            TextWatcher serialNumber = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (callBack != null) {
                        data.setSerialNum(viewHolder.serial_number.getText().toString());
                        callBack.inputSerialNumber((int)viewHolder.stocking_info_layout.getTag(),viewHolder.serial_number.getText().toString());
                    }
                }
            };
            TextWatcher coding = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (callBack != null) {
                        data.setProductNum(viewHolder.coding.getText().toString());
                        callBack.inputCoding((int)viewHolder.stocking_info_layout.getTag(),viewHolder.coding.getText().toString());
                    }
                }
            };
            viewHolder.batch_number.addTextChangedListener(batchNumber);
            viewHolder.serial_number.addTextChangedListener(serialNumber);
            viewHolder.coding.addTextChangedListener(coding);
            viewHolder.batch_number.setTag(batchNumber);
            viewHolder.serial_number.setTag(serialNumber);
            viewHolder.coding.setTag(coding);

        }else{
            for (TextView textView:viewHolder.marks){
                textView.setVisibility(View.GONE);
            }
            viewHolder.batch_number.setEnabled(false);
            viewHolder.serial_number.setEnabled(false);
            viewHolder.coding.setEnabled(false);

        }
        return convertView;
    }
    class ViewHolder {
       private LinearLayout make_up_info_layout ;
       private LinearLayout stocking_info_layout ;
       private EditText batch_number ;
        private EditText serial_number ;
        private TextView title_name;
        private EditText coding;
        private TextView num_until_tv ;
        private TextView specification_tv;
        private TextView num_tv;
        private LinearLayout stocking_select_btn;
        private TextView stocking_data_tv ;
        private TextView  mark_1,mark_2,mark_3,mark_4,mark_5,mark_6;
        private List<TextView> marks = new ArrayList<>();


        public ViewHolder(View view,int pisition){
            make_up_info_layout = view.findViewById(R.id.make_up_info_layout );
            stocking_info_layout = view.findViewById(R.id.stocking_info_layout);
            batch_number = view.findViewById(R.id.batch_number);
            serial_number = view.findViewById(R.id.serial_number);
            title_name = view.findViewById(R.id.title_name);
           coding = view.findViewById(R.id.coding);
            num_until_tv = view.findViewById(R.id.num_until_tv);
            specification_tv = view.findViewById(R.id.specification_tv);
            num_tv = view.findViewById(R.id.num_tv);
             stocking_select_btn = view.findViewById(R.id.stocking_select_btn);
             stocking_data_tv = view.findViewById(R.id.stocking_data_tv);
            mark_1 = view.findViewById(R.id.mark_1);
            mark_2 = view.findViewById(R.id.mark_2);
            mark_3 = view.findViewById(R.id.mark_3);
            mark_4 = view.findViewById(R.id.mark_4);
            mark_5 = view.findViewById(R.id.mark_5);
            mark_6 = view.findViewById(R.id.mark_6);
            marks.add(mark_1);
            marks.add(mark_2);
            marks.add(mark_3);
            marks.add(mark_4);
            marks.add(mark_5);
            marks.add(mark_6);
            stocking_info_layout.setTag(pisition);//存tag值

            // acceptance_num.setOnFocusChangeListener(new myFocusChange(this));
        }



    }

}
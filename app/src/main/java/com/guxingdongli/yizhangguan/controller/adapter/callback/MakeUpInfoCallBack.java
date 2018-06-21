package com.guxingdongli.yizhangguan.controller.adapter.callback;

import android.widget.TextView;

/**
 * Created by jackmask on 2018/3/8.
 */

public interface MakeUpInfoCallBack {
    public abstract void selectItem(int position,TextView view);
    public abstract void inputBatchNumber(int position,String view);
    public abstract void inputSerialNumber(int position,String view);
    public abstract void inputCoding(int position,String view);


}

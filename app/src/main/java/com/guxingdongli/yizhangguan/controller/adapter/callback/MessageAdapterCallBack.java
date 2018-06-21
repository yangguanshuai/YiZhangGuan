package com.guxingdongli.yizhangguan.controller.adapter.callback;

import android.widget.TextView;

import com.guxingdongli.yizhangguan.model.MessageBase;

/**
 * @author 余先德
 * @data 2018/4/17
 */

public interface MessageAdapterCallBack {
    public abstract void selectItem(int position,MessageBase.MessageDataBase.dataBase data);
    public abstract void delItem(int position,String guid);
}

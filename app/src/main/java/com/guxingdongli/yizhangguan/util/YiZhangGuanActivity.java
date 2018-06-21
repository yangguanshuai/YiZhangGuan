package com.guxingdongli.yizhangguan.util;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.yuxiaolong.yuxiandelibrary.YuXianDeActivity;

/**
 * @author 余先德
 * @data 2018/3/24
 */

public class YiZhangGuanActivity extends YuXianDeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }
    public void setNum(int position,String num){

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            finish();
            return true;//不执行父类点击事件
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }
}

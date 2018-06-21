package com.yuxiaolong.yuxiandelibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/4/25.
 */

public class NoSlideListView extends ListView {

    public NoSlideListView(Context context) {
        super(context);
    }

    public NoSlideListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSlideListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

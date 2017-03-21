package com.example.npc.myweather2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.npc.myweather2.R;

/**
 * Created by npc on 3-20 0020.
 */

public class MyBackgroundPreference extends DialogPreference {
    private ImageView image;
    public MyBackgroundPreference(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        setDialogLayoutResource(R.layout.background_dialog);
        //setPositiveButtonText("恢复默认");
        //setNegativeButtonText("选取图片");
        setDialogIcon(null);
        setDialogTitle(null);
    }
    public void onDialogClosed(boolean positiveResult){
        if(positiveResult){

        }
    }
    //获取sharepreference中的配置参数
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
        } else {
            // Set default state from the XML attribute
        }
    }
    //默认值
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a;
    }
    //弹出对话框时初始化
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }
}

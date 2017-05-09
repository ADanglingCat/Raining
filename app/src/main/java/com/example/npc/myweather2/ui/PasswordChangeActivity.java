package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class PasswordChangeActivity extends BaseActivity implements View.OnClickListener {
    private String passwordO;
    private String passwordF;
    private String passwordS;
    private EditText passwordOEd;//原密码
    private EditText passwordFEd;//新密码1
    private EditText passwordSEd;//新密码2
    private Button changeBu;//确认按钮
    private Button backBu;//返回按钮
    private boolean cancel;
    private View focusView;
    private static final String TAG = "TAGChangePWDActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        passwordOEd = (EditText) findViewById(R.id.password_old);
        passwordFEd = (EditText) findViewById(R.id.password_change_first);
        passwordSEd = (EditText) findViewById(R.id.password_change_second);
        changeBu = (Button) findViewById(R.id.change_button);
        backBu = (Button) findViewById(R.id.backBu_password);
        cancel = false;
        focusView = null;
        changeBu.setOnClickListener(this);
        backBu.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.change_button:
                resetPassword();
                break;
            case R.id.backBu_password:
                onBackPressed();
                finish();
                break;
            default:
        }
    }

    private void resetPassword() {
        passwordO = passwordOEd.getText().toString();
        passwordF = passwordFEd.getText().toString();
        passwordS = passwordSEd.getText().toString();
        cancel = false;
        passwordOEd.setError(null);
        passwordFEd.setError(null);
        passwordSEd.setError(null);
        //新密码2格式
        if (TextUtils.isEmpty(passwordS)) {
            passwordSEd.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = passwordSEd;
        } else if (!passwordF.equals(passwordS)) {
            passwordSEd.setError("两次密码输入不一致");
            cancel = true;
            focusView = passwordSEd;
        }
        //新密码1格式
        if (TextUtils.isEmpty(passwordF)) {
            passwordFEd.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = passwordFEd;
        } else if (!isPasswordValid(passwordF)) {
            passwordFEd.setError(getString(R.string.error_invalid_password));
            cancel = true;
            focusView = passwordFEd;
        }
        //旧密码格式
        if (TextUtils.isEmpty(passwordO)) {
            passwordOEd.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = passwordOEd;
        } else if (!isPasswordValid(passwordO)) {
            passwordOEd.setError(getString(R.string.error_invalid_password));
            cancel = true;
            focusView = passwordOEd;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            BmobUser.updateCurrentUserPassword(MyUtil.getMD5(passwordO),MyUtil.getMD5(passwordF), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        MyUtil.showToast("密码修改成功");
                        Intent intent = new Intent(getApplicationContext(), PersonalActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Log.e(TAG, "done: "+e );
                        MyUtil.showToast("密码修改失败"+e.getMessage());

                    }
                }
            });

        }

    }


    private boolean isPasswordValid(String password) {
        String pattern = "\\w{6,20}";
        return Pattern.matches(pattern, password);
    }

}

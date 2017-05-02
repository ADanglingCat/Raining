package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class FindPWDActivity extends BaseActivity implements View.OnClickListener {
    private EditText emailEd;
    private String email;
    private Button findBu;
    private Button backBu;
    private boolean cancel;
    private View focusView;
    private static final String TAG = "TAGFindPWDActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        emailEd = (EditText) findViewById(R.id.email_find);
        findBu = (Button) findViewById(R.id.find_button);
        backBu = (Button) findViewById(R.id.backBu_find);

        cancel = false;
        focusView = null;
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        if (email != null) {
            emailEd.setText(email);
        }
        findBu.setOnClickListener(this);
        backBu.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.find_button:
                resetPassword();
                break;
            case R.id.backBu_find:
                finish();
                onBackPressed();
                break;
            default:
        }
    }


    private void resetPassword() {
        email = emailEd.getText().toString();
        cancel = false;
        emailEd.setError(null);

        if (TextUtils.isEmpty(email)) {
            emailEd.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = emailEd;
        } else if (!isEmailValid(email)) {
            emailEd.setError(getString(R.string.error_invalid_email));
            cancel = true;
            focusView = emailEd;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        MyUtil.showToast("请到邮箱进行密码重置操作");
                        Intent intent = new Intent(FindPWDActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        MyUtil.showToast(e.getMessage());
                    }
                }
            });

        }

    }

    private boolean isEmailValid(String email) {
        String pattern = "\\w{1,30}@\\w+\\.\\w+";
        return Pattern.matches(pattern, email);
    }

}

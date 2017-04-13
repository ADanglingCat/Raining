package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import java.util.regex.Pattern;

public class FindPWDActivity extends BaseActivity implements View.OnClickListener {
    private EditText emailEd;
    private String email;
    private String code;
    private String passwordF;
    private String passwordS;
    private EditText codeEd;
    private EditText passwordFEd;
    private EditText passwordSEd;
    private Button sendCodeBu;
    private Button findBu;
    private Button backBu;
    private boolean cancel;
    private View focusView;
    private CountDownTimer timer;
    private static final String TAG = "TAGFindPWDActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        emailEd = (EditText) findViewById(R.id.email_find);
        codeEd = (EditText) findViewById(R.id.code);
        passwordFEd = (EditText) findViewById(R.id.password_first);
        passwordSEd = (EditText) findViewById(R.id.password_second);
        sendCodeBu = (Button) findViewById(R.id.send_code);
        findBu = (Button) findViewById(R.id.find_button);
        backBu = (Button) findViewById(R.id.backBu_find);
        timer=new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendCodeBu.setText("发送中..."+millisUntilFinished/1000+"秒");
                Log.d(TAG, "onTick: "+millisUntilFinished);
            }

            @Override
            public void onFinish() {
                sendCodeBu.setClickable(true);
                sendCodeBu.setText("发送验证码");
            }
        };
        cancel = false;
        focusView = null;
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        if (email != null) {
            emailEd.setText(email);
        }
        sendCodeBu.setOnClickListener(this);
        findBu.setOnClickListener(this);
        backBu.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.send_code:
                sendCode();
                break;
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

    private void sendCode() {
        email = emailEd.getText().toString();
        emailEd.setError(null);
        cancel = false;
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
            sendCodeBu.setClickable(false);
            timer.start();
            MyUtil.showToast(FindPWDActivity.this, "发送验证码");
        }
    }

    private void resetPassword() {
        email = emailEd.getText().toString();
        code = codeEd.getText().toString();
        passwordF = passwordFEd.getText().toString();
        passwordS =passwordSEd.getText().toString();
        cancel = false;
        codeEd.setError(null);
        emailEd.setError(null);
        passwordFEd.setError(null);
        passwordSEd.setError(null);
        if(TextUtils.isEmpty(code)){
            codeEd.setError(getString(R.string.error_field_required));
            cancel=true;
            focusView = codeEd;
        }else if(!isCodeValid(code)){
            codeEd.setError(getString(R.string.error_invalid_code));
            cancel=true;
            focusView = codeEd;
        }

        if (TextUtils.isEmpty(passwordF)) {
            passwordFEd.setError(getString(R.string.error_field_required));
            cancel = true;
            focusView = passwordFEd;
        } else if (!isPasswordValid(passwordF)) {
            passwordFEd.setError(getString(R.string.error_invalid_password));
            cancel = true;
            focusView = passwordFEd;
        }else{
            if (TextUtils.isEmpty(passwordS)) {
                passwordSEd.setError(getString(R.string.error_field_required));
                cancel = true;
                focusView = passwordSEd;
            }else if (!passwordF.equals(passwordS)) {
                passwordSEd.setError("两次密码输入不一致");
                cancel = true;
                focusView = passwordSEd;
            }
        }

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
            MyUtil.showToast(FindPWDActivity.this, "比对验证码");
            Intent intent = new Intent(FindPWDActivity.this, PersonalActivity.class);
            startActivity(intent);
            timer.cancel();
            finish();
        }

    }

    private boolean isEmailValid(String email) {
        String pattern = "\\w{1,30}@\\w+\\.\\w+";
        return Pattern.matches(pattern, email);
    }

    private boolean isPasswordValid(String password) {
        String pattern = "\\w{6,20}";
        return Pattern.matches(pattern, password);
    }

    private boolean isCodeValid(String code) {
        String pattern = "\\d{4}";
        return Pattern.matches(pattern, code);
    }
    public void onBackPressed(){
        super.onBackPressed();
        timer.cancel();
    }
}

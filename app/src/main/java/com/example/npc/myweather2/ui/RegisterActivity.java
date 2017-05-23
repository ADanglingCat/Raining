package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{
        private EditText emailEd;
        private String email;
        private String passwordF;
        private String passwordS;
        private EditText passwordFEd;
        private EditText passwordSEd;
        private Button registerBu;
        private Button backBu;
        private boolean cancel;
        private View focusView;
        private static final String TAG = "TAGRegisterActivity";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            emailEd = (EditText) findViewById(R.id.email_register);
            passwordFEd = (EditText) findViewById(R.id.password_first_register);
            passwordSEd = (EditText) findViewById(R.id.password_second_register);
            registerBu = (Button) findViewById(R.id.register_button);
            backBu = (Button) findViewById(R.id.backBu_register);
            cancel = false;
            focusView = null;
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            passwordF = intent.getStringExtra("password");
            if (email != null) {
                emailEd.setText(email);
            }
            if (passwordF != null) {
                passwordFEd.setText(passwordF);
            }
            registerBu.setOnClickListener(this);
            backBu.setOnClickListener(this);
        }

        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.register_button:
                    confirmPressed();

                    break;
                case R.id.backBu_register:
                    onBackPressed();
                    break;
                default:
            }
        }
        private void confirmPressed() {
            email = emailEd.getText().toString();
            passwordF = passwordFEd.getText().toString();
            passwordS =passwordSEd.getText().toString();
            cancel = false;
            emailEd.setError(null);
            passwordFEd.setError(null);
            passwordSEd.setError(null);

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
                registerBu.setClickable(false);
                _User user=new _User();
                user.setUsername(email);
                String p=MyUtil.getMD5(passwordF);
                user.setPassword(p);
                user.setEmail(email);
                MyUtil.showToast("注册中...");
                user.signUp(new SaveListener<_User>(){
                    public void done(_User u, BmobException e){
                        if(e==null){
                            MyUtil.showToast("注册成功,请及时验证邮箱");
                            Intent intent = new Intent(getApplicationContext(), PersonalActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            MyUtil.showToast("注册失败:"+e.getMessage());
                            registerBu.setClickable(true);
                            Log.e(TAG, "done: "+e);
                        }
                    }
                });

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
    @Override
    public void onBackPressed(){
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    }

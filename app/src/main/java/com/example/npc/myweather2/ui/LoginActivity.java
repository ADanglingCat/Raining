package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.Setting;
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.MyUtil;

import java.util.List;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.npc.myweather2.util.MyUtil.getMD5;

//import android.widget.AutoCompleteTextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private SharedPreferences.Editor editor;
    private Button mEmailSignInButton;
    private TextView findPWD;
    private TextView register;
    private String email;
    private String password;
    private Button back;
    private static final String TAG = "TAGLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        findPWD = (TextView) findViewById(R.id.find_pwd);
        register = (TextView) findViewById(R.id.register);
        mPasswordView = (EditText) findViewById(R.id.password);
        back = (Button) findViewById(R.id.backBu_login);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);
        findPWD.setOnClickListener(this);
        register.setOnClickListener(this);
        back.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

//        rememberPWD.setOnCheckedChangeListener(this);
//        autoLogin.setOnCheckedChangeListener(this);
    }

    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.email_sign_in_button:

                attemptLogin();
                break;
            case R.id.backBu_login:
                onBackPressed();
                finish();
                break;
            case R.id.find_pwd:
                intent = new Intent(LoginActivity.this, FindPWDActivity.class);
                if (email != null && !"".equals(email)) {
                    intent.putExtra("email", email);
                }
                startActivity(intent);
                finish();
                break;
            case R.id.register:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                if (email != null && !"".equals(email)) {
                    intent.putExtra("email", email);
                }
                if (password != null && !"".equals(password)) {
                    intent.putExtra("password", password);
                }
                startActivity(intent);
                finish();
                break;
            default:
        }
    }

    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final _User user = new _User();
            user.setUsername(email);
            user.setPassword(getMD5(password));
            MyUtil.showToast("登录中...");
            mEmailSignInButton.setClickable(false);
            user.login(new SaveListener<_User>() {
                public void done(_User u, BmobException e) {
                    if (e == null) {
                        syncSetting();
                        MyUtil.showToast("登陆成功...");
                        Intent intent = new Intent(LoginActivity.this, PersonalActivity.class);
                        intent.putExtra("login", "login");
                        startActivity(intent);
                        finish();
                    } else {
                        user.setPassword(password);
                        user.login(new SaveListener<_User>() {
                            public void done(_User u, BmobException e) {
                                if (e == null) {
                                    syncSetting();
                                    MyUtil.showToast("登陆成功...");
                                    Intent intent = new Intent(LoginActivity.this, PersonalActivity.class);
                                    intent.putExtra("login", "login");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    mEmailSignInButton.setClickable(true);
                                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                                    mPasswordView.requestFocus();
                                }
                            }
                        });

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

    public void syncSetting() {
        _User user = BmobUser.getCurrentUser(_User.class);
        if (user != null && user.getEmailVerified()) {

            BmobQuery<Setting> query = new BmobQuery<>();
            query.addWhereEqualTo("user", user);
            query.findObjects(new FindListener<Setting>() {
                @Override
                public void done(List<Setting> list, BmobException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            Setting setting = list.get(0);
                            editor.putBoolean("Notify", setting.getNotify())
                                    .putBoolean("autoUpdate", setting.getAutoUpdate())
                                    .putBoolean("danmaku", setting.getDanmaku())
                                    .putBoolean("updateMode", setting.getUpdateMode())
                                    .putBoolean("nightUpdate", setting.getNightUpdate())
                                    .putBoolean("diy", setting.getDiy())
                                    .putBoolean("autoBing", setting.getAutoBing())
                                    .putBoolean("save", setting.getSave())
                                    .putLong("notifyTime", setting.getNotifyTime())
                                    .putString("updateFre", setting.getUpdateFre())
                                    .putString("alpha", setting.getAlpha());
                            editor.apply();
                            MyUtil.showToast("设置同步成功");
                        }
                    } else {
                        MyUtil.showToast("设置同步失败:" + e.getMessage());
                    }
                }
            });

        } else {
            MyUtil.showToast("验证邮箱后可以同步设置");
            BmobUser.requestEmailVerify(String.valueOf(email), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyUtil.showToast("请求验证邮件成功，请到" + email + "邮箱中进行激活。");
                    } else {
                        MyUtil.showToast("失败:" + e.getMessage());
                    }
                }
            });
        }

    }
}


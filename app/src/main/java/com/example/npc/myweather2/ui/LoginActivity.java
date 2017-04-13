package com.example.npc.myweather2.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.MyUtil;

import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

//import android.widget.AutoCompleteTextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
//    private static final String[] DUMMY_CREDENTIALS = new String[]{
//            "foo@example.com:helloo", "bar@example.com:worldd"
//    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
   // private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences preferences;
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        findPWD = (TextView) findViewById(R.id.find_pwd);
        register = (TextView) findViewById(R.id.register);
        mPasswordView = (EditText) findViewById(R.id.password);
        back=(Button)findViewById(R.id.backBu_login);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
                mEmailSignInButton.setClickable(false);
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
                break;
            default:
        }
    }

    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
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
            _User user=new _User();
            String p= MyUtil.getMD5(password);
            user.setUsername(email);
            user.setPassword(p);


            user.login(new SaveListener<_User>() {
                public void done(_User u,BmobException e){
                    if(e==null){
                        //editor.putString("email", email);
                        //editor.putBoolean("state",true);
                        //editor.apply();
                        MyUtil.showToast("登陆成功...");
                        Intent intent = new Intent(LoginActivity.this, PersonalActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        mEmailSignInButton.setClickable(true);
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }
            });
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

           // showProgress(true);
            //mAuthTask = new UserLoginTask(email, password);
           // mAuthTask.execute((Void) null);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    /**
//     * Represents an asynchronous login/registration task used to authenticate
//     * the user.
//     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//        private final String mPassword;
//        final boolean[] flag={false};
//        UserLoginTask(String email, String password) {
//            mEmail = email;
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//            _User user=new _User();
//            String p=MyUtil.getMD5(mPassword);
//            user.setUsername(mEmail);
//            user.setPassword(p);
//            Log.d(TAG, "doInBackground: "+p);
//            user.login(new SaveListener<_User>() {
//                public void done(_User u,BmobException e){
//                    if(e==null){
//                        flag[0]=true;
//                        Log.d(TAG, "成功: !!!!"+flag[0]);
//                    }else{
//                        flag[0]=false;
//                        Log.e(TAG, "done: "+e);
//                    }
//                }
//            });
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            showProgress(false);
//            Log.d(TAG, "onPostExecute: 失败"+flag[0]);
//            if (flag[0]) {
//                editor.putString("email", mEmail);
//                editor.putString("password", getMD5(mPassword));
//                editor.putBoolean("state",true);
//                editor.apply();
//                MyUtil.showToast("登陆成功...");
//                Intent intent = new Intent(LoginActivity.this, PersonalActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }

}


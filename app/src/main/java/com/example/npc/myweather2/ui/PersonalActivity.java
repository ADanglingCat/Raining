package com.example.npc.myweather2.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.npc.myweather2.R.string.sign;

public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private Button backBu;
    private Button exitBu;
    private RelativeLayout imageLayout;
    private RelativeLayout nameLayout;
    private RelativeLayout signLayout;
    private RelativeLayout sexLayout;
    private RelativeLayout emailLayout;
    private CircleImageView pImage;
    private TextView pName;
    private TextView pSign;
    private TextView pEmail;
    private TextView pSex;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String TAG = "TAGPersonalActivity";
    private String sex;
    private boolean isChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        initVar();
        backBu.setOnClickListener(this);
        exitBu.setOnClickListener(this);
        imageLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        signLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);

    }

    public void onResume() {
        super.onResume();
        isChanged = preferences.getBoolean("isChanged", false);
        Log.d(TAG, "onResume: ");
        String headerPath = preferences.getString("headerPath", null);
        if (headerPath != null) {
            Bitmap bitmap = getBitmap(headerPath);
            pImage.setImageBitmap(bitmap);
        } else {
            pImage.setImageResource(R.drawable.ic_userimage);
        }
        //  BmobFile image=(BmobFile)BmobUser.getObjectByKey("userImage");
//        String name = (String)BmobUser.getObjectByKey("name");
//        String sign = (String)BmobUser.getObjectByKey("sign");
//        String email =(String)BmobUser.getObjectByKey("email");
        String name = preferences.getString("name",(String)BmobUser.getObjectByKey("name"));
        String sign = preferences.getString("sign", (String)BmobUser.getObjectByKey("sign"));
        String email = (String) BmobUser.getObjectByKey("email");
        //sex = preferences.getString("sex", (String)BmobUser.getObjectByKey("sex"));
        sex= (String)BmobUser.getObjectByKey("sex");
        //if (name == null || "".equals(name)) {
        //   pName.setText("蕾姆");
        // } else {
        pName.setText(name);
        // }
        // if (sign == null || "".equals(sign)) {
        //   pSign.setText(getString(R.string.my_sign));
        //} else {
        if (sign.length() > 14) {
            sign = sign.substring(0, 14) + "...";
        }
        pSign.setText(sign);

        // }
        if (email != null) {
            pEmail.setText(email);
        }
        //  if (sex != null) {
        pSex.setText(sex);
        // }else{
        //    pSex.setText("保密");
        // }
    }

    public void initVar() {
        isChanged = false;
        preferences = PreferenceManager.getDefaultSharedPreferences(PersonalActivity.this);
        editor = PreferenceManager.getDefaultSharedPreferences(PersonalActivity.this).edit();
        backBu = (Button) findViewById(R.id.backBu_personal);
        exitBu = (Button) findViewById(R.id.exit_bu);
        imageLayout = (RelativeLayout) findViewById(R.id.image_layout);
        nameLayout = (RelativeLayout) findViewById(R.id.name_layout);
        signLayout = (RelativeLayout) findViewById(R.id.sign_layout);
        sexLayout = (RelativeLayout) findViewById(R.id.sex_layout);
        emailLayout = (RelativeLayout) findViewById(R.id.email_layout);
        pImage = (CircleImageView) findViewById(R.id.p_image);
        pName = (TextView) findViewById(R.id.p_name);
        pSign = (TextView) findViewById(R.id.p_sign);
        pEmail = (TextView) findViewById(R.id.p_email);
        pSex = (TextView) findViewById(R.id.p_sex);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.backBu_personal:
                onBackPressed();
                break;
            case R.id.exit_bu:
                intent = new Intent(PersonalActivity.this, LoginActivity.class);
                startActivity(intent);
                BmobUser.logOut();
                //editor.putBoolean("state",false);
                //editor.apply();
                finish();
                //MyUtil.showToast(PersonalActivity.this, "退出登录");
                break;
            case R.id.image_layout:
                intent = new Intent(PersonalActivity.this, ChoosePictureActivity.class);
                intent.putExtra("headerPath", "headerPath");
                startActivity(intent);
                break;
            case R.id.name_layout:
                intent = new Intent(PersonalActivity.this, EditActivity.class);
                intent.putExtra("personalType", getString(R.string.name));
                startActivity(intent);
                break;
            case R.id.sign_layout:
                intent = new Intent(PersonalActivity.this, EditActivity.class);
                intent.putExtra("personalType", getString(sign));
                startActivity(intent);
                break;
            case R.id.sex_layout:
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalActivity.this)
                        .setItems(R.array.personal_sex_list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        pSex.setText("男");
                                        break;
                                    case 1:
                                        pSex.setText("女");

                                        break;
                                    case 2:
                                        pSex.setText("保密");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                builder.create().show();

                break;
        }
    }

    public void onBackPressed() {
        String content = pSex.getText().toString();
        if (!content.equals(sex)) {
            Log.d(TAG, "onClick: 222222222222");
            isChanged=true;
            editor.putString("sex",content);
        }else{
            isChanged=preferences.getBoolean("isChanged",false);
        }
        if (isChanged) {
            _User user=new _User();
            user.setName(preferences.getString("name",getString(R.string.my_name)))
                    .setSign(preferences.getString("sign",getString(R.string.my_sign)))
                    .setSex(preferences.getString("sex","保密"));
            user.update(BmobUser.getCurrentUser(_User.class).getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        MyUtil.showToast("资料已同步");
                    }else{
                        MyUtil.showToast("资料同步失败");
                        Log.e(TAG, "done: "+e);
                    }
                }
            });
        }
        editor.putBoolean("isChanged",false);
        editor.apply();
        super.onBackPressed();
        finish();
    }

    //图片处理
    public Bitmap getBitmap(String path) {

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        if (bitmap != null) {
            int bheight = bitmap.getHeight();
            int bwidth = bitmap.getWidth();
            if (bheight > 4096 || bwidth > 4096) {

                bheight = (int) (bitmap.getHeight() * 0.9);
                bwidth = (int) (bitmap.getWidth() * 0.9);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_userimage);
        }
        return bitmap;

    }
}

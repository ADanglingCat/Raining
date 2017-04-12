package com.example.npc.myweather2.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private static final String TAG = "TAGPersonalActivity";
    private SharedPreferences.Editor editor;

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
        String headerPath = preferences.getString("headerPath", null);
        if (headerPath != null) {
            Bitmap bitmap = getBitmap(headerPath);
            pImage.setImageBitmap(bitmap);
        } else {
            pImage.setImageResource(R.drawable.ic_userimage);
        }
        String name = preferences.getString("昵称", null);
        String sign = preferences.getString("签名", null);
        String email = preferences.getString("email", null);

        String sex = preferences.getString("sex", null);
        if (name == null || "".equals(name)) {
            editor.putString("昵称", "蕾姆");
            editor.apply();
            pName.setText("蕾姆");
        } else {
            pName.setText(name);
        }
        if (sign == null || "".equals(sign)) {
            pSign.setText("未来的事不笑着说出来可不行呢");
            editor.putString("签名", "未来的事不笑着说出来可不行呢");
            editor.apply();
        } else {
            if (sign.length() > 14) {
                sign = sign.substring(0, 14) + "...";
            }
            pSign.setText(sign);

        }
        if (email != null) {
            pEmail.setText(email);
        }
        if (sex != null) {
            pSex.setText(sex);

        }
    }

    public void initVar() {
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
                intent = new Intent(PersonalActivity.this,LoginActivity.class);
                startActivity(intent);
                editor.putBoolean("state",false);
                editor.apply();
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
                intent.putExtra("personalType", "昵称");
                startActivity(intent);
                break;
            case R.id.sign_layout:
                intent = new Intent(PersonalActivity.this, EditActivity.class);
                intent.putExtra("personalType", "签名");
                startActivity(intent);
                break;
            case R.id.sex_layout:
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalActivity.this)
                        .setItems(R.array.personal_sex_list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:

                                        editor.putString("sex", "男");
                                        editor.apply();
                                        pSex.setText("男");
                                        break;
                                    case 1:
                                        editor.putString("sex", "女");
                                        editor.apply();
                                        pSex.setText("女");

                                        break;
                                    case 2:
                                        editor.putString("sex", "保密");
                                        editor.apply();
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

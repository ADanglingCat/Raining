package com.example.npc.myweather2.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.Setting;
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.npc.myweather2.R.id.email;

public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private Button backBu;
    private Button exitBu;
    private RelativeLayout imageLayout;
    private RelativeLayout nameLayout;
    private RelativeLayout signLayout;
    private RelativeLayout sexLayout;
    private RelativeLayout danmuLayout;
    private RelativeLayout emailLayout;
    private RelativeLayout syncLayout;
    private CircleImageView pImage;
    private TextView pName;
    private TextView pSign;
    private TextView pEmail;
    private TextView pSex;
    private TextView pDanmu;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String TAG = "TAGPersonalActivity";
    private String sex;
    private boolean danmu;
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
       danmuLayout.setOnClickListener(this);
        emailLayout.setOnClickListener(this);
        syncLayout.setOnClickListener(this);
        Intent intent = getIntent();
        //登陆后保存昵称和签名
        String login = intent.getStringExtra("login");
        if (login != null) {
            editor.putString("name", (String) BmobUser.getObjectByKey("name"));
            editor.putString("sign", (String) BmobUser.getObjectByKey("sign"));
            editor.putString("sex", (String) BmobUser.getObjectByKey("sex"));
            editor.apply();
        }

    }

    public void onResume() {
        super.onResume();
        syncLayout.setClickable(true);

        isChanged = preferences.getBoolean("isChanged", false);

        String headerPath = preferences.getString("headerPath", null);
        if (headerPath != null) {
           // Bitmap bitmap = getBitmap(headerPath);
           // pImage.setImageBitmap(bitmap);
            File file=new File(headerPath);
            Glide.with(getApplicationContext())
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(pImage);
        } else {
            pImage.setImageResource(R.drawable.ic_userimage);
        }
        String name = preferences.getString("name", (String) BmobUser.getObjectByKey("name"));
        String sign = preferences.getString("sign", (String) BmobUser.getObjectByKey("sign"));
        String email = (String) BmobUser.getObjectByKey("email");
        sex = preferences.getString("sex", (String) BmobUser.getObjectByKey("sex"));
        danmu = preferences.getBoolean("danmu",false);
        if(name!=null){
            pName.setText(name);
        }
        if(sign!=null){
            if (sign.length() > 14) {
                sign = sign.substring(0, 14) + "...";
            }
            pSign.setText(sign);
        }
        if(sex!=null){
            pSex.setText(sex);
        }
        if(danmu){
            pDanmu.setText(getString(R.string.summary_on));
        }else{
            pDanmu.setText(getString(R.string.summary_off));

        }
        if (email != null) {
            pEmail.setText(email);
        }

    }

    public void initVar() {

        isChanged = false;
        preferences = PreferenceManager.getDefaultSharedPreferences(PersonalActivity.this);
        editor =preferences.edit();
        backBu = (Button) findViewById(R.id.backBu_personal);
        exitBu = (Button) findViewById(R.id.exit_bu);
        imageLayout = (RelativeLayout) findViewById(R.id.image_layout);
        nameLayout = (RelativeLayout) findViewById(R.id.name_layout);
        signLayout = (RelativeLayout) findViewById(R.id.sign_layout);
        sexLayout = (RelativeLayout) findViewById(R.id.sex_layout);
        danmuLayout = (RelativeLayout) findViewById(R.id.danmu_layout);
        emailLayout = (RelativeLayout) findViewById(R.id.email_layout);
        syncLayout = (RelativeLayout) findViewById(R.id.sync_layout);
        pImage = (CircleImageView) findViewById(R.id.p_image);
        pName = (TextView) findViewById(R.id.p_name);
        pSign = (TextView) findViewById(R.id.p_sign);
        pEmail = (TextView) findViewById(R.id.p_email);
        pSex = (TextView) findViewById(R.id.p_sex);
        pDanmu = (TextView) findViewById(R.id.p_danmu);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.email_layout:
                intent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
                startActivity(intent);
                break;
            case R.id.sync_layout:
                MyUtil.showToast("同步中...");
                syncLayout.setClickable(false);
                syncSetting();
                break;
            case R.id.backBu_personal:
                onBackPressed();
                break;
            case R.id.exit_bu:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                editor.remove("name");
                editor.remove("sign");
                editor.remove("sex");
                editor.remove("danmu");
                editor.apply();
                BmobUser.logOut();
                finish();
                break;
            case R.id.image_layout:
                intent = new Intent(getApplicationContext(), ChoosePictureActivity.class);
                intent.putExtra("headerPath", "headerPath");
                startActivity(intent);
                break;
            case R.id.name_layout:
                intent = new Intent(PersonalActivity.this, EditActivity.class);
                intent.putExtra("isSign", false);
                startActivity(intent);
                break;
            case R.id.sign_layout:
                intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("isSign", true);
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
            case R.id.danmu_layout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(PersonalActivity.this)
                        .setItems(R.array.personal_danmu_list, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        pDanmu.setText(getString(R.string.summary_on));
                                        danmu=true;
                                        break;
                                    case 1:
                                        pDanmu.setText(getString(R.string.summary_off));
                                        danmu=false;
                                        break;
                                    default:
                                        pDanmu.setText(getString(R.string.summary_off));
                                        danmu=false;
                                        break;
                                }
                            }
                        });
                builder1.create().show();

                break;
        }
    }

    public void onBackPressed() {
        String content = pSex.getText().toString();
        if (!content.equals(sex)) {
            isChanged = true;
            editor.putString("sex", content);
            editor.apply();
        } else {
            isChanged = preferences.getBoolean("isChanged", false);
        }
        if (isChanged ) {
            _User user = new _User();
            user.setName(preferences.getString("name", getString(R.string.my_name)))
                    .setSign(preferences.getString("sign", getString(R.string.my_sign)))
                    .setSex(content);
            user.update(BmobUser.getCurrentUser(_User.class).getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        editor.putBoolean("isChanged", false);
                        editor.apply();
                        MyUtil.showToast("资料已同步");
                    } else {
                        editor.putBoolean("isChanged",true);
                        editor.apply();
                        MyUtil.showToast("资料同步失败");
                        Log.e(TAG, "done: " + e);
                    }
                }
            });
        }
        editor.putBoolean("danmu",danmu);
        editor.apply();
        super.onBackPressed();
        finish();
    }

//    //图片处理
//    public Bitmap getBitmap(String path) {
//
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//
//       // if (bitmap != null) {
//         //   int bheight = bitmap.getHeight();
//         //   int bwidth = bitmap.getWidth();
//         //   if (bheight > 4096 || bwidth > 4096) {
//
//        //        bheight = (int) (bitmap.getHeight() * 0.9);
//        //        bwidth = (int) (bitmap.getWidth() * 0.9);
//        //    }
//        //    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight);
//      //  } else {
//        if(bitmap==null)
//            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_userimage);
//       // }
//        return bitmap;
//
//    }
    public void syncSetting(){
           _User user=BmobUser.getCurrentUser(_User.class);
        if(user!=null){
            if(user.getEmailVerified()==null){
                user.setEmailVerified(true);
            }
            if(user.getEmailVerified()){
                final Setting setting=new Setting();
                setting.setNotify(preferences.getBoolean("Notify",false))
                        .setNotifyTime(preferences.getLong("notifyTime",0))
                        .setAutoUpdate(preferences.getBoolean("autoUpdate",false))
                        .setUpdateMode(preferences.getBoolean("updateMode",true))
                        .setUpdateFre(preferences.getString("updateFre","3"))
                        .setNightUpdate(preferences.getBoolean("nightUpdate",false))
                        .setDiy(preferences.getBoolean("diy",false))
                        .setAutoBing(preferences.getBoolean("autoBing",false))
                        .setAlpha(preferences.getString("alpha","200"))
                        .setSave(preferences.getBoolean("save",false))
                        .setUser(user);
                BmobQuery<Setting> query=new BmobQuery<>();
                query.addWhereEqualTo("user",user);
                query.findObjects(new FindListener<Setting>() {
                    @Override
                    public void done(List<Setting> list, BmobException e) {
                        if(e==null){
                            if(list.size()>0){
                                setting.update(list.get(0).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            MyUtil.showToast("设置上传完成");
                                        }else{
                                            MyUtil.showToast("设置上传失败:"+e.getMessage());


                                        }
                                    }
                                });
                            }else{
                                setting.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(e==null){
                                            MyUtil.showToast("设置上传完成");
                                        }else{
                                            MyUtil.showToast("设置上传失败:"+e.getMessage());

                                        }
                                    }
                                });

                            }
                        }else{
                            MyUtil.showToast("设置上传失败:"+e.getMessage());
                            Log.e(TAG, "done: "+e.getMessage() );
                            return;
                        }
                    }
                });
            }else{
                MyUtil.showToast("请先验证邮箱");
                BmobUser.requestEmailVerify(String.valueOf(email), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            MyUtil.showToast("请求验证邮件成功，请到" + email + "邮箱中进行激活。");
                        }else{
                            MyUtil.showToast("失败:" + e.getMessage());
                        }
                    }
                });
            }
        }else{
            MyUtil.showToast("出现错误,请重新登录");
        }
        syncLayout.setClickable(true);
    }
}

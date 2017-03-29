package com.example.npc.myweather2.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener{
    TextView vision;
    TextView update_log;
    TextView contactEmail;
    TextView contactQQ;
    TextView contactWeibo;
    TextView title_licenseTx;
    TextView licenseTx;
    Button backBu_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_about_us);
        init();

        //String log="1.新增城市切换功能\n2.优化启动速度\n3.新增定时提醒功能";
        String log="☞天气API-和风天气\n\n☞'生活建议'图标-https://icons8.com\n\n☞默认背景图片-http://www.coolapk.com\n" +
                "\n☞'关于我们'图标-Pure天气 by han95\n\n";
        update_log.setText(log);
        backBu_about.setOnClickListener(this);
        title_licenseTx.setOnClickListener(this);
        contactEmail.setOnClickListener(this);
        contactQQ.setOnClickListener(this);
        contactWeibo.setOnClickListener(this);
        licenseTx.setOnClickListener(this);
    }
    public void onClick(View view){
        Uri uri;
        Intent intent;
        switch (view.getId()){
            case R.id.licenseTx:
                intent=new Intent(AboutUsActivity.this,LicenseActivity.class);
                startActivity(intent);
                break;
            case R.id.title_licenseTx:
                intent=new Intent(AboutUsActivity.this,LicenseActivity.class);
                startActivity(intent);
                break;
            case R.id.contactEmail:
                uri=Uri.parse("mailto:dw12278@outlook.com");
                intent=new Intent(Intent.ACTION_SENDTO,uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    textCopy(contactEmail.getText().toString());
                }
                break;
            case R.id.contactQQ:
                uri=Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+contactQQ.getText().toString());
                intent=new Intent(Intent.ACTION_VIEW,uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    textCopy(contactQQ.getText().toString());
                }
                break;
            case R.id.contactWeibo:
                uri=Uri.parse("http://m.weibo.cn/u/5872633972");
                intent=new Intent(Intent.ACTION_VIEW,uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    textCopy(contactWeibo.getText().toString());
                }
                break;
            case R.id.backBu_about:
                finish();
                break;

        }
    }
    public void init(){
        backBu_about=(Button)findViewById(R.id.backBu_about);
        vision=(TextView)findViewById(R.id.vision);
        update_log=(TextView)findViewById(R.id.update_log);
        contactEmail=(TextView)findViewById(R.id.contactEmail);
        Resources rs=getResources();
        contactQQ=(TextView)findViewById(R.id.contactQQ);
        contactWeibo=(TextView)findViewById(R.id.contactWeibo);
        title_licenseTx=(TextView)findViewById(R.id.title_licenseTx);
        licenseTx=(TextView)findViewById(R.id.licenseTx);
    }
    public void textCopy(String text){
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null,text));
        MyUtil.showToast(AboutUsActivity.this,text+" 已复制到剪切板");
    }
}

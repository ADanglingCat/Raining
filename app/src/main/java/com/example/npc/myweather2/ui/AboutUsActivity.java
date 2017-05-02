package com.example.npc.myweather2.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView vision;
    private TextView update_log;
    private TextView contactEmail;
    private TextView contactQQ;
    private TextView contactWeibo;
    private TextView title_licenseTx;
    private TextView licenseTx;
    private Button backBu_about;
    private ImageView icon;
    private int count;
    private Resources rs;

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

        String log = "☞天气API-和风天气\n\n☞'生活建议'图标-https://icons8.com\n\n☞默认背景图片-http://www.coolapk.com" +
                "\n\n☞'关于我们'图标-http://iconfont.cn\n\n" + "☞高德地图定位服务\n\n";
        update_log.setText(log);
        backBu_about.setOnClickListener(this);
        title_licenseTx.setOnClickListener(this);
        contactEmail.setOnClickListener(this);
        contactQQ.setOnClickListener(this);
        contactWeibo.setOnClickListener(this);
        licenseTx.setOnClickListener(this);
        icon.setOnClickListener(this);
        count = 0;
    }

    public void onClick(View view) {
        Uri uri;
        Intent intent;
        Bitmap bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catcat);
        switch (view.getId()) {
            case R.id.licenseTx:
                intent = new Intent(AboutUsActivity.this, LicenseActivity.class);
                startActivity(intent);
                break;
            case R.id.title_licenseTx:
                intent = new Intent(AboutUsActivity.this, LicenseActivity.class);
                startActivity(intent);
                break;
            case R.id.contactEmail:
                uri = Uri.parse("mailto:dfy12278@gmail.com");
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    textCopy(contactEmail.getText().toString());
                }
                break;
            case R.id.contactQQ:
                uri = Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + contactQQ.getText().toString());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    textCopy(contactQQ.getText().toString());
                }
                break;
            case R.id.contactWeibo:
                uri = Uri.parse("http://m.weibo.cn/u/5872633972");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    textCopy(contactWeibo.getText().toString());
                }
                break;
            case R.id.backBu_about:
                finish();
                break;
            case R.id.icon_about:
                switch (count) {
                    case 0:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catangry);
                        count++;
                        break;
                    case 1:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catrun);
                        count++;
                        break;
                    case 2:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catback);
                        count++;
                        break;
                    case 3:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catsweat);
                        count++;
                        break;
                    case 4:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_catturn);
                        count++;
                        break;
                    case 5:
                        bitmap = BitmapFactory.decodeResource(rs, R.mipmap.ic_cathead);
                        count++;
                        break;

                }

                break;
        }
        if (count >= 6)
            count = 0;
        icon.setImageBitmap(bitmap);
    }

    public void init() {
        backBu_about = (Button) findViewById(R.id.backBu_about);
        vision = (TextView) findViewById(R.id.vision);
        update_log = (TextView) findViewById(R.id.update_log);
        contactEmail = (TextView) findViewById(R.id.contactEmail);
        rs = getResources();
        contactQQ = (TextView) findViewById(R.id.contactQQ);
        contactWeibo = (TextView) findViewById(R.id.contactWeibo);
        title_licenseTx = (TextView) findViewById(R.id.title_licenseTx);
        licenseTx = (TextView) findViewById(R.id.licenseTx);
        icon = (ImageView) findViewById(R.id.icon_about);
    }

    public void textCopy(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        MyUtil.showToast(AboutUsActivity.this, text + " 已复制到剪切板");
    }
}

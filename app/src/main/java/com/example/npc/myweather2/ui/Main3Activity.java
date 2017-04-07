package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.service.UpdateWeatherService;
import com.example.npc.myweather2.util.ActivityCollector;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;
import com.example.npc.myweather2.util.PagerAdapter;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Main3Activity extends BaseActivity {
    private PagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private List<CountyList> countyLists;
    private List<Fragment> fragmentList;
    private Button menuBu;
    private TextView titleCounty;
    private DrawerLayout drawer;
    private ImageView backIm;
    private NavigationView navigationView;
    private String imagePath;
    private String bingPic;
    private Calendar calendar;
    private int today;
    private int yesterday;
    private SharedPreferences preference;
    private ImageView userImage;
    private TextView userId;
    private TextView userName;
    private View headerView;
    private static final String TAG = "TAG";

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
        setContentView(R.layout.activity_main3);
        menuBu = (Button) findViewById(R.id.menuBu);
        titleCounty = (TextView) findViewById(R.id.titleCity);
        countyLists = DataSupport.findAll(CountyList.class);
        fragmentList = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.menuNa);
        backIm = (ImageView) findViewById(R.id.backgroundIm);
        preference = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DATE);
        yesterday = preference.getInt("date", 0);

        headerView = navigationView.getHeaderView(0);
        userId = (TextView) headerView.findViewById(R.id.user_id);
        userName = (TextView) headerView.findViewById(R.id.user_name);
        userImage = (ImageView) headerView.findViewById(R.id.user_image);
        menuBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main3Activity.this, ChoosePictureActivity.class);
                intent.putExtra("headerPath", "headerPath");
                startActivity(intent);
            }
        });

        for (CountyList countyList : countyLists) {
            Fragment fragment = new PagerFragment();
            Bundle arg = new Bundle();
            arg.putString("weatherId", countyList.getWeatherId());
            fragment.setArguments(arg);
            fragmentList.add(fragment);
        }
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(pagerAdapter);
        //mViewPager.setOffscreenPageLimit(0);
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleCounty.setText(countyLists.get(position).getCountyName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(listener);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.cityMe:
                        intent = new Intent(Main3Activity.this, AreaManagerActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settingMe:
                        intent = new Intent(Main3Activity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.updateMe:
                        MyUtil.showToast(Main3Activity.this, "没有新版本");
                        break;
                    case R.id.aboutMe:
                        intent = new Intent(Main3Activity.this, AboutUsActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                drawer.closeDrawers();
                return true;
            }
        });

    }

    public void onResume() {
        super.onResume();

        String headerPath = preference.getString("headerPath", null);
        int mainPosition = getMainPosition();
        titleCounty.setText(countyLists.get(mainPosition).getCountyName());
        if (countyLists.size() != fragmentList.size()) {
            fragmentList.clear();
            for (CountyList countyList : countyLists) {
                Fragment fragment1 = new PagerFragment();
                Bundle arg = new Bundle();
                arg.putString("weatherId", countyList.getWeatherId());
                fragment1.setArguments(arg);
                fragmentList.add(fragment1);
            }
            pagerAdapter.notifyDataSetChanged();
        }
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", mainPosition);
        intent.removeExtra("position");
        if (fragmentList.size() != 0) {
            mViewPager.setCurrentItem(position);
        }

        if (preference.getBoolean("diy", false)) {
            if (preference.getBoolean("autoBing", false)) {
                setBackgroundByBing();
            } else if (preference.getString("imagePath", null) != null) {
                imagePath = preference.getString("imagePath", null);
                // Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap bitmap = getBitmap(imagePath);
                backIm.setImageBitmap(bitmap);
            } else {
                backIm.setImageResource(R.drawable.ic_background);
            }
            backIm.setImageAlpha(Integer.parseInt(preference.getString("alpha", "255")));
        } else {
            backIm.setImageAlpha(255);
            backIm.setImageResource(R.drawable.ic_background);
        }

        if (headerPath != null) {
            Bitmap bitmap = getBitmap(headerPath);
            userImage.setImageBitmap(bitmap);
            getColor(bitmap);

        } else {
            userImage.setImageResource(R.drawable.ic_userimage);
            headerView.setBackgroundResource(R.color.colorImage);
           // navigationView.setItemIconTintList();
        }


        if (preference.getBoolean("autoUpdate", true)) {
            //启动自动更新
            intent = new Intent(this, UpdateWeatherService.class);
            startService(intent);
        }

    }

    public void onBackPressed() {
        ActivityCollector.removeAll();
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    //获取默认城市
    public int getMainPosition() {
        countyLists = DataSupport.findAll(CountyList.class);
        int position = -1;
        for (int i = 0; i < countyLists.size(); i++) {
            if (countyLists.get(i).isMainCity()) {
                position = i;
                break;
            }
        }
        if (position == -1) {
            countyLists.get(0).setMainCity(true);
            position = 0;
        }
        return position;
    }

    //必应每日一图设置背景
    public void setBackgroundByBing() {
        preference = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        bingPic = preference.getString("bingPic", null);
        if (today != yesterday || bingPic == null) {
            requestBing();
        } else {
            Glide.with(Main3Activity.this).load(bingPic).into(backIm);
        }
    }

    //获取必应每日一图
    public void requestBing() {
        MyUtil.sendRequest(getResources().getString(R.string.bingPicture), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToast(Main3Activity.this, "获取图片失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this).edit();
                editor.putString("bingPic", bingPic);
                editor.putInt("date", today);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(Main3Activity.this).load(bingPic).into(backIm);

                    }
                });
            }
        });
    }

    //图片处理
    public Bitmap getBitmap(String path) {

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        if (bitmap!=null) {
            int bheight = bitmap.getHeight();
            int bwidth = bitmap.getWidth();
            if (bheight > 4096 || bwidth > 4096) {

                bheight = (int) (bitmap.getHeight() * 0.9);
                bwidth = (int) (bitmap.getWidth() * 0.9);
            }
            //  Log.d(TAG, "getBitmap: hei"+bitmap.getHeight()+"wid"+bitmap.getWidth());
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight);
        } else{
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.ic_userimage);
        }
        return bitmap;

    }
//从图片中取色
    public void getColor(Bitmap bitmap) {
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                Palette.Swatch vibrant1 = palette.getDarkVibrantSwatch();
                if (vibrant1 != null) {
                    headerView.setBackgroundColor(vibrant1.getRgb());

                } else if (vibrant != null) {
                    headerView.setBackgroundColor(vibrant.getRgb());
                } else {
                    headerView.setBackgroundResource(R.color.colorHeaderBackground);
                }
            }
        });
    }
}
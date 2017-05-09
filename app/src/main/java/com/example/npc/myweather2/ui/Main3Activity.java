package com.example.npc.myweather2.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.Palette;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.model.DanMu;
import com.example.npc.myweather2.model.MyDanmaku;
import com.example.npc.myweather2.model._User;
import com.example.npc.myweather2.service.UpdateWeatherService;
import com.example.npc.myweather2.util.ActivityCollector;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;
import com.example.npc.myweather2.util.PagerAdapter;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.litepal.crud.DataSupport.findAll;

public class Main3Activity extends BaseActivity implements View.OnClickListener {
    private String name;
    private boolean flag;//弹幕开关
    private boolean sex;
    private SharedPreferences.Editor editor;
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
    private SharedPreferences preferences;
    private ImageView userImage;
    private TextView userSign;
    private TextView userName;
    private View headerView;
    private Button openBu;
    private Button sendBu;
    private EditText editText;
    private LinearLayout operationLayout;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private _User user;
    private List<MyDanmaku> danMuList;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    public static TextToSpeech tts;
    private static final String TAG = "TAGMain3Activity";

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
        initVar();
        initTTS();
        initDanmaku();

        menuBu.setOnClickListener(this);
        userImage.setOnClickListener(this);
        openBu.setOnClickListener(this);
        sendBu.setOnClickListener(this);

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
        ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleCounty.setText(countyLists.get(position).getCountyName());
                stopTTS();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(listener);
        mViewPager.setOffscreenPageLimit(2);

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
                        MyUtil.showToast( "没有新版本");
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

        if (preferences.getBoolean("autoUpdate", true)) {
            //启动自动更新
            Intent intent = new Intent(this, UpdateWeatherService.class);
            startService(intent);
        }

    }

    public void onResume() {
        super.onResume();
        user = BmobUser.getCurrentUser(_User.class);
        name = preferences.getString("name", getString(R.string.my_name));

        if (user == null) {
            user = new _User();
            user.setName(getString(R.string.my_name)).setSex("女");
        }
        flag = preferences.getBoolean("danmu", false);
        if ("男".equals(preferences.getString("sex", "女"))) {
            sex = true;
        } else {
            sex = false;
        }
        //弹幕
        if (flag) {

            openBu.setVisibility(View.VISIBLE);

            if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
                danmakuView.resume();
                danmakuView.show();
            }
        } else {
            openBu.setVisibility(View.GONE);
            danmakuView.hide();
            danmakuView.pause();

        }

        String headerPath = preferences.getString("headerPath", null);
        String sign = preferences.getString("sign", getString(R.string.my_sign));

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

        if (preferences.getBoolean("diy", false)) {
            if (preferences.getBoolean("autoBing", false)) {
                setBackgroundByBing();
            } else if (preferences.getString("imagePath", null) != null) {
                imagePath = preferences.getString("imagePath", null);
                // Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                // Bitmap bitmap = getBitmap(imagePath);
                File file = new File(imagePath);
                Glide.with(this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(backIm);
                //backIm.setImageBitmap(bitmap);
            } else {
                backIm.setImageResource(R.drawable.ic_background);
            }
            backIm.setImageAlpha(Integer.parseInt(preferences.getString("alpha", "255")));
        } else {
            backIm.setImageAlpha(255);
            backIm.setImageResource(R.drawable.ic_background);
        }

        if (headerPath != null) {
            File file = new File(headerPath);
            Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(userImage);
            Bitmap bitmap = getBitmap(headerPath);
            //userImage.setImageBitmap(bitmap);
//            if(preferences.getBoolean("isUIChanged",false)&&!preferences.getBoolean("save",false)){
//                _User user=new _User();
//                BmobFile bmobFile=new BmobFile(new File(headerPath));
//                user.setUserImage(bmobFile);
//                user.update(BmobUser.getCurrentUser(_us))
//            }
            getColor(bitmap);

        } else {
            userImage.setImageResource(R.drawable.ic_userimage);
            headerView.setBackgroundResource(R.color.colorImage);
        }
        if (sign != null) {
            userSign.setText(sign);
        }
        if (name != null) {
            userName.setText(name);
        }


    }

    /**
     * 向弹幕View中添加一条弹幕
     *
     * @param content    弹幕的具体内容
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder, boolean color) {
        BaseDanmaku danmaku = createDanmaku();
        if (danmaku != null) {
            if (withBorder) {
                danmaku.borderColor = Color.GREEN;
            }
            danmaku.text = content;
            if (color) {
                danmaku.textColor = Color.CYAN;
            } else {
                danmaku.textColor = Color.MAGENTA;
            }
            danmakuView.addDanmaku(danmaku);
            //DanMu danmu=new DanMu(content, user,true);
//            danmu.save(new SaveListener<String>() {
//                @Override
//                public void done(String s, BmobException e) {
//                    if(e!=null){
//                        Log.d(TAG, "done: "+e.getMessage());
//                    }
//                }
//            });

        }

    }

    //将下载的弹幕显示出来
    private void generateSomeDanmaku() {
        getDanMaku();
        danMuList = DataSupport.findAll(MyDanmaku.class);
        if (danMuList.size() > 0) {
            Log.d(TAG, "generateSomeDanmaku: " + danMuList.size());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (MyDanmaku danmaku : danMuList) {
                        addDanmaku(danmaku.getContent(), false, danmaku.isColor());
                        try {
                            Thread.sleep(new Random().nextInt(2 * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        }
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTTS();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.hide();
            danmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTTS();
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    public void onBackPressed() {
//        if(operationLayout.VISIBLE==View.GONE){
            ActivityCollector.removeAll();
//        }else{
//            operationLayout.setVisibility(View.GONE);
//        }
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    //获取默认城市
    public int getMainPosition() {
        countyLists = findAll(CountyList.class);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        bingPic = preferences.getString("bingPic", null);
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
                        MyUtil.showToast("获取图片失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                bingPic = response.body().string();
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

        // if (bitmap != null) {
        //int bheight = bitmap.getHeight();
        // int bwidth = bitmap.getWidth();
        // if (bheight > 4096 || bwidth > 4096) {

        //     bheight = (int) (bitmap.getHeight() * 0.9);
        //     bwidth = (int) (bitmap.getWidth() * 0.9);
        //  }
        // bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight);
        //  } else {
        if (bitmap == null)
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_userimage);
        //  }
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

    public void initTTS() {
        tts = new TextToSpeech(Main3Activity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.CHINA);
                }
            }
        });
    }

    public void stopTTS() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
    }

    public void destroyTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();

        }
    }

    public static TextToSpeech getTTS() {
        if (tts != null) {
            return tts;
        } else {
            return null;
        }
    }

    public void onClick(View view) {
        InputMethodManager manager = (InputMethodManager) Main3Activity.this.getSystemService(INPUT_METHOD_SERVICE);
        switch (view.getId()) {
            //弹幕评论按钮
            case R.id.open_button:
                if (operationLayout.getVisibility() == View.GONE) {
                    operationLayout.setVisibility(View.VISIBLE);
                    editText.requestFocus();
                    manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    operationLayout.setVisibility(View.GONE);
                    editText.clearFocus();
                    manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.send:
                operationLayout.setVisibility(View.GONE);
                if (editText.getText().toString() != null) {
                    if (!"".equals(editText.getText().toString())) {
                        String content = name + "(" + titleCounty.getText().toString() + ")" + ":" + editText.getText().toString();
                        addDanmaku(content, true, sex);
                        DanMu danmu = new DanMu(content, user, sex);
                        danmu.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e != null) {
                                    Log.d(TAG, "done: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
                editText.setText("");
                editText.clearFocus();
                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.user_image:
                Intent intent = new Intent("com.example.myweather.PERSONAL");
                startActivity(intent);
                drawer.closeDrawers();
                break;
            case R.id.menuBu:
                drawer.openDrawer(GravityCompat.START);
            default:
        }
    }

    public void initVar() {

        operationLayout = (LinearLayout) findViewById(R.id.operation_layout);
        openBu = (Button) findViewById(R.id.open_button);
        sendBu = (Button) findViewById(R.id.send);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(37)});

        danmakuView = (DanmakuView) findViewById(R.id.danmaku_view);
        danMuList = new ArrayList<>();

        menuBu = (Button) findViewById(R.id.menuBu);
        titleCounty = (TextView) findViewById(R.id.titleCity);
        countyLists = findAll(CountyList.class);
        fragmentList = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.menuNa);
        backIm = (ImageView) findViewById(R.id.backgroundIm);
        preferences = PreferenceManager.getDefaultSharedPreferences(Main3Activity.this);
        editor = preferences.edit();
        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DATE);
        yesterday = preferences.getInt("date", 0);

        headerView = navigationView.getHeaderView(0);
        userSign = (TextView) headerView.findViewById(R.id.user_sign);
        userName = (TextView) headerView.findViewById(R.id.user_name);
        userImage = (ImageView) headerView.findViewById(R.id.user_image);
    }

    //初始化弹幕设置
    public void initDanmaku() {
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3);
        danmakuContext = new DanmakuContext().create();
        danmakuContext.preventOverlapping(overlappingEnablePair)
                .setScrollSpeedFactor(1.1f)
                .setMaximumLines(maxLinesPair)
                .setDuplicateMergingEnabled(true);
        try {
            danmakuView.setDrawingCacheEnabled(true);
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    danmakuView.start();
                    generateSomeDanmaku();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {
                    danmakuView.seekTo(0L);
                }
            });
            danmakuView.prepare(parser, danmakuContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BaseDanmaku createDanmaku() {
        BaseDanmaku danmaku = null;
        try {
            danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
            danmaku.padding = 5;
            danmaku.textSize = sp2px(20);
            danmaku.setTime(danmakuView.getCurrentTime());

            return danmaku;
        } catch (NullPointerException e) {
            e.printStackTrace();

            initDanmaku();
        }
        return null;
    }

    public void getDanMaku(){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        Date date = calendar.getTime();
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        BmobQuery<DanMu> query = new BmobQuery<>();
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date))
                .order("createAt")
                .addQueryKeys("user,content,isMine,color");
        query.findObjects(new FindListener<DanMu>() {
            @Override
            public void done(List<DanMu> list, BmobException e) {
                if (e == null) {
                    DataSupport.deleteAll(MyDanmaku.class);
                    if (list.size() > 0) {
                        for (DanMu danMu : list) {
                            MyDanmaku myDanmaku = new MyDanmaku(danMu);
                            myDanmaku.save();
                        }
                    }
                } else {
                    Log.d(TAG, "done: " + e.getMessage());
                }

            }
        });
    }
}
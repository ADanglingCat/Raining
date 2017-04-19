package com.example.npc.myweather2.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.gson.DailyForecast;
import com.example.npc.myweather2.gson.HourlyForecast;
import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.util.MyUtil;

import junit.framework.Assert;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by npc on 3-29 0029.
 */

public class PagerFragment extends Fragment{
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView aqiText;
    private TextView flText;
    private TextView windText;
    private TextView astroText;

    private LinearLayout hourlylayout;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView travelText;
    private TextView uvText;
    private TextView fluText;
    private TextView airText;
    private TextView designText;
    private TextView title_comfortText;
    private TextView title_carWashText;
    private TextView title_sportText;
    private TextView title_travelText;
    private TextView title_uvText;
    private TextView title_fluText;
    private TextView title_airText;
    private TextView title_designText;
    private Resources resources;
    private Button voiceBu;
    public String weatherId;
    public TextToSpeech tts;
    public String voiceWeather;
    public View drawerView;
    public View rootView;
    private SharedPreferences prefs;
    private int today;
    private int yesterday;
    private String weatherString;
    private static final String TAG = "TAGPagerFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pager, container, false);

            voiceWeather = "";
            voiceBu = (Button) rootView.findViewById(R.id.VoiceBu);
            resources = getActivity().getResources();
            drawerView = inflater.inflate(R.layout.activity_main3, container, false);

            drawerLayout = (DrawerLayout) drawerView.findViewById(R.id.drawer_layout);
            weatherLayout = (ScrollView) rootView.findViewById(R.id.sv_weather_layout);
            titleUpdateTime = (TextView) rootView.findViewById(R.id.timeTx);
            degreeText = (TextView) rootView.findViewById(R.id.tmpTx);
            flText = (TextView) rootView.findViewById(R.id.flTx);
            windText = (TextView) rootView.findViewById(R.id.windTx);
            astroText = (TextView) rootView.findViewById(R.id.astroTx);
            hourlylayout = (LinearLayout) rootView.findViewById(R.id.hourly_layout);
            forecastLayout = (LinearLayout) rootView.findViewById(R.id.forecast_layout);

            weatherInfoText = (TextView) rootView.findViewById(R.id.condTx);
            aqiText = (TextView) rootView.findViewById(R.id.aqiTx);
            comfortText = (TextView) rootView.findViewById(R.id.comf_sug);
            carWashText = (TextView) rootView.findViewById(R.id.cw_sug);
            sportText = (TextView) rootView.findViewById(R.id.sport_sug);
            travelText = (TextView) rootView.findViewById(R.id.trav_sug);
            uvText = (TextView) rootView.findViewById(R.id.uv_sug);
            fluText = (TextView) rootView.findViewById(R.id.flu_sug);
            airText = (TextView) rootView.findViewById(R.id.air_sug);
            designText = (TextView) rootView.findViewById(R.id.drsg_sug);
            title_comfortText = (TextView) rootView.findViewById(R.id.suggestion_comf);
            title_carWashText = (TextView) rootView.findViewById(R.id.suggestion_cw);
            title_sportText = (TextView) rootView.findViewById(R.id.suggestion_sport);
            title_travelText = (TextView) rootView.findViewById(R.id.suggestion_travel);
            title_uvText = (TextView) rootView.findViewById(R.id.suggestion_uv);
            title_fluText = (TextView) rootView.findViewById(R.id.suggestion_flu);
            title_airText = (TextView) rootView.findViewById(R.id.suggestion_air);
            title_designText = (TextView) rootView.findViewById(R.id.suggestion_drsg);

            swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
            swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            weatherId = getArguments().getString("weatherId");
            weatherString = prefs.getString("weather" + weatherId, null);

        }
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DATE);
        //每天第一次打开自动刷新天气
        yesterday = prefs.getInt("date" + weatherId, 0);
        if (today != yesterday || weatherString == null) {
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        } else {
            // 有缓存时直接解析天气数据
            Weather weather = MyUtil.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
               MyUtil.getDanMaku();
            }
        });
//语音播报按钮点击事件
        voiceBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tts==null){
                    MyUtil.showToast(getContext(), "暂时无法播报天气");
                } else if (Build.VERSION.SDK_INT >= 21) {
                    if (tts.isSpeaking()) {
                        tts.stop();
                    } else {
                        tts.speak(voiceWeather, TextToSpeech.QUEUE_FLUSH, null, "speech");
                    }
                } else {
                    MyUtil.showToast(getContext(), "不支持的机型");
                }

            }
        });
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherAddress = resources.getString(R.string.weatherAddress);
        String weatherKey = resources.getString(R.string.weatherKey);
        String weatherUrl = weatherAddress + weatherId + "&" + weatherKey;
        MyUtil.sendRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = MyUtil.handleWeatherResponse(responseText);
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString("weather" + weatherId, responseText);
                                if (today != yesterday) {
                                    editor.putString("hourWeather" + weatherId, responseText);
                                    editor.putInt("date" + weatherId, today);
                                }
                                editor.apply();
                                showWeatherInfo(weather);
                            } else {
                                MyUtil.showToast(getContext(), "获取天气信息失败,请稍后再试");
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyUtil.showToast(getContext(), "获取天气信息失败,请稍后再试");
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        //loadBingPic();
    }

    //根据名字获取drawable资源
    public int getDrawable(Context context, String name) {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        voiceWeather = "";
        if (weather.basic.update != null) {
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            titleUpdateTime.setText(updateTime + "更新");
        }
        if (weather.basic.cityName != null) {
            String countyName = weather.basic.cityName;
            //titleCity.setText(cityName);

            voiceWeather += countyName + "..今日天气:";
        }
        if (weather.now != null) {
            if (weather.now.tmp != null) {
                String degree = weather.now.tmp + "℃";
                degreeText.setText(degree);
            }
            if (weather.now.cond != null) {
                String weatherInfo = weather.now.cond.txt;
                weatherInfoText.setText(weatherInfo);
                voiceWeather += weatherInfo + "。";
            } else {
                voiceWeather += weatherInfoText.getText();
            }
            if (weather.now.wind != null) {
                String windDir = weather.now.wind.dir;
                String windSc = weather.now.wind.sc;

                if (windSc.contains("-")) {
                    windText.setText("风向/风力:" + windDir + "/" + windSc + "级");
                    voiceWeather += windDir + "," + windSc + "级。最高温度：";
                } else {
                    windText.setText("风向/风力:" + windDir + "/" + windSc);
                    voiceWeather += windDir + "," + windSc + "。最高温度：";
                }

            }
            if (weather.now.fl != null) {
                String fl = weather.now.fl;
                flText.setText("体感温度:" + fl + "℃");
            }
        }
        forecastLayout.removeAllViews();
        hourlylayout.removeAllViews();
        boolean flag = true;
        //未来几小时天气
        String hourWeatherText = prefs.getString("hourWeather" + weatherId, null);
        List<HourlyForecast> hourlyForecasts = null;
        if (hourWeatherText != null) {
            Weather hourWeather = MyUtil.handleWeatherResponse(hourWeatherText);
            if (hourWeather != null && "ok".equals(hourWeather.status)) {
                hourlyForecasts = hourWeather.hourlyForecasts;
            }

        } else if (weather.hourlyForecasts != null) {
            hourlyForecasts = weather.hourlyForecasts;
        }
        if (hourlyForecasts != null) {
            for (HourlyForecast forecast : hourlyForecasts) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.hourly_item, hourlylayout, false);
                TextView tmpText = (TextView) view.findViewById(R.id.hour_tmpTx);
                ImageView image = (ImageView) view.findViewById(R.id.hour_imageTx);
                TextView hourText = (TextView) view.findViewById(R.id.hour_time);
                String code = "ic_" + forecast.cond.code;
                int id = getDrawable(getContext(), code);
                image.setImageResource(id);
                tmpText.setText(forecast.tmp + "℃");
                hourText.setText(forecast.date.substring(11));
                hourlylayout.addView(view);
            }
        }

        //未来几天天气
        if (weather.dailyForecasts != null) {
            int i = 0;
            for (DailyForecast forecast : weather.dailyForecasts) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.daily_item, forecastLayout, false);
                i++;
                if (forecast.date != null) {
                    TextView dateText = (TextView) view.findViewById(R.id.daily_dateTx);
                    switch (i) {
                        case 1:
                            dateText.setText("今天");
                            break;
                        case 2:
                            dateText.setText("明天");
                            break;
                        case 4:
                            dateText.setText("昨天");
                            break;
                        default:
                            dateText.setText(forecast.date.substring(5));
                            break;
                    }
                }
                if (forecast.cond != null) {
                    TextView condText = (TextView) view.findViewById(R.id.daily_condTx);
                    condText.setText(forecast.cond.txt_d);
                }
                if (forecast.pop != null) {
                    TextView popText = (TextView) view.findViewById(R.id.daily_popTx);
                    popText.setText(forecast.pop + "%");
                }
                TextView maxText = (TextView) view.findViewById(R.id.daily_maxTx);
                if (forecast.tmp.max != null) {

                    maxText.setText(forecast.tmp.max + "℃");
                }
                TextView minText = (TextView) view.findViewById(R.id.daily_minTx);
                if (forecast.tmp.min != null) {

                    minText.setText(forecast.tmp.min + "℃");
                }
                forecastLayout.addView(view);
                //只获取第一天的日出日落时间
                if (forecast.astro != null) {
                    if (flag) {
                        voiceWeather += maxText.getText() + "，最低温度：" + minText.getText();
                        String sr = null;//日出
                        if (forecast.astro.sr != null) {
                            sr = forecast.astro.sr;
                        }
                        String ss = null;//日落
                        if (forecast.astro.ss != null) {
                            ss = forecast.astro.ss;
                        }
                        astroText.setText("日出/日落:" + sr + "/" + ss);
                        flag = false;
                    }
                }
            }
        }
        if (weather.aqi != null) {
            aqiText.setText("空气质量:" + weather.aqi.city.aqi + "/" + weather.aqi.city.qlty);
        }
        if (weather.suggestion != null) {
            if (weather.suggestion.comf != null) {
                String comfort = weather.suggestion.comf.txt;
                comfortText.setText(comfort);
                String title_comfort = "舒适度指数:" + weather.suggestion.comf.brf;
                title_comfortText.setText(title_comfort);
                voiceWeather += "。" + weather.suggestion.comf.txt + "。";
            }
            if (weather.suggestion.cw != null) {
                String carWash = weather.suggestion.cw.txt;
                String title_carWash = "洗车指数:" + weather.suggestion.cw.brf;
                title_carWashText.setText(title_carWash);
                carWashText.setText(carWash);
            }
            if (weather.suggestion.sport != null) {
                String sport = weather.suggestion.sport.txt;
                sportText.setText(sport);
                String title_sport = "运动指数:" + weather.suggestion.sport.brf;
                title_sportText.setText(title_sport);
            }
            if (weather.suggestion.drsg != null) {
                String drsg = weather.suggestion.drsg.txt;
                designText.setText(drsg);
                String title_drsg = "穿衣指数:" + weather.suggestion.drsg.brf;
                title_designText.setText(title_drsg);
                //voiceWeather+=drsg+"。";
            }
            if (weather.suggestion.uv != null) {
                String uv = weather.suggestion.uv.txt;
                uvText.setText(uv);
                String title_uv = "紫外线指数:" + weather.suggestion.uv.brf;
                title_uvText.setText(title_uv);
            }
            if (weather.suggestion.air != null) {
                String air = weather.suggestion.air.txt;
                airText.setText(air);
                String title_air = "污染指数:" + weather.suggestion.air.brf;
                title_airText.setText(title_air);
                //voiceWeather+=air+"。";
            }
            if (weather.suggestion.trav != null) {
                String trav = weather.suggestion.trav.txt;
                travelText.setText(trav);
                String title_trav = "旅行指数:" + weather.suggestion.trav.brf;
                title_travelText.setText(title_trav);
            }
            if (weather.suggestion.flu != null) {
                String flu = weather.suggestion.flu.txt;
                fluText.setText(flu);
                String title_flu = "感冒指数:" + weather.suggestion.flu.brf;
                title_fluText.setText(title_flu);
            }
            voiceWeather += "祝您一切顺利.";
            weatherLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        tts = Main3Activity.getTTS();
        super.onResume();
    }


    @Override
    public void onDestroyView() {

        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}

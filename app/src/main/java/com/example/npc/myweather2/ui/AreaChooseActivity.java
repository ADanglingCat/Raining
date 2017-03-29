package com.example.npc.myweather2.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.City;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.model.Province;
import com.example.npc.myweather2.util.BaseActivity;
import com.example.npc.myweather2.util.MyUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaChooseActivity extends BaseActivity {

    public static final int LEVEL_PROVINCE = 0;//省级
    public static final int LEVEL_CITY = 1;    //市级
    public static final int LEVEL_COUNTY = 2;  //县级
    public static final int LEVEL_SEARCH = 3;  //搜索
    public static final int LEVEL_LOCATION = 4;  //定位
    private SearchView searchView;
    private TextView titleText;
    private Button backButton;
    private ListView areaList;
    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表
    private Province selectedProvince;//选中的省
    private City selectedCity;//选中的市
    private County selectedCounty;//选中的县
    private int selectedLevel;//当前地区级别
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();//数据库表中查询到的数据
    private ProgressDialog progressDialog;
    private List<SearchCounty> searchCountyList;
    private ListView searchResult;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> searchDataList;
    private TextView locaCountyTx;
    private LinearLayout locaLayout;
    private AMapLocationClient client;
    private String locCounty;
    private String locprovince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_choose);
        initVar();
        List<String> needPermissions = new ArrayList<>(Arrays.asList(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE));
        List<String> requestPermissions = new ArrayList<>();
        for (int i = 0; i < needPermissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(AreaChooseActivity.this, needPermissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions.add(needPermissions.get(i));
            }
        }
        String[] list = requestPermissions.toArray(new String[requestPermissions.size()]);
        if (list.length < 1) {
            client.startLocation();
        }
        ActivityCompat.requestPermissions(AreaChooseActivity.this, list, 1);

        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                locCounty = aMapLocation.getCity().substring(0, aMapLocation.getCity().length() - 1);
                locprovince = aMapLocation.getProvince().substring(0, aMapLocation.getProvince().length() - 1);
                locaCountyTx.setText(locCounty + "  " + locprovince);

            }

        });

        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (selectedLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (selectedLevel == LEVEL_COUNTY) {
                    selectedCounty = countyList.get(position);
                    saveCounty(selectedCounty.getWeatherId(), selectedCounty.getCountyName());
                    Intent intent;

                    intent = new Intent(AreaChooseActivity.this, AreaManagerActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchCounty s = searchCountyList.get(position);
                saveCounty(s.basic.weatherId, s.basic.countyName);
                Intent intent;
                intent = new Intent(AreaChooseActivity.this, AreaManagerActivity.class);
                startActivity(intent);
                finish();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryCountyByName(query, LEVEL_SEARCH);
                //locaLayout.setVisibility(View.GONE);
                queryProvinces();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

//                InputMethodManager inManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inManager.hideSoftInputFromWindow(searchView.getWindowToken(),0);
                searchResult.setVisibility(View.GONE);
                return false;
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (selectedLevel == LEVEL_CITY || selectedLevel == LEVEL_SEARCH) {
                    queryProvinces();
                }
            }
        });

        locaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != locCounty && null != locprovince) {
                    queryCountyByName(locCounty, LEVEL_LOCATION);
//                    if (!progressDialog.isShowing()) {
//                        for (SearchCounty s : searchCountyList) {
//                            if (s.basic.provinceName.equals(locprovince)) {
//                                saveCounty(s.basic.weatherId, s.basic.countyName);
//                                Intent intent;
//                                intent = new Intent(AreaChooseActivity.this, AreaManagerActivity.class);
//                                startActivity(intent);
//                                closeProgressDialog();
//                                finish();
//                            }
//                        }
//                    }
                }
            }
        });
        queryProvinces();

    }

    //储存选中城市
    public void saveCounty(String weatherId, String countyName) {
        List<CountyList> countyLists = DataSupport.where("weatherId=?", weatherId).find(CountyList.class);
        if (countyLists.size() <= 0) {
            CountyList countyList = new CountyList(weatherId, countyName);
            countyList.save();
        }
    }

    //查询省
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel = LEVEL_PROVINCE;
        } else {
            //本地查询失败,去服务器查询
            String address = getResources().getString(R.string.areaAddress);
            queryFromServer(address, LEVEL_PROVINCE);
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel = LEVEL_CITY;
        } else {
            int selectedProvinceCode = selectedProvince.getProvinceCode();
            String address = getResources().getString(R.string.areaAddress) + selectedProvinceCode;
            queryFromServer(address, LEVEL_CITY);
        }
    }

    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel = LEVEL_COUNTY;
        } else {
            int selectedProvinceCode = selectedProvince.getProvinceCode();
            int selectedCityCode = selectedCity.getCityCode();
            String address = getResources().getString(R.string.areaAddress) + selectedProvinceCode + "/" + selectedCityCode;
            queryFromServer(address, LEVEL_COUNTY);
        }
    }

    //通过城市名搜索城市
    private void queryCountyByName(String countyName, int level) {
        String address = getResources().getString(R.string.countyAddress) + countyName + "&" + getResources().getString(R.string.weatherKey);
        queryFromServer(address, level);
    }

    //显示查询结果
    private void showSearchResult() {
        searchDataList.clear();
        if (searchCountyList.size() > 0 && searchCountyList.get(0).basic != null) {
            for (SearchCounty s : searchCountyList) {
                searchDataList.add(s.basic.countyName + "  " + s.basic.provinceName);
            }
        } else {

            MyUtil.showToast(AreaChooseActivity.this, "地区不存在,请重新输入");
            searchResult.setVisibility(View.GONE);
            //searchView搜索框数据清空
        }
        arrayAdapter.notifyDataSetChanged();
        searchResult.setVisibility(View.VISIBLE);
    }

    private boolean handleCounty(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String BasicContent = jsonArray.toString();
            searchCountyList.clear();
            searchCountyList = new Gson().fromJson(BasicContent, new TypeToken<List<SearchCounty>>() {
            }.getType());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //从服务器查询地区数据
    private void queryFromServer(String address, final int type) {
        showProgressDialog();
        MyUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(AreaChooseActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if (type == LEVEL_PROVINCE) {
                    result = MyUtil.handleProvinceResponse(responseText);
                } else if (type == LEVEL_CITY) {
                    result = MyUtil.handleCityResponse(responseText, selectedProvince.getId());
                } else if (type == LEVEL_COUNTY) {
                    result = MyUtil.handleCountyResponse(responseText, selectedCity.getId());
                } else {
                    result = handleCounty(responseText);
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (type == LEVEL_PROVINCE) {
                                queryProvinces();
                                closeProgressDialog();
                            } else if (type == LEVEL_CITY) {
                                queryCities();
                                closeProgressDialog();
                            } else if (type == LEVEL_COUNTY) {
                                queryCounties();
                                closeProgressDialog();
                            } else if (type == LEVEL_SEARCH) {
                                showSearchResult();
                                closeProgressDialog();
                            }else
                            //closeProgressDialog();
                            if (type==LEVEL_LOCATION){
                                //if(null!=locCounty&&null!=locprovince){
                                    //queryCountyByName(locCounty,LEVEL_LOCATION);
                                    for(SearchCounty s:searchCountyList){
                                        if(s.basic.provinceName.equals(locprovince)){
                                            saveCounty(s.basic.weatherId, s.basic.countyName);
                                            Intent intent;
                                            intent = new Intent(AreaChooseActivity.this, AreaManagerActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                                closeProgressDialog();
                            }
                        //}
                    });
                } else {
                    closeProgressDialog();
                }
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(AreaChooseActivity.this);
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onResume() {
        super.onResume();

    }

    public class SearchCounty {
        public Basic basic;

        public class Basic {
            @SerializedName("city")
            public String countyName;
            @SerializedName("prov")
            public String provinceName;
            @SerializedName("id")
            public String weatherId;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissons, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "请授予权限", Toast.LENGTH_SHORT).show();
                            //finish();
                            return;
                        }
                    }
                    client.startLocation();
                }
                break;
            default:
        }
    }

    public void initVar() {
        client = new AMapLocationClient(getApplicationContext());
        titleText = (TextView) findViewById(R.id.titleTx);
        backButton = (Button) findViewById(R.id.backBu);
        areaList = (ListView) findViewById(R.id.areaList);
        searchResult = (ListView) findViewById(R.id.search_result_listView);
        adapter = new ArrayAdapter<>(AreaChooseActivity.this, android.R.layout.simple_list_item_1, dataList);
        areaList.setAdapter(adapter);
        searchCountyList = new ArrayList<>();
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("输入城市名");
        searchDataList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(AreaChooseActivity.this, android.R.layout.simple_list_item_1, searchDataList);
        searchResult.setAdapter(arrayAdapter);
        locaCountyTx = (TextView) findViewById(R.id.locaCountyTx);
        locaLayout = (LinearLayout) findViewById(R.id.loca_layout);
    }

    public void onDestroy() {
        super.onDestroy();
        if (null != client) {
            client.onDestroy();
            client = null;
        }
    }

    public void onBackPressed() {
        if (selectedLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (selectedLevel == LEVEL_CITY) {
            queryProvinces();
        } else if (selectedLevel == LEVEL_PROVINCE) {
            Intent intent = new Intent(AreaChooseActivity.this, AreaManagerActivity.class);
            startActivity(intent);
            finish();
        }

    }
}

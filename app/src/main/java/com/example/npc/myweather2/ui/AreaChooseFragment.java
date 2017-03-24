package com.example.npc.myweather2.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.City;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.model.Province;
import com.example.npc.myweather2.util.MyUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by npc on 3-13 0013.
 */

public class AreaChooseFragment extends Fragment {
    public static final int LEVEL_PROVINCE=0;//省级
    public static final int LEVEL_CITY=1;    //市级
    public static final int LEVEL_COUNTY=2;  //县级
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
    private List<String> dataList=new ArrayList<>();//数据库表中查询到的数据
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_area_choose,container,false);
        titleText=(TextView) view.findViewById(R.id.titleTx);
        backButton=(Button)view.findViewById(R.id.backBu);
        areaList=(ListView)view.findViewById(R.id.areaList);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        areaList.setAdapter(adapter);
        searchView=(SearchView)view.findViewById(R.id.searchView);
       // searchView.setSubmitButtonEnabled(true);
        //searchView.setIconifiedByDefault(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectedLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(selectedLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(selectedLevel==LEVEL_COUNTY){
                    selectedCounty=countyList.get(position);
                    saveCounty();
                    Intent intent;
                    if(getActivity()instanceof MainActivity){
                        intent=new Intent(getContext(),Main2Activity.class);
                        intent.putExtra("weatherId",selectedCounty.getWeatherId());
                    }else{
                        intent=new Intent(getContext(),AreaManagerActivity.class);
                    }
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                InputMethodManager inManager=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inManager.hideSoftInputFromWindow(searchView.getWindowToken(),0);
//                selectedProvince=new Province();
//                selectedProvince.setProvinceName(query);
//                queryCities();
//               // if(!flag){
//                    selectedCity=new City();
//                    selectedCity.setCityName(query);
//                    //queryCounties();
//                //};
//                Intent intent;
//                if(getActivity()instanceof MainActivity){
//                    intent=new Intent(getContext(),Main2Activity.class);
//                }else{
//                    intent=new Intent(getContext(),AreaManagerActivity.class);
//                }
//                startActivity(intent);
//                getActivity().finish();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(selectedLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    //储存选中城市
    public void saveCounty(){
        List<CountyList>countyLists=DataSupport.where("countyId=?",selectedCounty.getId()+"").find(CountyList.class);
        if(countyLists.size()<=0){
            CountyList countyList=new CountyList(selectedCounty.getId());
            countyList.save();
        }
    }
    //查询省
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel=LEVEL_PROVINCE;
        }else{
            //本地查询失败,去服务器查询
            String address="http://guolin.tech/api/china";
            queryFromServer(address,0);
        }
    }
    //查询市
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceId=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel=LEVEL_CITY;
        }else{
            int selectedProvinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+selectedProvinceCode;
            queryFromServer(address,LEVEL_CITY);
        }
    }
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityId=?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            areaList.setSelection(0);
            selectedLevel=LEVEL_COUNTY;
        }else{
            int selectedProvinceCode=selectedProvince.getProvinceCode();
            int selectedCityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+selectedProvinceCode+"/"+selectedCityCode;
            queryFromServer(address,LEVEL_COUNTY);
        }
    }
    //从服务器查询地区数据
    private void queryFromServer(String address, final int type){
        showProgressDialog();
        MyUtil.sendRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "查询失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if (type==LEVEL_PROVINCE){
                    result= MyUtil.handleProvinceResponse(responseText);
                }else if(type==LEVEL_CITY){
                    result=MyUtil.handleCityResponse(responseText,selectedProvince.getId());
                }else{
                    result=MyUtil.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(type==LEVEL_PROVINCE){
                                queryProvinces();
                            }else if(type==LEVEL_CITY){
                                queryCities();
                            }else if(type==LEVEL_COUNTY){
                                queryCounties();
                            }
                            closeProgressDialog();
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    public void onResume(){
        super.onResume();
        getView().setFocusableInTouchMode(true);
        //得到Fragment的根布局并且使其获得焦点
        getView().requestFocus();
        //对该根布局View注册KeyListener的监听
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if(selectedLevel==LEVEL_COUNTY){
                        queryCities();
                        return true;
                    }else if(selectedLevel==LEVEL_CITY){
                        queryProvinces();
                        return true;
                    }else if(selectedLevel==LEVEL_PROVINCE){
                        Intent intent=new Intent(getContext(),AreaManagerActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        return true;
                    }
                }
                return false;
            }
        });
    }

}

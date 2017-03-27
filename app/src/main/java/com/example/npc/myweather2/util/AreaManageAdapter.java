package com.example.npc.myweather2.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.model.CountyList;

import java.util.List;

/**
 * Created by npc on 3-23 0023.
 */

public class AreaManageAdapter extends ArrayAdapter<CountyList> {
    private int resourceId;
    public AreaManageAdapter(Context context, int resourceId, List<CountyList> countyLists){
        super(context,resourceId,countyLists);
        this.resourceId=resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CountyList countyList=getItem(position);

        View view;
        ViewHolder viewHolder;
        if(convertView!=null){
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();

        }else{
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.cityFirstName=(TextView)view.findViewById(R.id.cityFirstName_manager);
            viewHolder.cityName=(TextView)view.findViewById(R.id.cityName_manager);
            view.setTag(viewHolder);
        }
        viewHolder.cityFirstName.setText(countyList.getCountyName().substring(0,1));
        viewHolder.cityName.setText(countyList.getCountyName());
        return view;
    }
    class ViewHolder{
        TextView cityFirstName;
        TextView cityName;
    }
}

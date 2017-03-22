package com.example.npc.myweather2.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by npc on 3-22 0022.
 */

public class ActivityCollector {
    public static List<Activity>activityList=new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void removeAll(){
        for(Activity activity:activityList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}

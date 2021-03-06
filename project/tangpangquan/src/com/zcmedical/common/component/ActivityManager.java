package com.zcmedical.common.component;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ActivityManager {

    private static List<Activity> activityList = new LinkedList<Activity>();
    private static ActivityManager instance;

    private ActivityManager() {
    }

    // 单例模式中获取唯一的MyApplication实例  
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    // 添加Activity到容器中  
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void clearAllActivity() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    // 遍历所有Activity并finish  
    public void exit() {
        clearAllActivity();
        System.exit(0);
    }
}

package com.cnki.cqmuseum.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.cnki.cqmuseum.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * activity管理栈
 * Created by liweidong on 2019/3/22.
 */

public class ActivityViewManager {
    private static ActivityViewManager instance;
    private volatile ArrayList<Activity> mActivityLists;

    private ActivityViewManager(){
        mActivityLists = new ArrayList<>();
    }

    public static ActivityViewManager getInstance(){
        synchronized (ActivityViewManager.class){
            if (instance == null){
                instance = new ActivityViewManager();
            }
        }
        return instance;
    }

    /**
     * 获取当前栈顶activity名字
     * @return
     */
    public String getTopActivity(Context context){
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f1 = runningTaskInfos.get(0).topActivity;
            return f1.getClassName();
        }
        return "";
    }

    /**
     * 添加activity进栈
     * @param activity
     */
    public void addActivity(Activity activity){
        if (activity != null && !mActivityLists.contains(activity)){
            mActivityLists.add(activity);
        }
    }

    /**
     * 移除activity出栈
     * @param activity
     */
    public void removeActivity(Activity activity){
        if (activity != null && mActivityLists.contains(activity)){
            mActivityLists.remove(activity);
        }
    }

    /**
     * 结束所有显示的界面
     */
    public void finishAllOpenActivity(){
        if (mActivityLists != null && mActivityLists.size() != 0){
            for (Activity activity : mActivityLists){
                activity.finish();
            }
        }
    }

    /**
     * 返回上一页
     * @param context
     */
    public void goLastActivity(Context context){
        if (mActivityLists != null && mActivityLists.size() != 0){
            String topActivityName = getTopActivity(context);
            Activity topActivity = mActivityLists.get(mActivityLists.size() - 1);
            //如果顶部界面不为空，并且顶部界面不为主界面
            if (!TextUtils.isEmpty(topActivityName) && !topActivityName.equals("com.cnki.cqmuseum.ui.home.HomeActivity")
                    && topActivity.getComponentName().getClassName().equals(topActivityName)){
                topActivity.finish();
            }
        }
    }

    /**
     * 结束出主页面显示的界面
     */
    public void goHome(){
        if (mActivityLists != null && mActivityLists.size() != 0){
            ArrayList<Activity> tempActivities = new ArrayList<>();
            BaseActivity.isRemoveAway = false;
            for (Activity activity : mActivityLists){
                if (!activity.getComponentName().getClassName().equals("com.cnki.cqmuseum.ui.home.HomeActivity")){
                    tempActivities.add(activity);
                    activity.finish();
                }
            }
            if (tempActivities.size() != 0){
                mActivityLists.removeAll(tempActivities);
            }
            BaseActivity.isRemoveAway = true;
        }
    }

}

package com.fotile.c2i.activity.music.util;

import android.app.Activity;

import java.util.Iterator;
import java.util.Stack;

/**
 * 文件名称：AppManagerUtil
 * 创建时间：2017/10/15 16:15
 * 文件作者：huanghuang
 * 功能描述：Activity堆栈管理类
 */
public class AppManagerUtil {

    private static Stack<Activity> activityStack;
    private static AppManagerUtil instance;

    /**
     * constructor
     */
    private AppManagerUtil() {

    }

    /**
     * get the AppManager instance, the AppManager is singleton.
     */
    public static AppManagerUtil getInstance() {
        if (instance == null) {
            instance = new AppManagerUtil();
        }
        return instance;
    }

    /**
     * add Activity to Stack
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get Activity size
     */
    public int getActivitySize() {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        return activityStack.size();
    }


    /**
     * remove Activity from Stack
     */
    public void removeActivity(Activity activity) {
        if (activityStack == null || activityStack.size() == 0) {
            return;
        }
        activityStack.remove(activity);
    }

    /**
     * get current activity from Stack
     */
    public synchronized Activity currentActivity() {
        Activity activity = null;
        if (null != activityStack && activityStack.size() != 0) {
            activity = activityStack.lastElement();
        }
        return activity;
    }


    /**
     * 当前class_name是否是当前的act
     *
     * @return
     */
    public boolean activtiyInStack(Class class_name) {
        if (activityStack != null) {
            Iterator<Activity> iterator = activityStack.iterator();
            while (iterator.hasNext()) {
                Activity activity = iterator.next();
                String name = activity.getClass().getSimpleName();
                if (name.equals(class_name.getSimpleName())) {
                    return true;
                }
            }
        }
        return false;
    }
}

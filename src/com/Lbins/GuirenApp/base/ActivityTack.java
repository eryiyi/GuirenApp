package com.Lbins.GuirenApp.base;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuzwei on 2014/11/27.
 * 栈管理
 */
public class ActivityTack {
    public List<Activity> activityList = new ArrayList<Activity>();

    public static ActivityTack tack = new ActivityTack();

    public static ActivityTack getInstanse() {
        return tack;
    }

    private ActivityTack() {

    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 完全退出
     *
     * @param context
     */
    public void exit(Context context) {
        while (activityList.size() > 0) {
            Activity activity = activityList.remove(activityList.size() - 1);
            activity.finish();
        }
        System.exit(0);
    }

    /**
     * 弹出activity
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        removeActivity(activity);
        activity.finish();
    }
    /**
     * 弹出activity到
     *
     * @param cs
     */
    @SuppressWarnings("rawtypes")
    public void popUntilActivity(Class... cs) {
        List<Activity> list = new ArrayList<Activity>();
        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity ac = activityList.get(i);
            boolean isTop = false;
            for (int j = 0; j < cs.length; j++) {
                if (ac.getClass().equals(cs[j])) {
                    isTop = true;
                    break;
                }
            }
            if (!isTop) {
                list.add(ac);
            } else break;
        }
        for (Iterator<Activity> iterator = list.iterator(); iterator.hasNext(); ) {
            Activity activity = iterator.next();
            popActivity(activity);
        }
    }
}

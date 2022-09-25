package com.rocasoftware.myworkout;

import com.google.android.material.color.DynamicColors;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}

package com.maxvidzgallery.storymaker.help;

import androidx.multidex.MultiDexApplication;


public class VideoApplication extends MultiDexApplication {

    private static VideoApplication instance;



    public static synchronized VideoApplication getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

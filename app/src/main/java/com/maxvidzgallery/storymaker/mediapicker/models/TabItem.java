package com.maxvidzgallery.storymaker.mediapicker.models;

import com.flyco.tablayout.listener.CustomTabEntity;

public class TabItem implements CustomTabEntity {

    public int selectedIcon;
    public String title;
    public int unSelectedIcon;

    public TabItem(String str) {
        this.title = str;
    }

    public String getTabTitle() {
        return this.title;
    }

    public int getTabSelectedIcon() {
        return this.selectedIcon;
    }

    public int getTabUnselectedIcon() {
        return this.unSelectedIcon;
    }
}

package com.maxvidzgallery.storymaker.utils;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnOneOffClickListener implements OnClickListener {

    private boolean clickable = true;

    public abstract void onOneClick(View view);

    public final void onClick(View view) {
        if (this.clickable) {
            this.clickable = false;
            onOneClick(view);
        }
    }

    public void reset() {
        this.clickable = true;
    }
}

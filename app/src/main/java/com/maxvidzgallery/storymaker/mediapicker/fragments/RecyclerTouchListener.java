package com.maxvidzgallery.storymaker.mediapicker.fragments;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.maxvidzgallery.storymaker.mediapicker.utils.ClickListener;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private final ClickListener clickListener;
    private final GestureDetector gestureDetector;

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    }

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener2) {
        this.clickListener = clickListener2;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            public void onLongPress(MotionEvent motionEvent) {
                View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (findChildViewUnder != null) {
                    ClickListener clickListener = clickListener2;
                    if (clickListener != null) {
                        clickListener.onLongClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
                    }
                }
            }
        });
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (!(findChildViewUnder == null || this.clickListener == null || !this.gestureDetector.onTouchEvent(motionEvent))) {
            this.clickListener.onClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
        }
        return false;
    }
}

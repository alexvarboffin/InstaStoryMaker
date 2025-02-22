package com.maxvidzgallery.storymaker.mediapicker.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.mediapicker.Gallery;
import com.maxvidzgallery.storymaker.mediapicker.adapters.MediaAdapter;
import com.maxvidzgallery.storymaker.mediapicker.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GalleryDetailFrag extends Fragment {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";

    ConnectionDetector connectionDetector;


    Activity activity;

    public static List<Boolean> selected = new ArrayList();
    public String from;
    private MediaAdapter mAdapter;

    private final List<String> mediaList = new ArrayList();
    private View rootView;
    private RecyclerView rvDetail;
    private TextView tbTitle;
    public String title;

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private final ClickListener clickListener;
        private final GestureDetector gestureDetector;

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener2) {
            this.clickListener = clickListener2;
            this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
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

    public static GalleryDetailFrag getInstance(String str, String str2) {
        GalleryDetailFrag galleryDetailFrag = new GalleryDetailFrag();
        galleryDetailFrag.title = str;
        galleryDetailFrag.from = str2;
        return galleryDetailFrag;
    }

    private void init() {
        this.tbTitle = (TextView) this.rootView.findViewById(R.id.tb_title);
        this.tbTitle.setText(this.title);
        this.rootView.findViewById(R.id.tb_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                GalleryDetailFrag.this.getActivity().onBackPressed();
            }
        });
        this.mediaList.clear();
        selected.clear();
        if (this.from == null) {
            getActivity().onBackPressed();
        }
        if (this.from.equals("Images")) {
            this.mediaList.addAll(ImagesFrag.imagesList);
            selected.addAll(ImagesFrag.selected);
        } else {
            this.mediaList.addAll(VideosFrag.videosList);
            selected.addAll(VideosFrag.selected);
        }
        this.rvDetail = (RecyclerView) this.rootView.findViewById(R.id.rv_detail);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_gallery_detail, viewGroup, false);

        sharedpreferences = getActivity().getSharedPreferences(mypreference, MODE_PRIVATE);
        activity = getActivity();


        connectionDetector = new ConnectionDetector(activity.getApplicationContext());
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        init();
        populateRecyclerView();


        return this.rootView;
    }

    private void populateRecyclerView() {
        if (this.from.equals("Images")) {
            this.mAdapter = new MediaAdapter(getActivity(), this.mediaList, selected, false);
        } else {
            this.mAdapter = new MediaAdapter(getActivity(), this.mediaList, selected, true);
        }
        this.rvDetail.setLayoutManager(new GridLayoutManager(getContext(), 3));
        this.rvDetail.getItemAnimator().setChangeDuration(0);
        this.rvDetail.setAdapter(this.mAdapter);
        this.rvDetail.addOnItemTouchListener(new RecyclerTouchListener(getContext(), this.rvDetail, new ClickListener() {
            public void onLongClick(View view, int i) {
            }

            public void onClick(View view, int i) {
                ((Gallery) GalleryDetailFrag.this.getActivity()).sendResult((String) GalleryDetailFrag.this.mediaList.get(i));
            }
        }));
    }







    public void onDestroy() {
        super.onDestroy();
    }
}

package com.maxvidzgallery.storymaker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.LaunchScreenActivity;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.adapters.RvDraftAdapter;
import com.maxvidzgallery.storymaker.models.Draft;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MyDraftsFrag extends Fragment {

    private ArrayList<Draft> drafts;

    LinearLayout llAddBtn;
    public ProgressBar loader;
    private MainActivity mainActivity;
    private View rootView;
    private RvDraftAdapter rvDraftAdapter;
    public RecyclerView rvDrafts;

    View wgCheckedList;

    FloatingActionButton fab_add;
    RelativeLayout btn_cancel;
    RelativeLayout btn_delete;

    public static MyDraftsFrag getInstance(MainActivity mainActivity2) {
        MyDraftsFrag myDraftsFrag = new MyDraftsFrag();
        myDraftsFrag.mainActivity = mainActivity2;
        return myDraftsFrag;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_my_drafts, viewGroup, false);
        llAddBtn = (LinearLayout)rootView.findViewById(R.id.ll_add_btn);
        loader = (ProgressBar)rootView.findViewById(R.id.loader);
        rvDrafts = (RecyclerView)rootView.findViewById(R.id.rv_drafts);
        wgCheckedList = (View)rootView.findViewById(R.id.wg_checked_list);

        fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        btn_cancel = (RelativeLayout) rootView.findViewById(R.id.btn_cancel);
        btn_delete = (RelativeLayout)rootView.findViewById(R.id.btn_delete);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdd();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete();
            }
        });
        return this.rootView;
    }

    public void onStart() {
        super.onStart();
        setDraftList();
    }

    public void onResume() {
        super.onResume();
        this.rvDrafts.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
    }

    @SuppressLint("WrongConstant")
    private void setDraftList() {
        if (this.mainActivity == null) {
            Intent intent = new Intent(getActivity(), LaunchScreenActivity.class);
            intent.addFlags(67108864);
            startActivity(intent);
            return;
        }
        this.drafts = new ArrayList<>();
        String sb = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/Android/data/" +
                getActivity().getPackageName() +
                "/drafts/Json/";

        File file = new File(sb);
        if (file.exists()) {
            File absoluteFile = file.getAbsoluteFile();
            if (absoluteFile.list().length > 0) {
                this.llAddBtn.setVisibility(View.GONE);
                for (String file2 : absoluteFile.list()) {
                    try {
                        this.drafts.add(new Gson().fromJson(AppUtil.inputStreamToString(new FileInputStream(new File(file, file2))), Draft.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.rvDraftAdapter = new RvDraftAdapter(this.mainActivity, this.drafts, ScreenUtil.getScreenWidth(getActivity()), this);
        this.rvDrafts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        this.rvDrafts.setAdapter(this.rvDraftAdapter);
    }

    public void setWgCheckedList(boolean z) {
        if (z) {
            this.wgCheckedList.setVisibility(View.VISIBLE);
        } else {
            this.wgCheckedList.setVisibility(View.GONE);
        }
    }

    public int getWgCheckedList() {
        return this.wgCheckedList.getVisibility();
    }

    public void onCancel() {
        this.rvDraftAdapter.setCheckedDrafts(new ArrayList());
        setWgCheckedList(false);
    }

    public void onDelete() {
        ArrayList checkedDrafts = this.rvDraftAdapter.getCheckedDrafts();
        setWgCheckedList(false);
        AppUtil.removeDrafts(checkedDrafts);
        setDraftList();
    }

    public void onAdd() {
        ((MainActivity) getContext()).swipeVP(0);
    }
}

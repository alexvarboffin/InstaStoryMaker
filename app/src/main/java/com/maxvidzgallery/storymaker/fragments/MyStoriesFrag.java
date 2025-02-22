package com.maxvidzgallery.storymaker.fragments;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.RelativeLayout;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.adapters.RvStoryAdapter;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class MyStoriesFrag extends Fragment {

    private ArrayList<String> imgPaths;

    LinearLayout llAddBtn;
    private String newSaveFolder;
    private String oldSaveFolder;
    private View rootView;
    RecyclerView rvStories;
    private RvStoryAdapter rvStoryAdapter;

    View wgCheckedList;

    FloatingActionButton fab_add;
    RelativeLayout btn_cancel;
    RelativeLayout btn_delete;

    public static MyStoriesFrag getInstance() {
        return new MyStoriesFrag();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_my_story, viewGroup, false);

        llAddBtn = (LinearLayout)rootView.findViewById(R.id.ll_add_btn);
        rvStories = (RecyclerView)rootView.findViewById(R.id.rv_stories);
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
                onTrashClick();
            }
        });


        String str = "/";
        String sb = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)) +
                str +
                getString(R.string.app_name);
        this.oldSaveFolder = sb;
        String sb2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                str +
                getString(R.string.app_name).replace(" ", "");
        this.newSaveFolder = sb2;

        if (SDK_INT >= 33) {
            if (AppUtil.permissionGranted(getActivity(), "android.permission.READ_MEDIA_IMAGES") && AppUtil.permissionGranted(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                movePhotos();
                setStoriesList();
            }
        }else if(SDK_INT >= 30){
            if (AppUtil.permissionGranted(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                movePhotos();
                setStoriesList();
            }
        }else{
            if (AppUtil.permissionGranted(getActivity(), "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                movePhotos();
                setStoriesList();
            }
        }


        return this.rootView;
    }


    public void onTrashClick() {
        final Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.wg_delete_alert_dialog, null);
        dialog.setCanceledOnTouchOutside(false);
        AppUtil.showBottomDialog(getActivity(), dialog, inflate, true);
        inflate.findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        inflate.findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onDelete();
                dialog.dismiss();
            }
        });
    }



    private void movePhotos() {
        File file = new File(this.oldSaveFolder);
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File name : listFiles) {
                    String name2 = name.getName();
                    String str = "/";
                    String sb = this.oldSaveFolder +
                            str +
                            name2;
                    File file2 = new File(sb);
                    String sb2 = this.newSaveFolder +
                            str +
                            name2;
                    file2.renameTo(new File(sb2));
                }
            }
            AppUtil.deleteFolder(file);
        }
    }

    private void setStoriesList() {
        File file = new File(this.newSaveFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] listFiles = file.listFiles();
        this.imgPaths = new ArrayList<>();
        if (listFiles != null && listFiles.length > 0) {
            this.llAddBtn.setVisibility(View.GONE);
            for (File name : listFiles) {
                ArrayList<String> arrayList = this.imgPaths;
                String sb = this.newSaveFolder +
                        "/" +
                        name.getName();
                arrayList.add(sb);
            }
        }
        this.rvStoryAdapter = new RvStoryAdapter(getActivity(), this.imgPaths, ScreenUtil.getScreenWidth(getActivity()), this);
        this.rvStories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        this.rvStories.setAdapter(this.rvStoryAdapter);
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

    public void onResume() {
        setStoriesList();
        super.onResume();
    }

    public void onCancel() {
        this.rvStoryAdapter.setCheckedImages(new ArrayList());
        setWgCheckedList(false);
    }

    public void onDelete() {
        ArrayList checkedImages = this.rvStoryAdapter.getCheckedImages();
        setWgCheckedList(false);
        Iterator it = checkedImages.iterator();
        while (it.hasNext()) {
            File file = new File((String) it.next());
            if (file.delete()) {
                getActivity().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(file)));
            }
        }
        setStoriesList();
    }

    public void onAdd() {
        ((MainActivity) getContext()).swipeVP(0);
    }
}

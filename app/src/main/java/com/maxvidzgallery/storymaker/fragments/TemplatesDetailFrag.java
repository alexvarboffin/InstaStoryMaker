package com.maxvidzgallery.storymaker.fragments;

import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.adapters.RvTemplateAdapter;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TemplatesDetailFrag extends Fragment {

    private String category;

    public ProgressBar loader;
    private MainActivity mainActivity;
    private View rootView;
    private RvTemplateAdapter rvTemplateAdapter;
    public RecyclerView rvTemplates;
    public TextView tbTitle;
    private ArrayList<String> thumbnails = new ArrayList<>();

    ImageView tb_back;

    public static TemplatesDetailFrag getInstance(MainActivity mainActivity2, String str) {
        TemplatesDetailFrag templatesDetailFrag = new TemplatesDetailFrag();
        templatesDetailFrag.category = str;
        templatesDetailFrag.mainActivity = mainActivity2;
        return templatesDetailFrag;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_templates_detail, viewGroup, false);

        loader = (ProgressBar) rootView.findViewById(R.id.loader);
        rvTemplates = (RecyclerView)rootView.findViewById(R.id.rv_templates);
        tbTitle = (TextView)rootView.findViewById(R.id.tb_title);
        tb_back = (ImageView)rootView.findViewById(R.id.tb_back);

        tb_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        this.tbTitle.setText(this.category);
        setTemplateCategories();
        return this.rootView;
    }

    public void onStart() {
        super.onStart();
        if (this.category == null) {
            getActivity().onBackPressed();
        }
    }

    public void onResume() {
        super.onResume();
        this.rvTemplates.setVisibility(View.VISIBLE);
        this.loader.setVisibility(View.GONE);
    }

    private void setTemplateCategories() {
        try {
            AssetManager assets = getActivity().getAssets();
            String sb = "Thumbnails/" +
                    this.category;
            String[] list = assets.list(sb);
            list.getClass();
            this.thumbnails = new ArrayList<>(Arrays.asList(list));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rvTemplateAdapter = new RvTemplateAdapter(this.mainActivity, this.category, this.thumbnails, ScreenUtil.getScreenWidth(getActivity()));
        this.rvTemplates.setLayoutManager(new GridLayoutManager(getContext(), 2));
        this.rvTemplates.setAdapter(this.rvTemplateAdapter);
    }

    public void goBack() {
        getActivity().onBackPressed();
    }
}

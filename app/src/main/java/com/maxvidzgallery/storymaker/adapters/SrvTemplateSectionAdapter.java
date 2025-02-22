package com.maxvidzgallery.storymaker.adapters;

import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.utils.ContractsUtil;

import com.google.android.gms.ads.nativead.NativeAd;

import java.util.ArrayList;



public class SrvTemplateSectionAdapter extends RecyclerView.Adapter<SrvTemplateSectionAdapter.SectionViewHolder> {

    private final ArrayList<String> categories;
    private final MainActivity mainActivity;
    public static NativeAd nativeAd;
    private static final int item_data = 0;
    private static final int item_banner = 1;

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView itemRecyclerView;
        private final LinearLayout llRoot;
        private TextView showAllButton;
        private final TextView tvTitle;
        private final View vLine;
        FrameLayout f2505e;

        LinearLayout item_layout;

        public SectionViewHolder(View view) {
            super(view);
            this.llRoot = (LinearLayout) view.findViewById(R.id.ll_root);
            this.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            this.vLine = view.findViewById(R.id.v_line);
            this.itemRecyclerView = (RecyclerView) view.findViewById(R.id.item_recycler_view);
            this.f2505e = (FrameLayout) itemView.findViewById(R.id.fl_adplaceholder);

            item_layout = (LinearLayout) itemView.findViewById(R.id.ll_root);
        }
    }


    public SrvTemplateSectionAdapter(MainActivity mainActivity2, ArrayList<String> arrayList) {
        this.mainActivity = mainActivity2;
        this.categories = arrayList;
    }

    public SectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater;

        switch (i) {
            case item_data:
                View dataview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_templates, viewGroup, false);
                return new SectionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_templates, viewGroup, false));

            case item_banner:
            default:
                View bannerview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_view, viewGroup, false);
                return new SectionViewHolder(bannerview);


        }
    }

    public void onBindViewHolder(SectionViewHolder sectionViewHolder, int i) {
        int itemViewType = sectionViewHolder.getItemViewType();

        int viwtype = getItemViewType(i);


        switch (viwtype) {
            case item_data:
                sectionViewHolder.tvTitle.setText((CharSequence) this.categories.get(i));
                sectionViewHolder.tvTitle.setBackgroundColor(Color.parseColor((String) ContractsUtil.templateCategories.get(this.categories.get(i))));
                sectionViewHolder.vLine.setBackgroundColor(Color.parseColor((String) ContractsUtil.templateCategories.get(this.categories.get(i))));
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mainActivity, RecyclerView.HORIZONTAL, false);
                SrvTemplateItemsAdapter srvTemplateItemsAdapter = new SrvTemplateItemsAdapter(this.mainActivity, (String) this.categories.get(i));
                sectionViewHolder.itemRecyclerView.setHasFixedSize(true);
                sectionViewHolder.itemRecyclerView.setNestedScrollingEnabled(true);
                sectionViewHolder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                sectionViewHolder.itemRecyclerView.setAdapter(srvTemplateItemsAdapter);
                break;

            case item_banner:

            default:

        }
    }

    public int getItemCount() {
        return this.categories.size();

    }

    public int getItemViewType(int i) {
        if (i % MainActivity.ITEM_PER_AD == 0)
            return item_banner;
        else
            return categories.size() == 0 ? 1 : 0;

    }



}

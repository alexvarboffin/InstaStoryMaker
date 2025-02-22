package com.maxvidzgallery.storymaker.adapters;

import android.content.res.AssetManager;
import android.net.Uri;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import java.util.ArrayList;
import java.util.Arrays;

public class SrvTemplateItemsAdapter extends RecyclerView.Adapter<SrvTemplateItemsAdapter.ItemViewHolder> {

    private final String category;
    private boolean isFirstClick;
    private long mLastClickTime = System.currentTimeMillis();
    private final MainActivity mainActivity;
    private ArrayList<String> thumbnails;

    public class ItemViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        ItemClickListener itemClickListener;
        private final CardView rootView;
        private final ImageView thumbnail;

        public ItemViewHolder(View view) {
            super(view);
            this.rootView = (CardView) view.findViewById(R.id.rootView);
            this.thumbnail = (ImageView) view.findViewById(R.id.iv_thumbnail);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public SrvTemplateItemsAdapter(MainActivity mainActivity2, String str) {
        this.mainActivity = mainActivity2;
        this.category = str;
        try {
            AssetManager assets = mainActivity2.getAssets();
            String sb = "Thumbnails/" +
                    str;
            String[] list = assets.list(sb);
            list.getClass();
            this.thumbnails = new ArrayList<>(Arrays.asList(list));
        } catch (Exception unused) {
            unused.printStackTrace();
        }
        this.isFirstClick = true;
    }

    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_srv_templates, viewGroup, false));
    }

    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        double screenWidth = (double) ScreenUtil.getScreenWidth(this.mainActivity);
        Double.isNaN(screenWidth);
        int i2 = (int) (screenWidth / 3.8d);
        double d = (double) i2;
        Double.isNaN(d);
        int i3 = (int) (d * 1.7777777777777777d);
        itemViewHolder.rootView.getLayoutParams().width = i2;
        itemViewHolder.rootView.getLayoutParams().height = i3;
        String sb = "file:///android_asset/Thumbnails/" +
                this.category +
                "/" +
                (String) this.thumbnails.get(i);
        Glide.with((FragmentActivity) this.mainActivity).load(Uri.parse(sb)).into(itemViewHolder.thumbnail);
        itemViewHolder.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                long currentTimeMillis = System.currentTimeMillis();
                if (SrvTemplateItemsAdapter.this.isFirstClick || currentTimeMillis - SrvTemplateItemsAdapter.this.mLastClickTime >= 3000) {
                    SrvTemplateItemsAdapter.this.mLastClickTime = currentTimeMillis;
                    SrvTemplateItemsAdapter.this.isFirstClick = false;
                    Log.e("harshil", "onItemClick: "+SrvTemplateItemsAdapter.this.thumbnails.get(i) );
                    if (SrvTemplateItemsAdapter.this.mainActivity instanceof MainActivity) {
                        AppUtil.mainPermissionGranted(SrvTemplateItemsAdapter.this.mainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, SrvTemplateItemsAdapter.this.category, ((String) SrvTemplateItemsAdapter.this.thumbnails.get(i)).replace(".jpg", ""), null);
                    }
                }
            }
        });
    }

    public int getItemCount() {
        return this.thumbnails.size();
    }
}

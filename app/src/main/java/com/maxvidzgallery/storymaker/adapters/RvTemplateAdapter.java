package com.maxvidzgallery.storymaker.adapters;

import android.graphics.Color;
import android.net.Uri;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.ContractsUtil;
import com.maxvidzgallery.storymaker.utils.DensityUtil;
import java.util.ArrayList;
import java.util.List;

public class RvTemplateAdapter extends RecyclerView.Adapter<RvTemplateAdapter.ViewHolderCollagePattern> {

    private ArrayList<String> categories;
    private String category;
    private boolean isFirstClick;
    private long mLastClickTime = System.currentTimeMillis();
    private final MainActivity mainActivity;
    private final List<String> newCategories = new ArrayList();
    private final int screenWidth;
    private final ArrayList<String> thumbnails;

    static class ViewHolderCollagePattern extends RecyclerView.ViewHolder implements OnClickListener {
        ItemClickListener itemClickListener;
        ImageView ivNew;
        ImageView ivThumb;
        RelativeLayout llWrapper;
        TextView tvName;

        public ViewHolderCollagePattern(View view) {
            super(view);
            this.ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.llWrapper = (RelativeLayout) view.findViewById(R.id.ll_wrapper);
            this.ivNew = (ImageView) view.findViewById(R.id.iv_new);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public RvTemplateAdapter(MainActivity mainActivity2, ArrayList<String> arrayList, ArrayList<String> arrayList2, int i) {
        this.mainActivity = mainActivity2;
        this.categories = arrayList;
        this.thumbnails = arrayList2;
        this.screenWidth = i;
        this.newCategories.add("Pinpur");
        this.isFirstClick = true;
    }

    public RvTemplateAdapter(MainActivity mainActivity2, String str, ArrayList<String> arrayList, int i) {
        this.mainActivity = mainActivity2;
        this.thumbnails = arrayList;
        this.category = str;
        this.screenWidth = i;
        this.isFirstClick = true;
    }

    public ViewHolderCollagePattern onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderCollagePattern(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_templates, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderCollagePattern viewHolderCollagePattern, int i) {
        String str;
        int dp2px = (this.screenWidth / 2) - DensityUtil.dp2px(this.mainActivity, 12.0f);
        viewHolderCollagePattern.ivThumb.getLayoutParams().width = dp2px;
        LayoutParams layoutParams = viewHolderCollagePattern.ivThumb.getLayoutParams();
        double d = (double) dp2px;
        Double.isNaN(d);
        layoutParams.height = (int) (d * 1.7777777777777777d);
        String str2 = "/";
        String str3 = "file:///android_asset/Thumbnails/";
        if (this.categories != null) {
            viewHolderCollagePattern.ivThumb.setPadding(10, 10, 10, 0);
            viewHolderCollagePattern.llWrapper.setBackgroundColor(Color.parseColor((String) ContractsUtil.templateCategories.get(this.categories.get(i))));
            viewHolderCollagePattern.tvName.setText((CharSequence) this.categories.get(i));
            String sb = str3 +
                    (String) this.categories.get(i) +
                    str2 +
                    (String) this.thumbnails.get(i);
            str = sb;
            if (this.newCategories.contains(this.categories.get(i))) {
                viewHolderCollagePattern.ivNew.setVisibility(View.VISIBLE);
            } else {
                viewHolderCollagePattern.ivNew.setVisibility(View.GONE);
            }
        } else {
            viewHolderCollagePattern.tvName.setVisibility(View.GONE);
            String sb2 = str3 +
                    this.category +
                    str2 +
                    (String) this.thumbnails.get(i);
            str = sb2;
        }
        Glide.with((FragmentActivity) this.mainActivity).load(Uri.parse(str)).into(viewHolderCollagePattern.ivThumb);
        viewHolderCollagePattern.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                long currentTimeMillis = System.currentTimeMillis();
                if (RvTemplateAdapter.this.isFirstClick || currentTimeMillis - RvTemplateAdapter.this.mLastClickTime >= 3000) {
                    RvTemplateAdapter.this.mLastClickTime = currentTimeMillis;
                    RvTemplateAdapter.this.isFirstClick = false;
                    if (RvTemplateAdapter.this.categories != null) {
                        RvTemplateAdapter.this.mainActivity.selectTempCategory((String) RvTemplateAdapter.this.categories.get(i));
                    } else {
                        AppUtil.mainPermissionGranted(RvTemplateAdapter.this.mainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, RvTemplateAdapter.this.category, ((String) RvTemplateAdapter.this.thumbnails.get(i)).replace(".PNG", ""), null);
                    }
                }
            }
        });
    }

    public int getItemCount() {
        ArrayList<String> arrayList = this.thumbnails;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }
}

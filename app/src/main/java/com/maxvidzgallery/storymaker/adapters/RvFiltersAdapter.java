package com.maxvidzgallery.storymaker.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.fragments.FiltersFrag;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.utils.GPUImageFilterTools.FilterList;
import java.util.ArrayList;

public class RvFiltersAdapter extends RecyclerView.Adapter<RvFiltersAdapter.ViewHolder> {

    private FilterList adjusts;
    private FiltersFrag filtersFrag;
    private ArrayList<String> filtersNameList = new ArrayList<>();
    public Context mContext;
    private int selectedItem = -1;
    private final String type;

    static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView imageView;
        ItemClickListener itemClickListener;
        RelativeLayout rlOverlay;
        TextView tvName;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.gpu_image);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.rlOverlay = (RelativeLayout) view.findViewById(R.id.rl_filter_overlay);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public RvFiltersAdapter(Context context, ArrayList<String> arrayList, String str, FiltersFrag filtersFrag2) {
        this.mContext = context;
        this.filtersNameList = arrayList;
        this.type = str;
        this.filtersFrag = filtersFrag2;
    }

    public RvFiltersAdapter(Context context, FilterList filterList, String str) {
        this.mContext = context;
        this.adjusts = filterList;
        this.type = str;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filters, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String str = ".png";
        if (this.type == "Filters") {
            int i2 = this.selectedItem;
            if (i2 != i || i2 == 0) {
                viewHolder.rlOverlay.setVisibility(View.GONE);
            } else {
                viewHolder.rlOverlay.setVisibility(View.VISIBLE);
            }
            viewHolder.tvName.setText((CharSequence) this.filtersNameList.get(i));
            String sb = "file:///android_asset/Filters/" +
                    (String) this.filtersNameList.get(i) +
                    str;
            Glide.with(this.mContext).load(Uri.parse(sb)).into(viewHolder.imageView);
        } else {
            viewHolder.tvName.setText((CharSequence) this.adjusts.names.get(i));
            String sb3 = "file:///android_asset/" +
                    (String) this.adjusts.names.get(i) +
                    str;
            viewHolder.imageView.setPadding(25, 25, 25, 25);
            Glide.with(this.mContext).load(Uri.parse(sb3)).into(viewHolder.imageView);
        }
        viewHolder.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                if (RvFiltersAdapter.this.type == "Filters") {
                    RvFiltersAdapter.this.filtersFrag.switchFilterTo(i - 1);
                    RvFiltersAdapter.this.selectedItem = i;
                    RvFiltersAdapter.this.notifyDataSetChanged();
                    return;
                }
                boolean z = RvFiltersAdapter.this.mContext instanceof EditorActivity;
            }
        });
    }

    public int getItemCount() {
        return this.type == "Filters" ? this.filtersNameList.size() : this.adjusts.names.size();
    }
}

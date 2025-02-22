package com.maxvidzgallery.storymaker.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.R;

public class RvColorsAdapter extends RecyclerView.Adapter<RvColorsAdapter.ViewHolderCollagePattern> {

    private final String[] colors;
    private final boolean isBackground;
    private final Context mContext;
    private final int screenWidth;

    static class ViewHolderCollagePattern extends RecyclerView.ViewHolder implements OnClickListener {
        ItemClickListener itemClickListener;
        LinearLayout root;
        View vColor;

        public ViewHolderCollagePattern(View view) {
            super(view);
            this.root = (LinearLayout)view.findViewById(R.id.root);
            this.vColor = (View)view.findViewById(R.id.v_shape);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public RvColorsAdapter(Context context, String[] strArr, boolean z, int i) {
        this.mContext = context;
        this.colors = strArr;
        this.isBackground = z;
        this.screenWidth = i;
    }

    public ViewHolderCollagePattern onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderCollagePattern(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_shape, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderCollagePattern viewHolderCollagePattern, int i) {

        viewHolderCollagePattern.vColor.setBackgroundColor(Color.parseColor(this.colors[i]));

        viewHolderCollagePattern.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                if (RvColorsAdapter.this.isBackground) {
                    ((EditorActivity) RvColorsAdapter.this.mContext).changeBackground(RvColorsAdapter.this.colors[i], null, null);
                } else {
                    ((EditorActivity) RvColorsAdapter.this.mContext).changeTextEntityColor(RvColorsAdapter.this.colors[i]);
                }
            }
        });
    }

    public int getItemCount() {
        String[] strArr = this.colors;
        if (strArr != null) {
            return strArr.length;
        }
        return 0;
    }
}

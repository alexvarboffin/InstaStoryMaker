package com.maxvidzgallery.storymaker.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import java.util.ArrayList;

public class RvGradientAdapter extends RecyclerView.Adapter<RvGradientAdapter.ViewHolderCollagePattern> {

    private final int[] colors = new int[0];
    private String[] gradient;
    private final String gradientType;
    private final String[] gradients;
    private boolean isBackground;
    private boolean isLocked;
    private final String linearDirection;
    private final ArrayList<String> lockedNumbers = new ArrayList<>();
    private final Context mContext;
    private final SharedPreferences prefs;
    private final int screenWidth;

    static class ViewHolderCollagePattern extends RecyclerView.ViewHolder implements OnClickListener {
        ItemClickListener itemClickListener;
        View vGradient;

        public ViewHolderCollagePattern(View view) {
            super(view);
            this.vGradient = view.findViewById(R.id.v_shape);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public RvGradientAdapter(Context context, String[] strArr, String str, String str2, boolean z, int i) {
        this.mContext = context;
        this.gradients = strArr;
        this.gradientType = str;
        this.linearDirection = str2;
        this.isBackground = z;
        this.screenWidth = i;
        this.prefs = context.getSharedPreferences("prefs", 0);
        this.isLocked = AppUtil.isLocked(this.prefs, "gradientsAddTime");
        this.lockedNumbers.addAll(AppUtil.getLockedNumbers(this.prefs));
    }

    public ViewHolderCollagePattern onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderCollagePattern(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_circle_shape, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderCollagePattern viewHolderCollagePattern, final int i) {
        this.gradient = new String[0];
        this.gradient = this.gradients[i].split(" ");
        viewHolderCollagePattern.vGradient.setBackgroundDrawable(AppUtil.generateViewGradient(this.gradient, this.gradientType, this.linearDirection, viewHolderCollagePattern.vGradient.getWidth(), viewHolderCollagePattern.vGradient.getHeight()));
        viewHolderCollagePattern.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                RvGradientAdapter.this.gradient = new String[0];
                RvGradientAdapter.this.gradient = AppUtil.strTOStrArray(RvGradientAdapter.this.gradients[i], " ");
                if (RvGradientAdapter.this.isBackground) {
                    ((EditorActivity) RvGradientAdapter.this.mContext).changeBackground(null, RvGradientAdapter.this.gradient, null);
                } else {
                    ((EditorActivity) RvGradientAdapter.this.mContext).changeTextEntityGradient(RvGradientAdapter.this.gradient);
                }
            }
        });
    }

    public int getItemCount() {
        String[] strArr = this.gradients;
        if (strArr != null) {
            return strArr.length;
        }
        return 0;
    }

    public void refreshList(boolean z) {
        this.isBackground = z;
        this.isLocked = AppUtil.isLocked(this.prefs, "gradientsAddTime");
        notifyDataSetChanged();
    }
}

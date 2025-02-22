package com.maxvidzgallery.storymaker.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.FontProvider;
import java.util.ArrayList;
import java.util.List;

public class RvFontAdapter extends RecyclerView.Adapter<RvFontAdapter.ViewHolderCollagePattern> {

    private final String category;
    private final FontProvider fontProvider;
    private final List<String> fonts;
    private boolean isLocked;
    private final ArrayList<String> lockedNumbers = new ArrayList<>();
    private final Context mContext;
    private final SharedPreferences prefs;
    private final int screenWidth;
    private String selectedFont;

    static class ViewHolderCollagePattern extends RecyclerView.ViewHolder implements OnClickListener {
        TextView font;
        ItemClickListener itemClickListener;
        View vSelected;

        public ViewHolderCollagePattern(View view) {
            super(view);
            this.font = (TextView) view.findViewById(R.id.tv_font);
            this.vSelected = view.findViewById(R.id.v_selected);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }
    }

    public RvFontAdapter(Context context, String str, List<String> list, FontProvider fontProvider2, String str2, int i) {
        this.mContext = context;
        this.category = str;
        this.fonts = list;
        this.fontProvider = fontProvider2;
        this.screenWidth = i;
        this.selectedFont = str2;
        this.prefs = context.getSharedPreferences("prefs", 0);
        this.isLocked = AppUtil.isLocked(this.prefs, "fontsAddTime");
        this.lockedNumbers.addAll(AppUtil.getLockedNumbers(this.prefs));
    }

    public ViewHolderCollagePattern onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderCollagePattern(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_fonts, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderCollagePattern viewHolderCollagePattern, int i) {
        viewHolderCollagePattern.font.setText((CharSequence) this.fonts.get(i));
        viewHolderCollagePattern.font.setTypeface(this.fontProvider.getTypeface(this.category, (String) this.fonts.get(i)));
        LayoutParams layoutParams = viewHolderCollagePattern.font.getLayoutParams();
        double d = (double) this.screenWidth;
        Double.isNaN(d);
        layoutParams.width = (int) (d / 3.5d);
        viewHolderCollagePattern.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                if (RvFontAdapter.this.mContext instanceof EditorActivity) {
                    ((EditorActivity) RvFontAdapter.this.mContext).changeTextEntityFont(RvFontAdapter.this.category, (String) RvFontAdapter.this.fonts.get(i));
                    RvFontAdapter rvFontAdapter = RvFontAdapter.this;
                    rvFontAdapter.selectedFont = (String) rvFontAdapter.fonts.get(i);
                    RvFontAdapter.this.notifyDataSetChanged();
                }
            }
        });
        if (((String) this.fonts.get(i)).equals(this.selectedFont)) {
            viewHolderCollagePattern.vSelected.setVisibility(View.VISIBLE);
        } else {
            viewHolderCollagePattern.vSelected.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        List<String> list = this.fonts;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public void refreshList() {
        this.isLocked = AppUtil.isLocked(this.prefs, "fontsAddTime");
        notifyDataSetChanged();
    }
}

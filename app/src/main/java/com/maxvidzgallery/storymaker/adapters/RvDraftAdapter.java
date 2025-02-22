package com.maxvidzgallery.storymaker.adapters;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.fragments.MyDraftsFrag;
import com.maxvidzgallery.storymaker.interfaces.ItemClickListener;
import com.maxvidzgallery.storymaker.interfaces.ItemLongClickListener;
import com.maxvidzgallery.storymaker.models.Draft;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.DensityUtil;
import java.util.ArrayList;

public class RvDraftAdapter extends RecyclerView.Adapter<RvDraftAdapter.ViewHolderCollagePattern> {

    private ArrayList<Draft> checkedDrafts;
    private final ArrayList<Draft> drafts;
    private boolean isFirstClick;
    private boolean isLongClick;
    private long mLastClickTime = System.currentTimeMillis();
    private final MainActivity mainActivity;
    private final MyDraftsFrag myDraftsFrag;
    private final int screenWidth;

    static class ViewHolderCollagePattern extends RecyclerView.ViewHolder implements OnClickListener, OnLongClickListener {
        ItemClickListener itemClickListener;
        ItemLongClickListener itemLongClickListener;
        ImageView ivThumb;
        RelativeLayout rlChecked;
        View vLine;

        public ViewHolderCollagePattern(View view) {
            super(view);
            this.ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            this.vLine = view.findViewById(R.id.v_line);
            this.rlChecked = (RelativeLayout) view.findViewById(R.id.rl_checked);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener2) {
            this.itemClickListener = itemClickListener2;
        }

        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener2) {
            this.itemLongClickListener = itemLongClickListener2;
        }

        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getLayoutPosition());
        }

        public boolean onLongClick(View view) {
            this.itemLongClickListener.onItemLongClick(view, getLayoutPosition());
            return true;
        }
    }

    public RvDraftAdapter(MainActivity mainActivity2, ArrayList<Draft> arrayList, int i, MyDraftsFrag myDraftsFrag2) {
        this.mainActivity = mainActivity2;
        this.drafts = arrayList;
        this.screenWidth = i;
        this.myDraftsFrag = myDraftsFrag2;
        this.checkedDrafts = new ArrayList<>();
        this.isFirstClick = true;
        this.isLongClick = false;
    }

    public ViewHolderCollagePattern onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolderCollagePattern(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drafts, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolderCollagePattern viewHolderCollagePattern, int i) {
        int dp2px = (this.screenWidth / 2) - DensityUtil.dp2px(this.mainActivity, 12.0f);
        double d = (double) dp2px;
        Double.isNaN(d);
        int i2 = (int) (d * 1.7777777777777777d);
        viewHolderCollagePattern.ivThumb.getLayoutParams().width = dp2px;
        viewHolderCollagePattern.ivThumb.getLayoutParams().height = i2;
        viewHolderCollagePattern.rlChecked.getLayoutParams().width = dp2px;
        viewHolderCollagePattern.rlChecked.getLayoutParams().height = i2;
        if (((Draft) this.drafts.get(i)).saved) {
            viewHolderCollagePattern.vLine.setBackgroundColor(this.mainActivity.getResources().getColor(R.color.colorAccent2));
        } else {
            viewHolderCollagePattern.vLine.setBackgroundColor(this.mainActivity.getResources().getColor(R.color.colorGrey));
        }
        Glide.with((FragmentActivity) this.mainActivity).load(((Draft) this.drafts.get(i)).thumbnail).into(viewHolderCollagePattern.ivThumb);
        if (this.checkedDrafts.contains(this.drafts.get(i))) {
            viewHolderCollagePattern.rlChecked.setVisibility(View.VISIBLE);
        } else {
            viewHolderCollagePattern.rlChecked.setVisibility(View.GONE);
        }
        viewHolderCollagePattern.setItemClickListener(new ItemClickListener() {
            public void onItemClick(View view, int i) {
                long currentTimeMillis = System.currentTimeMillis();
                if (RvDraftAdapter.this.isLongClick || RvDraftAdapter.this.isFirstClick || currentTimeMillis - RvDraftAdapter.this.mLastClickTime >= 3000) {
                    RvDraftAdapter.this.mLastClickTime = currentTimeMillis;
                    RvDraftAdapter.this.isFirstClick = false;
                    if (RvDraftAdapter.this.myDraftsFrag.getWgCheckedList() != 0) {
                        AppUtil.mainPermissionGranted(RvDraftAdapter.this.mainActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, ((Draft) RvDraftAdapter.this.drafts.get(i)).template_category, ((Draft) RvDraftAdapter.this.drafts.get(i)).template_name, ((Draft) RvDraftAdapter.this.drafts.get(i)).save_path);
                    } else if (!RvDraftAdapter.this.checkedDrafts.contains(RvDraftAdapter.this.drafts.get(i))) {
                        RvDraftAdapter.this.checkedDrafts.add(RvDraftAdapter.this.drafts.get(i));
                    } else {
                        RvDraftAdapter.this.checkedDrafts.remove(RvDraftAdapter.this.drafts.get(i));
                    }
                    RvDraftAdapter.this.notifyDataSetChanged();
                }
            }
        });
        viewHolderCollagePattern.setItemLongClickListener(new ItemLongClickListener() {
            public void onItemLongClick(View view, int i) {
                if (RvDraftAdapter.this.checkedDrafts.size() == 0) {
                    RvDraftAdapter.this.checkedDrafts.add(RvDraftAdapter.this.drafts.get(i));
                    RvDraftAdapter.this.myDraftsFrag.setWgCheckedList(true);
                    RvDraftAdapter.this.notifyDataSetChanged();
                    RvDraftAdapter.this.isLongClick = true;
                }
            }
        });
    }

    public int getItemCount() {
        ArrayList<Draft> arrayList = this.drafts;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public ArrayList<Draft> getCheckedDrafts() {
        this.isLongClick = false;
        return this.checkedDrafts;
    }

    public void setCheckedDrafts(ArrayList<Draft> arrayList) {
        this.checkedDrafts = arrayList;
        notifyDataSetChanged();
        this.isLongClick = false;
    }
}

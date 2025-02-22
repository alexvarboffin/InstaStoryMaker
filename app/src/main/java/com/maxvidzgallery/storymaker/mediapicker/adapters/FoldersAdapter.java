package com.maxvidzgallery.storymaker.mediapicker.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.mediapicker.utils.ScreenUtil;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.MyViewHolder> {

    private final Activity activity;
    private final List<String> bitmapList;
    private final List<String> folderNames;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView count;
        public ImageView thumbnail;
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.tv_title);
            this.count = (TextView) view.findViewById(R.id.tv_count);
            this.thumbnail = (ImageView) view.findViewById(R.id.iv_thumb);
        }
    }

    public FoldersAdapter(Activity activity2, List<String> list, List<String> list2) {
        this.folderNames = list;
        this.bitmapList = list2;
        this.activity = activity2;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder, viewGroup, false));
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.thumbnail.getLayoutParams().width = ScreenUtil.getScreenWidth(this.activity) / 3;
        myViewHolder.thumbnail.getLayoutParams().height = ScreenUtil.getScreenWidth(this.activity) / 3;
        myViewHolder.title.setText((CharSequence) this.folderNames.get(i));
        RequestManager with = Glide.with(this.activity);
        String sb = "file://" +
                (String) this.bitmapList.get(i);
        with.load(sb).apply(((RequestOptions) new RequestOptions().override(300, 300)).centerCrop()).into(myViewHolder.thumbnail);
    }

    public int getItemCount() {
        return this.folderNames.size();
    }
}

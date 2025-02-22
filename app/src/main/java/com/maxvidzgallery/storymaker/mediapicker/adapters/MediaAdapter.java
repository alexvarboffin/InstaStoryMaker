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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.mediapicker.utils.AppUtil;
import com.maxvidzgallery.storymaker.mediapicker.utils.ScreenUtil;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder> {

    private final Activity activity;
    private final boolean isVideo;
    private final List<String> mediaList;
    private final List<Boolean> selected;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView duration;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            this.thumbnail = (ImageView) view.findViewById(R.id.iv_thumb);
            this.duration = (TextView) view.findViewById(R.id.tv_duration);
        }
    }

    public MediaAdapter(Activity activity2, List<String> list, List<Boolean> list2, boolean z) {
        this.mediaList = list;
        this.activity = activity2;
        this.selected = list2;
        this.isVideo = z;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.thumbnail.getLayoutParams().width = ScreenUtil.getScreenWidth(this.activity) / 3;
        myViewHolder.thumbnail.getLayoutParams().height = ScreenUtil.getScreenWidth(this.activity) / 3;
        RequestManager with = Glide.with(this.activity);
        String str = "file://";
        String sb = str +
                (String) this.mediaList.get(i);
        with.load(sb).apply(((RequestOptions) ((RequestOptions) new RequestOptions().override(300, 300)).centerCrop()).skipMemoryCache(true)).transition(DrawableTransitionOptions.withCrossFade()).into(myViewHolder.thumbnail);
        if (this.isVideo) {
            myViewHolder.duration.setVisibility(View.VISIBLE);
            TextView textView = myViewHolder.duration;
            Activity activity2 = this.activity;
            String sb2 = str +
                    (String) this.mediaList.get(i);
            textView.setText(AppUtil.getVideoDuration(activity2, sb2));
        }
    }

    public int getItemCount() {
        return this.mediaList.size();
    }
}

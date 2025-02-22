package com.maxvidzgallery.storymaker.adapters;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.maxvidzgallery.storymaker.R;

import java.util.ArrayList;

public class Stickeradapter extends BaseAdapter {

    private final RequestOptions requestOptions;

    Context context;
    ArrayList<String> arrayList = new ArrayList();
    LayoutInflater inflater;

    class ViewHolder {

        ImageView imageview;

        ViewHolder() {
        }
    }

    public Stickeradapter(Context context, ArrayList<String> f) {
        this.context = context;
        arrayList = f;
        initImageLoader(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Кеширование на диск
                //.skipMemoryCache(false) // Кеширование в памяти
                .placeholder(R.drawable.sticker_placeholder) // Замена R.drawable.placeholder_image на ваш ресурс
                .error(R.drawable.error_image) // Замена R.drawable.error_image на ваш ресурс
             //   .fallback(R.drawable.empty_uri_image) // Замена R.drawable.empty_uri_image на ваш ресурс
             //   .bitmapConfig(Config.RGB_565) // Конфигурация Bitmap
                .fitCenter(); // Аналог ImageScaleType.EXACTLY_STRETCHED
    }



    public int getCount() {
        return arrayList.size();
    }

    public String getItem(int position) {
        return arrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View tmpView = convertView;

        if (tmpView == null) {
            ViewHolder holder = new ViewHolder();
            tmpView = inflater.inflate(R.layout.item_sticker, null);
            holder.imageview = tmpView.findViewById(R.id.grid_item);
            tmpView.setTag(holder);
        }

        ImageView holderimageView = ((ViewHolder) tmpView.getTag()).imageview;
        String imagePath = ("assets://crown/" + arrayList.get(position));
        Glide.with(context)
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holderimageView);




        Glide.with(context)
                .load(imagePath)
                .apply(requestOptions)
                .into(holderimageView);
        return tmpView;
    }

    public static void initImageLoader(Context context) {

//        DisplayImageOptions defaultOptions = new Builder()
//                .cacheInMemory(true).cacheOnDisc(true)
//                .showImageOnLoading(17301633)
//                .showImageForEmptyUri(17301543)
//                .showImageOnFail(17301624)
//                .considerExifParams(true)
//                .bitmapConfig(Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                .build();
//        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(context)
//                .threadPriority(3).denyCacheImageMultipleSizesInMemory()
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .defaultDisplayImageOptions(null).build());
    }
}

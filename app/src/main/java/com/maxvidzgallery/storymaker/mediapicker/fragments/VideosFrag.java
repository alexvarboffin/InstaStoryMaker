package com.maxvidzgallery.storymaker.mediapicker.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.maxvidzgallery.storymaker.mediapicker.Gallery;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.mediapicker.adapters.FoldersAdapter;
import com.maxvidzgallery.storymaker.mediapicker.utils.ClickListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VideosFrag extends Fragment {

    public static List<Boolean> selected = new ArrayList();
    public static List<String> videosList = new ArrayList();
    private final List<String> bitmapList = new ArrayList();

    private List<String> bucketNames = new ArrayList();
    private FoldersAdapter mAdapter;
    private final String[] projection;
    private final String[] projection2;
    private View rootView;
    private RecyclerView rvVideos;

    public VideosFrag() {
        String str = "_data";
        this.projection = new String[]{"bucket_display_name", str};
        this.projection2 = new String[]{"_display_name", str};
    }

    public static VideosFrag getInstance() {
        return new VideosFrag();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.bucketNames.clear();
        this.bitmapList.clear();
        videosList.clear();
        getVideoBuckets();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_videos, viewGroup, false);
        this.rvVideos = this.rootView.findViewById(R.id.rv_videos);
        populateRecyclerView();
        return this.rootView;
    }

    private void populateRecyclerView() {
        this.mAdapter = new FoldersAdapter(getActivity(), this.bucketNames, this.bitmapList);
        this.rvVideos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        this.rvVideos.setItemAnimator(new DefaultItemAnimator());
        this.rvVideos.setAdapter(this.mAdapter);
        this.rvVideos.addOnItemTouchListener(new RecyclerTouchListener(getContext(), this.rvVideos, new ClickListener() {
            public void onLongClick(View view, int i) {
            }

            public void onClick(View view, int i) {
                VideosFrag videosFrag = VideosFrag.this;
                videosFrag.getVideos(videosFrag.bucketNames.get(i));
                ((Gallery) VideosFrag.this.getActivity()).addFragment(GalleryDetailFrag.getInstance(VideosFrag.this.bucketNames.get(i), "Videos"));
            }
        }));
        this.mAdapter.notifyDataSetChanged();
    }

    public void getVideos(String str) {
        selected.clear();
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME+" =?",new String[]{str}, MediaStore.Video.Media.DATE_ADDED);
        ArrayList<String> imagesTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(projection2[1]));
                file = new File(path);
                if (file.exists() && !albumSet.contains(path)) {
                    imagesTEMP.add(path);
                    albumSet.add(path);
                    selected.add(false);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (imagesTEMP == null) {

            imagesTEMP = new ArrayList<>();
        }
        videosList.clear();
        videosList.addAll(imagesTEMP);
    }

    public void getVideoBuckets() {
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Video.Media.DATE_ADDED);
        ArrayList<String> bucketNamesTEMP = new ArrayList<>(cursor.getCount());
        ArrayList<String> bitmapListTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                @SuppressLint("Range") String image = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(image);
                if (file.exists() && !albumSet.contains(album)) {
                    bucketNamesTEMP.add(album);
                    bitmapListTEMP.add(image);
                    albumSet.add(album);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        if (bucketNamesTEMP == null) {
            bucketNames = new ArrayList<>();
        }
        bucketNames.clear();
        bitmapList.clear();
        bucketNames.addAll(bucketNamesTEMP);
        bitmapList.addAll(bitmapListTEMP);
    }
}

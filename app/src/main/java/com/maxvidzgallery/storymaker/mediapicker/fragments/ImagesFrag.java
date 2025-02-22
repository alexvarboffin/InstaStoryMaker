package com.maxvidzgallery.storymaker.mediapicker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class ImagesFrag extends Fragment {

    public static List<String> imagesList = new ArrayList();
    public static List<Boolean> selected = new ArrayList();
    private final List<String> bitmapList = new ArrayList();

    private List<String> bucketNames = new ArrayList();
    private FoldersAdapter foldersAdapter;
    private final String[] projection;
    private final String[] projection2;
    private View rootView;
    private RecyclerView rvImages;

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private final ClickListener clickListener;
        private final GestureDetector gestureDetector;

        public void onRequestDisallowInterceptTouchEvent(boolean z) {
        }

        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        }

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener2) {
            this.clickListener = clickListener2;
            this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return true;
                }

                public void onLongPress(MotionEvent motionEvent) {
                    View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (findChildViewUnder != null) {
                        ClickListener clickListener = clickListener2;
                        if (clickListener != null) {
                            clickListener.onLongClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
                        }
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if (!(findChildViewUnder == null || this.clickListener == null || !this.gestureDetector.onTouchEvent(motionEvent))) {
                this.clickListener.onClick(findChildViewUnder, recyclerView.getChildPosition(findChildViewUnder));
            }
            return false;
        }
    }

    public ImagesFrag() {
        String str = "_data";
        this.projection = new String[]{"bucket_display_name", str};
        this.projection2 = new String[]{"_display_name", str};
    }

    public static ImagesFrag getInstance() {
        return new ImagesFrag();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.bitmapList.clear();
        imagesList.clear();
        this.bucketNames.clear();

        getPicFolders();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.rootView = layoutInflater.inflate(R.layout.fragment_images, viewGroup, false);
        this.rvImages = (RecyclerView) this.rootView.findViewById(R.id.rv_images);
        populateRecyclerView();
        return this.rootView;
    }

    private void populateRecyclerView() {
        this.foldersAdapter = new FoldersAdapter(getActivity(), this.bucketNames, this.bitmapList);
        this.rvImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
        this.rvImages.setItemAnimator(new DefaultItemAnimator());
        this.rvImages.setAdapter(this.foldersAdapter);
        this.rvImages.addOnItemTouchListener(new RecyclerTouchListener(getContext(), this.rvImages, new ClickListener() {
            public void onLongClick(View view, int i) {
            }

            public void onClick(View view, int i) {
                ImagesFrag imagesFrag = ImagesFrag.this;
                imagesFrag.getPictures((String) imagesFrag.bucketNames.get(i));
                ((Gallery) ImagesFrag.this.getActivity()).addFragment(GalleryDetailFrag.getInstance((String) ImagesFrag.this.bucketNames.get(i), "Images"));
            }
        }));
        this.foldersAdapter.notifyDataSetChanged();
    }

    public void getPicFolders() {
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);
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

    public void getPictures(String str) {
        selected.clear();
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?",new String[]{str},MediaStore.Images.Media.DATE_ADDED);
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
        imagesList.clear();
        imagesList.addAll(imagesTEMP);
    }
}

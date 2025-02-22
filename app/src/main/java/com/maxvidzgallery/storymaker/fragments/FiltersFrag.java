package com.maxvidzgallery.storymaker.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;


import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.adapters.RvFiltersAdapter;
import com.maxvidzgallery.storymaker.databinding.FragmentFiltersBinding;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.utils.AnimationsUtil;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.BitmapUtil;
import com.maxvidzgallery.storymaker.utils.ContractsUtil;
import com.maxvidzgallery.storymaker.utils.GPUImageFilterTools;
import com.maxvidzgallery.storymaker.utils.GPUImageFilterTools.FilterAdjuster;
import com.maxvidzgallery.storymaker.utils.GPUImageFilterTools.FilterList;
import com.maxvidzgallery.storymaker.utils.GPUImageFilterTools.FilterType;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import jp.co.cyberagent.android.gpuimage.GPUImage;

import static android.content.Context.MODE_PRIVATE;

public class FiltersFrag extends Fragment {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";

    ConnectionDetector connectionDetector;

    Activity activity;

    private final String[] adjFilters = {"Sepia", ""};
    private FilterType choseFilter;
    private String choseFltName;

    private Bitmap crtBitmap;
    private float filterProgress = 50.0f;
    private int filterSelected = -1;

    private final ArrayList<String> filterWithVignette = new ArrayList<>();
    private FilterList filters;
    private final ArrayList<String> filtersNameList = new ArrayList<>();

    private float finalProgress = 0.0f;
    private Bitmap fitBitmap;
    private Bitmap fltBitmap;
    private boolean fltSeekbar;

    private final ArrayList<String> fullFilters = new ArrayList<>();
    private IndicatorSeekBar indicatorSeekBar;

    private FilterAdjuster mFilterAdjuster;
    private RvFiltersAdapter mFiltersAdapter;

    private GPUImage mGPUImage;
    private final GPUImageFilterTools mGpuImageFilterTools = new GPUImageFilterTools();
    private int numOfClick = 0;
    private Bitmap orgBitmap;

    private @NonNull FragmentFiltersBinding binding;
    
    private int sbMax;
    private int sbMin;
    private int screenHeight;
    private int screenWidth;
   

    private String vignetteColor;
    private int vignetteIntensity;
    private int vignetteLevel;

    private class GenerateFiltersThumbnails extends AsyncTask<Bitmap, String, ArrayList<Bitmap>> {
        private GenerateFiltersThumbnails() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            FiltersFrag.this.loading(true);
        }

        protected ArrayList<Bitmap> doInBackground(Bitmap... bitmapArr) {
            FilterList filterList = GPUImageFilterTools.getFilterList();
            GPUImage gPUImage = new GPUImage(FiltersFrag.this.getContext());
            try {
                int totalMemory = AppUtil.getTotalMemory(FiltersFrag.this.getContext());
                int i = 10;
                if (totalMemory <= 2000) {
                    i = 14;
                } else if (totalMemory <= 4000) {
                    i = 12;
                }
                gPUImage.setImage(Bitmap.createScaledBitmap(bitmapArr[0], bitmapArr[0].getWidth() / i, bitmapArr[0].getHeight() / i, false));
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(ContractsUtil.vignetteContracts.keySet());
                for (int i2 = 0; i2 < filterList.names.size(); i2++) {
                    if (FiltersFrag.this.fullFilters.contains(filterList.names.get(i2))) {
                        gPUImage.setFilter(GPUImageFilterTools.createFilterForType(FiltersFrag.this.getContext(), filterList.filters.get(i2), 1.0f, null));
                    } else {
                        gPUImage.setFilter(GPUImageFilterTools.createFilterForType(FiltersFrag.this.getContext(), filterList.filters.get(i2), 0.5f, null));
                    }
                    if (arrayList.contains(filterList.names.get(i2))) {
                        String[] split = ContractsUtil.vignetteContracts.get(filterList.names.get(i2)).split("x");
                        Integer.parseInt(split[0]);
                        String str = split[1];
                        Integer.parseInt(split[2]);
                    }
                }
            } catch (NullPointerException unused) {
                unused.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Bitmap> arrayList) {
            super.onPostExecute(arrayList);
            if (arrayList == null) {
                new GenerateFiltersThumbnails().execute(FiltersFrag.this.fitBitmap);
            }
        }
    }

    private class ResultDone extends AsyncTask<String, String, String> {

        protected String doInBackground(String... strArr) {
            return null;
        }

        private ResultDone() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            FiltersFrag.this.loading(true);
        }

        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (FiltersFrag.this.finalProgress != 0.0f) {
                EditorActivity editorActivity = (EditorActivity) FiltersFrag.this.getActivity();
                FiltersFrag filtersFrag = FiltersFrag.this;
                editorActivity.setSelectedPhoto(filtersFrag.applyFltBitmap(filtersFrag.orgBitmap, FiltersFrag.this.finalProgress));
                return;
            }
            ((EditorActivity) FiltersFrag.this.getActivity()).setSelectedPhoto(FiltersFrag.this.orgBitmap);
        }
    }

    public static FiltersFrag getInstance(Bitmap bitmap) {
        FiltersFrag filtersFrag = new FiltersFrag();
        filtersFrag.orgBitmap = bitmap;
        return filtersFrag;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void init() {
        AnimationsUtil.initAnimationsValue(getContext());
        this.screenWidth = ScreenUtil.getScreenWidth(getActivity());
        this.screenHeight = ScreenUtil.getScreenHeight(getActivity());
        this.fitBitmap = BitmapUtil.createFitBitmap(this.orgBitmap, 1920);
        this.binding.ivPhoto.setImageBitmap(this.fitBitmap);
        this.fltBitmap = this.fitBitmap;
        this.crtBitmap = this.fltBitmap;
        this.binding.ivPhoto.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            if (action == 0) {
                FiltersFrag.this.binding.ivPhoto.setImageBitmap(FiltersFrag.this.fitBitmap);
            } else if (action == 1) {
                if (FiltersFrag.this.binding.llSeekbarContainer.isShown()) {
                    FiltersFrag.this.binding.ivPhoto.setImageBitmap(FiltersFrag.this.fltBitmap);
                } else {
                    FiltersFrag.this.binding.ivPhoto.setImageBitmap(FiltersFrag.this.crtBitmap);
                }
            }
            return true;
        });
        this.fullFilters.add("Sepia");
        this.fullFilters.add("Moon");
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.binding = FragmentFiltersBinding.inflate(layoutInflater, viewGroup, false);
        sharedpreferences = getActivity().getSharedPreferences(mypreference, MODE_PRIVATE);
        activity = getActivity();

        connectionDetector = new ConnectionDetector(activity.getApplicationContext());
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        init();
        showFiltersMenu();
        binding.tbClose.setOnClickListener(v->tbClose());
        binding.tbDone.setOnClickListener(v->tbDone());
        binding.tvCancel.setOnClickListener(v->tvCancel());
        binding.tbDone.setOnClickListener(v->tbDone());
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
    }

    public void showFiltersMenu() {
        this.filters = GPUImageFilterTools.getFilterList();
        this.filtersNameList.add("Original");
        for (int i = 0; i < this.filters.names.size(); i++) {
            this.filtersNameList.add(this.filters.names.get(i));
        }
        this.mFiltersAdapter = new RvFiltersAdapter(getContext(), this.filtersNameList, "Filters", this);
        this.binding.rvMenuFilters.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.binding.rvMenuFilters.setAdapter(this.mFiltersAdapter);
    }

    public void switchFilterTo(final int i) {
        this.fltSeekbar = true;
        if (i < 0) {
            this.numOfClick = 0;
            this.finalProgress = 0.0f;
            this.fltBitmap = this.fitBitmap;
            this.crtBitmap = this.fltBitmap;
            this.binding.ivPhoto.setImageBitmap(this.mGpuImageFilterTools.applyAdjustment(getContext(), this.crtBitmap, AppUtil.getInAdjustsContrast(getContext())));
            showSeekBar(false);
            return;
        }
        this.numOfClick++;
        if (this.filterSelected != i) {
            if (this.fullFilters.contains(this.choseFltName)) {
                this.filterProgress = 100.0f;
            } else {
                this.filterProgress = 50.0f;
            }
            this.numOfClick = 1;
            this.filterSelected = i;
            showSeekBar(false);
        }
        if (this.numOfClick == 2) {
            this.numOfClick = 1;
            this.sbMax = 100;
            this.sbMin = 0;
            setUpSeekBar(this.filterProgress);
            this.indicatorSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
                public void onSeeking(SeekParams seekParams) {
                }

                public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
                }

                public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
                    float f;
                    if (Arrays.asList(FiltersFrag.this.adjFilters).contains(FiltersFrag.this.filters.filters.get(i))) {
                        FiltersFrag.this.mFilterAdjuster.adjust(indicatorSeekBar.getProgress());
                        FiltersFrag.this.mGPUImage.requestRender();
                        FiltersFrag filtersFrag = FiltersFrag.this;
                        filtersFrag.fltBitmap = filtersFrag.mGpuImageFilterTools.applyAdjustment(FiltersFrag.this.getContext(), FiltersFrag.this.mGPUImage.getBitmapWithFilterApplied(), AppUtil.getInAdjustsContrast(FiltersFrag.this.getContext()));
                    } else {
                        try {
                            f = Float.valueOf(new DecimalFormat("#.#").format(indicatorSeekBar.getProgressFloat() / 100.0f)).floatValue();
                        } catch (NumberFormatException unused) {
                            f = 1.0f;
                        }
                        FiltersFrag filtersFrag2 = FiltersFrag.this;
                        filtersFrag2.mGPUImage = new GPUImage(filtersFrag2.getContext());
                        FiltersFrag.this.mGPUImage.setImage(FiltersFrag.this.fitBitmap);
                        FiltersFrag.this.mGPUImage.setFilter(GPUImageFilterTools.createFilterForType(FiltersFrag.this.getContext(), FiltersFrag.this.filters.filters.get(i), f, null));
                        if (FiltersFrag.this.filterWithVignette.contains(FiltersFrag.this.filters.names.get(i))) {
                            FiltersFrag filtersFrag3 = FiltersFrag.this;
                            filtersFrag3.fltBitmap = filtersFrag3.mGpuImageFilterTools.applyAdjustment(FiltersFrag.this.getContext(), BitmapUtil.applyVignette(FiltersFrag.this.mGPUImage.getBitmapWithFilterApplied(), FiltersFrag.this.vignetteLevel, FiltersFrag.this.vignetteColor, (FiltersFrag.this.vignetteIntensity * indicatorSeekBar.getProgress()) / 100), AppUtil.getInAdjustsContrast(FiltersFrag.this.getContext()));
                        } else {
                            FiltersFrag filtersFrag4 = FiltersFrag.this;
                            filtersFrag4.fltBitmap = filtersFrag4.mGpuImageFilterTools.applyAdjustment(FiltersFrag.this.getContext(), FiltersFrag.this.mGPUImage.getBitmapWithFilterApplied(), AppUtil.getInAdjustsContrast(FiltersFrag.this.getContext()));
                        }
                    }
                    FiltersFrag.this.binding.ivPhoto.setImageBitmap(FiltersFrag.this.fltBitmap);
                }
            });
            showSeekBar(true);
            return;
        }
        this.choseFilter = this.filters.filters.get(i);
        this.choseFltName = this.filters.names.get(i);
        if (this.fullFilters.contains(this.choseFltName)) {
            this.filterProgress = 100.0f;
            this.finalProgress = 1.0f;
            applyFltBitmap(this.fitBitmap, 1.0f);
        } else {
            this.filterProgress = 50.0f;
            this.finalProgress = 0.5f;
            applyFltBitmap(this.fitBitmap, 0.5f);
        }
        this.crtBitmap = this.fltBitmap;
        this.binding.ivPhoto.setImageBitmap(this.crtBitmap);
        this.binding.tbFilter.setText(this.filtersNameList.get(i));
    }

    private Bitmap applyFltBitmap(Bitmap bitmap, float f) {
        this.mGPUImage = new GPUImage(getActivity());
        this.mGPUImage.setImage(bitmap);
        this.mGPUImage.setFilter(GPUImageFilterTools.createFilterForType(getActivity(), this.choseFilter, f, null));
        if (this.filterWithVignette.contains(this.choseFltName)) {
            String[] split = ContractsUtil.vignetteContracts.get(this.choseFltName).split("x");
            this.vignetteLevel = Integer.parseInt(split[0]);
            this.vignetteColor = split[1];
            this.vignetteIntensity = Integer.parseInt(split[2]);
            this.fltBitmap = BitmapUtil.applyVignette(this.mGPUImage.getBitmapWithFilterApplied(), this.vignetteLevel, this.vignetteColor, this.vignetteIntensity);
        } else {
            this.fltBitmap = this.mGPUImage.getBitmapWithFilterApplied();
        }
        return this.mGpuImageFilterTools.applyAdjustment(getContext(), this.fltBitmap, AppUtil.getInAdjustsContrast(getContext()));
    }

    private void showSeekBar(boolean z) {
        if (z) {
            this.binding.llSeekbarContainer.setVisibility(View.VISIBLE);
            this.binding.tbFilter.setVisibility(View.VISIBLE);
            return;
        }
        this.binding.llSeekbarContainer.setVisibility(View.GONE);
        this.binding.tbFilter.setVisibility(View.GONE);
    }

    private void setUpSeekBar(float f) {
        this.binding.llSeekbarContainer.removeAllViews();
        this.indicatorSeekBar = IndicatorSeekBar.with(getContext()).max((float) this.sbMax).min((float) this.sbMin).progress(f).trackBackgroundSize(1).trackBackgroundColor(getActivity().getResources().getColor(R.color.colorLightGrey)).trackProgressSize(1).trackProgressColor(getActivity().getResources().getColor(R.color.colorLightGrey)).indicatorColor(getActivity().getResources().getColor(R.color.colorAccent)).showIndicatorType(1).thumbSize(15).thumbColor(getActivity().getResources().getColor(R.color.colorAccent)).build();
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.gravity = 17;
        this.binding.llSeekbarContainer.addView(this.indicatorSeekBar, layoutParams);
    }

    public void loading(boolean z) {
        if (z) {
            this.binding.wgFltLoading.getRoot().setVisibility(View.VISIBLE);
        } else {
            this.binding.wgFltLoading.getRoot().setVisibility(View.GONE);
        }
    }

    public void tbClose() {
        ((EditorActivity) getActivity()).setSelectedPhoto(null);
    }


    public void tbDone() {
        new ResultDone().execute();
    }


    public void tvCancel() {
        this.binding.ivPhoto.setImageBitmap(this.crtBitmap);
        showSeekBar(false);
    }


    public void tvDone() {
        this.filterProgress = (float) this.indicatorSeekBar.getProgress();
        this.finalProgress = this.filterProgress / 100.0f;
        this.crtBitmap = this.fltBitmap;
        this.binding.ivPhoto.setImageBitmap(this.crtBitmap);
        showSeekBar(false);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}

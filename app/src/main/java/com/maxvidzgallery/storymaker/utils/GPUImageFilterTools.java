package com.maxvidzgallery.storymaker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.ExifInterface;

import com.maxvidzgallery.storymaker.R;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorDodgeBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDarkenBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDifferenceBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDivideBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExclusionBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHardLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLinearBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminosityBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMultiplyBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageNormalBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageScreenBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSoftLightBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSourceOverBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSubtractBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;

public class GPUImageFilterTools {

    public static class FilterAdjuster {

        private final float adjustEnd;
        private final float adjustStart;
        private final Adjuster<? extends GPUImageFilter> adjuster;

        private abstract class Adjuster<T extends GPUImageFilter> {
            private T filter;

            public abstract void adjust(int i);

            protected float range(int i, float f, float f2) {
                return (((f2 - f) * ((float) i)) / 100.0f) + f;
            }

            private Adjuster() {
            }

            public Adjuster<T> filter(GPUImageFilter gPUImageFilter) {
                this.filter = (T) gPUImageFilter;
                return this;
            }

            public T getFilter() {
                return this.filter;
            }

            protected int range(int i, int i2, int i3) {
                return (((i3 - i2) * i) / 100) + i2;
            }
        }

        private class BlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
            Context mContext;

            public BlurAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Blur", range);
                ((GPUImageGaussianBlurFilter) getFilter()).setBlurSize(range);
            }
        }

        private class BrightnessAdjuster extends Adjuster<GPUImageBrightnessFilter> {
            Context mContext;

            public BrightnessAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Brightness", range);
                ((GPUImageBrightnessFilter) getFilter()).setBrightness(range);
            }
        }

        private class ContrastAdjuster extends Adjuster<GPUImageContrastFilter> {
            Context mContext;

            public ContrastAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, ExifInterface.TAG_CONTRAST, range);
                ((GPUImageContrastFilter) getFilter()).setContrast(range);
            }
        }

        private class ExposureAdjuster extends Adjuster<GPUImageExposureFilter> {
            Context mContext;

            public ExposureAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Exposure", range);
                ((GPUImageExposureFilter) getFilter()).setExposure(range);
            }
        }

        private class HighlightShadowAdjuster extends Adjuster<GPUImageHighlightShadowFilter> {
            Context mContext;

            public HighlightShadowAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                ((GPUImageHighlightShadowFilter) getFilter()).setHighlights(range);
                ((GPUImageHighlightShadowFilter) getFilter()).setShadows(range);
            }
        }

        private class HueAdjuster extends Adjuster<GPUImageHueFilter> {
            Context mContext;

            public HueAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Hue", range);
                ((GPUImageHueFilter) getFilter()).setHue(range);
            }
        }

        private class SaturationAdjuster extends Adjuster<GPUImageSaturationFilter> {
            Context mContext;

            public SaturationAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, ExifInterface.TAG_SATURATION, range);
                ((GPUImageSaturationFilter) getFilter()).setSaturation(range);
            }
        }

        private class SepiaAdjuster extends Adjuster<GPUImageSepiaToneFilter> {
            private SepiaAdjuster() {
                super();
            }

            public void adjust(int i) {
                ((GPUImageSepiaToneFilter) getFilter()).setIntensity(range(i, 0.0f, 2.0f));
            }
        }

        private class SharpnessAdjuster extends Adjuster<GPUImageSharpenFilter> {
            Context mContext;

            public SharpnessAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Sharpen", range);
                ((GPUImageSharpenFilter) getFilter()).setSharpness(range);
            }
        }

        private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
            Context mContext;

            public VignetteAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Vignette", range);
                ((GPUImageVignetteFilter) getFilter()).setVignetteStart(range);
            }
        }

        private class WarmthAdjuster extends Adjuster<GPUImageWhiteBalanceFilter> {
            Context mContext;

            public WarmthAdjuster(Context context) {
                super();
                this.mContext = context;
            }

            public void adjust(int i) {
                float range = range(i, FilterAdjuster.this.adjustStart, FilterAdjuster.this.adjustEnd);
                AppUtil.putInAdjustsContrast(this.mContext, "Warmth", range);
                ((GPUImageWhiteBalanceFilter) getFilter()).setTemperature(range);
            }
        }

        public FilterAdjuster(Context context, GPUImageFilter gPUImageFilter, float f, float f2) {
            this.adjustStart = f;
            this.adjustEnd = f2;
            if (gPUImageFilter instanceof GPUImageSepiaToneFilter) {
                this.adjuster = new SepiaAdjuster().filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageBrightnessFilter) {
                this.adjuster = new BrightnessAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageContrastFilter) {
                this.adjuster = new ContrastAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageSaturationFilter) {
                this.adjuster = new SaturationAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageWhiteBalanceFilter) {
                this.adjuster = new WarmthAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageSharpenFilter) {
                this.adjuster = new SharpnessAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageHighlightShadowFilter) {
                this.adjuster = new HighlightShadowAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageExposureFilter) {
                this.adjuster = new ExposureAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageVignetteFilter) {
                this.adjuster = new VignetteAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageHueFilter) {
                this.adjuster = new HueAdjuster(context).filter(gPUImageFilter);
            } else if (gPUImageFilter instanceof GPUImageGaussianBlurFilter) {
                this.adjuster = new BlurAdjuster(context).filter(gPUImageFilter);
            } else {
                this.adjuster = null;
            }
        }

        public boolean canAdjust() {
            return this.adjuster != null;
        }

        public void adjust(int i) {
            Adjuster<? extends GPUImageFilter> adjuster2 = this.adjuster;
            if (adjuster2 != null) {
                adjuster2.adjust(i);
            }
        }
    }

    public static class FilterList {
        public List<FilterType> filters = new LinkedList();
        public List<String> names = new LinkedList();

        public void addFilter(String str, FilterType filterType) {
            this.names.add(str);
            this.filters.add(filterType);
        }
    }

    public enum FilterType {
        LOOKUP_HUDSON,
        LOOKUP_1977,
        LOOKUP_AMARO,
        LOOKUP_MAYFAIR,
        LOOKUP_VALENCIA,
        LOOKUP_NASHVILLE,
        LOOKUP_GINGHAM,
        LOOKUP_EARLYBIRD,
        LOOKUP_MOON,
        LOOKUP_AMATORKA,
        CONTRAST,
        SHARPEN,
        SEPIA,
        BRIGHTNESS,
        SATURATION,
        WARMTH,
        HIGHLIGHT_SHADOW,
        EXPOSURE,
        VIGNETTE,
        HUE,
        BLUR,
        PIXELATION,
        BLEND_NORMAL,
        BLEND_DISSOLVE,
        BLEND_DARKEN,
        BLEND_MULTIPLY,
        BLEND_COLOR_BURN,
        BLEND_LINEAR_BURN,
        BLEND_LIGHTEN,
        BLEND_SCREEN,
        BLEND_COLOR_DODGE,
        BLEND_ADD,
        BLEND_OVERLAY,
        BLEND_SOFT_LIGHT,
        BLEND_HARD_LIGHT,
        BLEND_CHROMA_KEY,
        BLEND_ALPHA,
        BLEND_SOURCE_OVER,
        BLEND_DIFFERENCE,
        BLEND_EXCLUSION,
        BLEND_SUBTRACT,
        BLEND_DIVIDE,
        BLEND_HUE,
        BLEND_SATURATION,
        BLEND_COLOR,
        BLEND_LUMINOSITY
    }

    public Bitmap applyAdjustment(Context context, Bitmap bitmap, LinkedHashMap linkedHashMap) {
        return bitmap;
    }

    public static FilterList getFilterList() {
        FilterList filterList = new FilterList();
        filterList.addFilter("Amaro", FilterType.LOOKUP_AMARO);
        filterList.addFilter("Mayfair", FilterType.LOOKUP_MAYFAIR);
        filterList.addFilter("Hudson", FilterType.LOOKUP_HUDSON);
        filterList.addFilter("Earlybird", FilterType.LOOKUP_EARLYBIRD);
        filterList.addFilter("Valencia", FilterType.LOOKUP_VALENCIA);
        filterList.addFilter("Nashville", FilterType.LOOKUP_NASHVILLE);
        filterList.addFilter("Gingham", FilterType.LOOKUP_GINGHAM);
        filterList.addFilter("Amatorka", FilterType.LOOKUP_AMATORKA);
        filterList.addFilter("1977", FilterType.LOOKUP_1977);
        filterList.addFilter("Sepia", FilterType.SEPIA);
        filterList.addFilter("Moon", FilterType.LOOKUP_MOON);
        return filterList;
    }

    public static GPUImageFilter createFilterForType(Context context, FilterType filterType, float f, Bitmap bitmap) {
        try {
            switch (filterType) {
                case BRIGHTNESS:
                    return new GPUImageBrightnessFilter(f);
                case CONTRAST:
                    return new GPUImageContrastFilter(f);
                case SATURATION:
                    return new GPUImageSaturationFilter(f);
                case WARMTH:
                    return new GPUImageWhiteBalanceFilter(f, 0.0f);
                case HIGHLIGHT_SHADOW:
                    return new GPUImageHighlightShadowFilter(f, f);
                case EXPOSURE:
                    return new GPUImageExposureFilter(f);
                case SHARPEN:
                    GPUImageSharpenFilter gPUImageSharpenFilter = new GPUImageSharpenFilter();
                    gPUImageSharpenFilter.setSharpness(f);
                    return gPUImageSharpenFilter;
                case VIGNETTE:
                    PointF pointF = new PointF();
                    pointF.x = 0.5f;
                    pointF.y = 0.5f;
                    return new GPUImageVignetteFilter(pointF, new float[]{0.0f, 0.0f, 0.0f}, -f, 1.0f);
                case HUE:
                    return new GPUImageHueFilter(f);
                case BLUR:
                    return new GPUImageGaussianBlurFilter(f);
                case SEPIA:
                    return new GPUImageSepiaToneFilter(f);
                case LOOKUP_EARLYBIRD:
                    GPUImageLookupFilter gPUImageLookupFilter = new GPUImageLookupFilter();
                    gPUImageLookupFilter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_earlybird));
                    gPUImageLookupFilter.setIntensity(f);
                    return gPUImageLookupFilter;
                case LOOKUP_HUDSON:
                    GPUImageLookupFilter gPUImageLookupFilter2 = new GPUImageLookupFilter();
                    gPUImageLookupFilter2.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_hudson));
                    gPUImageLookupFilter2.setIntensity(f);
                    return gPUImageLookupFilter2;
                case LOOKUP_AMATORKA:
                    GPUImageLookupFilter gPUImageLookupFilter3 = new GPUImageLookupFilter();
                    gPUImageLookupFilter3.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_amatorka));
                    gPUImageLookupFilter3.setIntensity(f);
                    return gPUImageLookupFilter3;
                case LOOKUP_1977:
                    GPUImageLookupFilter gPUImageLookupFilter4 = new GPUImageLookupFilter();
                    gPUImageLookupFilter4.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_1977));
                    gPUImageLookupFilter4.setIntensity(f);
                    return gPUImageLookupFilter4;
                case LOOKUP_AMARO:
                    GPUImageLookupFilter gPUImageLookupFilter5 = new GPUImageLookupFilter();
                    gPUImageLookupFilter5.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_amaro));
                    gPUImageLookupFilter5.setIntensity(f);
                    return gPUImageLookupFilter5;
                case LOOKUP_MAYFAIR:
                    GPUImageLookupFilter gPUImageLookupFilter6 = new GPUImageLookupFilter();
                    gPUImageLookupFilter6.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_mayfair));
                    gPUImageLookupFilter6.setIntensity(f);
                    return gPUImageLookupFilter6;
                case LOOKUP_VALENCIA:
                    GPUImageLookupFilter gPUImageLookupFilter7 = new GPUImageLookupFilter();
                    gPUImageLookupFilter7.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_valencia));
                    gPUImageLookupFilter7.setIntensity(f);
                    return gPUImageLookupFilter7;
                case LOOKUP_NASHVILLE:
                    GPUImageLookupFilter gPUImageLookupFilter8 = new GPUImageLookupFilter();
                    gPUImageLookupFilter8.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_nashville));
                    gPUImageLookupFilter8.setIntensity(f);
                    return gPUImageLookupFilter8;
                case LOOKUP_MOON:
                    GPUImageLookupFilter gPUImageLookupFilter9 = new GPUImageLookupFilter();
                    gPUImageLookupFilter9.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_moon));
                    gPUImageLookupFilter9.setIntensity(f);
                    return gPUImageLookupFilter9;
                case LOOKUP_GINGHAM:
                    GPUImageLookupFilter gPUImageLookupFilter10 = new GPUImageLookupFilter();
                    gPUImageLookupFilter10.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.lut_gingham));
                    gPUImageLookupFilter10.setIntensity(f);
                    return gPUImageLookupFilter10;
                case BLEND_NORMAL:
                    return createBlendFilter(context, bitmap, GPUImageNormalBlendFilter.class);
                case BLEND_DISSOLVE:
                    return createBlendFilter(context, bitmap, GPUImageDissolveBlendFilter.class);
                case BLEND_DARKEN:
                    return createBlendFilter(context, bitmap, GPUImageDarkenBlendFilter.class);
                case BLEND_MULTIPLY:
                    return createBlendFilter(context, bitmap, GPUImageMultiplyBlendFilter.class);
                case BLEND_COLOR_BURN:
                    return createBlendFilter(context, bitmap, GPUImageColorBurnBlendFilter.class);
                case BLEND_LINEAR_BURN:
                    return createBlendFilter(context, bitmap, GPUImageLinearBurnBlendFilter.class);
                case BLEND_LIGHTEN:
                    return createBlendFilter(context, bitmap, GPUImageLightenBlendFilter.class);
                case BLEND_SCREEN:
                    return createBlendFilter(context, bitmap, GPUImageScreenBlendFilter.class);
                case BLEND_COLOR_DODGE:
                    return createBlendFilter(context, bitmap, GPUImageColorDodgeBlendFilter.class);
                case BLEND_ADD:
                    return createBlendFilter(context, bitmap, GPUImageAddBlendFilter.class);
                case BLEND_OVERLAY:
                    return createBlendFilter(context, bitmap, GPUImageOverlayBlendFilter.class);
                case BLEND_SOFT_LIGHT:
                    return createBlendFilter(context, bitmap, GPUImageSoftLightBlendFilter.class);
                case BLEND_HARD_LIGHT:
                    return createBlendFilter(context, bitmap, GPUImageHardLightBlendFilter.class);
                case BLEND_CHROMA_KEY:
                    return createBlendFilter(context, bitmap, GPUImageChromaKeyBlendFilter.class);
                case BLEND_ALPHA:
                    return createBlendFilter(context, bitmap, GPUImageAlphaBlendFilter.class);
                case BLEND_SOURCE_OVER:
                    return createBlendFilter(context, bitmap, GPUImageSourceOverBlendFilter.class);
                case BLEND_DIFFERENCE:
                    return createBlendFilter(context, bitmap, GPUImageDifferenceBlendFilter.class);
                case BLEND_EXCLUSION:
                    return createBlendFilter(context, bitmap, GPUImageExclusionBlendFilter.class);
                case BLEND_SUBTRACT:
                    return createBlendFilter(context, bitmap, GPUImageSubtractBlendFilter.class);
                case BLEND_DIVIDE:
                    return createBlendFilter(context, bitmap, GPUImageDivideBlendFilter.class);
                case BLEND_HUE:
                    return createBlendFilter(context, bitmap, GPUImageHueBlendFilter.class);
                case BLEND_SATURATION:
                    return createBlendFilter(context, bitmap, GPUImageSaturationBlendFilter.class);
                case BLEND_COLOR:
                    return createBlendFilter(context, bitmap, GPUImageColorBlendFilter.class);
                case BLEND_LUMINOSITY:
                    return createBlendFilter(context, bitmap, GPUImageLuminosityBlendFilter.class);
                default:
                    throw new IllegalStateException("No filter of that type!");
            }
        } catch (Exception unused) {
            return new GPUImageAddBlendFilter();
        }
    }

    private static GPUImageFilter createBlendFilter(Context context, Bitmap bitmap, Class<? extends GPUImageTwoInputFilter> cls) {
        try {
            GPUImageTwoInputFilter gPUImageTwoInputFilter = (GPUImageTwoInputFilter) cls.newInstance();
            gPUImageTwoInputFilter.setBitmap(bitmap);
            return gPUImageTwoInputFilter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

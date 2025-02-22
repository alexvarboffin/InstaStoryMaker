package com.maxvidzgallery.storymaker.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.SystemClock;
import androidx.core.view.MotionEventCompat;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import com.maxvidzgallery.storymaker.R;
import java.io.InputStream;

public class ImageStickerView extends AppCompatImageView {

    private static final float BITMAP_SCALE = 0.53f;
    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private static final String TAG = "StickerView";
    private float MAX_SCALE;
    private float MIN_SCALE;
    private Bitmap deleteBitmap;
    private int deleteBitmapHeight;
    private int deleteBitmapWidth;
    private DisplayMetrics dm;
    private Rect dst_delete;
    private Rect dst_flipV;
    private Rect dst_resize;
    private Rect dst_top;
    private Bitmap flipVBitmap;
    private int flipVBitmapHeight;
    private int flipVBitmapWidth;
    private InputStream gifInputStream;
    private Movie gifMovie;
    private double halfDiagonalLength;
    private boolean isGif;
    private boolean isHorizonMirror;
    private boolean isInEdit;
    private boolean isInResize;
    private boolean isInSide;
    private boolean isPointerDown;
    private float lastLength;
    private float lastRotateDegree;
    private float lastX;
    private float lastY;
    private float layoutX;
    private float layoutY;
    private Paint localPaint;
    private Bitmap mBitmap;
    private int mCurrentAnimationTime;
    private long mMovieStart;
    private int mScreenHeight;
    private int mScreenWidth;
    private final Matrix matrix;
    private final PointF mid;
    private float oldDis;
    private OperationListener operationListener;
    private float originWidth;
    private final float pointerLimitDis;
    private final float pointerZoomOff;
    private Bitmap resizeBitmap;
    private int resizeBitmapHeight;
    private int resizeBitmapWidth;
    private int resourceHeight;
    private int resourceWidth;
    private float rotate;
    private float scale;
    private final int stickerId;
    private String stickerPath;
    private Bitmap topBitmap;
    private int topBitmapHeight;
    private int topBitmapWidth;

    public interface OperationListener {
        void onDeleteClick();

        void onEdit(ImageStickerView imageStickerView);

        void onTop(ImageStickerView imageStickerView);
    }

    public ImageStickerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mid = new PointF();
        this.isPointerDown = false;
        this.pointerLimitDis = 20.0f;
        this.pointerZoomOff = 0.09f;
        this.matrix = new Matrix();
        this.isInEdit = true;
        this.MIN_SCALE = 0.5f;
        this.MAX_SCALE = 1.2f;
        this.originWidth = 0.0f;
        this.scale = 0.3f;
        this.rotate = 0.0f;
        this.mMovieStart = 0;
        this.mCurrentAnimationTime = 0;
        this.stickerId = 0;
        init();
    }

    public ImageStickerView(Context context, String str, float f, float f2, float f3, float f4, int i) {
        super(context);
        this.mid = new PointF();
        this.isPointerDown = false;
        this.pointerLimitDis = 20.0f;
        this.pointerZoomOff = 0.09f;
        this.matrix = new Matrix();
        this.isInEdit = true;
        this.MIN_SCALE = 0.5f;
        this.MAX_SCALE = 1.2f;
        this.originWidth = 0.0f;
        this.scale = 0.3f;
        this.rotate = 0.0f;
        this.mMovieStart = 0;
        this.mCurrentAnimationTime = 0;
        this.stickerPath = str;
        this.layoutX = f;
        this.layoutY = f2;
        this.scale = f3;
        this.rotate = f4;
        this.stickerId = i;
        init();
    }

    public ImageStickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mid = new PointF();
        this.isPointerDown = false;
        this.pointerLimitDis = 20.0f;
        this.pointerZoomOff = 0.09f;
        this.matrix = new Matrix();
        this.isInEdit = true;
        this.MIN_SCALE = 0.5f;
        this.MAX_SCALE = 1.2f;
        this.originWidth = 0.0f;
        this.scale = 0.3f;
        this.rotate = 0.0f;
        this.mMovieStart = 0;
        this.mCurrentAnimationTime = 0;
        this.stickerId = 0;
        init();
    }

    private void init() {
        this.dst_delete = new Rect();
        this.dst_resize = new Rect();
        this.dst_flipV = new Rect();
        this.dst_top = new Rect();
        this.localPaint = new Paint();
        this.localPaint.setColor(-16777216);
        this.localPaint.setAntiAlias(true);
        this.localPaint.setDither(true);
        this.localPaint.setStyle(Style.STROKE);
        this.localPaint.setStrokeWidth(1.0f);
        this.dm = getResources().getDisplayMetrics();
        this.mScreenWidth = this.dm.widthPixels;
        this.mScreenHeight = this.dm.heightPixels;
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (this.mBitmap != null || this.gifMovie != null) {
            float[] fArr = new float[9];
            this.matrix.getValues(fArr);
            float f = (fArr[0] * 0.0f) + (fArr[1] * 0.0f) + fArr[2];
            float f2 = fArr[5] + (fArr[3] * 0.0f) + (fArr[4] * 0.0f);
            float f3 = fArr[0];
            int i = this.resourceWidth;
            float f4 = fArr[2] + (f3 * ((float) i)) + (fArr[1] * 0.0f);
            float f5 = fArr[5] + (fArr[3] * ((float) i)) + (fArr[4] * 0.0f);
            float f6 = fArr[0] * 0.0f;
            float f7 = fArr[1];
            int i2 = this.resourceHeight;
            float f8 = f6 + (f7 * ((float) i2)) + fArr[2];
            float f9 = (fArr[3] * 0.0f) + (fArr[4] * ((float) i2)) + fArr[5];
            float f10 = (fArr[0] * ((float) i)) + (fArr[1] * ((float) i2)) + fArr[2];
            float f11 = (fArr[3] * ((float) i)) + (fArr[4] * ((float) i2)) + fArr[5];
            canvas.save();
            Rect rect = this.dst_delete;
            int i3 = this.deleteBitmapWidth;
            rect.left = (int) (f4 - ((float) (i3 / 3)));
            rect.right = (int) (((float) (i3 / 3)) + f4);
            int i4 = this.deleteBitmapHeight;
            rect.top = (int) (f5 - ((float) (i4 / 3)));
            rect.bottom = (int) (((float) (i4 / 3)) + f5);
            Rect rect2 = this.dst_resize;
            int i5 = this.resizeBitmapWidth;
            rect2.left = (int) (f10 - ((float) (i5 / 3)));
            rect2.right = (int) (((float) (i5 / 3)) + f10);
            int i6 = this.resizeBitmapHeight;
            rect2.top = (int) (f11 - ((float) (i6 / 3)));
            rect2.bottom = (int) (((float) (i6 / 3)) + f11);
            Rect rect3 = this.dst_top;
            int i7 = this.flipVBitmapWidth;
            rect3.left = (int) (f - ((float) (i7 / 3)));
            rect3.right = (int) (((float) (i7 / 3)) + f);
            int i8 = this.flipVBitmapHeight;
            rect3.top = (int) (f2 - ((float) (i8 / 3)));
            rect3.bottom = (int) (((float) (i8 / 3)) + f2);
            Rect rect4 = this.dst_flipV;
            int i9 = this.topBitmapWidth;
            rect4.left = (int) (f8 - ((float) (i9 / 3)));
            rect4.right = (int) (f8 + ((float) (i9 / 3)));
            int i10 = this.topBitmapHeight;
            rect4.top = (int) (f9 - ((float) (i10 / 3)));
            rect4.bottom = (int) (((float) (i10 / 3)) + f9);
            if (this.isInEdit) {
                Canvas canvas3 = canvas;
                canvas3.drawLine(f, f2, f4, f5, this.localPaint);
                float f12 = f10;
                float f13 = f11;
                canvas3.drawLine(f4, f5, f12, f13, this.localPaint);
                float f14 = f8;
                float f15 = f9;
                canvas3.drawLine(f14, f15, f12, f13, this.localPaint);
                canvas3.drawLine(f14, f15, f, f2, this.localPaint);
                canvas2.drawBitmap(this.deleteBitmap, null, this.dst_delete, null);
                canvas2.drawBitmap(this.resizeBitmap, null, this.dst_resize, null);
                canvas2.drawBitmap(this.flipVBitmap, null, this.dst_flipV, null);
                canvas2.drawBitmap(this.topBitmap, null, this.dst_top, null);
            }
            if (this.isGif) {
                canvas2.setMatrix(this.matrix);
                updateAnimationTime();
                this.gifMovie.setTime(this.mCurrentAnimationTime);
                this.gifMovie.draw(canvas2, 0.0f, 0.0f);
                invalidate();
            } else {
                canvas2.drawBitmap(this.mBitmap, this.matrix, null);
            }
            canvas.restore();
        }
    }

    private void updateAnimationTime() {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.mMovieStart == 0) {
            this.mMovieStart = uptimeMillis;
        }
        int duration = this.gifMovie.duration();
        if (duration == 0) {
            duration = 1000;
        }
        this.mCurrentAnimationTime = (int) ((uptimeMillis - this.mMovieStart) % ((long) duration));
    }

    public void setImageResource(int i) {
        setBitmap(BitmapFactory.decodeResource(getResources(), i));
    }

    public void setGifInputStream(InputStream inputStream) {
        this.matrix.reset();
        this.gifInputStream = inputStream;
        this.gifMovie = Movie.decodeStream(this.gifInputStream);
        this.resourceWidth = this.gifMovie.width();
        this.resourceHeight = this.gifMovie.height();
        setDiagonalLength();
        initBitmaps();
        this.originWidth = (float) this.resourceWidth;
        StringBuilder sb = new StringBuilder();
        sb.append("scale = ");
        sb.append(this.scale);
        sb.append(" rotate = ");
        sb.append(this.rotate);
        sb.append(" x ");
        sb.append(this.layoutX);
        sb.append(" y ");
        sb.append(this.layoutY);
        this.matrix.setRotate(this.rotate, (float) (this.resourceWidth / 2), (float) (this.resourceHeight / 2));
        Matrix matrix2 = this.matrix;
        float f = this.scale;
        matrix2.postScale(f, f, (float) (this.resourceWidth / 2), (float) (this.resourceHeight / 2));
        this.matrix.postTranslate(this.layoutX - ((float) (this.resourceWidth / 2)), this.layoutY - ((float) (this.resourceHeight / 2)));
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        this.matrix.reset();
        this.mBitmap = bitmap;
        this.resourceWidth = this.mBitmap.getWidth();
        this.resourceHeight = this.mBitmap.getHeight();
        setDiagonalLength();
        initBitmaps();
        this.originWidth = (float) this.resourceWidth;
        StringBuilder sb = new StringBuilder();
        sb.append("scale = ");
        sb.append(this.scale);
        sb.append(" rotate = ");
        sb.append(this.rotate);
        sb.append(" x ");
        sb.append(this.layoutX);
        sb.append(" y ");
        sb.append(this.layoutY);
        this.matrix.setRotate(this.rotate, (float) (this.resourceWidth / 2), (float) (this.resourceHeight / 2));
        Matrix matrix2 = this.matrix;
        float f = this.scale;
        matrix2.postScale(f, f, (float) (this.resourceWidth / 2), (float) (this.resourceHeight / 2));
        this.matrix.postTranslate(this.layoutX - ((float) (this.resourceWidth / 2)), this.layoutY - ((float) (this.resourceHeight / 2)));
        invalidate();
    }

    private void setDiagonalLength() {
        this.halfDiagonalLength = Math.hypot((double) this.resourceWidth, (double) this.resourceHeight) / 2.0d;
    }

    private void initBitmaps() {
        int i = this.resourceWidth;
        int i2 = this.resourceHeight;
        if (i >= i2) {
            float f = (float) ((this.mScreenWidth * 10) / 100);
            if (((float) i) < f) {
                this.MIN_SCALE = 1.0f;
            } else {
                this.MIN_SCALE = (f) / ((float) i);
            }
            int i3 = this.resourceWidth;
            int i4 = this.mScreenWidth;
            if (i3 > i4) {
                this.MAX_SCALE = 1.0f;
            } else {
                this.MAX_SCALE = (((float) i4)) / ((float) i3);
            }
        } else {
            float f2 = (float) ((this.mScreenWidth * 10) / 100);
            if (((float) i2) < f2) {
                this.MIN_SCALE = 1.0f;
            } else {
                this.MIN_SCALE = (f2) / ((float) i2);
            }
            int i5 = this.resourceHeight;
            int i6 = this.mScreenWidth;
            if (i5 > i6) {
                this.MAX_SCALE = 1.0f;
            } else {
                this.MAX_SCALE = (((float) i6)) / ((float) i5);
            }
        }
        this.topBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_top_enable);
        this.deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_delete);
        this.flipVBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_flip);
        this.resizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_resize);
        this.deleteBitmapWidth = (int) (((float) this.deleteBitmap.getWidth()) * BITMAP_SCALE);
        this.deleteBitmapHeight = (int) (((float) this.deleteBitmap.getHeight()) * BITMAP_SCALE);
        this.resizeBitmapWidth = (int) (((float) this.resizeBitmap.getWidth()) * BITMAP_SCALE);
        this.resizeBitmapHeight = (int) (((float) this.resizeBitmap.getHeight()) * BITMAP_SCALE);
        this.flipVBitmapWidth = (int) (((float) this.flipVBitmap.getWidth()) * BITMAP_SCALE);
        this.flipVBitmapHeight = (int) (((float) this.flipVBitmap.getHeight()) * BITMAP_SCALE);
        this.topBitmapWidth = (int) (((float) this.topBitmap.getWidth()) * BITMAP_SCALE);
        this.topBitmapHeight = (int) (((float) this.topBitmap.getHeight()) * BITMAP_SCALE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isInButton(event, dst_delete)) {
                    if (operationListener != null) {
                        operationListener.onDeleteClick();
                    }
                } else if (isInResize(event)) {
                    isInResize = true;
                    lastRotateDegree = rotationToStartPoint(event);
                    midPointToStartPoint(event);
                    lastLength = diagonalLength(event);
                } else if (isInButton(event, dst_flipV)) {
                    PointF localPointF = new PointF();
                    midDiagonalPoint(localPointF);
                    matrix.postScale(-1.0F, 1.0F, localPointF.x, localPointF.y);
                    isHorizonMirror = !isHorizonMirror;
                    invalidate();
                } else if (isInButton(event, dst_top)) {
                    bringToFront();
                    if (operationListener != null) {
                        operationListener.onTop(this);
                    }
                } else if (isInBitmap(event)) {
                    isInSide = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                } else {
                    handled = false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event);
                    isPointerDown = true;
                    midPointToStartPoint(event);
                } else {
                    isPointerDown = false;
                }
                isInSide = false;
                isInResize = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPointerDown) {
                    float scale;
                    float disNew = spacing(event);

                    if (disNew == 0 || disNew < pointerLimitDis) {
                        scale = 1;
                    } else {
                        scale = disNew / oldDis;
                        scale = (scale - 1) * pointerZoomOff + 1;
                    }
                    float scaleTemp = (scale * Math.abs(dst_flipV.left - dst_resize.left)) / originWidth;
                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
                            (scaleTemp >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                    } else {
                        lastLength = diagonalLength(event);
                    }
                    matrix.postScale(scale, scale, mid.x, mid.y);
                    invalidate();
                } else if (isInResize) {

                    matrix.postRotate((rotationToStartPoint(event) - lastRotateDegree) * 2, mid.x, mid.y);
                    lastRotateDegree = rotationToStartPoint(event);

                    float scale = diagonalLength(event) / lastLength;

                    if (((diagonalLength(event) / halfDiagonalLength <= MIN_SCALE)) && scale < 1 ||
                            (diagonalLength(event) / halfDiagonalLength >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                        if (!isInResize(event)) {
                            isInResize = false;
                        }
                    } else {
                        lastLength = diagonalLength(event);
                    }
                    matrix.postScale(scale, scale, mid.x, mid.y);

                    invalidate();
                } else if (isInSide) {
                    float x = event.getX(0);
                    float y = event.getY(0);
                    matrix.postTranslate(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isInResize = false;
                isInSide = false;
                isPointerDown = false;
                break;

        }
        if (handled && operationListener != null) {
            operationListener.onEdit(this);
        }
        return handled;
    }

    public void calculate() {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        float f = fArr[2];
        float f2 = fArr[5];
        String sb2 = "tx : " +
                f +
                " ty : " +
                f2;
        String str = TAG;
        float f3 = fArr[0];
        float f4 = fArr[3];
        float sqrt = (float) Math.sqrt((double) ((f3 * f3) + (f4 * f4)));
        StringBuilder sb3 = new StringBuilder();
        sb3.append("rScale : ");
        sb3.append(sqrt);
        float round = (float) Math.round(Math.atan2((double) fArr[1], (double) fArr[0]) * 57.29577951308232d);
        StringBuilder sb4 = new StringBuilder();
        sb4.append("rAngle : ");
        sb4.append(round);
        PointF pointF = new PointF();
        midDiagonalPoint(pointF);
        StringBuilder sb5 = new StringBuilder();
        sb5.append(" width  : ");
        sb5.append(((float) this.resourceWidth) * sqrt);
        sb5.append(" height ");
        sb5.append(((float) this.resourceHeight) * sqrt);
        float f5 = pointF.x;
        float f6 = pointF.y;
        StringBuilder sb6 = new StringBuilder();
        sb6.append("midX : ");
        sb6.append(f5);
        sb6.append(" midY : ");
        sb6.append(f6);
        this.rotate = round * -1.0f;
        int i = this.resourceWidth;
        int i2 = this.mScreenWidth;
        this.scale = sqrt;
        this.layoutX = f5;
        this.layoutY = f6;
    }

    private boolean isInBitmap(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        float f = (fArr[0] * 0.0f) + (fArr[1] * 0.0f) + fArr[2];
        float f2 = (fArr[3] * 0.0f) + (fArr[4] * 0.0f) + fArr[5];
        float f3 = fArr[0];
        int i = this.resourceWidth;
        float f4 = (f3 * ((float) i)) + (fArr[1] * 0.0f) + fArr[2];
        float f5 = (fArr[3] * ((float) i)) + (fArr[4] * 0.0f) + fArr[5];
        float f6 = fArr[0] * 0.0f;
        float f7 = fArr[1];
        int i2 = this.resourceHeight;
        float f8 = (fArr[3] * 0.0f) + (fArr[4] * ((float) i2)) + fArr[5];
        float f9 = (fArr[0] * ((float) i)) + (fArr[1] * ((float) i2)) + fArr[2];
        float f10 = (fArr[3] * ((float) i)) + (fArr[4] * ((float) i2)) + fArr[5];
        return pointInRect(new float[]{f, f4, f9, f6 + (f7 * ((float) i2)) + fArr[2]}, new float[]{f2, f5, f10, f8}, motionEvent2.getX(0), motionEvent2.getY(0));
    }

    private boolean pointInRect(float[] fArr, float[] fArr2, float f, float f2) {
        double hypot = Math.hypot((double) (fArr[0] - fArr[1]), (double) (fArr2[0] - fArr2[1]));
        double hypot2 = Math.hypot((double) (fArr[1] - fArr[2]), (double) (fArr2[1] - fArr2[2]));
        double hypot3 = Math.hypot((double) (fArr[3] - fArr[2]), (double) (fArr2[3] - fArr2[2]));
        double hypot4 = Math.hypot((double) (fArr[0] - fArr[3]), (double) (fArr2[0] - fArr2[3]));
        double hypot5 = Math.hypot((double) (f - fArr[0]), (double) (f2 - fArr2[0]));
        double d = hypot;
        double hypot6 = Math.hypot((double) (f - fArr[1]), (double) (f2 - fArr2[1]));
        double hypot7 = Math.hypot((double) (f - fArr[2]), (double) (f2 - fArr2[2]));
        double hypot8 = Math.hypot((double) (f - fArr[3]), (double) (f2 - fArr2[3]));
        double d2 = ((d + hypot5) + hypot6) / 2.0d;
        double d3 = ((hypot2 + hypot6) + hypot7) / 2.0d;
        double d4 = ((hypot3 + hypot7) + hypot8) / 2.0d;
        double d5 = ((hypot4 + hypot8) + hypot5) / 2.0d;
        return Math.abs((d * hypot2) - (((Math.sqrt((((d2 - d) * d2) * (d2 - hypot5)) * (d2 - hypot6)) + Math.sqrt((((d3 - hypot2) * d3) * (d3 - hypot6)) * (d3 - hypot7))) + Math.sqrt((((d4 - hypot3) * d4) * (d4 - hypot7)) * (d4 - hypot8))) + Math.sqrt((((d5 - hypot4) * d5) * (d5 - hypot8)) * (d5 - hypot5)))) < 0.5d;
    }

    private boolean isInButton(MotionEvent motionEvent, Rect rect) {
        int i = rect.left;
        int i2 = rect.right;
        int i3 = rect.top;
        int i4 = rect.bottom;
        return !(motionEvent.getX(0) < ((float) i)) && !(motionEvent.getX(0) > ((float) i2)) && !(motionEvent.getY(0) < ((float) i3)) && !(motionEvent.getY(0) > ((float) i4));
    }

    private boolean isInResize(MotionEvent motionEvent) {
        int i = this.dst_resize.top - 20;
        int i2 = this.dst_resize.right + 20;
        int i3 = this.dst_resize.bottom + 20;
        return !(motionEvent.getX(0) < ((float) (this.dst_resize.left - 20))) && !(motionEvent.getX(0) > ((float) i2)) && !(motionEvent.getY(0) < ((float) i)) && !(motionEvent.getY(0) > ((float) i3));
    }

    private void midPointToStartPoint(MotionEvent motionEvent) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        this.mid.set(((((fArr[0] * 0.0f) + (fArr[1] * 0.0f)) + fArr[2]) + motionEvent.getX(0)) / 2.0f, ((((fArr[3] * 0.0f) + (fArr[4] * 0.0f)) + fArr[5]) + motionEvent.getY(0)) / 2.0f);
    }

    private void midDiagonalPoint(PointF pointF) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        float f = (fArr[0] * 0.0f) + (fArr[1] * 0.0f) + fArr[2];
        float f2 = (fArr[3] * 0.0f) + (fArr[4] * 0.0f) + fArr[5];
        float f3 = fArr[0];
        int i = this.resourceWidth;
        float f4 = f3 * ((float) i);
        float f5 = fArr[1];
        int i2 = this.resourceHeight;
        pointF.set((f + ((f4 + (f5 * ((float) i2))) + fArr[2])) / 2.0f, (f2 + (((fArr[3] * ((float) i)) + (fArr[4] * ((float) i2))) + fArr[5])) / 2.0f);
    }

    private float rotationToStartPoint(MotionEvent motionEvent) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        return (float) Math.toDegrees(Math.atan2((double) (motionEvent.getY(0) - (((fArr[3] * 0.0f) + (fArr[4] * 0.0f)) + fArr[5])), (double) (motionEvent.getX(0) - (((fArr[0] * 0.0f) + (fArr[1] * 0.0f)) + fArr[2]))));
    }

    private float diagonalLength(MotionEvent motionEvent) {
        return (float) Math.hypot((double) (motionEvent.getX(0) - this.mid.x), (double) (motionEvent.getY(0) - this.mid.y));
    }

    private float spacing(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() != 2) {
            return 0.0f;
        }
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    public void setOperationListener(OperationListener operationListener2) {
        this.operationListener = operationListener2;
    }

    public void setInEdit(boolean z) {
        this.isInEdit = z;
        invalidate();
    }

    public int getStickerId() {
        return this.stickerId;
    }

    public String getStickerPath() {
        return this.stickerPath;
    }

    public float getLayoutX() {
        return this.layoutX;
    }

    public float getLayoutY() {
        return this.layoutY;
    }

    public float getScale() {
        return this.scale;
    }

    public float getRotate() {
        return this.rotate;
    }

    public boolean isGif() {
        return this.isGif;
    }

    public void setGif(boolean z) {
        this.isGif = z;
    }
}

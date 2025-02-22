package com.maxvidzgallery.storymaker.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.interfaces.HorizontallyScrollable;

public class PhotoView extends AppCompatImageView implements OnGestureListener, OnDoubleTapListener, OnScaleGestureListener, HorizontallyScrollable{

    private static final float CROPPED_SIZE = 256.0f;
    private static final long SNAP_DELAY = 250;
    private static final float SNAP_THRESHOLD = 20.0f;
    private static Paint sCropDimPaint;
    private static Paint sCropPaint;
    private static int sCropSize;
    private static boolean sInitialized;
    private static Bitmap sVideoImage;
    private static Bitmap sVideoNotReadyImage;
    private boolean isCenterCropScaleType;
    private boolean mAllowCrop;
    private final Rect mCropRect = new Rect();
    private int mCropSize;
    private boolean mDoubleTapDebounce;
    private Matrix mDrawMatrix;
    private BitmapDrawable mDrawable;
    private OnClickListener mExternalClickListener;
    private int mFixedHeight = -1;
    private boolean mFullScreen;
    private GestureDetectorCompat mGestureDetector;
    private boolean mHaveLayout;
    private boolean mIsDoubleTouch;
    private final Matrix mMatrix = new Matrix();
    private float mMaxInitialScaleFactor;
    private float mMaxScale;
    private float mMinScale;
    private OnLongClickListener mOnLongClickListener;
    private final Matrix mOriginalMatrix = new Matrix();
    private RotateRunnable mRotateRunnable;
    private float mRotation;
    private ScaleGestureDetector mScaleGestureDetector;
    private ScaleRunnable mScaleRunnable;
    private SnapRunnable mSnapRunnable;
    private final RectF mTempDst = new RectF();
    private final RectF mTempSrc = new RectF();
    private boolean mTransformsEnabled;
    private final RectF mTranslateRect = new RectF();
    private TranslateRunnable mTranslateRunnable;
    private final float[] mValues = new float[9];
    private byte[] mVideoBlob;
    private boolean mVideoReady;
    private String photoPath;
    private int photoRotation;

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 3;

    private int mode = NONE;

    private final PointF mLastTouch = new PointF();
    private final PointF mStartTouch = new PointF();
    private final float minScale = 0.5f;
    private final float maxScale = 4f;
    private float[] mCriticPoints;

    private float mScale = 1f;
    private float mRight;
    private float mBottom;
    private float mOriginalBitmapWidth;
    private float mOriginalBitmapHeight;

    private static class RotateRunnable implements Runnable {
        private float mAppliedRotation;
        private final PhotoView mHeader;
        private long mLastRuntime;
        private boolean mRunning;
        private boolean mStop;
        private float mTargetRotation;
        private float mVelocity;

        public RotateRunnable(PhotoView photoView) {
            this.mHeader = photoView;
        }

        public void start(float f) {
            if (!this.mRunning) {
                this.mTargetRotation = f;
                this.mVelocity = this.mTargetRotation / 500.0f;
                this.mAppliedRotation = 0.0f;
                this.mLastRuntime = -1;
                this.mStop = false;
                this.mRunning = true;
                this.mHeader.post(this);
            }
        }

        public void stop() {
            this.mRunning = false;
            this.mStop = true;
        }

        public void run() {
            if (!this.mStop) {
                if (this.mAppliedRotation != this.mTargetRotation) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long j = this.mLastRuntime;
                    float f = this.mVelocity * ((float) (j != -1 ? currentTimeMillis - j : 0));
                    float f2 = this.mAppliedRotation;
                    float f3 = this.mTargetRotation;
                    if (f2 >= f3 || f2 + f <= f3) {
                        float f4 = this.mAppliedRotation;
                        float f5 = this.mTargetRotation;
                        if (f4 > f5) {
                        }
                        this.mHeader.rotate(f, false);
                        this.mAppliedRotation += f;
                        if (this.mAppliedRotation == this.mTargetRotation) {
                            stop();
                        }
                        this.mLastRuntime = currentTimeMillis;
                    }
                    f = this.mTargetRotation - this.mAppliedRotation;
                    this.mHeader.rotate(f, false);
                    this.mAppliedRotation += f;
                    if (this.mAppliedRotation == this.mTargetRotation) {
                    }
                    this.mLastRuntime = currentTimeMillis;
                }
                if (!this.mStop) {
                    this.mHeader.post(this);
                }
            }
        }
    }

    private static class ScaleRunnable implements Runnable {
        private float mCenterX;
        private float mCenterY;
        private final PhotoView mHeader;
        private boolean mRunning;
        private float mStartScale;
        private long mStartTime;
        private boolean mStop;
        private float mTargetScale;
        private float mVelocity;
        private boolean mZoomingIn;

        public ScaleRunnable(PhotoView photoView) {
            this.mHeader = photoView;
        }

        public boolean start(float f, float f2, float f3, float f4) {
            if (this.mRunning) {
                return false;
            }
            this.mCenterX = f3;
            this.mCenterY = f4;
            this.mTargetScale = f2;
            this.mStartTime = System.currentTimeMillis();
            this.mStartScale = f;
            this.mZoomingIn = this.mTargetScale > this.mStartScale;
            this.mVelocity = (this.mTargetScale - this.mStartScale) / 300.0f;
            this.mRunning = true;
            this.mStop = false;
            this.mHeader.post(this);
            return true;
        }

        public void stop() {
            this.mRunning = false;
            this.mStop = true;
        }

        public void run() {
            if (!this.mStop) {
                float currentTimeMillis = this.mStartScale + (this.mVelocity * ((float) (System.currentTimeMillis() - this.mStartTime)));
                this.mHeader.scale(currentTimeMillis, this.mCenterX, this.mCenterY);
                float f = this.mTargetScale;
                if (currentTimeMillis != f) {
                }
                this.mHeader.scale(this.mTargetScale, this.mCenterX, this.mCenterY);
                stop();
                if (!this.mStop) {
                    this.mHeader.post(this);
                }
            }
        }
    }

    private static class SnapRunnable implements Runnable {

        private final PhotoView mHeader;
        private boolean mRunning;
        private long mStartRunTime = -1;
        private boolean mStop;
        private float mTranslateX;
        private float mTranslateY;

        public SnapRunnable(PhotoView photoView) {
            this.mHeader = photoView;
        }

        public boolean start(float f, float f2) {
            if (this.mRunning) {
                return false;
            }
            this.mStartRunTime = -1;
            this.mTranslateX = f;
            this.mTranslateY = f2;
            this.mStop = false;
            this.mRunning = true;
            this.mHeader.postDelayed(this, PhotoView.SNAP_DELAY);
            return true;
        }

        public void stop() {
            this.mRunning = false;
            this.mStop = true;
        }

        public void run() {
            float f;
            float f2;
            if (!this.mStop) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = this.mStartRunTime;
                float f3 = j != -1 ? (float) (currentTimeMillis - j) : 0.0f;
                if (this.mStartRunTime == -1) {
                    this.mStartRunTime = currentTimeMillis;
                }
                if (f3 >= 100.0f) {
                    f = this.mTranslateX;
                    f2 = this.mTranslateY;
                } else {
                    float f4 = 100.0f - f3;
                    f = (this.mTranslateX / f4) * 10.0f;
                    f2 = (this.mTranslateY / f4) * 10.0f;
                    if (Math.abs(f) > Math.abs(this.mTranslateX) || f == Float.NaN) {
                        f = this.mTranslateX;
                    }
                    if (Math.abs(f2) > Math.abs(this.mTranslateY) || f2 == Float.NaN) {
                        f2 = this.mTranslateY;
                    }
                }
                this.mHeader.translate(f, f2);
                this.mTranslateX -= f;
                this.mTranslateY -= f2;
                if (this.mTranslateX == 0.0f && this.mTranslateY == 0.0f) {
                    stop();
                }
                if (!this.mStop) {
                    this.mHeader.post(this);
                }
            }
        }
    }

    private static class TranslateRunnable implements Runnable {
        private static final float DECELERATION_RATE = 1000.0f;
        private final PhotoView mHeader;
        private long mLastRunTime = -1;

        private boolean mRunning;
        private boolean mStop;
        private float mVelocityX;
        private float mVelocityY;

        public TranslateRunnable(PhotoView photoView) {
            this.mHeader = photoView;
        }

        public boolean start(float f, float f2) {
            if (this.mRunning) {
                return false;
            }
            this.mLastRunTime = -1;
            this.mVelocityX = f;
            this.mVelocityY = f2;
            this.mStop = false;
            this.mRunning = true;
            this.mHeader.post(this);
            return true;
        }

        public void stop() {
            this.mRunning = false;
            this.mStop = true;
        }

        public void run() {
            if (!this.mStop) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = this.mLastRunTime;
                float f = j != -1 ? ((float) (currentTimeMillis - j)) / DECELERATION_RATE : 0.0f;
                boolean translate = this.mHeader.translate(this.mVelocityX * f, this.mVelocityY * f);
                this.mLastRunTime = currentTimeMillis;
                float f2 = f * DECELERATION_RATE;
                float f3 = this.mVelocityX;
                if (f3 > 0.0f) {
                    this.mVelocityX = f3 - f2;
                    if (this.mVelocityX < 0.0f) {
                        this.mVelocityX = 0.0f;
                    }
                } else {
                    this.mVelocityX = f3 + f2;
                    if (this.mVelocityX > 0.0f) {
                        this.mVelocityX = 0.0f;
                    }
                }
                float f4 = this.mVelocityY;
                if (f4 > 0.0f) {
                    this.mVelocityY = f4 - f2;
                    if (this.mVelocityY < 0.0f) {
                        this.mVelocityY = 0.0f;
                    }
                } else {
                    this.mVelocityY = f4 + f2;
                    if (this.mVelocityY > 0.0f) {
                        this.mVelocityY = 0.0f;
                    }
                }
                if ((this.mVelocityX == 0.0f && this.mVelocityY == 0.0f) || !translate) {
                    stop();
                    this.mHeader.snap();
                }
                if (!this.mStop) {
                    this.mHeader.post(this);
                }
            }
        }
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {}

    public void onShowPress(MotionEvent motionEvent) {}

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public PhotoView(Context context) {
        super(context);
        init(context);
        initialize();
    }

    public PhotoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
        initialize();
    }

    public PhotoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
        initialize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        if (this.mScaleGestureDetector != null) {
            GestureDetectorCompat gestureDetectorCompat = this.mGestureDetector;
            if (gestureDetectorCompat != null) {
                gestureDetectorCompat.onTouchEvent(event);
                mMatrix.getValues(mCriticPoints);
                float translateX = mCriticPoints[Matrix.MTRANS_X];
                float trnslateY = mCriticPoints[Matrix.MTRANS_Y];
                PointF currentPoint = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    
                    
                    case MotionEvent.ACTION_DOWN:
                        mLastTouch.set(event.getX(), event.getY());
                        mStartTouch.set(mLastTouch);
                        mode = DRAG;
                        break;
                    
                    
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mLastTouch.set(event.getX(), event.getY());
                        mStartTouch.set(mLastTouch);
                        mode = ZOOM;
                        break;
                    
                    
                    case MotionEvent.ACTION_MOVE:
                        
                        
                        if (mode == ZOOM || (mode == DRAG && mScale > minScale)) {

                            

                            float deltaX = currentPoint.x - mLastTouch.x;
                            float deltaY = currentPoint.y - mLastTouch.y;
                            float scaleWidth = Math.round(mOriginalBitmapWidth * mScale);
                            float scaleHeight = Math.round(mOriginalBitmapHeight * mScale);

                            
                            if (scaleWidth > getWidth()) {
                                if (translateX + deltaX > 0) {
                                    deltaX = -translateX;
                                } else if (translateX + deltaX < -mRight) {
                                    deltaX = -(translateX + mRight);
                                }
                            } else {
                                deltaX = 0;
                            }
                            
                            if (scaleHeight > getHeight()) {
                                if (trnslateY + deltaY > 0) {
                                    deltaY = -trnslateY;
                                } else if (trnslateY + deltaY < -mBottom) {
                                    deltaY = -(trnslateY + mBottom);
                                }
                            } else {
                                deltaY = 0;
                            }

                            
                            mMatrix.postTranslate(deltaX, deltaY);
                            
                            mLastTouch.set(currentPoint.x, currentPoint.y);

                            
                        }
                        break;
                    
                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        int xDiff = (int) Math.abs(currentPoint.x - mStartTouch.x);
                        int yDiff = (int) Math.abs(currentPoint.y - mStartTouch.y);
                        if (xDiff < CLICK && yDiff < CLICK)
                            performClick();
                        break;
                    
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }
                setImageMatrix(mMatrix);
                invalidate();
            }
        }
        return true;
    }

    private void init(Context context) {
        super.setClickable(true);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mCriticPoints = new float[9];
        setImageMatrix(mMatrix);
        setScaleType(ScaleType.MATRIX);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = mScale * scaleFactor;
            if (newScale < maxScale && newScale > minScale) {
                mScale = newScale;
                float width = getWidth();
                float height = getHeight();
                mRight = (mOriginalBitmapWidth * mScale) - width;
                mBottom = (mOriginalBitmapHeight * mScale) - height;

                float scaledBitmapWidth = mOriginalBitmapWidth * mScale;
                float scaledBitmapHeight = mOriginalBitmapHeight * mScale;

                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    mMatrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                } else {
                    mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                }
            }
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bmHeight = getBmHeight();
        int bmWidth = getBmWidth();

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        float scale = 1;

        
        if (width < bmWidth || height < bmHeight) {
            scale = width > height ? height / bmHeight : width / bmWidth;
        }

        mMatrix.setScale(scale, scale);
        mScale = 1f;

        mOriginalBitmapWidth = scale * bmWidth;
        mOriginalBitmapHeight = scale * bmHeight;

        Log.e("scale","==>"+scale);

        Log.e("height","==>"+height);
        Log.e("width","==>"+width);

        Log.e("mOriginalBitmapWidth","==>"+mOriginalBitmapWidth);
        Log.e("mOriginalBitmapHeight","==>"+mOriginalBitmapHeight);

        
        float redundantYSpace = (height - mOriginalBitmapHeight);
        float redundantXSpace = (width - mOriginalBitmapWidth);

        Log.e("redundantYSpace","==>"+redundantYSpace);
        Log.e("redundantXSpace","==>"+redundantXSpace);

        mMatrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        Log.e("mMatrix","==>"+mMatrix);

        setImageMatrix(mMatrix);
    }

    private int getBmWidth() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    private int getBmHeight() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }

    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        OnClickListener onClickListener = this.mExternalClickListener;
        if (onClickListener != null && !this.mIsDoubleTouch) {
            onClickListener.onClick(this);
        }
        this.mIsDoubleTouch = false;
        return true;
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.mTransformsEnabled) {
            translate(-f, -f2);
        }
        return true;
    }

    public boolean onDown(MotionEvent motionEvent) {
        if (this.mTransformsEnabled) {
            this.mTranslateRunnable.stop();
            this.mSnapRunnable.stop();
        }
        return true;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.mTransformsEnabled) {
            this.mTranslateRunnable.start(f, f2);
        }
        return true;
    }

    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (this.mTransformsEnabled) {
            this.mIsDoubleTouch = false;
            scale(getScale() * scaleGestureDetector.getScaleFactor(), scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        }
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        if (this.mTransformsEnabled) {
            this.mScaleRunnable.stop();
            this.mIsDoubleTouch = true;
        }
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        if (this.mTransformsEnabled && this.mIsDoubleTouch) {
            this.mDoubleTapDebounce = true;
            resetTransformations();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mExternalClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mOnLongClickListener = onLongClickListener;
    }

    public boolean interceptMoveLeft(float f, float f2) {
        if (!this.mTransformsEnabled) {
            return false;
        }
        if (this.mTranslateRunnable.mRunning) {
            return true;
        }
        this.mMatrix.getValues(this.mValues);
        this.mTranslateRect.set(this.mTempSrc);
        this.mMatrix.mapRect(this.mTranslateRect);
        float width = (float) getWidth();
        float f3 = this.mValues[2];
        float f4 = this.mTranslateRect.right - this.mTranslateRect.left;
        if (!this.mTransformsEnabled || f4 <= width || f3 == 0.0f) {
            return false;
        }
        if (width >= f4 + f3) {}
        return true;
    }

    public boolean interceptMoveRight(float f, float f2) {
        if (!this.mTransformsEnabled) {
            return false;
        }
        if (this.mTranslateRunnable.mRunning) {
            return true;
        }
        this.mMatrix.getValues(this.mValues);
        this.mTranslateRect.set(this.mTempSrc);
        this.mMatrix.mapRect(this.mTranslateRect);
        float width = (float) getWidth();
        float f3 = this.mValues[2];
        float f4 = this.mTranslateRect.right - this.mTranslateRect.left;
        if (!this.mTransformsEnabled || f4 <= width) {
            return false;
        }
        return f3 == 0.0f || !(width >= f4 + f3);
    }

    public void clear() {
        this.mGestureDetector = null;
        this.mScaleGestureDetector = null;
        this.mDrawable = null;
        this.mScaleRunnable.stop();
        this.mScaleRunnable = null;
        this.mTranslateRunnable.stop();
        this.mTranslateRunnable = null;
        this.mSnapRunnable.stop();
        this.mSnapRunnable = null;
        this.mRotateRunnable.stop();
        this.mRotateRunnable = null;
        setOnClickListener(null);
        this.mExternalClickListener = null;
    }

    public void bindPhoto(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = this.mDrawable;
        boolean z = false;
        if (bitmapDrawable != null) {
            if (bitmap != bitmapDrawable.getBitmap()) {
                if (!(bitmap == null || (this.mDrawable.getIntrinsicWidth() == bitmap.getWidth() && this.mDrawable.getIntrinsicHeight() == bitmap.getHeight()))) {
                    z = true;
                }
                this.mMinScale = 0.0f;
                this.mDrawable = null;
            } else {
                return;
            }
        }
        if (this.mDrawable == null && bitmap != null) {
            this.mDrawable = new BitmapDrawable(getResources(), bitmap);
        }
        configureBounds(z);
        invalidate();
    }

    public Bitmap getPhoto() {
        BitmapDrawable bitmapDrawable = this.mDrawable;
        if (bitmapDrawable != null) {
            return bitmapDrawable.getBitmap();
        }
        return null;
    }

    public byte[] getVideoData() {
        return this.mVideoBlob;
    }

    public boolean isVideo() {
        return this.mVideoBlob != null;
    }

    public boolean isVideoReady() {
        return this.mVideoBlob != null && this.mVideoReady;
    }

    public boolean isPhotoBound() {
        return this.mDrawable != null;
    }

    public void setFullScreen(boolean z, boolean z2) {
        if (z != this.mFullScreen) {
            this.mFullScreen = z;
            requestLayout();
            invalidate();
        }
    }

    public void enableAllowCrop(boolean z) {
        if (z && this.mHaveLayout) {
            throw new IllegalArgumentException("Cannot set crop after view has been laid out");
        } else if (z || !this.mAllowCrop) {
            this.mAllowCrop = z;
        } else {
            throw new IllegalArgumentException("Cannot unset crop mode");
        }
    }

    public Bitmap getCroppedPhoto() {
        if (!this.mAllowCrop) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        float f = CROPPED_SIZE / ((float) (this.mCropRect.right - this.mCropRect.left));
        Matrix matrix = new Matrix(this.mDrawMatrix);
        matrix.postTranslate((float) (-this.mCropRect.left), (float) (-this.mCropRect.top));
        matrix.postScale(f, f);
        if (this.mDrawable != null) {
            canvas.concat(matrix);
            this.mDrawable.draw(canvas);
        }
        return createBitmap;
    }

    public void resetTransformations() {
        this.mMatrix.set(this.mOriginalMatrix);
        invalidate();
    }

    public void rotateClockwise() {
        rotate(90.0f, true);
    }

    public void rotateCounterClockwise() {
        rotate(-90.0f, true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawable != null) {
            int saveCount = canvas.getSaveCount();
            canvas.save();
            Matrix matrix = this.mDrawMatrix;
            if (matrix != null) {
                canvas.concat(matrix);
            }
            this.mDrawable.draw(canvas);
            canvas.restoreToCount(saveCount);
            if (this.mVideoBlob != null) {
                Bitmap bitmap = this.mVideoReady ? sVideoImage : sVideoNotReadyImage;
                canvas.drawBitmap(bitmap, (float) ((getWidth() - bitmap.getWidth()) / 2), (float) ((getHeight() - bitmap.getHeight()) / 2), null);
            }
            this.mTranslateRect.set(this.mDrawable.getBounds());
            Matrix matrix2 = this.mDrawMatrix;
            if (matrix2 != null) {
                matrix2.mapRect(this.mTranslateRect);
            }
            if (this.mAllowCrop) {
                int saveCount2 = canvas.getSaveCount();
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), sCropDimPaint);
                canvas.save();
                canvas.clipRect(this.mCropRect);
                Matrix matrix3 = this.mDrawMatrix;
                if (matrix3 != null) {
                    canvas.concat(matrix3);
                }
                this.mDrawable.draw(canvas);
                canvas.restoreToCount(saveCount2);
                canvas.drawRect(this.mCropRect, sCropPaint);
            }
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mHaveLayout = true;
        int width = getWidth();
        int height = getHeight();
        if (this.mAllowCrop) {
            this.mCropSize = Math.min(sCropSize, Math.min(width, height));
            int i5 = this.mCropSize;
            int i6 = (width - i5) / 2;
            int i7 = (height - i5) / 2;
            this.mCropRect.set(i6, i7, i6 + i5, i5 + i7);
        }
        configureBounds(z);
    }

    public void setFixedHeight(int i) {
        boolean z = i != this.mFixedHeight;
        this.mFixedHeight = i;
        setMeasuredDimension(getMeasuredWidth(), this.mFixedHeight);
        if (z) {
            configureBounds(true);
            requestLayout();
        }
    }

    public void enableImageTransforms(boolean z) {
        this.mTransformsEnabled = z;
        if (!this.mTransformsEnabled) {
            resetTransformations();
        }
    }

    private void configureBounds(boolean z) {
        BitmapDrawable bitmapDrawable = this.mDrawable;
        if (bitmapDrawable != null && this.mHaveLayout) {
            int intrinsicWidth = bitmapDrawable.getIntrinsicWidth();
            int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
            boolean z2 = (intrinsicWidth < 0 || getWidth() == intrinsicWidth) && (intrinsicHeight < 0 || getHeight() == intrinsicHeight);
            this.mDrawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
            if (z || (this.mMinScale == 0.0f && this.mDrawable != null && this.mHaveLayout)) {
                generateMatrix();
                generateScale();
            }
            if (z2 || this.mMatrix.isIdentity()) {
                this.mDrawMatrix = null;
            } else {
                this.mDrawMatrix = this.mMatrix;
            }
        }
    }

    private void generateMatrix() {
        float f;
        float f2;
        int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        int width = this.mAllowCrop ? sCropSize : getWidth();
        int height = this.mAllowCrop ? sCropSize : getHeight();
        float f3 = 0.0f;
        if (!((intrinsicWidth < 0 || width == intrinsicWidth) && (intrinsicHeight < 0 || height == intrinsicHeight)) || this.mAllowCrop) {
            float f4 = (float) intrinsicWidth;
            float f5 = (float) intrinsicHeight;
            this.mTempSrc.set(0.0f, 0.0f, f4, f5);
            if (this.mAllowCrop) {
                this.mTempDst.set(this.mCropRect);
            } else {
                this.mTempDst.set(0.0f, 0.0f, (float) width, (float) height);
            }
            float f6 = (float) (width / 2);
            float f7 = this.mMaxInitialScaleFactor;
            float f8 = (float) (height / 2);
            RectF rectF = new RectF(f6 - ((f4 * f7) / 2.0f), f8 - ((f5 * f7) / 2.0f), f6 + ((f4 * f7) / 2.0f), f8 + ((f5 * f7) / 2.0f));
            if (this.mTempDst.contains(rectF)) {
                this.mMatrix.setRectToRect(this.mTempSrc, rectF, ScaleToFit.CENTER);
            } else {
                this.mMatrix.setRectToRect(this.mTempSrc, this.mTempDst, ScaleToFit.CENTER);
            }
        } else {
            this.mMatrix.reset();
        }
        if (this.isCenterCropScaleType) {
            if (intrinsicWidth * height > width * intrinsicHeight) {
                float f9 = ((float) height) / ((float) intrinsicHeight);
                f3 = (((float) width) - (((float) intrinsicWidth) * f9)) * 0.5f;
                f2 = f9;
                f = 0.0f;
            } else {
                f2 = ((float) width) / ((float) intrinsicWidth);
                f = (((float) height) - (((float) intrinsicHeight) * f2)) * 0.5f;
            }
            this.mMatrix.setScale(f2, f2);
            this.mMatrix.postTranslate((float) Math.round(f3), (float) Math.round(f));
        }
        this.mOriginalMatrix.set(this.mMatrix);
    }

    private void generateScale() {
        int intrinsicWidth = this.mDrawable.getIntrinsicWidth();
        int intrinsicHeight = this.mDrawable.getIntrinsicHeight();
        int cropSize = this.mAllowCrop ? getCropSize() : getWidth();
        int cropSize2 = this.mAllowCrop ? getCropSize() : getHeight();
        if (this.isCenterCropScaleType || intrinsicWidth >= cropSize || intrinsicHeight >= cropSize2 || this.mAllowCrop) {
            this.mMinScale = getScale();
        } else {
            this.mMinScale = 1.0f;
        }
        this.mMaxScale = Math.max(this.mMinScale * 8.0f, 8.0f);
    }

    private int getCropSize() {
        int i = this.mCropSize;
        return i > 0 ? i : sCropSize;
    }

    public float getScale() {
        this.mMatrix.getValues(this.mValues);
        return this.mValues[0];
    }

    private void scale(float f, float f2, float f3) {
        this.mMatrix.postRotate(-this.mRotation, (float) (getWidth() / 2), (float) (getHeight() / 2));
        float min = Math.min(Math.max(f, this.mMinScale), this.mMaxScale) / getScale();
        this.mMatrix.postScale(min, min, f2, f3);
        snap();
        this.mMatrix.postRotate(this.mRotation, (float) (getWidth() / 2), (float) (getHeight() / 2));
        invalidate();
    }

    private boolean translate(float f, float f2) {
        float f3;
        float f4;
        this.mTranslateRect.set(this.mTempSrc);
        this.mMatrix.mapRect(this.mTranslateRect);
        float f5 = 0.0f;
        float f6 = this.mAllowCrop ? (float) this.mCropRect.left : 0.0f;
        float width = (float) (this.mAllowCrop ? this.mCropRect.right : getWidth());
        float f7 = this.mTranslateRect.left;
        float f8 = this.mTranslateRect.right;
        if (this.mAllowCrop) {
            f3 = Math.max(f6 - this.mTranslateRect.right, Math.min(width - this.mTranslateRect.left, f));
        } else {
            float f9 = width - f6;
            f3 = f8 - f7 < f9 ? f6 + ((f9 - (f8 + f7)) / 2.0f) : Math.max(width - f8, Math.min(f6 - f7, f));
        }
        if (this.mAllowCrop) {
            f5 = (float) this.mCropRect.top;
        }
        float height = (float) (this.mAllowCrop ? this.mCropRect.bottom : getHeight());
        float f10 = this.mTranslateRect.top;
        float f11 = this.mTranslateRect.bottom;
        if (this.mAllowCrop) {
            f4 = Math.max(f5 - this.mTranslateRect.bottom, Math.min(height - this.mTranslateRect.top, f2));
        } else {
            float f12 = height - f5;
            f4 = f11 - f10 < f12 ? f5 + ((f12 - (f11 + f10)) / 2.0f) : Math.max(height - f11, Math.min(f5 - f10, f2));
        }
        this.mMatrix.postTranslate(f3, f4);
        invalidate();
        return f3 == f && f4 == f2;
    }

    private void snap() {
        this.mTranslateRect.set(this.mTempSrc);
        this.mMatrix.mapRect(this.mTranslateRect);
        float f = 0.0f;
        float f2 = this.mAllowCrop ? (float) this.mCropRect.left : 0.0f;
        float width = (float) (this.mAllowCrop ? this.mCropRect.right : getWidth());
        float f3 = this.mTranslateRect.left;
        float f4 = this.mTranslateRect.right;
        float f5 = width - f2;
        float f6 = f4 - f3 < f5 ? f2 + ((f5 - (f4 + f3)) / 2.0f) : f3 > f2 ? f2 - f3 : f4 < width ? width - f4 : 0.0f;
        float f7 = this.mAllowCrop ? (float) this.mCropRect.top : 0.0f;
        float height = (float) (this.mAllowCrop ? this.mCropRect.bottom : getHeight());
        float f8 = this.mTranslateRect.top;
        float f9 = this.mTranslateRect.bottom;
        float f10 = height - f7;
        if (f9 - f8 < f10) {
            f = f7 + ((f10 - (f9 + f8)) / 2.0f);
        } else if (f8 > f7) {
            f = f7 - f8;
        } else if (f9 < height) {
            f = height - f9;
        }
        if (Math.abs(f6) > SNAP_THRESHOLD || Math.abs(f) > SNAP_THRESHOLD) {
            this.mSnapRunnable.start(f6, f);
            return;
        }
        this.mMatrix.postTranslate(f6, f);
        invalidate();
    }

    private void rotate(float f, boolean z) {
        if (z) {
            this.mRotateRunnable.start(f);
            return;
        }
        this.mRotation += f;
        this.mMatrix.postRotate(f, (float) (getWidth() / 2), (float) (getHeight() / 2));
        invalidate();
    }

    private void initialize() {
        Context context = getContext();
        if (!sInitialized) {
            sInitialized = true;
            Resources resources = context.getApplicationContext().getResources();
            sCropSize = resources.getDimensionPixelSize(R.dimen.photo_crop_width);
            sCropDimPaint = new Paint();
            sCropDimPaint.setAntiAlias(true);
            sCropDimPaint.setColor(resources.getColor(R.color.colorTransparentBlack));
            sCropDimPaint.setStyle(Style.FILL);
            sCropPaint = new Paint();
            sCropPaint.setAntiAlias(true);
            sCropPaint.setColor(resources.getColor(R.color.colorWhite));
            sCropPaint.setStyle(Style.STROKE);
            sCropPaint.setStrokeWidth(resources.getDimension(R.dimen.photo_crop_stroke_width));
        }
        this.mGestureDetector = new GestureDetectorCompat(context, this, null);
        this.mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.mScaleRunnable = new ScaleRunnable(this);
        this.mTranslateRunnable = new TranslateRunnable(this);
        this.mSnapRunnable = new SnapRunnable(this);
        this.mRotateRunnable = new RotateRunnable(this);
    }

    public void setMaxInitialScale(float f) {
        this.mMaxInitialScaleFactor = f;
    }

    public boolean isCenterCropScaleType() {
        return this.isCenterCropScaleType;
    }

    public void setCenterCropScaleType(boolean z) {
        this.isCenterCropScaleType = z;
    }

    public void clearDrawable() {
        bindPhoto(null);
    }

    public String getPhotoPath() {
        return this.photoPath;
    }

    public void setPhotoPath(String str) {
        this.photoPath = str;
    }

    public int getPhotoRotation() {
        return this.photoRotation;
    }

    public void setPhotoRotation(int i) {
        this.photoRotation = i;
    }
}

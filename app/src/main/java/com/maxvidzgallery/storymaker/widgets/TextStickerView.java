package com.maxvidzgallery.storymaker.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.os.Build.VERSION;
import androidx.annotation.IntRange;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.models.Font;
import com.maxvidzgallery.storymaker.utils.BitmapUtil;
import com.maxvidzgallery.storymaker.utils.FontProvider;
import com.maxvidzgallery.storymaker.utils.RectUtil;

public class TextStickerView extends View {

    private static final int DELETE_MODE = 5;
    private static final int EDIT_MODE = 6;
    private static final int IDLE_MODE = 2;
    private static final int MOVE_MODE = 3;
    private static final int ROTATE_MODE = 4;
    private static final int SCALE_MODE = 7;
    public int PADDING;
    public int STICKER_BTN_HALF_SIZE;
    private Alignment align = Alignment.ALIGN_CENTER;
    private int canvasHeight;
    private int canvasWidth;
    private Context context;
    private final Paint debugPaint = new Paint();
    private float dx;
    private float dy;
    private boolean editText = true;
    private int finalPaddingLeft;
    private int finalPaddingRight;
    private Font font;
    private FontProvider fontProvider;
    private boolean isInEdit = true;
    private boolean isInitLayout = true;
    private boolean isShowHelpBox = true;
    private float last_x = 0.0f;
    private float last_y = 0.0f;
    private int layoutX;
    private int layoutY;
    private float letterSpacing = 0.0f;
    private float lineSpacing = 10.0f;
    private int mCurrentMode = 2;
    private Bitmap mDeleteBitmap;
    private RectF mDeleteDstRect = new RectF();
    private final Rect mDeleteRect = new Rect();
    private Bitmap mEditBitmap;
    private RectF mEditDstRect = new RectF();
    private final Rect mEditRect = new Rect();
    private final RectF mHelpBoxRect = new RectF();
    private final Paint mHelpPaint = new Paint();
    private Bitmap mRotateBitmap;
    private RectF mRotateDstRect = new RectF();
    private final Rect mRotateRect = new Rect();
    private Bitmap mScaleBitmap;
    private RectF mScaleDstRect = new RectF();
    private final Rect mScaleRect = new Rect();
    private final Rect mTextRect = new Rect();
    private int opacity = 255;
    private OperationListener operationListener;
    private int paddingLeft = 0;
    private int paddingRight = 0;
    private float rotateAngle;
    private float scale;
    private StaticLayout staticLayout;
    private boolean strikethrough = false;
    private String text;
    private final int textHeight = 0;
    private final TextPaint textPaint = new TextPaint();
    private int textWidth;
    private int totalPadding;
    private boolean underLine = false;

    private final Point mPoint = new Point(0,0);

    public interface OperationListener {
        void onDelete(TextStickerView textStickerView);

        void onEdit(TextStickerView textStickerView);

        void onSelect(TextStickerView textStickerView);

        void onTouch(TextStickerView textStickerView);

        void onUnselect(TextStickerView textStickerView);
    }

    public TextStickerView(Context context2, @IntRange(from = 1) int i, @IntRange(from = 1) int i2, FontProvider fontProvider2) {
        super(context2);
        this.context = context2;
        this.fontProvider = fontProvider2;
        this.canvasWidth = i;
        this.canvasHeight = i2;
        initView(context2);
    }

    public TextStickerView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        initView(context2);
    }

    public TextStickerView(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        initView(context2);
    }

    private void initView(Context context2) {
        this.debugPaint.setColor(Color.parseColor("#66ff0000"));
        this.mDeleteBitmap = BitmapFactory.decodeResource(context2.getResources(), R.drawable.icon_delete);
        this.mEditBitmap = BitmapFactory.decodeResource(context2.getResources(), R.drawable.icon_edit);
        this.mRotateBitmap = BitmapFactory.decodeResource(context2.getResources(), R.drawable.icon_rotate);
        this.mScaleBitmap = BitmapFactory.decodeResource(context2.getResources(), R.drawable.icon_resize);
        this.STICKER_BTN_HALF_SIZE = this.mDeleteBitmap.getWidth() / 6;
        this.PADDING = this.STICKER_BTN_HALF_SIZE + 4;
        this.mDeleteRect.set(0, 0, this.mDeleteBitmap.getWidth(), this.mDeleteBitmap.getHeight());
        this.mEditRect.set(0, 0, this.mEditBitmap.getWidth(), this.mEditBitmap.getHeight());
        this.mRotateRect.set(0, 0, this.mRotateBitmap.getWidth(), this.mRotateBitmap.getHeight());
        this.mScaleRect.set(0, 0, this.mScaleBitmap.getWidth(), this.mScaleBitmap.getHeight());
        int i = this.STICKER_BTN_HALF_SIZE;
        this.mDeleteDstRect = new RectF(0.0f, 0.0f, (float) (i << 1), (float) (i << 1));
        int i2 = this.STICKER_BTN_HALF_SIZE;
        this.mEditDstRect = new RectF(0.0f, 0.0f, (float) (i2 << 1), (float) (i2 << 1));
        int i3 = this.STICKER_BTN_HALF_SIZE;
        this.mRotateDstRect = new RectF(0.0f, 0.0f, (float) (i3 << 1), (float) (i3 << 1));
        int i4 = this.STICKER_BTN_HALF_SIZE;
        this.mScaleDstRect = new RectF(0.0f, 0.0f, (float) (i4 << 1), (float) (i4 << 1));
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.isInitLayout && !this.editText) {
            this.isInitLayout = false;
            resetView();
        }
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        if (!TextUtils.isEmpty(getText())) {
            initTextPaint();
            canvas.save();
            float f = this.scale;
            canvas.scale(f, f, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY());
            canvas.rotate(this.rotateAngle, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY());
            canvas.translate(((this.mHelpBoxRect.centerX() - ((float) (this.staticLayout.getWidth() / 2))) + ((float) (this.finalPaddingLeft / 2))) - ((float) (this.finalPaddingRight / 2)), this.mHelpBoxRect.centerY() - ((float) (this.staticLayout.getHeight() / 2)));
            this.staticLayout.draw(canvas);
            canvas.restore();
            super.onDraw(canvas);
            drawContent(canvas);
        }
    }

    private void initTextPaint() {
        int i;
        int i2;
        this.textPaint.setShader(null);
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setTextSize(this.font.getSize() * ((float) this.canvasWidth));
        this.textPaint.setColor(this.font.getColor());
        this.textPaint.setTypeface(this.fontProvider.getTypeface(this.font.getCategory(), this.font.getTypeface()));
        if (VERSION.SDK_INT >= 21) {
            this.textPaint.setLetterSpacing(getLetterSpacing() / 10.0f);
        }
        this.textPaint.setUnderlineText(this.underLine);
        this.textPaint.setAlpha(this.opacity);
        this.textPaint.setStrikeThruText(this.strikethrough);
        this.mHelpPaint.setColor(-16777216);
        this.mHelpPaint.setStyle(Style.STROKE);
        this.mHelpPaint.setAntiAlias(true);
        this.mHelpPaint.setStrokeWidth(1.0f);
        this.mHelpPaint.setPathEffect(new DashPathEffect(new float[]{5.0f, 20.0f}, 0.0f));
        TextPaint textPaint2 = this.textPaint;
        String str = this.text;
        textPaint2.getTextBounds(str, 0, str.length(), this.mTextRect);
        Rect rect = this.mTextRect;
        rect.offset(this.layoutX - (rect.width() >> 1), this.layoutY);
        this.textWidth = this.mTextRect.width();
        int i3 = this.textWidth;
        int i4 = this.PADDING;
        int i5 = (i4 * 4) + i3;
        int i6 = this.canvasWidth;
        if (i5 > i6) {
            this.finalPaddingLeft = (((i6 - (i4 * 4)) / 2) * this.paddingLeft) / 100;
            this.finalPaddingRight = (((i6 - (i4 * 4)) / 2) * this.paddingRight) / 100;
            i2 = this.mTextRect.left + this.PADDING + ((this.textWidth - this.canvasWidth) / 2);
            i = (this.mTextRect.right - this.PADDING) - ((this.textWidth - this.canvasWidth) / 2);
        } else {
            this.finalPaddingLeft = ((i3 / 2) * this.paddingLeft) / 100;
            this.finalPaddingRight = ((i3 / 2) * this.paddingRight) / 100;
            i2 = this.mTextRect.left - this.PADDING;
            i = this.mTextRect.right + this.PADDING;
        }
        this.totalPadding = this.finalPaddingLeft + this.finalPaddingRight;
        int i7 = this.totalPadding;
        int i8 = i - i2;
        if ((this.PADDING * 2) + i7 >= i8) {
            this.totalPadding = i7 - ((this.textWidth / this.text.length()) - this.PADDING);
        }
        StaticLayout staticLayout2 = new StaticLayout(this.text, this.textPaint, (i8 - this.totalPadding) - this.PADDING, this.align, this.lineSpacing / 10.0f, 1.0f, true);
        this.staticLayout = staticLayout2;
        if (this.font.getGradient() != null) {
            this.textPaint.setShader(generateGradient());
        }
        if (this.font.getPatternPath() != null) {
            this.textPaint.setShader(generatePattern());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("textLeft: ");
        sb.append(i2);
        this.mHelpBoxRect.set((float) i2, (float) ((this.layoutY - (this.staticLayout.getHeight() / 2)) - this.PADDING), (float) i, (float) (this.layoutY + (this.staticLayout.getHeight() / 2) + this.PADDING));
        RectUtil.scaleRect(this.mHelpBoxRect, this.scale);
    }

    private void drawContent(Canvas canvas) {
        float width = (float) (((int) this.mDeleteDstRect.width()) >> 1);
        this.mDeleteDstRect.offsetTo(this.mHelpBoxRect.left - width, this.mHelpBoxRect.top - width);
        this.mEditDstRect.offsetTo(this.mHelpBoxRect.left - width, this.mHelpBoxRect.bottom - width);
        this.mRotateDstRect.offsetTo(this.mHelpBoxRect.right - width, this.mHelpBoxRect.top - width);
        this.mScaleDstRect.offsetTo(this.mHelpBoxRect.right - width, this.mHelpBoxRect.bottom - width);
        RectUtil.rotateRect(this.mDeleteDstRect, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY(), this.rotateAngle);
        RectUtil.rotateRect(this.mEditDstRect, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY(), this.rotateAngle);
        RectUtil.rotateRect(this.mRotateDstRect, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY(), this.rotateAngle);
        RectUtil.rotateRect(this.mScaleDstRect, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY(), this.rotateAngle);
        if (this.isShowHelpBox && this.isInEdit) {
            canvas.save();
            canvas.rotate(this.rotateAngle, this.mHelpBoxRect.centerX(), this.mHelpBoxRect.centerY());
            canvas.drawRoundRect(this.mHelpBoxRect, 10.0f, 10.0f, this.mHelpPaint);
            canvas.restore();
            canvas.drawBitmap(this.mDeleteBitmap, this.mDeleteRect, this.mDeleteDstRect, null);
            canvas.drawBitmap(this.mEditBitmap, this.mEditRect, this.mEditDstRect, null);
            canvas.drawBitmap(this.mRotateBitmap, this.mRotateRect, this.mRotateDstRect, null);
            canvas.drawBitmap(this.mScaleBitmap, this.mScaleRect, this.mScaleDstRect, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean ret = super.onTouchEvent(event);

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mDeleteDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = DELETE_MODE;
                    if (operationListener != null) {
                        operationListener.onDelete(this);
                    }
                } else if (mRotateDstRect.contains(x, y)) {
                    isShowHelpBox = true;
                    mCurrentMode = ROTATE_MODE;
                    last_x = mRotateDstRect.centerX();
                    last_y = mRotateDstRect.centerY();
                    ret = true;
                }else if (this.mScaleDstRect.contains(x, y)) {
                    this.isShowHelpBox = true;
                    this.mCurrentMode = SCALE_MODE;
                    this.last_x = this.mScaleDstRect.centerX();
                    this.last_y = this.mScaleDstRect.centerY();
                    ret = true;
                }else if (this.mEditDstRect.contains(x, y)) {
                    this.isShowHelpBox = true;
                    this.mCurrentMode = EDIT_MODE;
                    if (operationListener != null) {
                        operationListener.onEdit(this);
                    }
                    invalidate();
                } else if (detectInHelpBox(x , y)) {
                    isShowHelpBox = true;
                    mCurrentMode = MOVE_MODE;
                    last_x = x;
                    last_y = y;
                    ret = true;
                } else {
                    isShowHelpBox = false;
                    invalidate();
                }

                if (mCurrentMode == DELETE_MODE) {
                    mCurrentMode = IDLE_MODE;
                    clearTextContent();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (mCurrentMode == MOVE_MODE) {
                    mCurrentMode = MOVE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;
                    layoutX += dx;
                    layoutY += dy;
                    invalidate();
                    last_x = x;
                    last_y = y;
                } else if (mCurrentMode == ROTATE_MODE) {
                    mCurrentMode = ROTATE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;
                    updateRotateAndScale(dx, dy,false);
                    invalidate();
                    last_x = x;
                    last_y = y;
                }else if (mCurrentMode == SCALE_MODE) {
                    mCurrentMode = SCALE_MODE;
                    float dx = x - last_x;
                    float dy = y - last_y;
                    updateRotateAndScale(dx, dy, true);
                    invalidate();
                    this.last_x = x;
                    this.last_y = y;
                }
                if (operationListener != null) {
                    operationListener.onSelect(this);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                mCurrentMode = IDLE_MODE;
                break;
        }

        return ret;
    }

    private boolean detectInHelpBox(float x , float y){
        mPoint.set((int)x , (int)y);
        RectUtil.rotatePoint(mPoint , mHelpBoxRect.centerX() , mHelpBoxRect.centerY() , -rotateAngle);
        return mHelpBoxRect.contains(mPoint.x, mPoint.y);
    }

    public void clearTextContent() {
        setText(null);
    }

    public void updateRotateAndScale(float f, float f2, boolean z) {
        float centerX = this.mHelpBoxRect.centerX();
        float centerY = this.mHelpBoxRect.centerY();
        float centerX2 = this.mRotateDstRect.centerX();
        float centerY2 = this.mRotateDstRect.centerY();
        float f3 = f + centerX2;
        float f4 = f2 + centerY2;
        float f5 = centerX2 - centerX;
        float f6 = centerY2 - centerY;
        float f7 = f3 - centerX;
        float f8 = f4 - centerY;
        float sqrt = (float) Math.sqrt((double) ((f5 * f5) + (f6 * f6)));
        float sqrt2 = (float) Math.sqrt((double) ((f7 * f7) + (f8 * f8)));
        if (z) {
            float f9 = sqrt2 / sqrt;
            this.scale *= f9;
            float width = this.mHelpBoxRect.width() * this.scale;
            int i = this.canvasWidth;
            if (width < ((float) ((i * 10) / 100)) || width > ((float) (i + ((i * 10) / 100)))) {
                this.scale /= f9;
            }
        } else {
            double d = (double) (((f5 * f7) + (f6 * f8)) / (sqrt * sqrt2));
            if (d <= 1.0d && d >= -1.0d) {
                this.rotateAngle += ((float) ((f5 * f8) - (f7 * f6) > 0.0f ? 1 : -1)) * ((float) Math.toDegrees(Math.acos(d)));
            }
        }
    }

    public void resetView() {
        this.layoutX = getMeasuredWidth() / 2;
        this.layoutY = getMeasuredHeight() / 3;
        this.rotateAngle = 0.0f;
        this.scale = 1.0f;
    }

    public boolean isShowHelpBox() {
        return this.isShowHelpBox;
    }

    public void setShowHelpBox(boolean z) {
        this.isShowHelpBox = z;
        invalidate();
    }

    public void setOperationListener(OperationListener operationListener2) {
        this.operationListener = operationListener2;
    }

    public void setInEdit(boolean z) {
        this.isInEdit = z;
        invalidate();
    }

    private Shader generateGradient() {
        String[] gradient = this.font.getGradient();
        TileMode tileMode = TileMode.MIRROR;
        int[] iArr = new int[gradient.length];
        for (int i = 0; i < gradient.length; i++) {
            iArr[i] = Color.parseColor(gradient[i]);
        }
        LinearGradient linearGradient = null;
        if (this.font.getGradientType().equals("Linear")) {
            if (this.font.getLinearDirection().equals("Horizontal")) {
                linearGradient = new LinearGradient(0.0f, 0.0f, (float) this.staticLayout.getWidth(), 0.0f, iArr, null, tileMode);
            } else if (this.font.getLinearDirection().equals("Vertical")) {
                linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.staticLayout.getHeight(), iArr, null, tileMode);
            }
        } else if (this.font.getGradientType().equals("Radial")) {
            RadialGradient radialGradient = new RadialGradient((float) (this.staticLayout.getWidth() / 2), (float) (this.staticLayout.getHeight() / 2), (float) this.staticLayout.getWidth(), iArr, null, tileMode);
            return radialGradient;
        } else if (this.font.getGradientType().equals("Sweep")) {
            return new SweepGradient((float) (this.staticLayout.getWidth() / 2), (float) (this.staticLayout.getHeight() / 2), iArr, null);
        }
        return linearGradient;
    }

    private Shader generatePattern() {
        return new BitmapShader(BitmapUtil.createFitBitmap(this.font.getPatternPath(), this.canvasWidth / this.font.getPatternRepeats()), this.font.getPatternMode(), TileMode.MIRROR);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
        invalidate();
    }

    public int getLayoutX() {
        return this.layoutX;
    }

    public void setLayoutX(int i) {
        this.layoutX = i;
        invalidate();
    }

    public int getLayoutY() {
        return this.layoutY;
    }

    public void setLayoutY(int i) {
        this.layoutY = i;
        invalidate();
    }

    public int getOpacity() {
        return this.opacity;
    }

    public void setOpacity(int i) {
        this.opacity = i;
        invalidate();
    }

    public float getRotateAngle() {
        return this.rotateAngle;
    }

    public void setRotateAngle(float f) {
        this.rotateAngle = f;
        invalidate();
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float f) {
        this.scale = f;
        invalidate();
    }

    public float getLetterSpacing() {
        return this.letterSpacing;
    }

    public void setLetterSpacing(float f) {
        this.letterSpacing = f;
        invalidate();
    }

    public float getLineSpacing() {
        return this.lineSpacing;
    }

    public void setLineSpacing(float f) {
        this.lineSpacing = f;
        invalidate();
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font2) {
        this.font = font2;
    }

    public Alignment getAlign() {
        return this.align;
    }

    public void setAlign(Alignment alignment) {
        this.align = alignment;
        invalidate();
    }

    public boolean isEditText() {
        return this.editText;
    }

    public void setEditText(boolean z) {
        this.editText = z;
        invalidate();
    }

    public boolean isUnderLine() {
        return this.underLine;
    }

    public void setUnderLine(boolean z) {
        this.underLine = z;
        invalidate();
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public void setStrikethrough(boolean z) {
        this.strikethrough = z;
        invalidate();
    }

    public int getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(int i) {
        this.paddingLeft = i;
        invalidate();
    }

    public int getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(int i) {
        this.paddingRight = i;
        invalidate();
    }

    public int getTextHeight() {
        initTextPaint();
        return this.staticLayout.getHeight() + (this.PADDING * 2);
    }

    public float getTextCenterY() {
        initTextPaint();
        return this.mHelpBoxRect.centerY();
    }
}

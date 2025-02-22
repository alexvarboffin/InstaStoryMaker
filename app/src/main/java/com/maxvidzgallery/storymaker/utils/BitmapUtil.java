package com.maxvidzgallery.storymaker.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import jp.wasabeef.blurry.Blurry;

public class BitmapUtil {

    public static Bitmap createFitBitmap(String str, int i) {
        int i2;
        if (str == null || str.isEmpty()) {
            return null;
        }
        Bitmap decodeFile = BitmapFactory.decodeFile(new File(str).getAbsolutePath());
        if (decodeFile.getWidth() > decodeFile.getHeight()) {
            i2 = (decodeFile.getHeight() * i) / decodeFile.getWidth();
        } else if (decodeFile.getWidth() < decodeFile.getHeight()) {
            i2 = i;
            i = (decodeFile.getWidth() * i) / decodeFile.getHeight();
        } else if (decodeFile.getWidth() == decodeFile.getHeight()) {
            i2 = i;
        } else {
            i = 0;
            i2 = 0;
        }
        return Bitmap.createScaledBitmap(decodeFile, i, i2, false);
    }

    public static Bitmap createFitBitmap(Bitmap bitmap, int i) {
        int i2;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            i2 = (bitmap.getHeight() * i) / bitmap.getWidth();
        } else if (bitmap.getWidth() < bitmap.getHeight()) {
            i2 = i;
            i = (bitmap.getWidth() * i) / bitmap.getHeight();
        } else if (bitmap.getWidth() == bitmap.getHeight()) {
            i2 = i;
        } else {
            i = 0;
            i2 = 0;
        }
        return createScaledBitmap(bitmap, i, i2, true);
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f = ((float) i) / ((float) width);
        float f2 = ((float) i2) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(f, f2);
        if (width == 0 || height == 0) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!z) {
            bitmap.recycle();
        }
        return createBitmap;
    }

    public static ArrayList<Bitmap> createCollageBitmaps(ArrayList<String> arrayList, int i) {
        ArrayList<Bitmap> arrayList2 = new ArrayList<>();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            File file = new File((String) arrayList.get(i2));
            int length = ((int) file.length()) / 1024;
            Options options = new Options();
            if (i != 0) {
                options.inSampleSize = (i / 2) + (length / 800);
            } else {
                options.inSampleSize = 0;
            }
            arrayList2.add(BitmapFactory.decodeFile(file.getAbsolutePath(), options));
        }
        return arrayList2;
    }

    public static void applyBlur(Activity activity, String str, int i, ImageView imageView) {
        if (i <= 5) {
            imageView.setImageBitmap(createFitBitmap(str, ScreenUtil.getScreenWidth(activity)));
        } else {
            Blurry.with(activity).radius(((i * 25) / 100)).from(createFitBitmap(str, ScreenUtil.getScreenWidth(activity))).into(imageView);
        }
    }

    public static Bitmap applyVignette(Bitmap bitmap, int i, String str, int i2) {
        int i3;
        int i4;
        Bitmap bitmap2 = bitmap;
        int i5 = i;
        if (i5 == 0) {
            return bitmap2;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, new Paint());
        if (height < width) {
            i3 = bitmap.getHeight() * 2;
            i4 = (((bitmap.getHeight() * 2) / 100) * i5) / 2;
        } else {
            i3 = bitmap.getWidth() * 2;
            i4 = (((bitmap.getWidth() * 2) / 100) * i5) / 2;
        }
        int i6 = i3 - i4;
        StringBuilder sb = new StringBuilder();
        sb.append("rad = ");
        sb.append(i6);
        sb.append(" ");
        sb.append(i5);

        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        RadialGradient radialGradient = new RadialGradient((float) rect.centerX(), (float) rect.centerY(), (float) i6, new int[]{0, 0, Color.parseColor(str)}, new float[]{0.0f, 0.1f, 1.0f}, TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(radialGradient);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha((i2 * 255) / 100);
        canvas.drawRect(rectF, paint);
        return createBitmap;
    }

    public static Bitmap createMask(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] iArr = new int[(width * height)];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int i = 0;
        while (i < iArr.length) {
            iArr[i] = iArr[i] != -16777216 ? 0 : iArr[i];
            i++;
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return createBitmap;
    }

    public static void savePhoto(Bitmap bitmap, String str, String str2) {
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, str2);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePhoto(Bitmap bitmap, String str, String str2, int i, int i2, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append("savePhoto ");
        sb.append(bitmap);

        Bitmap createScaledBitmap = createScaledBitmap(bitmap, i, i2, true);
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, str2);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            createScaledBitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!z) {
            bitmap.recycle();
            createScaledBitmap.recycle();
        }
    }
}

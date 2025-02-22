package com.maxvidzgallery.storymaker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Registry;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.Activity.EditorActivity;
import com.maxvidzgallery.storymaker.Activity.MainActivity;
import com.maxvidzgallery.storymaker.models.Draft;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCrop.Options;
import es.dmoral.toasty.Toasty;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AppUtil {

    public static void mainPermissionGranted(final MainActivity mainActivity, String str, final String str2, final String str3, final String str4) {
        Dexter.withActivity(mainActivity).withPermission(str).withListener(new PermissionListener() {
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                mainActivity.onPermissionGranted(str2, str3, str4);
            }

            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if (permissionDeniedResponse.isPermanentlyDenied()) {
                    AppUtil.showSettingsDialog(mainActivity);
                }
            }

            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                AppUtil.showPermissionDialog(mainActivity, permissionToken);
            }
        }).check();
    }

    public static void editorPermissionGranted(final EditorActivity editorActivity, String str) {
        Dexter.withActivity(editorActivity).withPermission(str).withListener(new PermissionListener() {
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                editorActivity.onPermissionGranted();
            }

            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if (permissionDeniedResponse.isPermanentlyDenied()) {
                    AppUtil.showSettingsDialog(editorActivity);
                }
            }

            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                AppUtil.showPermissionDialog(editorActivity, permissionToken);
            }
        }).check();
    }

    public static boolean permissionGranted(final Activity activity, String str) {
        final boolean[] zArr = {false};
        Dexter.withActivity(activity).withPermission(str).withListener(new PermissionListener() {
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                zArr[0] = true;
            }

            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if (permissionDeniedResponse.isPermanentlyDenied()) {
                    AppUtil.showSettingsDialog(activity);
                } else {
                    activity.onBackPressed();
                }
            }

            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                AppUtil.showPermissionDialog(activity, permissionToken);
            }
        }).check();
        return zArr[0];
    }

    private static void showSettingsDialog(final Activity activity) {
        Builder builder = new Builder(activity);
        builder.setTitle((CharSequence) "Need Permissions");
        builder.setMessage((CharSequence) "This app needs permissions to use this feature. You can grant them in app settings.");
        builder.setPositiveButton((CharSequence) "GOTO SETTINGS", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                AppUtil.openSettings(activity);
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                activity.onBackPressed();
            }
        });
        builder.show();
    }

    @SuppressLint("ResourceType")
    private static void showPermissionDialog(final Activity activity, final PermissionToken permissionToken) {
        new Builder(activity).setMessage((int) R.string.MSG_ASK_PERMISSION).setNegativeButton(17039360, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                permissionToken.cancelPermissionRequest();
                activity.onBackPressed();
            }
        }).setPositiveButton(17039370, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                permissionToken.continuePermissionRequest();
            }
        }).setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                permissionToken.cancelPermissionRequest();
            }
        }).show();
    }

    private static void openSettings(Activity activity) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivityForResult(intent, 101);
    }

    public static void showKeyboard(Activity activity, EditText editText) {
        editText.requestFocus();
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 1);
    }

    public static void hideKeyboard(Activity activity, EditText editText) {
        editText.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 1);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showWidget(List<View> list, View view) {
        for (View view2 : list) {
            if (view2 != view) {
                view2.setVisibility(View.GONE);
            } else {
                view2.setVisibility(View.VISIBLE);
            }
        }
    }

    public static String getFileType(String str) {
        File file = new File(str);
        for (String endsWith : new String[]{"jpg", "png", "jpeg"}) {
            if (file.getName().toLowerCase().endsWith(endsWith)) {
                return "Image";
            }
        }
        return file.getName().toLowerCase().endsWith("gif") ? Registry.BUCKET_GIF : "Video";
    }

    public static boolean equals(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bArr = new byte[inputStream.available()];
            inputStream.read(bArr, 0, bArr.length);
            return new String(bArr);
        } catch (IOException unused) {
            return null;
        }
    }

    public static void putInAdjustsContrast(Context context, String str, float f) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Data Holder", 0);
        Editor edit = sharedPreferences.edit();
        String str2 = "adjustContracts";
        LinkedHashMap linkedHashMap = (LinkedHashMap) new Gson().fromJson(sharedPreferences.getString(str2, ""), new TypeToken<LinkedHashMap<String, Float>>() {
        }.getType());
        linkedHashMap.put(str, Float.valueOf(f));
        edit.putString(str2, new Gson().toJson((Object) linkedHashMap));
        edit.commit();
    }

    public static LinkedHashMap getInAdjustsContrast(Context context) {
        return (LinkedHashMap) new Gson().fromJson(context.getSharedPreferences("Data Holder", 0).getString("adjustContracts", ""), new TypeToken<LinkedHashMap<String, Float>>() {
        }.getType());
    }

    public static String readableFileSize(long j) {
        if (j <= 0) {
            return "0";
        }
        String[] strArr = {"B", "kB", "MB", "GB", "TB"};
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, (double) log10);
        Double.isNaN(d);
        sb.append(decimalFormat.format(d / pow));
        sb.append(" ");
        sb.append(strArr[log10]);
        return sb.toString();
    }

    public static Drawable getRoundedRect(Activity activity, int i, int i2, int i3, int i4, int i5) {
        int dp2px = DensityUtil.dp2px(activity, (float) i3);
        int dp2px2 = DensityUtil.dp2px(activity, (float) i2);
        int dp2px3 = DensityUtil.dp2px(activity, (float) i4);
        float f = (float) dp2px2;
        float f2 = (float) dp2px;
        float dp2px4 = (float) DensityUtil.dp2px(activity, (float) i5);
        float f3 = (float) dp2px3;
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f2, f2, dp2px4, dp2px4, f3, f3}, null, null));
        shapeDrawable.getPaint().setColor(i);
        shapeDrawable.getPaint().setStyle(Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable;
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    public static void removeDrafts(ArrayList<Draft> arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Draft draft = (Draft) it.next();
            String sb = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                    "/Android/data/com.maxvidzgallery.storymaker/drafts/" +
                    draft.draft_name;
            deleteFolder(new File(sb));
            File file = new File(draft.save_path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void removeDraft(Draft draft) {
        if (draft.thumbnail != null) {
            File file = new File(draft.thumbnail);
            if (file.exists()) {
                file.delete();
            }
        }
        File file2 = new File(draft.save_path);
        if (file2.exists()) {
            file2.delete();
        }
    }

    public static String[] strTOStrArray(String str, String str2) {
        if (str == null) {
            return null;
        }
        return str.split(str2);
    }

    public static String strArrayToStr(String[] strArr, String str) {
        if (strArr == null) {
            return null;
        }
        String str2 = "";
        for (String str3 : strArr) {
            String sb = str2 +
                    str3 +
                    str;
            str2 = sb;
        }
        return str2.trim();
    }

    public static PaintDrawable generateViewGradient(String[] strArr, final String str, final String str2, int i, int i2) {
        final int[] iArr = new int[strArr.length];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            iArr[i3] = Color.parseColor(strArr[i3]);
        }
        final TileMode tileMode = TileMode.MIRROR;
        ShaderFactory r5 = new ShaderFactory() {
            public Shader resize(int i, int i2) {
                LinearGradient linearGradient;
                if (str.equals("Linear")) {
                    if (str2.equals("Horizontal")) {
                        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, (float) i, 0.0f, iArr, null, tileMode);
                        linearGradient = linearGradient2;
                    } else {
                        linearGradient = str2.equals("Vertical") ? new LinearGradient(0.0f, 0.0f, 0.0f, (float) i2, iArr, null, tileMode) : null;
                    }
                    return linearGradient;
                } else if (str.equals("Radial")) {
                    RadialGradient radialGradient = new RadialGradient((float) (i / 2), (float) (i2 / 2), (float) i, iArr, null, tileMode);
                    return radialGradient;
                } else if (str.equals("Sweep")) {
                    return new SweepGradient((float) (i / 2), (float) (i2 / 2), iArr, null);
                } else {
                    return null;
                }
            }
        };
        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setShape(new RectShape());
        paintDrawable.setShaderFactory(r5);
        return paintDrawable;
    }

    public static ArrayList<Integer> convertDecimalToFraction(float f, float f2) {
        float f3 = f / f2;
        if (f3 < 0.0f) {
            return null;
        }
        double d = (double) f3;
        double d2 = 0.0d;
        double d3 = 0.0d;
        double d4 = 1.0d;
        double d5 = 1.0d;
        double d6 = d;
        while (true) {
            double floor = Math.floor(d6);
            double d7 = (floor * d4) + d2;
            double d8 = (floor * d3) + d5;
            d6 = 1.0d / (d6 - floor);
            double d9 = d7 / d8;
            Double.isNaN(d);
            double abs = Math.abs(d - d9);
            Double.isNaN(d);
            if (abs <= d * 1.0E-6d) {
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.valueOf((int) d7));
                arrayList.add(Integer.valueOf((int) d8));
                StringBuilder sb = new StringBuilder();
                sb.append("ratio = ");
                sb.append(f3);
                sb.append(" ");
                sb.append(d7);
                sb.append(":");
                sb.append(d8);
                return arrayList;
            }
            d5 = d3;
            d3 = d8;
            double d10 = d4;
            d4 = d7;
            d2 = d10;
        }
    }

    public static void openUrl(Context context, String str) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.BROWSABLE");
        intent.setData(Uri.parse(str));
        context.startActivity(intent);
    }

    public static void shareIntent(Context context, String str, String str2, Uri uri) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.setPackage(str);
        if (TextUtils.isEmpty(str2)) {
            str2 = "";
        }
        intent.putExtra("android.intent.extra.TEXT", str2);
        if (uri != null) {
            intent.putExtra("android.intent.extra.STREAM", uri);
            intent.setType("image/*");
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toasty.warning(context, (CharSequence) "No Such App Found", 0, true).show();
        }
    }



    @SuppressLint("ResourceType")
    public static void showBottomDialog(Context context, Dialog dialog, View view, boolean z) {
        dialog.setContentView(view);
        LayoutParams layoutParams = view.getLayoutParams();
        view.setLayoutParams(layoutParams);
        dialog.setCanceledOnTouchOutside(z);
        dialog.getWindow().setWindowAnimations(2131886298);
        dialog.show();
    }

    public static void cropPhoto(Activity activity, File file, int i, int i2, int i3) {
        int i4;
        float f;
        if (i3 <= 1500) {
            i4 = 90;
            f = 0.9f;
        } else if (i3 <= 2500) {
            i4 = 92;
            f = 1.0f;
        } else if (i3 <= 4500) {
            i4 = 95;
            f = 1.2f;
        } else {
            i4 = 100;
            f = 1.4f;
        }
        Options options = new Options();
        options.setCompressionQuality(i4);
        options.setMaxBitmapSize(8000);
        options.withAspectRatio((float) i, (float) i2);
        options.withMaxResultSize((int) (((float) ScreenUtil.getScreenWidth(activity)) * f), (int) (((float) ScreenUtil.getScreenHeight(activity)) * f));
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));

        options.setToolbarWidgetColor(ContextCompat.getColor(activity, R.color.colorBlack));
        options.setFreeStyleCropEnabled(false);
        UCrop.of(Uri.fromFile(file), Uri.fromFile(new File(activity.getCacheDir(), "tempCropImage"))).withOptions(options).start(activity);
    }

    public static void deleteFolder(File file) {
        if (file.isDirectory()) {
            for (File deleteFolder : file.listFiles()) {
                deleteFolder(deleteFolder);
            }
        }
        file.delete();
    }

    public static ArrayList<String> getFilesPath(String str) {
        String[] list;
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(str);
        if (file.exists()) {
            File absoluteFile = file.getAbsoluteFile();
            if (absoluteFile.list().length > 0) {
                for (String str2 : absoluteFile.list()) {
                    String sb = str +
                            str2;
                    arrayList.add(sb);
                }
            }
        }
        return arrayList;
    }

    public static int getTotalMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return (int) (memoryInfo.totalMem / 1000000);
    }

    public static Set<String> getLockedNumbers(SharedPreferences sharedPreferences) {
        return sharedPreferences.getStringSet("lockedNumbers", new LinkedHashSet());
    }

    public static boolean isLocked(SharedPreferences sharedPreferences, String str) {
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        String format = simpleDateFormat.format(Calendar.getInstance().getTime());
        String string = sharedPreferences.getString(str, format);
        Date date2 = null;
        try {
            date = simpleDateFormat.parse(format);
            try {
                date2 = simpleDateFormat.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
                if (!date.equals(date2)) {
                }
            }
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
            if (!date.equals(date2)) {
            }
        }
        return !date.equals(date2) || date.after(date2);
    }
}

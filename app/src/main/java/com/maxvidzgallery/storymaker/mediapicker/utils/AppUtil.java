package com.maxvidzgallery.storymaker.mediapicker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import com.maxvidzgallery.storymaker.mediapicker.Gallery;
import com.maxvidzgallery.storymaker.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AppUtil {

    public static void permissionGranted(final Gallery gallery, String str) {
        Dexter.withActivity(gallery).withPermission(str).withListener(new PermissionListener() {
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                gallery.setTabBar();
            }
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if (permissionDeniedResponse.isPermanentlyDenied()) {
                    AppUtil.showSettingsDialog(gallery);
                } else {
                    gallery.onBackPressed();
                }
            }
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                AppUtil.showPermissionDialog(gallery, permissionToken);
            }
        }).check();
    }

    private static void showSettingsDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.TITLE_PERMISSIONS));
        builder.setMessage(activity.getString(R.string.MSG_ASK_PERMISSION));
        builder.setPositiveButton(activity.getString(R.string.BTN_GOTO_SETTINGS), (dialogInterface, i) -> {
            dialogInterface.cancel();
            AppUtil.openSettings(activity);
            activity.onBackPressed();
        });
        builder.setNegativeButton(activity.getString(R.string.BTN_CANCEL), (dialogInterface, i) -> {
            dialogInterface.cancel();
            activity.onBackPressed();
        });
        builder.show();
    }

    @SuppressLint("ResourceType")
    private static void showPermissionDialog(final Activity activity, final PermissionToken permissionToken) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.MSG_ASK_PERMISSION)
                .setNegativeButton(17039360, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    permissionToken.cancelPermissionRequest();
                    activity.onBackPressed();
                }).setPositiveButton(activity.getString(R.string.BTN_OK), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    permissionToken.continuePermissionRequest();
                }).setOnDismissListener(dialogInterface -> permissionToken.cancelPermissionRequest()).show();
    }

    private static void openSettings(Activity activity) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivityForResult(intent, 101);
    }

    public static String getVideoDuration(Context context, String str) {
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, Uri.parse(str));
            long parseLong = Long.parseLong(mediaMetadataRetriever.extractMetadata(9));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpleDateFormat.format(new Date(parseLong));
        } catch (RuntimeException unused) {
            return "";
        }
    }
}

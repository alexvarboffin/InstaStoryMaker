package com.maxvidzgallery.storymaker.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.utils.BitmapUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

class SavePhoto extends AsyncTask<String, String, File> {
    private final EditorActivity editorActivity;
    private Bitmap btmDraw;
    private final int height;
    private final String photoName;
    private final String savePath;
    private final boolean showLoading;
    private final int width;

    public SavePhoto(EditorActivity editorActivity, String str, String str2, int i, int i2, boolean z) {
        this.editorActivity = editorActivity;
        this.savePath = str;
        this.photoName = str2;
        this.width = i;
        this.height = i2;
        this.showLoading = z;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        editorActivity.setCurrentTextStickerEdit(false, editorActivity.currentTextSticker);
        editorActivity.setCurrentImgStickerEdit(false, editorActivity.currentImgSticker);
        Log.d("@@@@@@@@@@@", "onPreExecute: " + editorActivity.currentImgSticker);
        editorActivity.binding.flWrapper.setDrawingCacheEnabled(true);
        editorActivity.binding.flWrapper.buildDrawingCache(true);
        this.btmDraw = BitmapUtil.createScaledBitmap(editorActivity.binding.flWrapper.getDrawingCache(), this.width, this.height, true);
    }

    protected File doInBackground(String... strArr) {
        File file = new File(this.savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, this.photoName);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            this.btmDraw.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.showLoading) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", this.photoName);
            String sb = editorActivity.getString(R.string.app_name) +
                    " Application Android";
            contentValues.put("description", sb);
            contentValues.put("datetaken", System.currentTimeMillis());
            contentValues.put("bucket_id", file2.toString().toLowerCase(Locale.US).hashCode());
            contentValues.put("bucket_display_name", file2.getName().toLowerCase(Locale.US));
            contentValues.put("_data", file2.getAbsolutePath());
            editorActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        return file2;
    }

    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (this.showLoading) {
            editorActivity.loading(false, true);
            if (file != null) {
                editorActivity.binding.flWrapper.setDrawingCacheEnabled(false);
                Intent intent = PreviewActivity.newIntent(editorActivity, file, editorActivity.draftJson);
                editorActivity.startActivity(intent);
                editorActivity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        }
    }
}

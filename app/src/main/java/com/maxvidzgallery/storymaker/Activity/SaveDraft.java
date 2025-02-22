package com.maxvidzgallery.storymaker.Activity;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.models.Draft;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.BitmapUtil;
import com.maxvidzgallery.storymaker.widgets.ImageStickerView;
import com.maxvidzgallery.storymaker.widgets.PhotoView;
import com.maxvidzgallery.storymaker.widgets.TextStickerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

class SaveDraft extends AsyncTask<String, String, JSONObject> {

    private final EditorActivity editorActivity;
    private Bitmap btmDraw;
    private String coverName;
    private final boolean isPhoto;
    private final String traceName;
    private final int videoQuality;

    public SaveDraft(EditorActivity editorActivity, int i, boolean z, String str) {
        this.editorActivity = editorActivity;
        this.videoQuality = i;
        this.isPhoto = z;
        this.traceName = str;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        editorActivity.setCurrentTextStickerEdit(false, editorActivity.currentTextSticker);
        editorActivity.setCurrentImgStickerEdit(false, editorActivity.currentImgSticker);
        Iterator it = editorActivity.fabControllers.iterator();
        while (it.hasNext()) {
            ((ViewGroup) it.next()).setVisibility(View.INVISIBLE);
        }
        editorActivity.loading(true, this.isPhoto);
        editorActivity.binding.flWrapper.setDrawingCacheEnabled(true);
        editorActivity.binding.flWrapper.buildDrawingCache(true);
        String sb = editorActivity.templateName +
                "-thumbnail-" +
                AppUtil.getCurrentTime() +
                ".png";
        this.coverName = sb;
        Bitmap drawingCache = editorActivity.binding.flWrapper.getDrawingCache();
        String str = "/";
        String sb2 = editorActivity.draftsPath +
                str +
                editorActivity.draftFolder +
                str;
        BitmapUtil.savePhoto(drawingCache, sb2, this.coverName, 600, 1066, true);
        if (editorActivity.isDraft) {
            File file = new File(editorActivity.draft.thumbnail);
            if (file.exists()) {
                file.delete();
            }
            File file2 = new File(editorActivity.draft.save_path);
            if (file2.exists()) {
                file2.delete();
            }
        }
        if (editorActivity.isSaved) {
            this.btmDraw = BitmapUtil.createScaledBitmap(editorActivity.binding.flWrapper.getDrawingCache(), editorActivity.saveWidth, editorActivity.saveHeight, true);
        }
    }

    protected JSONObject doInBackground(String... strArr) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8 = "gradient_type";
        String str9 = "/";
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("draft_name", editorActivity.draftFolder);
            String sb = editorActivity.draftsPath +
                    str9 +
                    editorActivity.draftFolder +
                    str9 +
                    this.coverName;
            jSONObject.put("thumbnail", sb);
            jSONObject.put("template_name", editorActivity.templateName);
            jSONObject.put("template_category", editorActivity.templateCategory);
            jSONObject.put("background_color", editorActivity.selectedBgColor);
            jSONObject.put("background_gradient", editorActivity.selectedBgGradient);
            jSONObject.put("gradient_linear_direction", editorActivity.linearDirectionBg);
            jSONObject.put(str8, editorActivity.gradientTypeBg);
            jSONObject.put("background_photo", editorActivity.selectedBgPattern);
            jSONObject.put("photo_scale", editorActivity.binding.ivBackground.getScaleX());
            jSONObject.put("photo_blur", editorActivity.bgSeekBars.get(1).getProgress());
            jSONObject.put("saved", editorActivity.isSaved);
            ArrayList arrayList = new ArrayList();
            Iterator it = editorActivity.allTextSticker.iterator();
            while (true) {
                str = "rotate";
                str2 = "layout_y";
                str3 = "layout_x";
                str4 = "scale";
                str5 = "id";
                if (!it.hasNext()) {
                    break;
                }
                TextStickerView textStickerView = (TextStickerView) it.next();
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put(str5, textStickerView.getTag());
                jSONObject2.put(str3, textStickerView.getLayoutX());
                jSONObject2.put(str2, textStickerView.getLayoutY());
                jSONObject2.put(str, textStickerView.getRotateAngle());
                jSONObject2.put(str4, textStickerView.getScale());
                jSONObject2.put("paddingLeft", textStickerView.getPaddingLeft());
                jSONObject2.put("paddingRight", textStickerView.getPaddingRight());
                jSONObject2.put("opacity", textStickerView.getOpacity());
                jSONObject2.put("text", textStickerView.getText());
                jSONObject2.put("size", textStickerView.getFont().getSize());
                jSONObject2.put("color", textStickerView.getFont().getColor());
                jSONObject2.put("font_category", textStickerView.getFont().getCategory());
                jSONObject2.put("font_name", textStickerView.getFont().getTypeface());
                jSONObject2.put("letter_spacing", textStickerView.getLetterSpacing());
                jSONObject2.put("line_spacing", textStickerView.getLineSpacing());
                jSONObject2.put("underline", textStickerView.isUnderLine());
                jSONObject2.put("strikethrough", textStickerView.isStrikethrough());
                jSONObject2.put("align", textStickerView.getAlign());
                jSONObject2.put("gradient", AppUtil.strArrayToStr(textStickerView.getFont().getGradient(), " "));
                jSONObject2.put(str8, textStickerView.getFont().getGradientType());
                jSONObject2.put("linear_direction", textStickerView.getFont().getLinearDirection());
                jSONObject2.put("pattern_path", textStickerView.getFont().getPatternPath());
                jSONObject2.put("pattern_mode", textStickerView.getFont().getPatternMode());
                jSONObject2.put("pattern_repeats", textStickerView.getFont().getPatternRepeats());
                arrayList.add(jSONObject2);
            }
            jSONObject.put("texts", arrayList);
            ArrayList arrayList2 = new ArrayList();
            Iterator it2 = editorActivity.allImageSticker.iterator();
            while (true) {
                str6 = "path";
                if (!it2.hasNext()) {
                    break;
                }
                ImageStickerView imageStickerView = (ImageStickerView) it2.next();
                imageStickerView.calculate();
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put(str5, imageStickerView.getStickerId());
                jSONObject3.put(str6, imageStickerView.getStickerPath());
                jSONObject3.put(str3, imageStickerView.getLayoutX());
                jSONObject3.put(str2, imageStickerView.getLayoutY());
                jSONObject3.put(str4, imageStickerView.getScale());
                jSONObject3.put(str, imageStickerView.getRotate());
                arrayList2.add(jSONObject3);
            }
            jSONObject.put("stickers", arrayList2);
            ArrayList arrayList3 = new ArrayList();
            Iterator it3 = editorActivity.addedPhotos.iterator();
            while (it3.hasNext()) {
                PhotoView photoView = (PhotoView) it3.next();
                JSONObject jSONObject4 = new JSONObject();
                jSONObject4.put(str5, photoView.getTag());
                jSONObject4.put(str4, photoView.getScale());
                jSONObject4.put(str6, photoView.getPhotoPath());
                arrayList3.add(jSONObject4);
            }
            jSONObject.put("photos", arrayList3);
            try {
                String sb3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                        "/Android/data/" +
                        editorActivity.getPackageName() +
                        "/drafts/Json/";
                if (!new File(sb3).exists()) {
                    new File(sb3).mkdirs();
                }
                if (editorActivity.isDraft) {
                    str7 = editorActivity.draft.save_path.replace(sb3, "");
                } else {
                    String sb4 = editorActivity.templateName +
                            "-" +
                            AppUtil.getCurrentTime() +
                            ".json";
                    str7 = sb4;
                }
                String sb5 = sb3 +
                        str7;
                jSONObject.put("save_path", sb5);
                File file = new File(sb3, str7);

                try {
                    FileWriter fileWriter = new FileWriter(file);
                    try {
                        fileWriter.write(jSONObject.toJSONString());
                    } finally {
                        fileWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                editorActivity.draftJson = AppUtil.inputStreamToString(new FileInputStream(file));
                Gson gson = new Gson();
                String sb6 = sb3 +
                        str7;
                editorActivity.draft = gson.fromJson(AppUtil.inputStreamToString(new FileInputStream(new File(sb6))), Draft.class);
                editorActivity.removeUnusedFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (editorActivity.isSaved) {
                editorActivity.imageProcessing(this.btmDraw);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }

    protected void onPostExecute(JSONObject jSONObject) {
        super.onPostExecute(jSONObject);
        if (editorActivity.isSaved) {
            editorActivity.binding.flWrapper.setDrawingCacheEnabled(false);
            String sb = editorActivity.savePath +
                    "/" +
                    editorActivity.outputName;
            File file = new File(sb);
            if (file.exists()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", editorActivity.outputName);
                String sb2 = editorActivity.getString(R.string.app_name) +
                        " Application Android";
                contentValues.put("description", sb2);
                contentValues.put("datetaken", System.currentTimeMillis());
                contentValues.put("bucket_id", file.toString().toLowerCase(Locale.US).hashCode());
                contentValues.put("bucket_display_name", file.getName().toLowerCase(Locale.US));
                contentValues.put("_data", file.getAbsolutePath());
                editorActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                editorActivity.whichActivitytoStart = 1;
                editorActivity.A = file;
                editorActivity.draftJson0 = editorActivity.draftJson;
                editorActivity.replaceScreen();
            } else {
                Toasty.error(editorActivity, editorActivity.getResources().getString(R.string.MSG_SOMETHING_WRONG), 0, true).show();
            }
            editorActivity.loading(false, true);
            return;
        }
        editorActivity.loading(false, this.isPhoto);
        editorActivity.finish();
        editorActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

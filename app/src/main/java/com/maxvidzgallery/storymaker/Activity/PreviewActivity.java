package com.maxvidzgallery.storymaker.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;

import com.maxvidzgallery.storymaker.BuildConfig;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.databinding.ActivityPreviewBinding;
import com.maxvidzgallery.storymaker.databinding.WgDeleteAlertDialogBinding;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.models.Draft;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.DLog;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import com.google.gson.Gson;

import java.io.File;


public class PreviewActivity extends BaseActivity {
    private static final String KEY_SAVE_IMAGE_FILE = "savedImageFile";
    private static final String KEY_ARG_DRAFT = "draft";

    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";
    ConnectionDetector connectionDetector;

    int whichActivitytoStart = 0;
    boolean isActivityLeft;

    AppCompatActivity activity;


    private Draft draft;

    private File imgFile;
    private Uri imgUri;
    private Intent intent;
    private boolean isFromCreation;

    private long mLastClickTime = System.currentTimeMillis();
    private ActivityPreviewBinding binding;

    public static Intent newIntent(Activity activity, File file, String draftJson) {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(KEY_SAVE_IMAGE_FILE, file);
        intent.putExtra(KEY_ARG_DRAFT, draftJson);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent newIntentFromCreation(Activity activity, File file, boolean b) {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(KEY_SAVE_IMAGE_FILE, file);
        intent.putExtra("FromCreation", true);
        return intent;
    }


    private void init0() {

        Intent intent = getIntent();
        if (intent != null) {
            Bundle e = intent.getExtras();
            this.draft = new Gson().fromJson(intent.getStringExtra(KEY_ARG_DRAFT), Draft.class);
            if (e != null && e.containsKey(KEY_SAVE_IMAGE_FILE)) {
                this.imgFile = (File) e.getSerializable(KEY_SAVE_IMAGE_FILE);
            }
            this.isFromCreation = intent.getBooleanExtra("FromCreation", false);
        }


        if (BuildConfig.DEBUG && imgFile == null) {
            this.imgUri = Uri.parse("/mnt/shared/Pictures/InstaStoryMaker/StoryMaker20240602-184741.png");
        }


        if (imgFile != null) {
            if (VERSION.SDK_INT > 24) {
                Context applicationContext = getApplicationContext();
                String sb5 = getPackageName() +
                        ".provider";
                this.imgUri = FileProvider.getUriForFile(applicationContext, sb5, this.imgFile);
            } else {
                this.imgUri = Uri.fromFile(this.imgFile);
            }
        }

        DLog.d("@@@" + draft);
        DLog.d("@@@" + imgFile);
        DLog.d("@@@" + isFromCreation);

        this.binding.cvWrapper.post(() -> {
            int measuredHeight = PreviewActivity.this.binding.cvWrapper.getMeasuredHeight();
            if (measuredHeight > ScreenUtil.getScreenWidth(PreviewActivity.this)) {
                measuredHeight = ScreenUtil.getScreenWidth(PreviewActivity.this);
            }
            double d = measuredHeight;
            Double.isNaN(d);
            int i1 = (int) (d * 0.5625d);
            LayoutParams layoutParams = PreviewActivity.this.binding.cvWrapper.getLayoutParams();
            layoutParams.width = i1;
            layoutParams.height = measuredHeight;
            PreviewActivity.this.binding.cvWrapper.setLayoutParams(layoutParams);
        });
        binding.ivSaved.setImageURI(this.imgUri);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd(this);
        adAdmob.BannerAd(binding.banner, this);

        sharedpreferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        isActivityLeft = false;
        activity = PreviewActivity.this;

        connectionDetector = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();
        binding.tbEdit.setOnClickListener(v -> onEditClick());
        binding.tbClose.setOnClickListener(v -> onCloseClick());
        binding.tbHome.setOnClickListener(v -> onHomeClick());
        binding.tbTrash.setOnClickListener(v -> onTrashClick());
        binding.fabInstagram.setOnClickListener(v -> onInstagramClick());

        binding.fabFacebook.setOnClickListener(v -> onFacebookClick());

        binding.fabWhatsapp.setOnClickListener(v -> onWhatsappClick());

        binding.fabOther.setOnClickListener(v -> onOtherClick());
        binding.tvRateus.setOnClickListener(v -> onRateClick());
        binding.tvSetas.setOnClickListener(v -> onSetAsClick());
        init0();


    }

    public void isClickable() {

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastClickTime >= 3000) {
            this.mLastClickTime = currentTimeMillis;
        }
    }

    public void onEditClick() {
        isClickable();
        if (this.imgFile.delete()) {
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(this.imgFile)));
        }

        AppUtil.removeDraft(this.draft);
        this.isFromCreation = true;

        onBackPressed();
    }

    public void onCloseClick() {
        isClickable();
        onBackPressed();
    }


    public void onHomeClick() {
        whichActivitytoStart = 1;
        replaceScreen();
    }

    public void onTrashClick() {
        isClickable();
        final Dialog dialog = new Dialog(this, R.style.BottomDialog);
        WgDeleteAlertDialogBinding binding = WgDeleteAlertDialogBinding.inflate(LayoutInflater.from(this));
        dialog.setCanceledOnTouchOutside(false);
        AppUtil.showBottomDialog(this, dialog, binding.getRoot(), true);
        binding.btnNegative.setOnClickListener(view -> dialog.dismiss());
        binding.btnPositive.setOnClickListener(view -> {
            if (PreviewActivity.this.imgFile.delete()) {
                PreviewActivity previewActivity = PreviewActivity.this;
                previewActivity.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(previewActivity.imgFile)));
            }
            PreviewActivity.this.onBackPressed();
        });

    }

    public void onBackPressed() {
        if (this.isFromCreation) {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
            finish();
            return;

        }

        final Dialog dialog = new Dialog(this, R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.wg_bottom_alert_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        CardView btn_neutral = dialog.findViewById(R.id.btn_neutral);
        CardView btn_negative = dialog.findViewById(R.id.btn_negative);
        CardView btn_positive = dialog.findViewById(R.id.btn_positive);
        TextView text_positive = dialog.findViewById(R.id.text_positive);

        btn_neutral.setOnClickListener(view -> dialog.dismiss());
        btn_negative.setOnClickListener(view -> {
            dialog.dismiss();
            PreviewActivity.this.onHomeClick();
        });

        text_positive.setText(getString(R.string.BTN_EDIT));
        btn_positive.setOnClickListener(view -> {
            dialog.dismiss();
            PreviewActivity.this.onEditClick();
        });

        dialog.show();
    }


    public void onInstagramClick() {
        String sb = "Create by " +
                getString(R.string.app_name) +
                " " +
                "https://play.google.com/store/apps/details?id=" +
                getPackageName();
        AppUtil.shareIntent(this, "com.instagram.android", sb, this.imgUri);
    }


    public void onFacebookClick() {
        String sb = "Create by " +
                getString(R.string.app_name) +
                " " +
                "https://play.google.com/store/apps/details?id=" +
                getPackageName();
        AppUtil.shareIntent(this, "com.facebook.katana", sb, this.imgUri);
    }


    public void onWhatsappClick() {
        String sb = "Create by " +
                getString(R.string.app_name) +
                " " +
                "https://play.google.com/store/apps/details?id=" +
                getPackageName();
        AppUtil.shareIntent(this, "com.whatsapp", sb, this.imgUri);
    }


    public void onOtherClick() {
        this.intent = new Intent();
        this.intent.setAction("android.intent.action.SEND");
        this.intent.putExtra("android.intent.extra.STREAM", this.imgUri);
        Intent intent2 = this.intent;
        String sb = "Create by https://play.google.com/store/apps/details?id=" +
                getPackageName();
        intent2.putExtra("android.intent.extra.TEXT", sb);
        this.intent.setType("image/jpeg");
        startActivity(Intent.createChooser(this.intent, getResources().getText(R.string.APD_SEND_TO)));
    }


    public void onRateClick() {
        Context applicationContext = getApplicationContext();
        String sb = "https://play.google.com/store/apps/details?id=" +
                getPackageName();
        AppUtil.openUrl(applicationContext, sb);
    }

    @SuppressLint("WrongConstant")

    public void onSetAsClick() {
        this.intent = new Intent("android.intent.action.ATTACH_DATA");
        this.intent.addCategory("android.intent.category.DEFAULT");
        String str = "image/*";
        this.intent.setDataAndType(this.imgUri, str);
        this.intent.putExtra("mimeType", str);
        this.intent.addFlags(1);
        startActivityForResult(Intent.createChooser(this.intent, "Set As"), Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }


    public void onPause() {
        super.onPause();
        this.isActivityLeft = true;
    }

    public void onResume() {
        super.onResume();
        this.isActivityLeft = false;
    }

    protected void onStop() {
        super.onStop();
        this.isActivityLeft = true;
    }

    protected void onDestroy() {
        super.onDestroy();
        this.isActivityLeft = true;
    }

    @SuppressLint("WrongConstant")
    private void replaceScreen() {

        isClickable();
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }
}

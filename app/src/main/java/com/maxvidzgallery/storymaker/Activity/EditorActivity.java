package com.maxvidzgallery.storymaker.Activity;

import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Registry;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.adapters.RvColorsAdapter;
import com.maxvidzgallery.storymaker.adapters.RvFontAdapter;
import com.maxvidzgallery.storymaker.adapters.RvGradientAdapter;
import com.maxvidzgallery.storymaker.databinding.ActivityEditorBinding;
import com.maxvidzgallery.storymaker.fragments.FiltersFrag;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.mediapicker.Gallery;
import com.maxvidzgallery.storymaker.models.Draft;
import com.maxvidzgallery.storymaker.models.Draft.Photo;
import com.maxvidzgallery.storymaker.models.Draft.Text;
import com.maxvidzgallery.storymaker.models.Font;
import com.maxvidzgallery.storymaker.models.Glob_Sticker;
import com.maxvidzgallery.storymaker.models.Template;
import com.maxvidzgallery.storymaker.utils.AnimationsUtil;
import com.maxvidzgallery.storymaker.utils.AppUtil;
import com.maxvidzgallery.storymaker.utils.BitmapUtil;
import com.maxvidzgallery.storymaker.utils.ContractsUtil;
import com.maxvidzgallery.storymaker.utils.DLog;
import com.maxvidzgallery.storymaker.utils.DensityUtil;
import com.maxvidzgallery.storymaker.utils.FontProvider;
import com.maxvidzgallery.storymaker.utils.OnOneOffClickListener;
import com.maxvidzgallery.storymaker.utils.ScreenUtil;
import com.maxvidzgallery.storymaker.widgets.ImageStickerView;
import com.maxvidzgallery.storymaker.widgets.PhotoView;
import com.maxvidzgallery.storymaker.widgets.TextStickerView;
import com.github.florent37.shapeofview.ShapeOfView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import devlight.io.library.ntb.NavigationTabBar.Model;
import devlight.io.library.ntb.NavigationTabBar.Model.Builder;
import devlight.io.library.ntb.NavigationTabBar.OnTabBarSelectedIndexListener;
import es.dmoral.toasty.Toasty;

public class EditorActivity extends BaseActivity {

    private static final String KEY_ARG_TEMPLATE = "template";
    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";

    ConnectionDetector connectionDetector;
    boolean isInternetPresent;


    int whichActivitytoStart = 0;
    boolean isActivityLeft;

    AppCompatActivity activity;

    File A;
    String draftJson0;

    private static final String KEY_ARG_DRAFT = "draft";
    private static final String KEY_SAVE_IMAGE_FILE = "savedImageFile";


    public static Intent selectTemplate(Activity activity, String category, String str2, boolean b) {
        Intent intent = new Intent(activity, EditorActivity.class);
        intent.putExtra("category", category);
        intent.putExtra(KEY_ARG_TEMPLATE, str2);
        intent.putExtra(KEY_ARG_DRAFT, b);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    public static Intent selectTemplate(Activity activity, String category, String str2, boolean b, String savePath) {
        Intent intent = new Intent(activity, EditorActivity.class);
        intent.putExtra("category", category);
        intent.putExtra(KEY_ARG_TEMPLATE, str2);
        intent.putExtra(KEY_ARG_DRAFT, b);
        intent.putExtra("savePath", savePath);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    protected static final Queue<Callable> actionQueue = new ConcurrentLinkedQueue();

    protected static final Handler handler = new Handler();

    protected static final Runnable runnable = new Runnable() {
        public void run() {
            Callable callable;
            do {
                callable = EditorActivity.actionQueue.poll();
                if (callable != null) {
                    try {
                        callable.call();
                        continue;
                    } catch (Exception unused) {
                        continue;
                    }
                }
            } while (callable != null);
            EditorActivity.handler.postDelayed(this, 250);
        }
    };

    private ArrayList<PhotoView> addedPhotos = new ArrayList<>();
    private final ArrayList<ImageStickerView> allImageSticker = new ArrayList<>();
    private final ArrayList<View> allLayouts = new ArrayList<>();
    private final ArrayList<TextStickerView> allTextSticker = new ArrayList<>();


    List<IndicatorSeekBar> bgSeekBars;
    private int canvasHeight;
    private int canvasWidth;
    private int centreX;
    private int centreY;

    private ImageStickerView currentImgSticker;
    public TextStickerView currentTextSticker;

    private final String[] directionsMenu;
    private Draft draft;
    private String draftFolder;
    private String draftJson;

    private final ArrayList<String> draftPhotoIds = new ArrayList<>();
    private ArrayList<Text> draftTexts = new ArrayList<>();

    private String draftsPath;


    private final ArrayList<ViewGroup> fabControllers = new ArrayList<>();
    private boolean firstLaunch;
    private FragmentManager fm;
    private ArrayList<String> fontCategories = new ArrayList<>();

    private FontProvider fontProvider;

    private final ArrayList<View> frameLayouts = new ArrayList<>();
    private final String gradientTile;

    private String gradientType;

    private String gradientTypeBg;

    private ImageStickerView imgSticker;

    private boolean isDraft;

    private boolean isSaved;

    private String linearDirection;
    private String linearDirectionBg;

    private long mLastClickTime;

    private final ArrayList<View> mediaMasks = new ArrayList<>();

    private String outputName;

    private RelativeLayout overlayClicked;
    private final LayoutParams params;

    private String patternPath;

    private final int patternRepeats;

    private final TileMode patternTile1;

    private PhotoView photoClicked;

    private int photoTag;
    private ArrayList<JSONObject> photos;

    private SharedPreferences prefs;


    private RvGradientAdapter rvBgGradientAdapter;

    private RvColorsAdapter rvColorAdapter;


    private RvFontAdapter rvFontAdapter;

    private RvGradientAdapter rvGradientAdapter;

    private int saveHeight;
    private String savePath;
    private int saveWidth;
    private int screenHeight;
    private int screenWidth;
    private String selectedBgColor;
    private String[] selectedBgGradient;
    private String selectedBgPattern;
    private int selectedBtn;

    private ArrayList<Integer> selectedViewRatio = new ArrayList<>();
    List<IndicatorSeekBar> teSeekBars;


    private Template template;

    private String templateCategory;
    private String templateJson;
    private String templateName;

    private ViewGroup templateViewGroup;
    private ArrayList<Template.Text> templeteTexts = new ArrayList<>();

    private TextStickerView textSticker;

    private final String[] tilesMenu;


    private int totalMemory;

    private TextStickerView touchTextSticker;

    private boolean txBtnClicked;


    List<View> vWidgets;


    Alignment alignment;
    private Drawable d;

    final String[] colorList = new String[]{"#ffffff", "#d9d9d9", "#c4c4c4", "#9d9d9d", "#7b7b7b", "#555555", "#434343", "#262626", "#e1bee7", "#ce93d8", "#ba68c8", "#ab47bc", "#9c27b0", "#8e24aa", "#7b1fa2", "#6a1b9a", "#4a148c", "#d1c4e9", "#b39ddb", "#9575cd", "#7e57c2", "#673ab7", "#5e35b1", "#512da8", "#4527a0", "#311b92", "#c5cae9", "#9fa8da", "#7986cb", "#5c6ac0", "#3f50b5", "#3948ab", "#303e9f", "#283493", "#1a227e", "#bbdefb", "#91cbf9", "#65b6f6", "#43a6f5", "#2397f3", "#1f89e5", "#1a77d2", "#1666c0", "#0d48a1", "#b3e5fc", "#82d5fa", "#50c4f7", "#2bb7f6", "#08aaf4", "#069ce5", "#0389d1", "#0378bd", "#01589b", "#b2ebf2", "#80dfea", "#4dd1e1", "#26c7da", "#00bdd4", "#00adc1", "#0098a7", "#00848f", "#006164", "#b2dfdb", "#80cbc4", "#4db6ac", "#26a69a", "#009688", "#00897b", "#00796b", "#00695c", "#004d40", "#c8e6c9", "#a5d6a7", "#81c784", "#66bb6a", "#4caf4f", "#43a046", "#388e3b", "#2e7d31", "#1b5e1f", "#dcedc8", "#c5e1a5", "#aed581", "#9ccc65", "#8bc34a", "#7cb342", "#689f38", "#558b2f", "#33691e", "#f0f4c3", "#e6ee9c", "#dce775", "#d4e157", "#cddc39", "#c0ca33", "#afb42b", "#9e9d24", "#827717", "#fff9c4", "#fff59d", "#fddb00", "#fceb55", "#fae635", "#fdd835", "#fbc02d", "#f9a825", "#f57f17", "#ffecb3", "#ffe082", "#ffd54f", "#ffca28", "#ffc106", "#ffb300", "#ffa000", "#ff8f00", "#ff6f00", "#ffe0b2", "#ffcc80", "#ffb74d", "#ffa726", "#ff9800", "#fb8c00", "#f57c00", "#ef6c00", "#e65100", "#ffccbc", "#ffab91", "#ff8a65", "#ff7043", "#ff5722", "#f4511e", "#e64a19", "#d84315", "#bf360c", "#ffccbc", "#ffab91", "#ff8a65", "#ff7043", "#ff5722", "#f4511e", "#e64a19", "#d84315", "#bf360c", "#ffcdd2", "#ef9a9a", "#e57373", "#ef5350", "#f44336", "#e53935", "#d32f2f", "#c62828", "#b71c1c", "#f8bbd0", "#f48fb1", "#f06292", "#ec407a", "#e91e63", "#d81b60", "#c2185b", "#ad1457", "#880e4f", "#d7ccc8", "#bcaaa4", "#a1887f", "#8d6e63", "#795548", "#6d4c41", "#5d4037", "#4e342e", "#3e2723", "#cfd8dc", "#b0bec5", "#90a4ae", "#78909c", "#607d8b", "#546e7a", "#455a64", "#37474f", "#263238"};
    public ActivityEditorBinding binding;


    static class AnonymousClass67 {
        static final int[] SwitchMapandroidtextLayoutAlignment = new int[Alignment.values().length];


        static {
            SwitchMapandroidtextLayoutAlignment[Alignment.ALIGN_NORMAL.ordinal()] = 1;
            SwitchMapandroidtextLayoutAlignment[Alignment.ALIGN_CENTER.ordinal()] = 2;
            SwitchMapandroidtextLayoutAlignment[Alignment.ALIGN_OPPOSITE.ordinal()] = 3;
        }
    }


    public class DragListener implements OnDragListener {

        public DragListener() {
        }

        public boolean onDrag(View view, DragEvent dragEvent) {
            View view2 = (View) dragEvent.getLocalState();
            int action = dragEvent.getAction();
            if (action != 1) {
                if (action == 3) {
                    ViewGroup viewGroup = (ViewGroup) view2.getParent().getParent();
                    FrameLayout frameLayout = (FrameLayout) viewGroup.getChildAt(0);
                    View childAt = viewGroup.getChildAt(1);
                    viewGroup.removeAllViews();
                    FrameLayout frameLayout2 = (FrameLayout) view;
                    ViewGroup viewGroup2 = (ViewGroup) frameLayout2.getParent();
                    if (viewGroup2 != null) {
                        View childAt2 = viewGroup2.getChildAt(1);
                        viewGroup2.removeAllViews();
                        viewGroup2.addView(frameLayout);
                        viewGroup2.addView(childAt);
                        viewGroup.addView(frameLayout2);
                        viewGroup.addView(childAt2);
                    } else {
                        viewGroup.addView(frameLayout);
                        viewGroup.addView(childAt);
                    }
                    view2.setVisibility(View.VISIBLE);
                    EditorActivity.this.addedPhotos = new ArrayList();
                    EditorActivity.this.photoTag = 0;
                    setMediaOptions(templateViewGroup, true);
                    updateMediaOrder(templateViewGroup);

                } else if (action == 5) {
                    view2.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
    }

    public EditorActivity() {
        String str = "Linear";
        this.directionsMenu = new String[]{str, "Radial", "Sweep"};
        String str2 = "Clamp";
        this.tilesMenu = new String[]{str2, "Mirror", "Repeat"};
        this.selectedBgGradient = null;
        this.photoTag = 0;
        this.patternRepeats = 2;
        this.gradientTile = str2;
        this.gradientType = str;
        String str3 = "Horizontal";
        this.linearDirection = str3;
        this.gradientTypeBg = str;
        this.linearDirectionBg = str3;
        this.firstLaunch = true;
        this.patternTile1 = TileMode.MIRROR;
        this.params = new LayoutParams(-1, -1);
    }

    private void init() {
        AnimationsUtil.initAnimationsValue(this);
        this.fm = getSupportFragmentManager();
        this.totalMemory = AppUtil.getTotalMemory(this);
        this.prefs = getSharedPreferences("Data Holder", 0);
        this.screenWidth = ScreenUtil.getScreenWidth(this);
        this.screenHeight = ScreenUtil.getScreenHeight(this);
        this.fontProvider = new FontProvider(this, getResources());
        this.isSaved = getIntent().getBooleanExtra("IsDraft", false);
        this.isDraft = getIntent().getBooleanExtra(KEY_ARG_DRAFT, false);

        String str = "";
        String sb = Environment.DIRECTORY_PICTURES +
                "/" +
                getString(R.string.app_name).replace(" ", str);
        this.savePath = Environment.getExternalStoragePublicDirectory(sb).getAbsolutePath();

        String sb2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/Android/data/" +
                getPackageName() +
                "/drafts";
        this.draftsPath = sb2;

        this.binding.flWrapper.post(() -> {
            canvasHeight = binding.flWrapper.getMeasuredHeight();
            double canvasHeight1 = canvasHeight;
            Double.isNaN(canvasHeight1);
            canvasWidth = (int) (canvasHeight1 * 0.5625d);
            if (EditorActivity.this.canvasWidth >= EditorActivity.this.screenWidth) {
                canvasWidth = (screenWidth * 90) / 100;
                double canvasWidth1 = canvasWidth;
                Double.isNaN(canvasWidth1);
                canvasHeight = (int) (canvasWidth1 * 1.7777777777777777d);
            }
            ViewGroup.LayoutParams layoutParams = EditorActivity.this.binding.flWrapper.getLayoutParams();
            layoutParams.width = EditorActivity.this.canvasWidth;
            layoutParams.height = EditorActivity.this.canvasHeight;
            EditorActivity.this.binding.flWrapper.setLayoutParams(layoutParams);
            centreX = (int) (binding.flWrapper.getX() + ((float) (EditorActivity.this.binding.flWrapper.getWidth() / 2)));
            centreY = (int) (binding.flWrapper.getY() + ((float) (EditorActivity.this.binding.flWrapper.getHeight() / 2)));
            if (EditorActivity.this.isDraft) {
                EditorActivity.this.setupDraft();
            } else {
                EditorActivity.this.setupTemplate();
                String sb1 = "draft-" +
                        AppUtil.getCurrentTime();
                draftFolder = sb1;
            }
            String str1 = "/";
            String sb21 = EditorActivity.this.draftsPath +
                    str1 +
                    EditorActivity.this.draftFolder +
                    str1;
            File file = new File(sb21);
            if (!file.exists()) {
                file.mkdirs();
            }
            EditorActivity.this.initTextEntitiesListeners();
            EditorActivity.this.setBackgroundListeners();
        });
        this.mLastClickTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bgSeekBars = new ArrayList<>();
        bgSeekBars.add(binding.wgBackgroundMenu.sbBgScale);
        bgSeekBars.add(binding.wgBackgroundMenu.sbBgBlur);

        vWidgets = new ArrayList<>();
        vWidgets.add(binding.wgMainMenu);
        vWidgets.add(binding.wgTextEdit.getRoot());

        teSeekBars = new ArrayList<>();
        teSeekBars.add(binding.wgTextEdit.sbTextFontSize);
        teSeekBars.add(binding.wgTextEdit.sbTextOpacity);
        teSeekBars.add(binding.wgTextEdit.sbTextWidthSize);
        teSeekBars.add(binding.wgTextEdit.sbTextHeightSize);
        teSeekBars.add(binding.wgTextEdit.sbTextPaddingLeft);
        teSeekBars.add(binding.wgTextEdit.sbTextPaddingRight);

        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd(this);
        adAdmob.BannerAd(findViewById(R.id.banner), this);

        sharedpreferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        isActivityLeft = false;
        activity = EditorActivity.this;

        connectionDetector = new ConnectionDetector(getApplicationContext());
        isInternetPresent = connectionDetector.isConnectingToInternet();


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        addTemplate();
        init();


        handleClicks();
    }

    private void handleClicks() {
        binding.menuClose.setOnClickListener(v -> goBack());
        binding.menuSticker.setOnClickListener(v -> {
            isClickable();
            if (SDK_INT >= 33) {
                if (AppUtil.permissionGranted(this, "android.permission.READ_MEDIA_IMAGES")) {
                    startActivityForResult(new Intent(getApplicationContext(), StickerActivity.class), 12);
                }
            } else if (SDK_INT >= 30) {
                if (AppUtil.permissionGranted(this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startActivityForResult(new Intent(getApplicationContext(), StickerActivity.class), 12);
                }
            } else {
                if (AppUtil.permissionGranted(this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    startActivityForResult(new Intent(getApplicationContext(), StickerActivity.class), 12);
                }
            }
        });


        binding.menuSave.setOnClickListener(v -> {
            isClickable();
            if (addedPhotos.size() < frameLayouts.size()) {
                Toasty.warning(EditorActivity.this, getString(R.string.MSG_FILL_FRAMES), 0, true).show();
                return;
            }
            selectedBtn = R.id.menu_save;
            AppUtil.editorPermissionGranted(EditorActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE");
        });

        binding.menuText.setOnClickListener(v -> {
            isClickable();

            Font font = new Font();
            font.setColor(-16777216);
            font.setSize(0.075f);
            font.setCategory(this.fontProvider.getDefaultFontCategory());
            font.setTypeface(this.fontProvider.getDefaultFontName());
            createTextStickView(this.allTextSticker.size() - 1, "My Story", font, this.canvasWidth / 2, this.centreY, 0.0f, 1.0f, 0, 0, Alignment.ALIGN_CENTER, 255, false, false, 0.0f, 10.0f);

        });
        binding.menuBackground.setOnClickListener(v -> {
            isClickable();
            findViewById(R.id.wg_background_menu).setVisibility(View.VISIBLE);
        });

        binding.wgBackgroundMenu.bgClose.setOnClickListener(v -> {
            bgClose();
        });

    }

    private void bgClose() {
        isClickable();
        findViewById(R.id.wg_background_menu).setVisibility(View.GONE);
    }

    public void addTemplate() {
        this.templateCategory = getIntent().getStringExtra("category");
        this.templateName = getIntent().getStringExtra(KEY_ARG_TEMPLATE);

        Log.e("templateCategory", "==>" + templateCategory);
        Log.e("templateName", "==>" + ContractsUtil.initTemplates(templateCategory).get(templateName));

        this.binding.flLayout.removeAllViewsInLayout();
        this.templateViewGroup = (ViewGroup) LayoutInflater.from(this).inflate(ContractsUtil.initTemplates(templateCategory).get(templateName), this.binding.flLayout, false);
        this.binding.flLayout.addView(this.templateViewGroup);
        this.binding.flLayout.setBackgroundColor(0);
        try {
            AssetManager assets = getAssets();
            StringBuilder sb = new StringBuilder();
            sb.append("Templates/");
            sb.append(this.templateName);
            sb.append(".json");
            Log.e("templateJson", "==>" + sb);
            this.templateJson = AppUtil.inputStreamToString(assets.open(sb.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < this.templateViewGroup.getChildCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) this.templateViewGroup.getChildAt(i);
            this.allLayouts.add(viewGroup);
            if (viewGroup.getChildAt(0) instanceof ShapeOfView) {
                ShapeOfView shapeOfView = (ShapeOfView) viewGroup.getChildAt(0);
                if ((shapeOfView.getChildAt(0) instanceof FrameLayout) && (((FrameLayout) shapeOfView.getChildAt(0)).getChildAt(0) instanceof PhotoView)) {
                    this.frameLayouts.add(viewGroup);
                }
            }
        }

        this.template = new Gson().fromJson(this.templateJson, Template.class);
    }

    public void setupTemplate() {

        int j;
        String str = " ";
        int textCenterY;
        int i = 0;
        char c;
        boolean z;

        setMediaOptions(templateViewGroup, false);
        templeteTexts = template.texts;

        while (i < templeteTexts.size()) {

            Font font = new Font();
            font.setColor(Color.parseColor(templeteTexts.get(i).color));
            font.setSize(templeteTexts.get(i).size);
            font.setCategory(templeteTexts.get(i).font_category);
            font.setTypeface(templeteTexts.get(i).font_name);
            font.setGradient(AppUtil.strTOStrArray(templeteTexts.get(i).gradient, str));
            font.setGradientType(templeteTexts.get(i).gradient_type);
            font.setLinearDirection(templeteTexts.get(i).linear_direction);
            font.setPatternPath(templeteTexts.get(i).pattern_path);
            font.setPatternMode(templeteTexts.get(i).pattern_mode);
            font.setPatternRepeats(templeteTexts.get(i).pattern_repeats);

            alignment = Alignment.ALIGN_CENTER;
            String str2 = templeteTexts.get(i).align;

            if (str2.contains("right")) {

                c = 2;
                if (c != 0) {
                    alignment = Alignment.ALIGN_NORMAL;
                } else if (c == 1) {
                    alignment = Alignment.ALIGN_CENTER;
                } else if (c == 2) {
                    alignment = Alignment.ALIGN_OPPOSITE;
                }

                j = templeteTexts.get(i).layout_id;
                textSticker = new TextStickerView(EditorActivity.this, canvasWidth, canvasHeight, fontProvider);
                if (templeteTexts.get(i).position.bottom != null) {
                    TextStickerView textStickerView = allTextSticker.get(Integer.parseInt(templeteTexts.get(i).position.bottom));
                    textCenterY = (int) (((textStickerView.getTextCenterY() + ((float) ((textStickerView.getTextHeight() * 70) / 100))) + ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_top))) - ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_bottom)));
                } else {
                    textCenterY = (int) ((((float) canvasHeight) * templeteTexts.get(i).layout_y) / 100.0f);
                }

                createTextStickView(templeteTexts.get(i).id,
                        templeteTexts.get(i).text,
                        font,
                        (int) ((((float) canvasWidth) * templeteTexts.get(i).layout_x) / 100.0f),
                        textCenterY,
                        templeteTexts.get(i).rotate,
                        templeteTexts.get(i).scale,
                        templeteTexts.get(i).padding_left,
                        templeteTexts.get(i).padding_right,
                        alignment,
                        templeteTexts.get(i).opacity,
                        templeteTexts.get(i).underLine,
                        templeteTexts.get(i).strikethrough,
                        (float) templeteTexts.get(i).letter_spacing,
                        (float) templeteTexts.get(i).line_spacing);

            } else if (str2.contains("center")) {
                c = 1;
                if (c != 0) {
                }
                j = templeteTexts.get(i).layout_id;
                textSticker = new TextStickerView(EditorActivity.this, canvasWidth, canvasHeight, fontProvider);
                if (templeteTexts.get(i).position.bottom != null) {
                    TextStickerView textStickerView = allTextSticker.get(Integer.parseInt(templeteTexts.get(i).position.bottom));
                    textCenterY = (int) (((textStickerView.getTextCenterY() + ((float) ((textStickerView.getTextHeight() * 70) / 100))) + ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_top))) - ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_bottom)));
                } else {

                    textCenterY = (int) ((((float) canvasHeight) * templeteTexts.get(i).layout_y) / 100.0f);

                }

                createTextStickView(templeteTexts.get(i).id,
                        templeteTexts.get(i).text,
                        font,
                        (int) ((((float) canvasWidth) * templeteTexts.get(i).layout_x) / 100.0f),
                        textCenterY,
                        templeteTexts.get(i).rotate,
                        templeteTexts.get(i).scale,
                        templeteTexts.get(i).padding_left,
                        templeteTexts.get(i).padding_right,
                        alignment,
                        templeteTexts.get(i).opacity,
                        templeteTexts.get(i).underLine,
                        templeteTexts.get(i).strikethrough,
                        (float) templeteTexts.get(i).letter_spacing,
                        (float) templeteTexts.get(i).line_spacing);
            } else if (str2.contains("left")) {

                c = 0;

                if (c != 0) {
                }
                j = templeteTexts.get(i).layout_id;
                textSticker = new TextStickerView(EditorActivity.this, canvasWidth, canvasHeight, fontProvider);
                if (templeteTexts.get(i).position.bottom != null) {
                    TextStickerView textStickerView = allTextSticker.get(Integer.parseInt(templeteTexts.get(i).position.bottom));
                    textCenterY = (int) (((textStickerView.getTextCenterY() + ((float) ((textStickerView.getTextHeight() * 70) / 100))) + ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_top))) - ((float) DensityUtil.dp2px(EditorActivity.this, (float) templeteTexts.get(i).margin_bottom)));
                } else {
                    textCenterY = (int) ((((float) canvasHeight) * templeteTexts.get(i).layout_y) / 100.0f);
                }

                createTextStickView(templeteTexts.get(i).id,
                        templeteTexts.get(i).text,
                        font,
                        (int) ((((float) canvasWidth) * templeteTexts.get(i).layout_x) / 100.0f),
                        textCenterY,
                        templeteTexts.get(i).rotate,
                        templeteTexts.get(i).scale,
                        templeteTexts.get(i).padding_left,
                        templeteTexts.get(i).padding_right,
                        alignment,
                        templeteTexts.get(i).opacity,
                        templeteTexts.get(i).underLine,
                        templeteTexts.get(i).strikethrough,
                        (float) templeteTexts.get(i).letter_spacing,
                        (float) templeteTexts.get(i).line_spacing);
            }
            i += 1;
        }

        selectedBgColor = template.background_color;
        selectedBgGradient = template.background_gradient;

        if (selectedBgColor != null) {
            binding.ivBackground.setVisibility(View.GONE);
            binding.vBackground.setBackgroundColor(Color.parseColor(selectedBgColor));
        } else if (selectedBgGradient != null) {
            binding.ivBackground.setVisibility(View.GONE);
            gradientTypeBg = template.gradient_type;
            linearDirectionBg = template.gradient_linear_direction;
            changeBackground(null, selectedBgGradient, null);
        } else {
            z = false;
            findViewById(R.id.menu_background).setVisibility(View.GONE);
            setRoundedRect();
            firstLaunch = z;
        }
        z = false;
        setRoundedRect();
        firstLaunch = z;
    }

    public void setupDraft() {

        boolean z;

        try {
            draft = new Gson().fromJson(AppUtil.inputStreamToString(new FileInputStream(new File(getIntent().getStringExtra("savePath")))), Draft.class);
            Iterator it = draft.photos.iterator();
            while (it.hasNext()) {

                draftPhotoIds.add(((Photo) it.next()).id);

            }
            setMediaOptions(templateViewGroup, false);
            draftFolder = draft.draft_name;
            draftTexts = draft.texts;
            if (draftTexts != null) {
                int i = 0;
                while (i < draftTexts.size()) {
                    try {
                        Font font = new Font();
                        font.setColor(draftTexts.get(i).color);
                        font.setSize(draftTexts.get(i).size);
                        font.setCategory(draftTexts.get(i).font_category);
                        font.setTypeface(draftTexts.get(i).font_name);
                        font.setGradient(AppUtil.strTOStrArray(draftTexts.get(i).gradient, " "));
                        font.setGradientType(draftTexts.get(i).gradient_type);
                        font.setLinearDirection(draftTexts.get(i).linear_direction);
                        font.setPatternPath(draftTexts.get(i).pattern_path);
                        font.setPatternMode(draftTexts.get(i).pattern_mode);
                        font.setPatternRepeats(draftTexts.get(i).pattern_repeats);
                        Alignment alignment = Alignment.ALIGN_CENTER;
                        String str = draftTexts.get(i).align;
                        char c = 65535;
                        int hashCode = str.hashCode();
                        if (hashCode != -1371700497) {
                            if (hashCode != -1047432319) {
                                if (hashCode == 1015327489) {
                                    if (str.equals("ALIGN_OPPOSITE")) {
                                        c = 2;
                                    }
                                }
                            } else if (str.equals("ALIGN_NORMAL")) {
                                c = 0;
                            }
                        } else if (str.equals("ALIGN_CENTER")) {
                            c = 1;
                        }
                        if (c == 0) {
                            alignment = Alignment.ALIGN_NORMAL;
                        } else if (c == 1) {
                            alignment = Alignment.ALIGN_CENTER;
                        } else if (c == 2) {
                            alignment = Alignment.ALIGN_OPPOSITE;
                        }
                        Alignment alignment2 = alignment;
                        if (draftTexts.get(i).padding_left <= 100) {
                        }
                        draftTexts.get(i).padding_left = (draftTexts.get(i).padding_left * ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) / canvasWidth;
                        draftTexts.get(i).padding_right = (draftTexts.get(i).padding_right * ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION) / canvasWidth;
                        float f = (float) draftTexts.get(i).letter_spacing;
                        createTextStickView(draftTexts.get(i).id, draftTexts.get(i).text, font, draftTexts.get(i).layout_x, draftTexts.get(i).layout_y, draftTexts.get(i).rotate, draftTexts.get(i).scale, draftTexts.get(i).padding_left, draftTexts.get(i).padding_right, alignment2, draftTexts.get(i).opacity, draftTexts.get(i).underLine, draftTexts.get(i).strikethrough, f, (float) draftTexts.get(i).line_spacing);
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }

                try {
                    selectedBgColor = draft.background_color;
                    selectedBgGradient = draft.background_gradient;
                    selectedBgPattern = draft.background_photo;
                    if (selectedBgColor != null) {
                        binding.ivBackground.setVisibility(View.GONE);
                        binding.vBackground.setBackgroundColor(Color.parseColor(selectedBgColor));
                    } else if (selectedBgGradient != null) {
                        binding.ivBackground.setVisibility(View.GONE);
                        gradientTypeBg = draft.gradient_type;
                        linearDirectionBg = draft.gradient_linear_direction;
                        changeBackground(null, selectedBgGradient, null);
                    } else if (selectedBgPattern != null) {
                        z = false;
                        binding.ivBackground.setVisibility(View.VISIBLE);
                        BitmapUtil.applyBlur(EditorActivity.this, selectedBgPattern, bgSeekBars.get(1).getProgress(), binding.ivBackground);
                        binding.ivBackground.setScaleX(draft.photo_scale);
                        binding.ivBackground.setScaleY(draft.photo_scale);
                        bgSeekBars.get(0).setProgress((draft.photo_scale - 1.0f) * 100.0f);
                        bgSeekBars.get(1).setProgress((float) draft.photo_blur);
                        setRoundedRect();
                        firstLaunch = z;

                    }

                } catch (Exception e2) {
                    e2.printStackTrace();
                    finish();
                }
            }
            z = false;
            setRoundedRect();
            firstLaunch = z;
        } catch (Exception e3) {
            e3.printStackTrace();
            finish();
        }
    }


    private void setRoundedRect() {
        for (int i = 0; i < this.allLayouts.size(); i++) {
            if (this.template.layouts.get(i).rounded_rect) {
                ViewGroup viewGroup = (ViewGroup) this.allLayouts.get(i);
                if (viewGroup.getChildAt(0) instanceof ShapeOfView) {
                    ((ShapeOfView) viewGroup.getChildAt(0)).setDrawable(AppUtil.getRoundedRect(this, getResources().getColor(R.color.colorWhite), this.template.layouts.get(i).topLeftRadius, this.template.layouts.get(i).topRightRadius, this.template.layouts.get(i).bottomLeftRadius, this.template.layouts.get(i).bottomRightRadius));
                }
            }
        }
    }

    private void setMediaOptions(@NonNull ViewGroup viewGroup, boolean z) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ShapeOfView) {
                ShapeOfView shapeOfView = (ShapeOfView) childAt;
                shapeOfView.setDrawingCacheEnabled(false);
                shapeOfView.buildDrawingCache(false);
                if (shapeOfView.getChildCount() > 1 && !shapeOfView.getChildAt(1).isShown()) {
                    this.mediaMasks.add(shapeOfView.getChildAt(1));
                }
            }

            if (childAt instanceof PhotoView) {
                final PhotoView photoView = (PhotoView) childAt;
                photoView.setTag(this.photoTag);
                this.photoTag++;
                if (!z && this.isDraft && this.draftPhotoIds.contains(photoView.getTag().toString())) {
                    String str = this.draft.photos.get(this.draftPhotoIds.indexOf(photoView.getTag().toString())).path;
                    Bitmap decodeFile = BitmapFactory.decodeFile(str, null);
                    this.addedPhotos.add(photoView);
                    photoView.setFullScreen(true, false);
                    photoView.enableImageTransforms(true);
                    photoView.setCenterCropScaleType(true);
                    photoView.bindPhoto(decodeFile);
                    photoView.setPhotoPath(str);
                    photoView.setVisibility(View.VISIBLE);
                }

                ViewGroup viewGroup2 = (ViewGroup) photoView.getParent();
                viewGroup2.setOnDragListener(new DragListener());
                final ViewGroup viewGroup3 = (ViewGroup) viewGroup2.getParent().getParent();
                final ViewGroup viewGroup4 = (ViewGroup) ((ViewGroup) viewGroup3.getChildAt(2)).getChildAt(0);
                this.fabControllers.add(viewGroup4);
                for (int i2 = 0; i2 < this.allLayouts.size(); i2++) {
                    if (this.allLayouts.get(i2).equals(viewGroup3)) {
                        photoView.setPhotoRotation(this.template.layouts.get(i2).rotation);
                    }
                }

                photoView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {

                        if (!EditorActivity.this.txBtnClicked) {
                            EditorActivity.this.isClickable();
                            EditorActivity.this.hideControllersFab(viewGroup4);
                            setCurrentTextStickerEdit(false, currentTextSticker);
                            setCurrentImgStickerEdit(false, currentImgSticker);

                            EditorActivity.this.photoClicked = photoView;
                            if (viewGroup4.isShown()) {
                                viewGroup4.setVisibility(View.GONE);
                                return;
                            }
                            viewGroup4.setVisibility(View.VISIBLE);
                            viewGroup4.getChildAt(0).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {
                                    EditorActivity.this.isClickable();
                                    EditorActivity.this.addedPhotos.remove(EditorActivity.this.photoClicked);
                                    EditorActivity.this.photoClicked.clearDrawable();
                                    viewGroup4.setVisibility(View.INVISIBLE);
                                    viewGroup3.getChildAt(1).setVisibility(View.VISIBLE);
                                    EditorActivity.this.photoClicked.setVisibility(View.INVISIBLE);
                                    EditorActivity.this.photoClicked = null;
                                }
                            });
                            viewGroup4.getChildAt(1).setOnClickListener(new OnClickListener() {
                                public void onClick(View view) {

                                    if (SDK_INT >= 33) {
                                        if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_MEDIA_IMAGES")) {
                                            EditorActivity.this.findViewById(R.id.fl_fragment).setVisibility(View.VISIBLE);
                                            EditorActivity.this.addFragment(FiltersFrag.getInstance(EditorActivity.this.photoClicked.getPhoto()), R.id.fl_fragment, 0, 0);
                                        }
                                    } else if (SDK_INT >= 30) {
                                        if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(EditorActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            EditorActivity.this.findViewById(R.id.fl_fragment).setVisibility(View.VISIBLE);
                                            EditorActivity.this.addFragment(FiltersFrag.getInstance(EditorActivity.this.photoClicked.getPhoto()), R.id.fl_fragment, 0, 0);
                                        }
                                    } else {
                                        if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(EditorActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            EditorActivity.this.findViewById(R.id.fl_fragment).setVisibility(View.VISIBLE);
                                            EditorActivity.this.addFragment(FiltersFrag.getInstance(EditorActivity.this.photoClicked.getPhoto()), R.id.fl_fragment, 0, 0);
                                        }
                                    }

                                }
                            });
                            viewGroup4.getChildAt(1).setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            boolean z2 = childAt instanceof PhotoView;

            if (z2) {
                PhotoView photoView = (PhotoView) childAt;
                final ViewGroup viewGroup8 = (ViewGroup) photoView.getParent();
                final ViewGroup viewGroup9 = (ViewGroup) ((ViewGroup) viewGroup8.getParent()).getParent();
                if (viewGroup9.getChildAt(1) instanceof RelativeLayout) {
                    viewGroup9.getChildAt(1).setOnClickListener(new OnOneOffClickListener() {
                        public void onOneClick(View view) {
                            if (!EditorActivity.this.txBtnClicked) {
                                EditorActivity.this.isClickable();
                                EditorActivity.this.hideControllersFab(null);
                                EditorActivity.this.overlayClicked = (RelativeLayout) viewGroup9.getChildAt(1);
                                setCurrentTextStickerEdit(false, currentTextSticker);
                                setCurrentImgStickerEdit(false, currentImgSticker);

                                EditorActivity.this.photoClicked = (PhotoView) viewGroup8.getChildAt(0);
                                selectedViewRatio = AppUtil.convertDecimalToFraction((float) overlayClicked.getMeasuredWidth(), (float) EditorActivity.this.overlayClicked.getMeasuredHeight());
                                Intent intent = new Intent(EditorActivity.this, Gallery.class);
                                intent.putExtra("title", "Select media");
                                intent.putExtra("mode", 0);
                                intent.putExtra("maxSelection", 1);
                                EditorActivity.this.startActivityForResult(intent, 0);
                                reset();
                            }
                        }
                    });
                }
                if (viewGroup8.getChildAt(0).isShown() || photoView.isShown()) {
                    viewGroup9.getChildAt(1).setVisibility(View.INVISIBLE);
                }
            } else if (childAt instanceof ViewGroup) {
                setMediaOptions((ViewGroup) childAt, z);
            }
        }
    }


    public void updateMediaOrder(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof PhotoView) {
                PhotoView photoView = (PhotoView) childAt;
                if (photoView.isShown()) {
                    this.addedPhotos.add(photoView);
                }
            } else if (childAt instanceof ViewGroup) {
                updateMediaOrder((ViewGroup) childAt);
            }
        }
    }


    public void setSelectedPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            String sb2 = "photo-" +
                    AppUtil.getCurrentTime() +
                    ".png";
            String str = "/";
            String sb4 = this.draftsPath +
                    str +
                    this.draftFolder +
                    str;
            BitmapUtil.savePhoto(bitmap, sb4, sb2);
            PhotoView photoView = this.photoClicked;
            if (photoView != null) {
                photoView.setVisibility(View.VISIBLE);
            }
            this.overlayClicked.setVisibility(View.INVISIBLE);
            PhotoView photoView2 = this.photoClicked;
            if (photoView2 != null) {
                this.addedPhotos.add(photoView2);
                this.photoClicked.setFullScreen(true, false);
                this.photoClicked.enableImageTransforms(true);
                this.photoClicked.setCenterCropScaleType(true);
                this.photoClicked.bindPhoto(bitmap);
                PhotoView photoView3 = this.photoClicked;
                String sb5 = sb4 +
                        sb2;
                photoView3.setPhotoPath(sb5);
                Log.e("photoView", "==>" + photoView3.getPhotoPath());
            }
        }
        onBackPressed();
    }

    private void createTextStickView(int i, String str, Font font, int i2, int i3, float f, float f2, int i4, int i5, Alignment alignment, int i6, boolean z, boolean z2, float f3, float f4) {

        this.textSticker = new TextStickerView(this, this.canvasWidth, this.canvasHeight, this.fontProvider);

        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(10);

        this.textSticker.setLayoutParams(layoutParams);
        this.textSticker.setText(str);
        this.textSticker.setLayoutX(i2);
        this.textSticker.setLayoutY(i3);
        this.textSticker.setRotateAngle(f);
        this.textSticker.setScale(f2);
        this.textSticker.setPaddingLeft(i4);
        this.textSticker.setPaddingRight(i5);
        this.textSticker.setFont(font);
        this.textSticker.setAlign(alignment);
        this.textSticker.setOpacity(i6);
        this.textSticker.setUnderLine(z);
        this.textSticker.setStrikethrough(z2);
        this.textSticker.setLetterSpacing(f3);
        this.textSticker.setLineSpacing(f4);
        this.textSticker.setTag(i);

        this.textSticker.setOperationListener(new TextStickerView.OperationListener() {
            public void onUnselect(TextStickerView textStickerView) {
            }

            public void onDelete(TextStickerView textStickerView) {
                if (EditorActivity.this.currentTextSticker != null && textStickerView.getTag().equals(EditorActivity.this.currentTextSticker.getTag()) && textStickerView.isShowHelpBox()) {
                    if (EditorActivity.this.binding.wgTextEdit.getRoot().isShown()) {
                        EditorActivity.this.setCurrentTextStickerEdit(false, textStickerView);
                    }
                    EditorActivity.this.binding.flTextSticker.removeView(textStickerView);
                    EditorActivity.this.allTextSticker.remove(textStickerView);
                }
                EditorActivity.this.setTxBtnClicked();
            }

            public void onEdit(TextStickerView textStickerView) {
                EditorActivity.this.bgClose();
                EditorActivity.this.setCurrentTextStickerEdit(true, textStickerView);
                EditorActivity.this.initTextEntitiesValues(textStickerView);
                EditorActivity.this.setTxBtnClicked();
            }

            public void onTouch(TextStickerView textStickerView) {
                EditorActivity.this.touchTextSticker = textStickerView;
                EditorActivity.this.setTxBtnClicked();
            }

            public void onSelect(TextStickerView textStickerView) {

                setCurrentImgStickerEdit(false, imgSticker);

                Iterator it = EditorActivity.this.fabControllers.iterator();
                while (it.hasNext()) {
                    ((ViewGroup) it.next()).setVisibility(View.INVISIBLE);
                }
                if (EditorActivity.this.currentTextSticker != null) {
                    EditorActivity.this.currentTextSticker.setInEdit(false);
                }
                EditorActivity.this.currentTextSticker = textStickerView;
                EditorActivity.this.currentTextSticker.setInEdit(true);
                EditorActivity.this.currentTextSticker.setShowHelpBox(true);
                EditorActivity.this.setTxBtnClicked();
            }
        });

        this.binding.flTextSticker.addView(this.textSticker);
        this.allTextSticker.add(this.textSticker);
        setCurrentTextStickerEdit(true, this.textSticker);
    }

    private void initTextEntitiesListeners() {

        this.binding.wgTextEdit.etTextEditor.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (EditorActivity.this.currentTextSticker != null) {
                    EditorActivity.this.currentTextSticker.setText(editable.toString());
                }
            }
        });

        this.binding.wgTextEdit.etTextEditor.setOnClickListener(view -> {
            EditorActivity.this.findViewById(R.id.iv_text_keyboard).setVisibility(View.GONE);
            EditorActivity.this.findViewById(R.id.iv_text_edit).setVisibility(View.VISIBLE);
            EditorActivity.this.binding.wgTextEdit.ntbTextEditor.setVisibility(View.GONE);
            EditorActivity.this.findViewById(R.id.swg_text_editor).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.iv_text_done).setOnClickListener(view -> setCurrentTextStickerEdit(false, currentTextSticker));

        findViewById(R.id.iv_text_edit).setOnClickListener(view -> {
            initTextEntitiesValues(currentTextSticker);
            AppUtil.hideKeyboard(EditorActivity.this, binding.wgTextEdit.etTextEditor);
            EditorActivity.this.findViewById(R.id.iv_text_keyboard).setVisibility(View.VISIBLE);
            EditorActivity.this.findViewById(R.id.iv_text_edit).setVisibility(View.GONE);
            EditorActivity.this.binding.wgTextEdit.ntbTextEditor.setVisibility(View.VISIBLE);
            EditorActivity.this.findViewById(R.id.swg_text_editor).setVisibility(View.VISIBLE);
            EditorActivity.this.findViewById(R.id.swg_text_editor).post(() -> {
                if (EditorActivity.this.currentTextSticker.getLayoutY() <= EditorActivity.this.centreY - ((EditorActivity.this.canvasHeight * 10) / 100)) {
                    EditorActivity.this.params.setMargins(0, 0, 0, DensityUtil.dp2px(EditorActivity.this, 80.0f));
                } else {
                    EditorActivity.this.params.setMargins(0, ((-EditorActivity.this.screenHeight) / 2) + EditorActivity.this.findViewById(R.id.swg_text_editor).getMeasuredHeight(), 0, 0);
                }
                EditorActivity.this.binding.rlContainer.setLayoutParams(EditorActivity.this.params);
            });
        });

        findViewById(R.id.iv_text_keyboard).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AppUtil.showKeyboard(EditorActivity.this, binding.wgTextEdit.etTextEditor);
                if (EditorActivity.this.currentTextSticker.getLayoutY() <= EditorActivity.this.centreY - ((EditorActivity.this.canvasHeight * 10) / 100)) {
                    EditorActivity.this.params.setMargins(0, 0, 0, 0);
                } else {
                    EditorActivity.this.params.setMargins(0, ((-EditorActivity.this.screenHeight) / 2) + DensityUtil.dp2px(EditorActivity.this, 80.0f), 0, 0);
                }
                EditorActivity.this.binding.rlContainer.setLayoutParams(EditorActivity.this.params);
                EditorActivity.this.binding.wgTextEdit.ntbTextEditor.setVisibility(View.GONE);
                EditorActivity.this.findViewById(R.id.swg_text_editor).setVisibility(View.VISIBLE);
                EditorActivity.this.findViewById(R.id.iv_text_keyboard).setVisibility(View.GONE);
                EditorActivity.this.findViewById(R.id.iv_text_edit).setVisibility(View.VISIBLE);
            }
        });

        binding.wgTextEdit.ivAlignLeft.setOnClickListener(view -> {
            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.align_left_s);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.ic_text_align_center);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.ic_text_align_right);
            EditorActivity.this.currentTextSticker.setAlign(Alignment.ALIGN_NORMAL);
        });

        binding.wgTextEdit.ivAlignCenter.setOnClickListener(view -> {
            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.ic_text_align_left);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.center_s);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.ic_text_align_right);
            EditorActivity.this.currentTextSticker.setAlign(Alignment.ALIGN_CENTER);
        });

        binding.wgTextEdit.ivAlignRight.setOnClickListener(view -> {
            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.ic_text_align_left);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.ic_text_align_center);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.align_right_s);
            EditorActivity.this.currentTextSticker.setAlign(Alignment.ALIGN_OPPOSITE);
        });
        binding.wgTextEdit.ivTextUnderline.setOnClickListener(view -> {
            if (EditorActivity.this.currentTextSticker.isUnderLine()) {
                binding.wgTextEdit.ivTextUnderline.setImageResource(R.drawable.ic_underline);
                EditorActivity.this.currentTextSticker.setUnderLine(false);
                return;
            }
            binding.wgTextEdit.ivTextUnderline.setImageResource(R.drawable.underline_s);
            EditorActivity.this.currentTextSticker.setUnderLine(true);
        });

        binding.wgTextEdit.ivTextStrikethrough.setOnClickListener(view -> {
            if (EditorActivity.this.currentTextSticker.isStrikethrough()) {
                binding.wgTextEdit.ivTextStrikethrough.setImageResource(R.drawable.ic_text_strikethrough);
                EditorActivity.this.currentTextSticker.setStrikethrough(false);
                return;
            }
            binding.wgTextEdit.ivTextStrikethrough.setImageResource(R.drawable.text_line_s);
            EditorActivity.this.currentTextSticker.setStrikethrough(true);
        });

        findViewById(R.id.iv_quote).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                TextStickerView currentTextSticker1 = EditorActivity.this.currentTextSticker;
                String str = "\"";
                String sb = str +
                        EditorActivity.this.currentTextSticker.getText() +
                        str;
                currentTextSticker1.setText(sb);
            }
        });

        this.teSeekBars.get(0).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.getFont().setSize((seekParams.progressFloat * 3.0f) / 1000.0f);
                EditorActivity.this.currentTextSticker.invalidate();
            }
        });

        this.teSeekBars.get(1).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.setOpacity((seekParams.progress * 255) / 100);
                EditorActivity.this.currentTextSticker.invalidate();
            }
        });

        this.teSeekBars.get(2).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.setLetterSpacing((float) seekParams.progress);
                EditorActivity.this.currentTextSticker.invalidate();
            }
        });

        this.teSeekBars.get(3).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.setLineSpacing((float) (seekParams.progress * 2));
                EditorActivity.this.currentTextSticker.invalidate();
            }
        });

        this.teSeekBars.get(4).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.setPaddingLeft(seekParams.progress);
            }
        });

        this.teSeekBars.get(5).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.currentTextSticker.setPaddingRight(seekParams.progress);
            }
        });

        setTexEditorTabs();

        this.fontCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.font_categories)));
        for (int i = 0; i < this.fontCategories.size(); i++) {
            TabLayout tabLayout = binding.wgTextEdit.tlFontCategories;
            tabLayout.addTab(tabLayout.newTab().setText(this.fontCategories.get(i)));
        }
        String defaultFontCategory = this.fontProvider.getDefaultFontCategory();
        FontProvider fontProvider2 = this.fontProvider;
        List fontNames = fontProvider2.getFontNames(fontProvider2.getDefaultFontCategory());
        FontProvider fontProvider3 = this.fontProvider;
        this.rvFontAdapter = new RvFontAdapter(this, defaultFontCategory, fontNames, fontProvider3, fontProvider3.getDefaultFontName(), this.screenWidth);
        binding.wgTextEdit.rvFontDetail.setAdapter(this.rvFontAdapter);
        binding.wgTextEdit.tlFontCategories.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) new TabLayout.OnTabSelectedListener() {
            public void onTabReselected(TabLayout.Tab tab) {
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabSelected(TabLayout.Tab tab) {
                RvFontAdapter rvFontAdapter = new RvFontAdapter(EditorActivity.this, tab.getText().toString(), EditorActivity.this.fontProvider.getFontNames(tab.getText().toString()), EditorActivity.this.fontProvider, EditorActivity.this.fontProvider.getDefaultFontName(), EditorActivity.this.screenWidth);
                EditorActivity.this.rvFontAdapter = rvFontAdapter;
                EditorActivity.this.binding.wgTextEdit.rvFontDetail.setAdapter(EditorActivity.this.rvFontAdapter);
            }
        });

        this.rvColorAdapter = new RvColorsAdapter(this, this.colorList, false, this.screenWidth);
        this.binding.wgBackgroundMenu.rvBgColor.setLayoutManager(new GridLayoutManager(this, 9, RecyclerView.VERTICAL, false));
        this.binding.wgBackgroundMenu.rvBgColor.setAdapter(this.rvColorAdapter);
        findViewById(R.id.btn_text_custom_color).setOnClickListener(view -> {
            EditorActivity.this.findViewById(R.id.wg_custom_color).setVisibility(View.VISIBLE);
            EditorActivity.this.findViewById(R.id.wg_custom_color).startAnimation(AnimationsUtil.SlideUpIn);
        });

        final String[] stringArray = getResources().getStringArray(R.array.gradient_palette);
        this.rvGradientAdapter = new RvGradientAdapter(this, stringArray, this.gradientType, this.linearDirection, false, this.screenWidth);
        binding.wgTextEdit.rvGradients.setLayoutManager(new GridLayoutManager(this, 9, RecyclerView.VERTICAL, false));
        binding.wgTextEdit.rvGradients.setAdapter(this.rvGradientAdapter);
        binding.wgTextEdit.msgType.setItems(getResources().getStringArray(R.array.directions_menu));
        binding.wgTextEdit.msgType.setOnItemSelectedListener((materialSpinner, i, j, obj) -> {
            gradientType = directionsMenu[i];
            rvGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientType, EditorActivity.this.linearDirection, false, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgTextEdit.rvGradients.setAdapter(EditorActivity.this.rvGradientAdapter);
        });

        findViewById(R.id.iv_gradientH).setOnClickListener(view -> {
            EditorActivity.this.linearDirection = "Horizontal";
            rvGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientType, EditorActivity.this.linearDirection, false, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgTextEdit.rvGradients.setAdapter(EditorActivity.this.rvGradientAdapter);
        });

        findViewById(R.id.iv_gradientV).setOnClickListener(view -> {
            EditorActivity.this.linearDirection = "Vertical";
            rvGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientType, EditorActivity.this.linearDirection, false, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgTextEdit.rvGradients.setAdapter(EditorActivity.this.rvGradientAdapter);
        });
    }

    private void setTexEditorTabs() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_align), getResources().getColor(R.color.colorGrey)).build());
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_adjust), getResources().getColor(R.color.colorGrey)).build());
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_font), getResources().getColor(R.color.colorGrey)).build());
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_color), getResources().getColor(R.color.colorGrey)).build());
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_gradient), getResources().getColor(R.color.colorGrey)).build());

        this.binding.wgTextEdit.ntbTextEditor.setModels(arrayList);
        this.binding.wgTextEdit.ntbTextEditor.setModelIndex(0);
        this.binding.wgTextEdit.ntbTextEditor.getLayoutParams().width = arrayList.size() * DensityUtil.dp2px(this, 35.0f);
        this.binding.wgTextEdit.ntbTextEditor.setOnTabBarSelectedIndexListener(new OnTabBarSelectedIndexListener() {
            public void onEndTabSelected(Model model, int i) {
            }

            public void onStartTabSelected(Model model, int i) {
                if (i == 0) {
                    EditorActivity.this.findViewById(R.id.sswg_text_align).setVisibility(View.VISIBLE);
                    EditorActivity.this.findViewById(R.id.sswg_text_adjust).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_font).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_color).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_gradient).setVisibility(View.GONE);
                } else if (i == 1) {
                    EditorActivity.this.findViewById(R.id.sswg_text_align).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_adjust).setVisibility(View.VISIBLE);
                    EditorActivity.this.findViewById(R.id.sswg_text_font).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_color).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_gradient).setVisibility(View.GONE);
                } else if (i == 2) {
                    EditorActivity.this.findViewById(R.id.sswg_text_align).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_adjust).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_font).setVisibility(View.VISIBLE);
                    EditorActivity.this.findViewById(R.id.sswg_text_color).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_gradient).setVisibility(View.GONE);
                } else if (i == 3) {
                    EditorActivity.this.findViewById(R.id.sswg_text_align).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_adjust).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_font).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_color).setVisibility(View.VISIBLE);
                    EditorActivity.this.findViewById(R.id.sswg_text_gradient).setVisibility(View.GONE);
                } else if (i == 4) {
                    EditorActivity.this.findViewById(R.id.sswg_text_align).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_adjust).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_font).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_color).setVisibility(View.GONE);
                    EditorActivity.this.findViewById(R.id.sswg_text_gradient).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initTextEntitiesValues(TextStickerView textStickerView) {
        int i = AnonymousClass67.SwitchMapandroidtextLayoutAlignment[textStickerView.getAlign().ordinal()];
        if (i == 1) {
            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.align_left_s);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.ic_text_align_center);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.ic_text_align_right);
        } else if (i == 2) {
            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.ic_text_align_left);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.center_s);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.ic_text_align_right);

        } else if (i == 3) {

            binding.wgTextEdit.ivAlignLeft.setImageResource(R.drawable.ic_text_align_left);
            binding.wgTextEdit.ivAlignCenter.setImageResource(R.drawable.ic_text_align_center);
            binding.wgTextEdit.ivAlignRight.setImageResource(R.drawable.align_right_s);

        }

        if (textStickerView.isUnderLine()) {
            binding.wgTextEdit.ivTextUnderline.setImageResource(R.drawable.underline_s);
        } else {
            binding.wgTextEdit.ivTextUnderline.setImageResource(R.drawable.ic_underline);
        }
        if (textStickerView.isStrikethrough()) {
            binding.wgTextEdit.ivTextStrikethrough.setImageResource(R.drawable.text_line_s);
        } else {
            binding.wgTextEdit.ivTextStrikethrough.setImageResource(R.drawable.ic_text_strikethrough);
        }
        this.teSeekBars.get(0).setProgress((textStickerView.getFont().getSize() * 1000.0f) / 3.0f);
        this.teSeekBars.get(1).setProgress((float) ((textStickerView.getOpacity() * 100) / 255));
        this.teSeekBars.get(2).setProgress(textStickerView.getLetterSpacing());
        this.teSeekBars.get(3).setProgress(textStickerView.getLineSpacing() / 2.0f);
        this.teSeekBars.get(4).setProgress((float) textStickerView.getPaddingLeft());
        this.teSeekBars.get(5).setProgress((float) textStickerView.getPaddingRight());
        this.binding.wgTextEdit.tlFontCategories.getTabAt(this.fontCategories.indexOf(textStickerView.getFont().getCategory())).select();
        this.rvFontAdapter = new RvFontAdapter(this, textStickerView.getFont().getCategory(), this.fontProvider.getFontNames(textStickerView.getFont().getCategory()), this.fontProvider, textStickerView.getFont().getTypeface(), this.screenWidth);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        int fontPosition = this.fontProvider.getFontPosition(textStickerView.getFont().getCategory(), textStickerView.getFont().getTypeface());
        int i2 = this.screenWidth;
        int i3 = i2 / 2;
        double d = i2;
        Double.isNaN(d);
        linearLayoutManager.scrollToPositionWithOffset(fontPosition, (i3 - (((int) (d / 3.5d)) / 2)) - DensityUtil.dp2px(this, 6.0f));
        this.binding.wgTextEdit.rvFontDetail.setLayoutManager(linearLayoutManager);
        this.binding.wgTextEdit.rvFontDetail.setAdapter(this.rvFontAdapter);
    }

    public void changeTextEntityFont(String str, String str2) {
        this.currentTextSticker.getFont().setCategory(str);
        this.currentTextSticker.getFont().setTypeface(str2);
        this.currentTextSticker.invalidate();
    }

    public void changeTextEntityColor(String str) {
        this.currentTextSticker.getFont().setColor(Color.parseColor(str));
        this.currentTextSticker.getFont().setGradient(null);
        this.currentTextSticker.getFont().setPatternPath(null);
        this.currentTextSticker.invalidate();
    }

    public void changeTextEntityGradient(String[] strArr) {
        this.currentTextSticker.getFont().setGradient(strArr);
        this.currentTextSticker.getFont().setGradientType(this.gradientType);
        this.currentTextSticker.getFont().setLinearDirection(this.linearDirection);
        this.currentTextSticker.getFont().setPatternPath(null);
        this.currentTextSticker.invalidate();
    }

    private void setTxBtnClicked() {
        this.txBtnClicked = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                EditorActivity.this.txBtnClicked = false;
                setMediaOptions(templateViewGroup, false);
            }
        }, 1500);
    }

    public void setCurrentTextStickerEdit(boolean z, TextStickerView textStickerView) {

        if (!this.firstLaunch) {
            TextStickerView textStickerView2 = this.currentTextSticker;
            if (textStickerView2 != null) {
                textStickerView2.setInEdit(false);
            }
            this.currentTextSticker = textStickerView;
            if (z) {
                this.binding.wgTextEdit.etTextEditor.setText(this.currentTextSticker.getText());
                EditText editText = this.binding.wgTextEdit.etTextEditor;
                editText.setSelection(editText.getText().length());
                if (this.currentTextSticker.getLayoutY() <= this.centreY - ((this.canvasHeight * 10) / 100)) {
                    this.params.setMargins(0, 0, 0, 0);
                } else {
                    this.params.setMargins(0, ((-this.screenHeight) / 2) + DensityUtil.dp2px(this, 00.0f), 0, 0);
                }
                this.binding.rlContainer.setLayoutParams(this.params);
                AppUtil.showKeyboard(this, this.binding.wgTextEdit.etTextEditor);
                AppUtil.showWidget(this.vWidgets, findViewById(R.id.wg_text_edit));
                this.currentTextSticker.setInEdit(true);
            } else if (textStickerView != null) {
                textStickerView.setInEdit(false);

                if (findViewById(R.id.wg_main_menu).getVisibility() == View.GONE) {
                    this.params.setMargins(0, 0, 0, DensityUtil.dp2px(this, 00.0f));
                    this.binding.rlContainer.setLayoutParams(this.params);
                    AppUtil.hideKeyboard(this, this.binding.wgTextEdit.etTextEditor);
                    AppUtil.showWidget(this.vWidgets, findViewById(R.id.wg_main_menu));
                }
                this.currentTextSticker = null;
            }
            findViewById(R.id.iv_text_keyboard).setVisibility(View.GONE);
            findViewById(R.id.iv_text_edit).setVisibility(View.VISIBLE);
            this.binding.wgTextEdit.ntbTextEditor.setVisibility(View.GONE);
            findViewById(R.id.swg_text_editor).setVisibility(View.VISIBLE);

        } else if (textStickerView != null) {
            textStickerView.setInEdit(false);
        }
    }

    public void setCurrentImgStickerEdit(boolean z, ImageStickerView imageStickerView) {

        if (!this.firstLaunch) {
            ImageStickerView imageStickerView2 = this.currentImgSticker;
            if (imageStickerView2 != null) {
                imageStickerView2.setInEdit(false);
            }
            this.currentImgSticker = imageStickerView;
            if (z) {
                imageStickerView.setInEdit(true);
            } else if (imageStickerView != null) {
                imageStickerView.setInEdit(false);
            }
        } else if (imageStickerView != null) {
            imageStickerView.setInEdit(false);
        }
    }

    private void setBackgroundListeners() {
        setBgEditorTabs();
        this.rvColorAdapter = new RvColorsAdapter(this, this.colorList, true, this.screenWidth);
        this.binding.wgBackgroundMenu.rvBgColor.setLayoutManager(new GridLayoutManager(this, 9, RecyclerView.VERTICAL, false));
        this.binding.wgBackgroundMenu.rvBgColor.setAdapter(this.rvColorAdapter);

        findViewById(R.id.btn_bg_custom_color).setOnClickListener(view -> {
            EditorActivity.this.findViewById(R.id.wg_custom_color).setVisibility(View.VISIBLE);
            EditorActivity.this.findViewById(R.id.wg_custom_color).startAnimation(AnimationsUtil.SlideUpIn);
        });

        findViewById(R.id.btn_custom_color_close).setOnClickListener(view -> {
            EditorActivity.this.findViewById(R.id.wg_custom_color).startAnimation(AnimationsUtil.SlideUpOut);
            EditorActivity.this.findViewById(R.id.wg_custom_color).setVisibility(View.GONE);
        });

        final String[] stringArray = getResources().getStringArray(R.array.gradient_palette);
        this.rvBgGradientAdapter = new RvGradientAdapter(this, stringArray, this.gradientTypeBg, this.linearDirectionBg, true, this.screenWidth);
        this.binding.wgBackgroundMenu.rvBgGradients.setLayoutManager(new GridLayoutManager(this, 9, RecyclerView.VERTICAL, false));
        this.binding.wgBackgroundMenu.rvBgGradients.setAdapter(this.rvBgGradientAdapter);
        this.binding.wgBackgroundMenu.spbggType.setItems(getResources().getStringArray(R.array.directions_menu));
        this.binding.wgBackgroundMenu.spbggType.setOnItemSelectedListener((materialSpinner, i, j, obj) -> {
            gradientTypeBg = directionsMenu[i];
            rvBgGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientTypeBg, EditorActivity.this.linearDirectionBg, true, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgBackgroundMenu.rvBgGradients.setAdapter(EditorActivity.this.rvBgGradientAdapter);
        });

        findViewById(R.id.iv_bg_gradientH).setOnClickListener(view -> {
            EditorActivity.this.linearDirectionBg = "Horizontal";
            rvBgGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientTypeBg, EditorActivity.this.linearDirectionBg, true, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgBackgroundMenu.rvBgGradients.setAdapter(EditorActivity.this.rvBgGradientAdapter);
        });

        findViewById(R.id.iv_bg_gradientV).setOnClickListener(view -> {
            EditorActivity.this.linearDirectionBg = "Vertical";
            rvBgGradientAdapter = new RvGradientAdapter(EditorActivity.this, stringArray, gradientTypeBg, EditorActivity.this.linearDirectionBg, true, EditorActivity.this.screenWidth);
            EditorActivity.this.binding.wgBackgroundMenu.rvBgGradients.setAdapter(EditorActivity.this.rvBgGradientAdapter);
        });

        this.bgSeekBars.get(0).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                EditorActivity.this.binding.ivBackground.setScaleX((((float) seekParams.progress) / 1000.0f));
                EditorActivity.this.binding.ivBackground.setScaleY((((float) seekParams.progress) / 1000.0f));
            }
        });

        this.bgSeekBars.get(1).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                if (EditorActivity.this.selectedBgPattern != null) {
                    BitmapUtil.applyBlur(EditorActivity.this, EditorActivity.this.selectedBgPattern, seekParams.progress, EditorActivity.this.binding.ivBackground);
                    return;
                }
                Toast.makeText(EditorActivity.this, EditorActivity.this.getString(R.string.MSG_SELECT_PATTERN), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBgEditorTabs() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_color), getResources().getColor(R.color.colorGrey)).build());
        arrayList.add(new Builder(getResources().getDrawable(R.drawable.icon_text_gradient), getResources().getColor(R.color.colorGrey)).build());

        this.binding.wgBackgroundMenu.ntbBgEditor.setModels(arrayList);
        this.binding.wgBackgroundMenu.ntbBgEditor
                .setModelIndex(0);
        this.binding.wgBackgroundMenu.ntbBgEditor
                .getLayoutParams().width = arrayList.size() * DensityUtil.dp2px(this, 60.0f);
        this.binding.wgBackgroundMenu.ntbBgEditor
                .setOnTabBarSelectedIndexListener(new OnTabBarSelectedIndexListener() {
                    public void onEndTabSelected(Model model, int i) {
                    }

                    public void onStartTabSelected(Model model, int i) {
                        if (i == 0) {
                            EditorActivity.this.findViewById(R.id.sswg_bg_color).setVisibility(View.VISIBLE);
                            EditorActivity.this.findViewById(R.id.sswg_bg_gradient).setVisibility(View.GONE);

                        } else if (i == 1) {
                            EditorActivity.this.findViewById(R.id.sswg_bg_color).setVisibility(View.GONE);
                            EditorActivity.this.findViewById(R.id.sswg_bg_gradient).setVisibility(View.VISIBLE);
                            EditorActivity.this.findViewById(R.id.sswg_bg_pattern).setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void changeBackground(String str, String[] strArr, String str2) {
        if (str != null) {
            this.binding.ivBackground.setVisibility(View.GONE);
            this.binding.vBackground.setBackgroundColor(Color.parseColor(str));
            this.selectedBgColor = str;
            this.selectedBgGradient = null;
            this.selectedBgPattern = null;
        } else if (strArr != null) {
            this.binding.ivBackground.setVisibility(View.GONE);
            this.binding.vBackground.setBackgroundDrawable(null);
            this.binding.vBackground.setBackgroundDrawable(AppUtil.generateViewGradient(strArr, this.gradientTypeBg, this.linearDirectionBg, this.canvasWidth, this.canvasHeight));
            this.selectedBgColor = null;
            this.selectedBgGradient = strArr;
            this.selectedBgPattern = null;
        } else if (str2 != null) {
            this.binding.ivBackground.setVisibility(View.VISIBLE);
            BitmapUtil.applyBlur(this, str2, this.bgSeekBars.get(1).getProgress(), this.binding.ivBackground);
            this.selectedBgColor = null;
            this.selectedBgGradient = null;
            this.selectedBgPattern = str2;
        }
    }

    private void addFragment(Fragment fragment, int i, int i2, int i3) {
        this.fm.executePendingTransactions();
        FragmentTransaction beginTransaction = this.fm.beginTransaction();
        if (!(i2 == 0 && i3 == 0)) {
            beginTransaction.setCustomAnimations(i2, i3);
        }
        beginTransaction.replace(i, fragment);
        beginTransaction.commitAllowingStateLoss();
    }

    public void loading(boolean z, boolean z2) {
        if (!z) {
            findViewById(R.id.wg_loading).setVisibility(View.GONE);
        } else if (z2) {
            findViewById(R.id.wg_loading).setVisibility(View.VISIBLE);
        }
    }

    private void setCustomColorListeners() {
        final int[] iArr = {255};
        final int[] iArr2 = {255};
        final int[] iArr3 = {255};
        ((IndicatorSeekBar) findViewById(R.id.sb_red)).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                iArr[0] = seekParams.progress;
                String str = "#%02x%02x%02x";
                if (EditorActivity.this.binding.wgTextEdit.getRoot().isShown()) {
                    EditorActivity.this.changeTextEntityColor(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])));
                    return;
                }
                EditorActivity.this.changeBackground(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])), null, null);
            }
        });
        ((IndicatorSeekBar) findViewById(R.id.sb_green)).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                iArr2[0] = seekParams.progress;
                String str = "#%02x%02x%02x";
                if (EditorActivity.this.binding.wgTextEdit.getRoot().isShown()) {
                    EditorActivity.this.changeTextEntityColor(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])));
                    return;
                }
                EditorActivity.this.changeBackground(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])), null, null);
            }
        });
        ((IndicatorSeekBar) findViewById(R.id.sb_blue)).setOnSeekChangeListener(new OnSeekChangeListener() {
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            public void onSeeking(SeekParams seekParams) {
                iArr3[0] = seekParams.progress;
                String str = "#%02x%02x%02x";
                if (EditorActivity.this.binding.wgTextEdit.getRoot().isShown()) {
                    EditorActivity.this.changeTextEntityColor(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])));
                    return;
                }
                EditorActivity.this.changeBackground(String.format(str, Integer.valueOf(iArr[0]), Integer.valueOf(iArr2[0]), Integer.valueOf(iArr3[0])), null, null);
            }
        });
    }

    public void isClickable() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastClickTime >= 3000) {
            this.mLastClickTime = currentTimeMillis;
        }
    }

    private void hideControllersFab(View view) {
        Iterator it = this.fabControllers.iterator();
        while (it.hasNext()) {
            ViewGroup viewGroup = (ViewGroup) it.next();
            if (viewGroup != view) {
                viewGroup.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void removeUnusedFiles() {
        String str = "/";
        String sb = this.draftsPath +
                str +
                this.draftFolder +
                str;
        ArrayList filesPath = AppUtil.getFilesPath(sb);
        ArrayList arrayList = new ArrayList();
        Iterator it = this.draft.photos.iterator();
        while (it.hasNext()) {
            arrayList.add(((Photo) it.next()).path);
        }
        Iterator it3 = filesPath.iterator();
        while (it3.hasNext()) {
            String str2 = (String) it3.next();
            if (!str2.contains("thumb") && !arrayList.contains(str2) && new File(str2).exists()) {
                new File(str2).delete();
            }
        }
    }

    public void onPermissionGranted() {
        if (this.selectedBtn == R.id.menu_save) {

            final Dialog dialog = new Dialog(this, R.style.BottomDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.wg_resolution_alert_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            dialog.getWindow().setAttributes(lp);

            CardView btn_standard = dialog.findViewById(R.id.btn_standard);
            CardView btn_medium = dialog.findViewById(R.id.btn_medium);
            CardView btn_high = dialog.findViewById(R.id.btn_high);

            btn_standard.setOnClickListener(view -> {
                if (EditorActivity.this.totalMemory <= 1500) {
                    EditorActivity.this.saveWidth = 450;
                    EditorActivity.this.saveHeight = 800;
                } else if (EditorActivity.this.totalMemory <= 2500) {
                    EditorActivity.this.saveWidth = 720;
                    EditorActivity.this.saveHeight = 1280;
                } else if (EditorActivity.this.totalMemory <= 4500) {
                    EditorActivity.this.saveWidth = 810;
                    EditorActivity.this.saveHeight = 1440;
                } else {
                    EditorActivity.this.saveWidth = 1080;
                    EditorActivity.this.saveHeight = 1920;
                }
                dialog.dismiss();
                EditorActivity.this.isSaved = true;
                new SaveDraft(this, 0, true, "save_photo").execute();
                new SaveDraft(this, 0, true, "save_photo").execute();
            });

            btn_medium.setOnClickListener(view -> {

                if (EditorActivity.this.totalMemory <= 1500) {
                    EditorActivity.this.saveWidth = 720;
                    EditorActivity.this.saveHeight = 1280;
                } else if (EditorActivity.this.totalMemory <= 2500) {
                    EditorActivity.this.saveWidth = 810;
                    EditorActivity.this.saveHeight = 1440;
                } else if (EditorActivity.this.totalMemory < 4500) {
                    EditorActivity.this.saveWidth = 1080;
                    EditorActivity.this.saveHeight = 1920;
                } else {
                    EditorActivity.this.saveWidth = 1620;
                    EditorActivity.this.saveHeight = 2880;
                }
                dialog.dismiss();
                EditorActivity.this.isSaved = true;
                new SaveDraft(this, 0, true, "save_photo").execute();
            });

            btn_high.setOnClickListener(view -> {
                if (EditorActivity.this.totalMemory <= 1500) {
                    EditorActivity.this.saveWidth = 810;
                    EditorActivity.this.saveHeight = 1440;
                } else if (EditorActivity.this.totalMemory <= 2500) {
                    EditorActivity.this.saveWidth = 1080;
                    EditorActivity.this.saveHeight = 1920;
                } else if (EditorActivity.this.totalMemory < 4500) {
                    EditorActivity.this.saveWidth = 1620;
                    EditorActivity.this.saveHeight = 2880;
                } else {
                    EditorActivity.this.saveWidth = 2160;
                    EditorActivity.this.saveHeight = 3840;
                }
                dialog.dismiss();
                EditorActivity.this.isSaved = true;
                new SaveDraft(this, 0, true, "save_photo").execute();
            });

            dialog.show();
        }
    }

    private void imageProcessing(Bitmap bitmap) {
        String sb = "StoryMaker" +
                AppUtil.getCurrentTime() +
                ".png";
        this.outputName = sb;
        File file = new File(this.savePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File file2 = new File(file, this.outputName);
        if (file2.exists()) {
            file2.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        String str = "/";
        super.onActivityResult(i, i2, intent);
        char c = 65535;
        if (i2 == -1) {

            if (i == 0) {

                try {
                    String stringExtra = intent.getStringExtra("filePath");
                    String fileType = AppUtil.getFileType(stringExtra);
                    int hashCode = fileType.hashCode();

                    DLog.d("@@"+fileType+"@@"+hashCode);

                    if (hashCode != 71588) {
                        if (hashCode != 70760763) {
                            if (hashCode == 82650203) {
                                if (fileType.equals("Video")) {
                                    c = 2;
                                }
                            }
                        } else if (fileType.equals("Image")) {
                            c = 0;
                        }
                    } else if (fileType.equals(Registry.BUCKET_GIF)) {
                        c = 1;
                    }
                    if (c == 0) {
                        AppUtil.cropPhoto(this, new File(stringExtra), this.selectedViewRatio.get(0).intValue(), this.selectedViewRatio.get(1).intValue(), this.totalMemory);
                    } else if (c == 1) {
                    }
                } catch (Exception unused) {
                    Toasty.error(this, getString(R.string.MSG_SOMETHING_WRONG), 0, true).show();
                }
            } else if (i == 69) {
                try {
                    Bitmap bitmap = Media.getBitmap(getContentResolver(), UCrop.getOutput(intent));
                    findViewById(R.id.fl_fragment).setVisibility(View.VISIBLE);
                    addFragment(FiltersFrag.getInstance(bitmap), R.id.fl_fragment, 0, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (i == 11) {
                String stringExtra2 = intent.getStringExtra("rewardType");
                if (stringExtra2.equals(getString(R.string.TITLE_FONTS_LOCKED))) {
                    this.rvFontAdapter.refreshList();
                    this.binding.wgTextEdit.rvFontDetail.setAdapter(this.rvFontAdapter);
                } else if (stringExtra2.equals(getString(R.string.TITLE_GRADIENTS_LOCKED))) {
                    this.rvGradientAdapter.refreshList(findViewById(R.id.wg_background_menu).isShown());
                    this.binding.wgTextEdit.rvGradients.setAdapter(this.rvGradientAdapter);
                    this.binding.wgBackgroundMenu.rvBgGradients.setAdapter(this.rvGradientAdapter);
                }
                Toast.makeText(this, "Thanks for watching", Toast.LENGTH_SHORT).show();
            } else if (i == 12) {
                try {
                    addStickerView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (i2 == 96) {
            Toasty.error(this, UCrop.getError(intent).getMessage(), 0, true).show();
        }
    }

    private void addStickerView() throws IOException {
        Log.e("@@@@@@@@@@@", "onDeleteClick: _______4");

        this.d = Drawable.createFromStream(getAssets().open("crown/" + Glob_Sticker.SelectedTattooName.replace("assets://crown/", "")), null);

        ImageStickerView imageStickerView = new ImageStickerView(EditorActivity.this, "", (float) (EditorActivity.this.canvasWidth / 2), (float) (EditorActivity.this.canvasHeight / 2), 0.3f, 0.0f, EditorActivity.this.allImageSticker.size());
        Bitmap B = convertDrawableToBitmap(this.d);
        imgSticker = imageStickerView;
        imgSticker.setBitmap(B);
        EditorActivity.this.allImageSticker.remove(EditorActivity.this.currentImgSticker);
        EditorActivity.this.binding.flImgSticker.removeView(EditorActivity.this.currentImgSticker);
        Log.e("@@@@@@@@@@@", "onDeleteClick: _______3" + currentImgSticker);
        this.imgSticker.setOperationListener(new ImageStickerView.OperationListener() {
            public void onDeleteClick() {
                EditorActivity.this.allImageSticker.remove(EditorActivity.this.currentImgSticker);
                EditorActivity.this.binding.flImgSticker.removeView(EditorActivity.this.currentImgSticker);
                Log.e("@@@@@@@@@@@", "onDeleteClick: _______3" + currentImgSticker);
            }

            public void onEdit(ImageStickerView imageStickerView) {
                EditorActivity.this.bgClose();
                setCurrentTextStickerEdit(false, textSticker);

                Iterator it = EditorActivity.this.fabControllers.iterator();
                while (it.hasNext()) {
                    ((ViewGroup) it.next()).setVisibility(View.INVISIBLE);
                }
                if (EditorActivity.this.currentImgSticker != null) {
                    EditorActivity.this.currentImgSticker.setInEdit(false);
                }
                EditorActivity.this.currentImgSticker = imageStickerView;
                EditorActivity.this.currentImgSticker.setInEdit(true);
            }

            public void onTop(ImageStickerView imageStickerView) {
                int indexOf = EditorActivity.this.allImageSticker.indexOf(imageStickerView);
                if (indexOf != EditorActivity.this.allImageSticker.size() - 1) {
                    EditorActivity.this.allImageSticker.add(EditorActivity.this.allImageSticker.size(), EditorActivity.this.allImageSticker.remove(indexOf));
                }
            }
        });

        this.binding.flImgSticker.addView(imgSticker);
        Log.d("@@@@@@@", "addStickerView: " + imgSticker);
        Log.d("@@@@@@@", "addStickerView: " + imgSticker);
        this.allImageSticker.add(this.imgSticker);
        Log.d("@@@@@@@", "addStickerView:_____1 " + allImageSticker);
        Log.d("@@@@@@@", "addStickerView:_____1.1 " + binding.flImgSticker);

        setCurrentImgStickerEdit(true, this.imgSticker);
    }

    private Bitmap convertDrawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(12, 12, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    public void onBackPressed() {
        if (!findViewById(R.id.wg_loading).isShown()) {
            if (findViewById(R.id.fl_fragment).isShown()) {
                findViewById(R.id.fl_fragment).setVisibility(View.GONE);
                if (this.fm.findFragmentById(R.id.fl_fragment) != null) {
                    FragmentTransaction beginTransaction = this.fm.beginTransaction();
                    Fragment findFragmentById = this.fm.findFragmentById(R.id.fl_fragment);
                    findFragmentById.getClass();
                    beginTransaction.remove(findFragmentById).commit();
                    return;
                }
                return;
            }
            goBack();
        }
    }


    public void goBack() {
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

        btn_neutral.setOnClickListener(view -> dialog.dismiss());

        btn_negative.setOnClickListener(view -> {
            if (EditorActivity.this.isDraft) {
                EditorActivity.this.removeUnusedFiles();
            } else {
                String sb = EditorActivity.this.draftsPath +
                        "/" +
                        EditorActivity.this.draftFolder;
                AppUtil.deleteFolder(new File(sb));
            }

            dialog.dismiss();
            Intent intent = new Intent(EditorActivity.this, MainActivity.class);
            startActivity(intent);
            EditorActivity.this.finish();
            EditorActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });


        btn_positive.setOnClickListener(view -> {

            if (SDK_INT >= 33) {
                if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_MEDIA_IMAGES") && AppUtil.permissionGranted(EditorActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    EditorActivity.this.isSaved = false;
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                    EditorActivity.this.finish();
                    dialog.dismiss();
                    new SaveDraft(this, 0, true, "save_draft").execute();
                }
            }
            if (SDK_INT >= 32) {
                if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(EditorActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    EditorActivity.this.isSaved = false;
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                    EditorActivity.this.finish();
                    dialog.dismiss();
                    new SaveDraft(this, 0, true, "save_draft").execute();
                }
            } else {
                if (AppUtil.permissionGranted(EditorActivity.this, "android.permission.READ_EXTERNAL_STORAGE") && AppUtil.permissionGranted(EditorActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    EditorActivity.this.isSaved = false;
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                    EditorActivity.this.finish();
                    dialog.dismiss();
                    new SaveDraft(this, 0, true, "save_draft").execute();
                }
            }

        });
        dialog.show();
    }


    private void replaceScreen() {
        if (whichActivitytoStart == 1) {
            Intent intent = PreviewActivity.newIntent(EditorActivity.this, A, draftJson0);

            EditorActivity.this.startActivity(intent);
            EditorActivity.this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        }
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
        handler.removeCallbacks(runnable);
        this.isActivityLeft = true;
    }
}

package com.maxvidzgallery.storymaker.mediapicker;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.maxvidzgallery.storymaker.Activity.AdAdmob;
import com.maxvidzgallery.storymaker.Activity.BaseActivity;
import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.mediapicker.adapters.VpMainAdapter;
import com.maxvidzgallery.storymaker.mediapicker.fragments.ImagesFrag;
import com.maxvidzgallery.storymaker.mediapicker.models.TabItem;
import com.maxvidzgallery.storymaker.mediapicker.utils.AppUtil;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

public class Gallery extends BaseActivity {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";

    ConnectionDetector connectionDetector;


    AppCompatActivity activity;

    private CommonTabLayout ctlMain;
    private FragmentManager fm;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private int mode;
    private ArrayList<CustomTabEntity> tabItems = new ArrayList<>();
    private String[] tabTitles;
    private TextView tbTitle;

    private ViewPager vpMain;

    private void init() {
        this.fm = getSupportFragmentManager();
        this.vpMain = findViewById(R.id.vp_main);
        this.ctlMain = findViewById(R.id.ctl_main);
        this.tbTitle = findViewById(R.id.tb_title);
        this.tbTitle.setText(getIntent().getStringExtra("title"));
        findViewById(R.id.tb_close).setOnClickListener(view -> Gallery.this.onBackPressed());
        this.mode = getIntent().getIntExtra("mode", 0);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_gallery);
        AdAdmob adAdmob = new AdAdmob(this);
        adAdmob.FullscreenAd(this);
        adAdmob.BannerAd(findViewById(R.id.banner), this);

        sharedpreferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        activity = Gallery.this;

        connectionDetector = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();


        init();
        if (SDK_INT >= 33) {
            AppUtil.permissionGranted(this, "android.permission.READ_MEDIA_IMAGES");
        } else if (SDK_INT >= 32){
            AppUtil.permissionGranted(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else{
            AppUtil.permissionGranted(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


    }

    public void setTabBar() {
        this.mFragments = new ArrayList<>();
        int i = this.mode;
        if (i == 0) {
            this.mFragments.add(ImagesFrag.getInstance());
        } else if (i == 1) {
            this.mFragments.add(ImagesFrag.getInstance());
        }
        this.tabTitles = new String[0];
        this.tabTitles = getResources().getStringArray(R.array.tab_titles);
        this.tabItems = new ArrayList<>();
        int i2 = 0;
        while (true) {
            String[] strArr = this.tabTitles;
            if (i2 < strArr.length) {
                this.tabItems.add(new TabItem(strArr[i2]));
                i2++;
            } else {
                this.ctlMain.setTabData(this.tabItems);
                this.ctlMain.setOnTabSelectListener(new OnTabSelectListener() {
                    public void onTabReselect(int i) {
                    }

                    public void onTabSelect(int i) {
                        Gallery.this.vpMain.setCurrentItem(i);
                    }
                });
                this.vpMain.setAdapter(new VpMainAdapter(getSupportFragmentManager(), this.mFragments));
                this.vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    public void onPageScrollStateChanged(int i) {
                    }

                    public void onPageScrolled(int i, float f, int i2) {
                    }

                    public void onPageSelected(int i) {
                        Gallery.this.ctlMain.setCurrentTab(i);
                    }
                });
                this.vpMain.setCurrentItem(0);
                this.vpMain.setOffscreenPageLimit(this.mode + 1);
                return;
            }
        }
    }

    public void addFragment(Fragment fragment) {
        this.fm.executePendingTransactions();
        FragmentTransaction beginTransaction = this.fm.beginTransaction();
        beginTransaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_in_top);
        beginTransaction.replace(R.id.fl_fragment_detail, fragment);
        beginTransaction.commitAllowingStateLoss();
        findViewById(R.id.fl_fragment_detail).setVisibility(View.VISIBLE);
    }

    public void sendResult(String str) {
        Intent intent = new Intent();
        intent.putExtra("filePath", str);
        setResult(-1, intent);
        finish();
    }

    public void onBackPressed() {
        if (findViewById(R.id.fl_fragment_detail).isShown()) {
            findViewById(R.id.fl_fragment_detail).setVisibility(View.GONE);
            if (this.fm.findFragmentById(R.id.fl_fragment_detail) != null) {
                this.fm.beginTransaction().remove(this.fm.findFragmentById(R.id.fl_fragment_detail)).commit();
            }
        }
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}

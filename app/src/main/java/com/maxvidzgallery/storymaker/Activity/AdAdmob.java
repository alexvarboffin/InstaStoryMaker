package com.maxvidzgallery.storymaker.Activity;

import android.content.Context;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

public class AdAdmob {

    String BannerAdID = "/6499/example/banner", FullscreenAdID = "/6499/example/interstitial";

    //ProgressDialog dialog;

    public AdAdmob(AppCompatActivity activity) {


//        MobileAds.initialize(activity, initializationStatus -> {
//        });


    }


    public void BannerAd(final RelativeLayout Ad_Layout, AppCompatActivity activity) {


//        AdView mAdView = new AdView(activity);
//        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdUnitId(BannerAdID);
//        AdRequest adore = new AdRequest.Builder().build();
//        mAdView.loadAd(adore);
//        Ad_Layout.addView(mAdView);
//
//
//        mAdView.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdLoaded() {
//                Ad_Layout.setVisibility(View.VISIBLE);
//                super.onAdLoaded();
//                Log.e("ddddd", "dddd");
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//                Ad_Layout.setVisibility(View.INVISIBLE);
//                Log.e("ddddd1", "dddd");
//
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//                mAdView.destroy();
//                Ad_Layout.setVisibility(View.INVISIBLE);
//                Log.e("ddddd2", "dddd" + loadAdError.getMessage());
//
//            }
//        });


    }

    public void FullscreenAd(final AppCompatActivity activity) {


//        Ad_Popup(activity);
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        InterstitialAd.load(activity, FullscreenAdID, adRequest,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//
//                        interstitialAd.show(activity);
//                        hideDialog();
//
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        hideDialog();
//                    }
//                });


    }

    private void hideDialog() {
        //dialog.dismiss();
    }


    private void Ad_Popup(Context mContext) {
//        dialog = ProgressDialog.show(mContext, "", "Ad Loading . . .", true);
//        dialog.setCancelable(true);
//        dialog.show();
    }


}
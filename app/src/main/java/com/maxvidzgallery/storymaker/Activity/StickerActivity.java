package com.maxvidzgallery.storymaker.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;


import com.maxvidzgallery.storymaker.R;
import com.maxvidzgallery.storymaker.adapters.Stickeradapter;
import com.maxvidzgallery.storymaker.help.ConnectionDetector;
import com.maxvidzgallery.storymaker.models.Glob_Sticker;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class StickerActivity extends BaseActivity {

    SharedPreferences sharedpreferences;
    public static final String mypreference = "myprefadmob";

    ConnectionDetector connectionDetector;


    AppCompatActivity activity;

    GridView imggriddy;
    String[] stickername;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_sticker);



        sharedpreferences = getSharedPreferences(mypreference, MODE_PRIVATE);
        activity = StickerActivity.this;

        connectionDetector = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = connectionDetector.isConnectingToInternet();

        imggriddy = (GridView) findViewById(R.id.imggriddy);

        try {
            stickername = getImage("crown");
        } catch (IOException e) { e.printStackTrace(); }

        imggriddy.setAdapter(new Stickeradapter(getApplicationContext(), new ArrayList(Arrays.asList(stickername))));

        imggriddy.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Glob_Sticker.SelectedTattooName = StickerActivity.this.stickername[i];
                Log.d("@@@@@@@@@", "onItemClick: "+stickername[i]);
                setResult(-1);
                finish();

            }
        });


    }

    private String[] getImage(String folderName) throws IOException { return getAssets().list(folderName); }

    protected void onDestroy() {
        super.onDestroy();
    }
}
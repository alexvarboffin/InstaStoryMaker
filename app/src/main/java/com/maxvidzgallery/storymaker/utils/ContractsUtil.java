package com.maxvidzgallery.storymaker.utils;

import android.util.Log;


import com.maxvidzgallery.storymaker.R;

import java.util.LinkedHashMap;

public class ContractsUtil {

    public static LinkedHashMap<String, String> templateCategories = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> templates;
    public static LinkedHashMap<String, String> vignetteContracts = new LinkedHashMap<>();

    public static LinkedHashMap<String, Integer> initTemplates(String str) {

        templates = new LinkedHashMap<>();

        Log.e("String","==>"+str);

        switch (str) {
            case "Summer":
                templates.put("template_summer_1", R.layout.template_summer_1);
                templates.put("template_summer_2", R.layout.template_summer_2);
                templates.put("template_summer_3", R.layout.template_summer_3);
                templates.put("template_summer_4", R.layout.template_summer_4);
                templates.put("template_summer_5", R.layout.template_summer_5);
                templates.put("template_summer_6", R.layout.template_summer_6);
                templates.put("template_summer_7", R.layout.template_summer_7);
                templates.put("template_summer_8", R.layout.template_summer_8);
                templates.put("template_summer_9", R.layout.template_summer_9);
                templates.put("template_summer_a1", R.layout.template_summer_a1);
                templates.put("template_summer_a2", R.layout.template_summer_a2);
                templates.put("template_summer_a3", R.layout.template_summer_a3);
                break;
            case "Spring":
                templates.put("template_spring_1", R.layout.template_spring_1);
                templates.put("template_spring_2", R.layout.template_spring_2);
                templates.put("template_spring_3", R.layout.template_spring_3);
                templates.put("template_spring_4", R.layout.template_spring_4);
                templates.put("template_spring_5", R.layout.template_spring_5);
                templates.put("template_spring_6", R.layout.template_spring_6);
                break;

            case "Feugiat":
                templates.put("template_feugiat_1", R.layout.template_feugiat_1);
                templates.put("template_feugiat_2", R.layout.template_feugiat_2);
                templates.put("template_feugiat_3", R.layout.template_feugiat_3);
                templates.put("template_feugiat_4", R.layout.template_feugiat_4);
                templates.put("template_feugiat_5", R.layout.template_feugiat_5);
                templates.put("template_feugiat_6", R.layout.template_feugiat_6);
                break;

            case "Birthday":
                templates.put("template_birthday_1", R.layout.template_birthday_1);
                templates.put("template_birthday_2", R.layout.template_birthday_2);
                templates.put("template_birthday_3", R.layout.template_birthday_3);
                templates.put("template_birthday_4", R.layout.template_birthday_4);
                templates.put("template_birthday_5", R.layout.template_birthday_5);
                templates.put("template_birthday_6", R.layout.template_birthday_6);
                templates.put("template_birthday_7", R.layout.template_birthday_7);
                templates.put("template_birthday_8", R.layout.template_birthday_8);
                templates.put("template_birthday_9", R.layout.template_birthday_9);
                templates.put("template_birthday_10", R.layout.template_birthday_10);
                break;
            case "Brush":
                templates.put("template_brush_1", R.layout.template_brush_1);
                templates.put("template_brush_2", R.layout.template_brush_2);
                templates.put("template_brush_3", R.layout.template_brush_3);
                templates.put("template_brush_4", R.layout.template_brush_4);
                templates.put("template_brush_5", R.layout.template_brush_5);
                templates.put("template_brush_6", R.layout.template_brush_6);
                templates.put("template_brush_7", R.layout.template_brush_7);
                templates.put("template_brush_8", R.layout.template_brush_8);
                templates.put("template_brush_9", R.layout.template_brush_9);
                templates.put("template_brush_a1",R.layout.template_brush_a1);
                templates.put("template_brush_a2",R.layout.template_brush_a2);
                templates.put("template_brush_a3",R.layout.template_brush_a3);
                templates.put("template_brush_a5",R.layout.template_brush_a5);
                templates.put("template_brush_a6",R.layout.template_brush_a6);
                templates.put("template_brush_a7",R.layout.template_brush_a7);
                templates.put("template_brush_a8",R.layout.template_brush_a8);
                break;
            case "Christmas":
                templates.put("template_christmas_1", R.layout.template_christmas_1);
                templates.put("template_christmas_2", R.layout.template_christmas_2);
                templates.put("template_christmas_3", R.layout.template_christmas_3);
                templates.put("template_christmas_4", R.layout.template_christmas_4);
                templates.put("template_christmas_5", R.layout.template_christmas_5);
                templates.put("template_christmas_6", R.layout.template_christmas_6);
                templates.put("template_christmas_7", R.layout.template_christmas_7);
                templates.put("template_christmas_8", R.layout.template_christmas_8);
                templates.put("template_christmas_9", R.layout.template_christmas_9);
                templates.put("template_christmas_a1", R.layout.template_christmas_a1);
                templates.put("template_christmas_a2", R.layout.template_christmas_a2);
                break;
            case "Deserts":
                templates.put("template_deserts_1", R.layout.template_deserts_1);
                templates.put("template_deserts_2", R.layout.template_deserts_2);
                templates.put("template_deserts_3", R.layout.template_deserts_3);
                templates.put("template_deserts_4", R.layout.template_deserts_4);
                templates.put("template_deserts_5", R.layout.template_deserts_5);
                templates.put("template_deserts_6", R.layout.template_deserts_6);
                templates.put("template_deserts_7", R.layout.template_deserts_7);
                templates.put("template_deserts_8", R.layout.template_deserts_8);
                templates.put("template_deserts_9", R.layout.template_deserts_9);
                templates.put("template_deserts_10", R.layout.template_deserts_10);
                break;
            case "Forests":
                templates.put("template_forests_1", R.layout.template_forests_1);
                templates.put("template_forests_2", R.layout.template_forests_2);
                templates.put("template_forests_3", R.layout.template_forests_3);
                templates.put("template_forests_4", R.layout.template_forests_4);
                templates.put("template_forests_5", R.layout.template_forests_5);
                templates.put("template_forests_6", R.layout.template_forests_6);
                templates.put("template_forests_7", R.layout.template_forests_7);
                templates.put("template_forests_8", R.layout.template_forests_8);
                templates.put("template_forests_9", R.layout.template_forests_9);
                templates.put("template_forests_a1",R.layout.template_forests_a1);
                break;
            case "Love":
                templates.put("template_love_1", R.layout.template_love_1);
                templates.put("template_love_2", R.layout.template_love_2);
                templates.put("template_love_3", R.layout.template_love_3);
                templates.put("template_love_4", R.layout.template_love_4);
                templates.put("template_love_5", R.layout.template_love_5);
                templates.put("template_love_6", R.layout.template_love_6);
                templates.put("template_love_7", R.layout.template_love_7);
                templates.put("template_love_8", R.layout.template_love_8);
                templates.put("template_love_9", R.layout.template_love_9);
                break;
            case "Mothers Day":
                templates.put("template_mothers_day_1", R.layout.template_mothers_day_1);
                templates.put("template_mothers_day_2", R.layout.template_mothers_day_2);
                templates.put("template_mothers_day_3", R.layout.template_mothers_day_3);
                templates.put("template_mothers_day_4", R.layout.template_mothers_day_4);
                templates.put("template_mothers_day_5", R.layout.template_mothers_day_5);
                break;

            case "Urban":
                templates.put("template_urban_1", R.layout.template_urban_1);
                templates.put("template_urban_2", R.layout.template_urban_2);
                templates.put("template_urban_3", R.layout.template_urban_3);
                templates.put("template_urban_4", R.layout.template_urban_4);
                templates.put("template_urban_5", R.layout.template_urban_5);
                templates.put("template_urban_6", R.layout.template_urban_6);
                break;

            case "Simple":
                templates.put("template_simple_1", R.layout.template_simple_1);
                templates.put("template_simple_2", R.layout.template_simple_2);
                templates.put("template_simple_3", R.layout.template_simple_3);
                templates.put("template_simple_4", R.layout.template_simple_4);
                templates.put("template_simple_5", R.layout.template_simple_5);
                templates.put("template_simple_6", R.layout.template_simple_6);
                templates.put("template_simple_7", R.layout.template_simple_7);
                templates.put("template_simple_8", R.layout.template_simple_8);
                break;

            case "Valentine":
                templates.put("template_valentine_1", R.layout.template_valentine_1);
                templates.put("template_valentine_2", R.layout.template_valentine_2);
                templates.put("template_valentine_3", R.layout.template_valentine_3);
                templates.put("template_valentine_4", R.layout.template_valentine_4);
                templates.put("template_valentine_5", R.layout.template_valentine_5);
                templates.put("template_valentine_6", R.layout.template_valentine_6);
                break;
            case "Cracked":
                templates.put("template_cracked_1", R.layout.template_cracked_1);
                templates.put("template_cracked_2", R.layout.template_cracked_2);
                templates.put("template_cracked_3", R.layout.template_cracked_3);
                templates.put("template_cracked_4", R.layout.template_cracked_4);
                templates.put("template_cracked_5", R.layout.template_cracked_5);
                templates.put("template_cracked_6", R.layout.template_cracked_6);
                break;

            case "Hologram":
                templates.put("template_hologram_1", R.layout.template_hologram_1);
                templates.put("template_hologram_2", R.layout.template_hologram_2);
                templates.put("template_hologram_3", R.layout.template_hologram_3);
                templates.put("template_hologram_4", R.layout.template_hologram_4);
                templates.put("template_hologram_5", R.layout.template_hologram_5);
                templates.put("template_hologram_6", R.layout.template_hologram_6);
                break;
            case "Glitch":
                templates.put("template_glitch_1", R.layout.template_glitch_1);
                templates.put("template_glitch_2", R.layout.template_glitch_2);
                templates.put("template_glitch_3", R.layout.template_glitch_3);
                templates.put("template_glitch_4", R.layout.template_glitch_4);
                templates.put("template_glitch_5", R.layout.template_glitch_5);
                templates.put("template_glitch_6", R.layout.template_glitch_6);
                templates.put("template_glitch_7", R.layout.template_glitch_7);
                break;

            case "Tranquil":
                templates.put("template_tranquil_1", R.layout.template_tranquil_1);
                templates.put("template_tranquil_2", R.layout.template_tranquil_2);
                templates.put("template_tranquil_3", R.layout.template_tranquil_3);
                templates.put("template_tranquil_4", R.layout.template_tranquil_4);
                templates.put("template_tranquil_5", R.layout.template_tranquil_5);
                templates.put("template_tranquil_6", R.layout.template_tranquil_6);
                templates.put("template_tranquil_7", R.layout.template_tranquil_7);
                templates.put("template_tranquil_8", R.layout.template_tranquil_8);
                break;

            case "Elegant":
                templates.put("template_elegant_1", R.layout.template_elegant_1);
                templates.put("template_elegant_2", R.layout.template_elegant_2);
                templates.put("template_elegant_3", R.layout.template_elegant_3);
                templates.put("template_elegant_4", R.layout.template_elegant_4);
                templates.put("template_elegant_5", R.layout.template_elegant_5);
                templates.put("template_elegant_6", R.layout.template_elegant_6);
                templates.put("template_elegant_7", R.layout.template_elegant_7);
                templates.put("template_elegant_8", R.layout.template_elegant_8);
                break;

            case "Autumn":
                templates.put("template_autumn_1", R.layout.template_autumn_1);
                templates.put("template_autumn_2", R.layout.template_autumn_2);
                templates.put("template_autumn_3", R.layout.template_autumn_3);
                templates.put("template_autumn_4", R.layout.template_autumn_4);
                templates.put("template_autumn_5", R.layout.template_autumn_5);
                templates.put("template_autumn_6", R.layout.template_autumn_6);
                break;

            case "Vintage":
                templates.put("template_vintage_1", R.layout.template_vintage_1);
                templates.put("template_vintage_2", R.layout.template_vintage_2);
                templates.put("template_vintage_3", R.layout.template_vintage_3);
                templates.put("template_vintage_4", R.layout.template_vintage_4);
                templates.put("template_vintage_5", R.layout.template_vintage_5);
                templates.put("template_vintage_6", R.layout.template_vintage_6);
                templates.put("template_vintage_7", R.layout.template_vintage_7);
                templates.put("template_vintage_8", R.layout.template_vintage_8);
                break;
            case "JShine":
                templates.put("template_jshine_1", R.layout.template_jshine_1);
                templates.put("template_jshine_2", R.layout.template_jshine_2);
                templates.put("template_jshine_3", R.layout.template_jshine_3);
                templates.put("template_jshine_4", R.layout.template_jshine_4);
                templates.put("template_jshine_5", R.layout.template_jshine_5);
                templates.put("template_jshine_6", R.layout.template_jshine_6);
                templates.put("template_jshine_7", R.layout.template_jshine_7);
                templates.put("template_jshine_8", R.layout.template_jshine_8);
                break;

            case "Roundel":
                templates.put("template_roundel_1", R.layout.template_roundel_1);
                templates.put("template_roundel_2", R.layout.template_roundel_2);
                templates.put("template_roundel_3", R.layout.template_roundel_3);
                templates.put("template_roundel_4", R.layout.template_roundel_4);
                templates.put("template_roundel_5", R.layout.template_roundel_5);
                templates.put("template_roundel_6", R.layout.template_roundel_6);
                templates.put("template_roundel_7", R.layout.template_roundel_7);
                templates.put("template_roundel_8", R.layout.template_roundel_8);
                templates.put("template_roundel_9", R.layout.template_roundel_9);
                templates.put("template_roundel_a1", R.layout.template_roundel_a1);
                break;
            case "Oceans":
                templates.put("template_oceans_1", R.layout.template_oceans_1);
                templates.put("template_oceans_2", R.layout.template_oceans_2);
                templates.put("template_oceans_3", R.layout.template_oceans_3);
                templates.put("template_oceans_4", R.layout.template_oceans_4);
                templates.put("template_oceans_5", R.layout.template_oceans_5);
                templates.put("template_oceans_6", R.layout.template_oceans_6);
                templates.put("template_oceans_7", R.layout.template_oceans_7);
                templates.put("template_oceans_8", R.layout.template_oceans_8);
                break;
            case "Foody":
                templates.put("template_foody_1", R.layout.template_foody_1);
                templates.put("template_foody_2", R.layout.template_foody_2);
                templates.put("template_foody_3", R.layout.template_foody_3);
                templates.put("template_foody_4", R.layout.template_foody_4);
                templates.put("template_foody_5", R.layout.template_foody_5);
                templates.put("template_foody_6", R.layout.template_foody_6);
                templates.put("template_foody_7", R.layout.template_foody_7);
                templates.put("template_foody_8", R.layout.template_foody_8);
                templates.put("template_foody_9", R.layout.template_foody_9);
                break;

            case "Clean":
                templates.put("template_clean_1", R.layout.template_clean_1);
                templates.put("template_clean_2", R.layout.template_clean_2);
                templates.put("template_clean_3", R.layout.template_clean_3);
                templates.put("template_clean_4", R.layout.template_clean_4);
                templates.put("template_clean_5", R.layout.template_clean_5);
                templates.put("template_clean_6", R.layout.template_clean_6);
                templates.put("template_clean_7", R.layout.template_clean_7);
                break;

            case "Pinpur":
                templates.put("template_pinpur_1", R.layout.template_pinpur_1);
                templates.put("template_pinpur_2", R.layout.template_pinpur_2);
                templates.put("template_pinpur_3", R.layout.template_pinpur_3);
                templates.put("template_pinpur_4", R.layout.template_pinpur_4);
                templates.put("template_pinpur_5", R.layout.template_pinpur_5);
                templates.put("template_pinpur_6", R.layout.template_pinpur_6);
                templates.put("template_pinpur_7", R.layout.template_pinpur_7);
                templates.put("template_pinpur_8", R.layout.template_pinpur_8);
                templates.put("template_pinpur_9", R.layout.template_pinpur_9);
                break;
            case "Candy":
                templates.put("template_candy_1", R.layout.template_candy_1);
                templates.put("template_candy_2", R.layout.template_candy_2);
                templates.put("template_candy_3", R.layout.template_candy_3);
                templates.put("template_candy_4", R.layout.template_candy_4);
                templates.put("template_candy_5", R.layout.template_candy_5);
                templates.put("template_candy_6", R.layout.template_candy_6);
                templates.put("template_candy_7", R.layout.template_candy_7);
                break;
            case "Ramadan":
                templates.put("template_ramadan_1", R.layout.template_ramadan_1);
                templates.put("template_ramadan_2", R.layout.template_ramadan_2);
                templates.put("template_ramadan_3", R.layout.template_ramadan_3);
                templates.put("template_ramadan_4", R.layout.template_ramadan_4);
                templates.put("template_ramadan_5", R.layout.template_ramadan_5);
                templates.put("template_ramadan_6", R.layout.template_ramadan_6);
                break;

            case "Happy New Year":
                templates.put("template_happy_new_year_1", R.layout.template_happy_new_year_1);
                templates.put("template_happy_new_year_2", R.layout.template_happy_new_year_2);
                templates.put("template_happy_new_year_3", R.layout.template_happy_new_year_3);
                templates.put("template_happy_new_year_4", R.layout.template_happy_new_year_4);
                templates.put("template_happy_new_year_5", R.layout.template_happy_new_year_5);
                templates.put("template_happy_new_year_6", R.layout.template_happy_new_year_6);
                break;
            case "Halloween":
                templates.put("template_halloween_1", R.layout.template_halloween_1);
                templates.put("template_halloween_2", R.layout.template_halloween_2);
                templates.put("template_halloween_3", R.layout.template_halloween_3);
                templates.put("template_halloween_4", R.layout.template_halloween_4);
                templates.put("template_halloween_5", R.layout.template_halloween_5);
                templates.put("template_halloween_6", R.layout.template_halloween_6);
                break;
            case "Islamic Holidays":
                templates.put("template_islamic_holidays_1", R.layout.template_islamic_holidays_1);
                templates.put("template_islamic_holidays_2", R.layout.template_islamic_holidays_2);
                templates.put("template_islamic_holidays_3", R.layout.template_islamic_holidays_3);
                templates.put("template_islamic_holidays_4", R.layout.template_islamic_holidays_4);
                templates.put("template_islamic_holidays_5", R.layout.template_islamic_holidays_5);
                templates.put("template_islamic_holidays_6", R.layout.template_islamic_holidays_6);
                break;
            case "White Christmas":
                templates.put("template_white_christmas_1", R.layout.template_white_christmas_1);
                templates.put("template_white_christmas_2", R.layout.template_white_christmas_2);
                templates.put("template_white_christmas_3", R.layout.template_white_christmas_3);
                templates.put("template_white_christmas_4", R.layout.template_white_christmas_4);
                templates.put("template_white_christmas_5", R.layout.template_white_christmas_5);
                templates.put("template_white_christmas_6", R.layout.template_white_christmas_6);
                break;

            default:
                break;
        }

        return templates;
    }

    static {

        templateCategories.put("Summer", "#fbc02d");
        templateCategories.put("Feugiat", "#ced0c4");
        templateCategories.put("Spring", "#9DCD8C");
        templateCategories.put("Urban", "#A4BFC8");
        templateCategories.put("Simple", "#F3BFB3");
        templateCategories.put("Valentine", "#EE2C73");
        templateCategories.put("Cracked", "#76785E");
        templateCategories.put("Hologram", "#cfccff");
        templateCategories.put("Glitch", "#0ffed5");
        templateCategories.put("Brush", "#EAD2AC");
        templateCategories.put("Tranquil", "#f0aac8");
        templateCategories.put("Elegant", "#A1A4A9");
        templateCategories.put("Autumn", "#C4502A");
        templateCategories.put("Vintage", "#E7C899");
        templateCategories.put("Birthday", "#F68100");
        templateCategories.put("JShine", "#c471ed");
        templateCategories.put("Roundel", "#C0CBD5");
        templateCategories.put("Oceans", "#7cd7ed");
        templateCategories.put("Foody", "#424242");
        templateCategories.put("Forests", "#8ebf56");
        templateCategories.put("Deserts", "#D4BBB2");
        templateCategories.put("Clean", "#cecece");
        templateCategories.put("Pinpur", "#929BDA");
        templateCategories.put("Candy", "#FFA4CD");
        templateCategories.put("Mothers Day", "#92505e");
        templateCategories.put("Love", "#ef9a9a");
        templateCategories.put("Ramadan", "#B37319");
        templateCategories.put("Happy New Year", "#1CB5E0");
        templateCategories.put("Christmas", "#3B6543");
        templateCategories.put("Halloween", "#FE9B13");
        templateCategories.put("Islamic Holidays", "#7dce3b");
        templateCategories.put("White Christmas", "#CBE1EF");

        vignetteContracts.put("Mayfair", "100x#000000x50");
        vignetteContracts.put("Amaro", "100x#4B5A82x80");
        vignetteContracts.put("Hudson", "50x#000000x100");
        vignetteContracts.put("Earlybird", "100x#000000x80");
        vignetteContracts.put("Nashville", "20x#000000x100");
    }
}

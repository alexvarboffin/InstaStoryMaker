package com.maxvidzgallery.storymaker.models;

import android.graphics.Shader.TileMode;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Draft {
    @SerializedName("background_color")
    public String background_color;
    @SerializedName("background_gradient")
    public String[] background_gradient;
    @SerializedName("background_photo")
    public String background_photo;
    @SerializedName("draft_name")
    public String draft_name;
    @SerializedName("gradient_linear_direction")
    public String gradient_linear_direction;
    @SerializedName("gradient_type")
    public String gradient_type;
    @SerializedName("photo_blur")
    public int photo_blur;
    @SerializedName("photo_scale")
    public float photo_scale;
    @SerializedName("photos")
    public ArrayList<Photo> photos;
    @SerializedName("save_path")
    public String save_path;
    @SerializedName("saved")
    public boolean saved;
    @SerializedName("stickers")
    public ArrayList<Sticker> stickers;
    @SerializedName("template_category")
    public String template_category;
    @SerializedName("template_name")
    public String template_name;
    @SerializedName("texts")
    public ArrayList<Text> texts;
    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("videos")
    public ArrayList<Video> videos;

    public static class Photo {
        @SerializedName("id")
        public String id;
        @SerializedName("path")
        public String path;
        @SerializedName("scale")
        public String scale;
    }

    public static class Sticker {
        @SerializedName("id")
        public int id;
        @SerializedName("layout_x")
        public float layout_x;
        @SerializedName("layout_y")
        public float layout_y;
        @SerializedName("path")
        public String path;
        @SerializedName("rotate")
        public float rotate;
        @SerializedName("scale")
        public float scale;
    }

    public static class Text {
        @SerializedName("align")
        public String align;
        @SerializedName("color")
        public int color;
        @SerializedName("font_category")
        public String font_category;
        @SerializedName("font_name")
        public String font_name;
        @SerializedName("gradient")
        public String gradient;
        @SerializedName("gradient_type")
        public String gradient_type;
        @SerializedName("id")
        public int id;
        @SerializedName("layout_padding")
        public boolean layout_padding;
        @SerializedName("layout_x")
        public int layout_x;
        @SerializedName("layout_y")
        public int layout_y;
        @SerializedName("letter_spacing")
        public int letter_spacing;
        @SerializedName("line_spacing")
        public int line_spacing;
        @SerializedName("linear_direction")
        public String linear_direction;
        @SerializedName("opacity")
        public int opacity;
        @SerializedName("paddingLeft")
        public int padding_left;
        @SerializedName("paddingRight")
        public int padding_right;
        @SerializedName("pattern_mode")
        public TileMode pattern_mode;
        @SerializedName("pattern_path")
        public String pattern_path;
        @SerializedName("pattern_repeats")
        public int pattern_repeats;
        @SerializedName("rotate")
        public float rotate;
        @SerializedName("scale")
        public float scale;
        @SerializedName("size")
        public float size;
        @SerializedName("strikethrough")
        public boolean strikethrough;
        @SerializedName("text")
        public String text;
        @SerializedName("tile_mode")
        public String tile_mode;
        @SerializedName("underLine")
        public boolean underLine;
    }

    public static class Video {
        @SerializedName("id")
        public String id;
        @SerializedName("muted")
        public boolean muted;
        @SerializedName("path")
        public String path;
    }
}

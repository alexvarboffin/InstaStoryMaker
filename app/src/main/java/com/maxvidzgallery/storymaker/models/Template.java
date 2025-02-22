package com.maxvidzgallery.storymaker.models;

import android.graphics.Shader.TileMode;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class Template {
    @SerializedName("background_color")
    public String background_color;
    @SerializedName("background_gradient")
    public String[] background_gradient;
    @SerializedName("background_photo_url")
    public String background_photo_url;
    @SerializedName("gradient_linear_direction")
    public String gradient_linear_direction;
    @SerializedName("gradient_type")
    public String gradient_type;
    @SerializedName("have_card")
    public boolean have_card;
    @SerializedName("layout_type")
    public String layout_type;
    @SerializedName("layouts")
    public ArrayList<Layout> layouts;
    @SerializedName("orientation")
    public String orientation;
    @SerializedName("photo_blur")
    public int photo_blur;
    @SerializedName("photo_category")
    public String photo_category;
    @SerializedName("photo_id")
    public String photo_id;
    @SerializedName("photo_scale")
    public float photo_scale;
    @SerializedName("texts")
    public ArrayList<Text> texts;


    public int status;


    public static class Layout {
        @SerializedName("bottomLeftRadius")
        public int bottomLeftRadius;
        @SerializedName("bottomRightRadius")
        public int bottomRightRadius;
        @SerializedName("id")
        public String id;

        public int status;

        @SerializedName("is_overlay")
        public boolean is_overlay;
        @SerializedName("rotation")
        public int rotation;
        @SerializedName("rounded_rect")
        public boolean rounded_rect;
        @SerializedName("topLeftRadius")
        public int topLeftRadius;
        @SerializedName("topRightRadius")
        public int topRightRadius;
        @SerializedName("weight_x")
        public int weight_x;
        @SerializedName("weight_y")
        public int weight_y;
    }

    public static class Position {
        @SerializedName("bottom")
        public String bottom;
        @SerializedName("left")
        public String left;
        @SerializedName("right")
        public String right;
        @SerializedName("top")
        public String top;
    }

    public static class Text {
        @SerializedName("align")
        public String align;
        @SerializedName("color")
        public String color;
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
        @SerializedName("layout_id")
        public int layout_id;
        @SerializedName("layout_padding")
        public boolean layout_padding;
        @SerializedName("layout_x")
        public float layout_x;
        @SerializedName("layout_y")
        public float layout_y;
        @SerializedName("letter_spacing")
        public int letter_spacing;
        @SerializedName("line_spacing")
        public int line_spacing;
        @SerializedName("linear_direction")
        public String linear_direction;
        @SerializedName("margin_bottom")
        public int margin_bottom;
        @SerializedName("margin_top")
        public int margin_top;
        @SerializedName("opacity")
        public int opacity;
        @SerializedName("padding_left")
        public int padding_left;
        @SerializedName("padding_right")
        public int padding_right;
        @SerializedName("pattern_mode")
        public TileMode pattern_mode;
        @SerializedName("pattern_path")
        public String pattern_path;
        @SerializedName("pattern_repeats")
        public int pattern_repeats;
        @SerializedName("position")
        public Position position;
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
}

package com.android.bluetoothmusic.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

import androidx.core.content.ContextCompat;

import com.android.bluetoothmusic.R;

public class DrawableUtils {

    public static Drawable listDrawable(Context context) {

        int strokeWidth = 2;

        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] colorsGradient = {
                color_sky(context),
                color_sky(context),
        };

        GradientDrawable gradientDrawablePressed = new GradientDrawable();
        gradientDrawablePressed.setColors(colorsGradient);
        gradientDrawablePressed.setCornerRadius(10f);
        gradientDrawablePressed.setStroke(strokeWidth, black(context));

        int[] colors = {
                light_violet(context),
                light_black(context),
        };
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(colors);
        gradientDrawable.setCornerRadius(10f);
        gradientDrawable.setStroke(strokeWidth, black(context));

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{}, gradientDrawable);

        return stateListDrawable;
    }

    public static Drawable listDrawable(Context context, float cornerRadius) {
        int strokeWidth = 2;

        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] colorsGradient = {
                color_sky(context),
                color_sky(context),
        };

        GradientDrawable gradientDrawablePressed = new GradientDrawable();
        gradientDrawablePressed.setColors(colorsGradient);
        gradientDrawablePressed.setCornerRadius(cornerRadius);
        gradientDrawablePressed.setStroke(strokeWidth, black(context));

        int[] colors = {
                light_violet(context),
                light_black(context),
        };
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(colors);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setStroke(strokeWidth, black(context));

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{}, gradientDrawable);

        return stateListDrawable;
    }

    public static Drawable listDrawable(String color_sky, int strokeWidth, String light_violet, String light_black, String black) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] colorsGradient = {
                Color.parseColor(color_sky),
                Color.parseColor(color_sky),
        };

        GradientDrawable gradientDrawablePressed = new GradientDrawable();
        gradientDrawablePressed.setColors(colorsGradient);
        gradientDrawablePressed.setCornerRadius(10f);
        gradientDrawablePressed.setStroke(strokeWidth, Color.parseColor(black));

        int[] colors = {
                Color.parseColor(light_violet),
                Color.parseColor(light_black),
        };
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(colors);
        gradientDrawable.setCornerRadius(10f);
        gradientDrawable.setStroke(strokeWidth, Color.parseColor(black));

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, gradientDrawablePressed);
        stateListDrawable.addState(new int[]{}, gradientDrawable);

        return stateListDrawable;
    }

    public static LayerDrawable layerDrawable(int light_violet, int light_black, int black, int strokeWidth) {
        int[] colorsGradient = {
                light_violet,
                light_black,
        };

        GradientDrawable shadow = new GradientDrawable();
        shadow.setBounds(0, 0, 0, 0);
        shadow.setColors(colorsGradient);
        shadow.setCornerRadius(10f);
        shadow.setStroke(strokeWidth, black);

        String colorTransparent = "#00000000";
        int[] colors = {
                Color.parseColor(colorTransparent),
                Color.parseColor(colorTransparent),
        };

        GradientDrawable backColor = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
        backColor.setBounds(0, 0, 0, 0);

        Drawable[] layers = new Drawable[2];
        layers[0] = backColor;
        layers[1] = shadow;

        return new LayerDrawable(layers);
    }

    public static int white(Context context) {
        return ContextCompat.getColor(context, R.color.white);
    }

    public static int transparent(Context context) {
        return ContextCompat.getColor(context, R.color.colorTransparent);
    }

    public static int color_sky(Context context) {
        return ContextCompat.getColor(context, R.color.colorSkyBlue);
    }

    public static int light_violet(Context context) {
        return ContextCompat.getColor(context, R.color.colorBlueViolet);
    }

    public static int light_black(Context context) {
        return ContextCompat.getColor(context, R.color.colorLightBlack);
    }

    public static int black(Context context) {
        return ContextCompat.getColor(context, R.color.black);
    }

    public static int color_danger(Context context) {
        return ContextCompat.getColor(context, R.color.colorIndianRed);
    }

    public static int color_success(Context context) {
        return ContextCompat.getColor(context, R.color.colorDarkSeaGreen);
    }

    public static int color_warning(Context context) {
        return ContextCompat.getColor(context, R.color.colorLightGoldenrodYellow);
    }

    public GradientDrawable gradientDrawable(String colorsGradient, int strokeWidth, String black) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(strokeWidth, Color.parseColor(black));
        gradientDrawable.setColor(Color.parseColor(colorsGradient));

        return gradientDrawable;
    }

    public static GradientDrawable setGradientDrawable(Context context, int backgroundColor, int borderColor, int stroke) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setColors(new int[]{ContextCompat.getColor(context, backgroundColor), ContextCompat.getColor(context, borderColor)});
        shape.setStroke(stroke, borderColor);
        return shape;
    }
}

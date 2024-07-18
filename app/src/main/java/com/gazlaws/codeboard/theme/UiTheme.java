package com.gazlaws.codeboard.theme;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.core.graphics.ColorUtils;
import com.gazlaws.codeboard.KeyboardPreferences;
import android.graphics.BlurMaskFilter;

public class UiTheme {

    public Paint foregroundPaint;
    public int backgroundColor;
    public float fontHeight;

    public float buttonBodyPadding = 5.0f;
    public Paint buttonBodyPaint;
    public float buttonBodyBorderRadius = 8.0f;
    public float defaultBlurRadius = 5.0f;
    public float defaultBgBlurRadius = 5.0f;
    public boolean enablePreview = false;
    public boolean enableBorder;
    public float portraitSize;
    public float landscapeSize;

    // Gradient fields
    public int buttonBodyStartColor;
    public int buttonBodyEndColor;

    // New fields for transparency and blur
    public float bgTransparency;
    public boolean bgBlurEffectEnabled;

    private UiTheme() {
        this.foregroundPaint = new Paint();
        this.buttonBodyPaint = new Paint();
        backgroundColor = 0xff000000;
    }

    public static UiTheme buildFromInfo(ThemeInfo info, KeyboardPreferences preferences) {
        UiTheme theme = new UiTheme();
        theme.portraitSize = info.size;
        theme.landscapeSize = info.sizeLandscape;
        theme.enablePreview = info.enablePreview;
        theme.enableBorder = info.enableBorder;

        // Get background transparency from preferences
        float transparency = preferences.getBgTransparency();

        // Apply transparency to background color
        int alpha = (int) (255 * transparency);
        theme.backgroundColor = ColorUtils.setAlphaComponent(info.backgroundColor, alpha);

        // Get blur effect enabled status from preferences
        boolean blurEnabled = preferences.isBgBlurEffectEnabled();

        // Apply blur effect if enabled
        if (blurEnabled) {
            theme.buttonBodyPaint.setMaskFilter(new BlurMaskFilter(theme.defaultBlurRadius, BlurMaskFilter.Blur.NORMAL));
        }

        // Background - darker border
        if (info.enableBorder) {
            theme.backgroundColor = ColorUtils.blendARGB(theme.backgroundColor, Color.BLACK, 0.2f);
        }

        // Button body
        theme.buttonBodyPaint.setColor(info.backgroundColor);

        // Assign start and end colors for the gradient
        theme.buttonBodyStartColor = info.buttonBodyStartColor;
        theme.buttonBodyEndColor = info.buttonBodyEndColor;

        // Foreground
        theme.foregroundPaint.setColor(info.foregroundColor);
        theme.fontHeight = info.fontSize;
        theme.foregroundPaint.setTextSize(theme.fontHeight);
        theme.foregroundPaint.setTextAlign(Paint.Align.CENTER);
        theme.foregroundPaint.setAntiAlias(true);
        theme.foregroundPaint.setTypeface(Typeface.DEFAULT);

        return theme;
    }
}

package com.gazlaws.codeboard.theme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.core.graphics.ColorUtils;
import com.gazlaws.codeboard.KeyboardPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;

public class UiTheme {

    public Paint foregroundPaint;
    public int backgroundColor;
    public float fontHeight;

    public float buttonBodyPadding = 5.0f;
    public Paint buttonBodyPaint;
    public float buttonBodyBorderRadius = 8.0f;
    public float defaultBlurRadius = 5.0f; // Used for button body blur
    public float defaultBgBlurRadius = 5.0f; // Could be used for background blur if needed
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

        // Initialize new fields
        bgTransparency = 1.0f; // Default fully opaque
        bgBlurEffectEnabled = false; // Default disabled
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

        // Background - apply blur effect if enabled
        if (blurEnabled) {
            theme.backgroundColor = applyBlur(theme.backgroundColor, theme.defaultBgBlurRadius);
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

        // Set transparency and blur effect fields
        theme.bgTransparency = transparency;
        theme.bgBlurEffectEnabled = blurEnabled;

        return theme;
    }

    // Method to apply blur effect to a color
    private static int applyBlur(int color, float radius) {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setMaskFilter(new BlurMaskFilter(radius, Blur.NORMAL));
        canvas.drawRect(0, 0, 1, 1, paint);
        return bitmap.getPixel(0, 0);
    }
}

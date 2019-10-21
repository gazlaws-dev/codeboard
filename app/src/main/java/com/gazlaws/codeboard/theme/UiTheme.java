package com.gazlaws.codeboard.theme;

import android.graphics.Paint;
import android.graphics.Typeface;

public class UiTheme {

    public Paint foregroundPaint;
    public int backgroundColor;
    public float fontHeight = 48.0f;

    public float buttonBodyPadding = 1.0f;
    public Paint buttonBodyPaint;
    public float buttonBodyBorderRadius = 8.0f;
    public boolean enablePreview = false;
    public float portraitSize;
    public float landscapeSize;

    private UiTheme(){
        this.foregroundPaint = new Paint();
        this.buttonBodyPaint = new Paint();
        backgroundColor = 0xff000000;
    }

    public static UiTheme buildFromInfo(ThemeInfo info){
        UiTheme theme = new UiTheme();
        theme.portraitSize = info.size;
        theme.landscapeSize = info.size;
        theme.enablePreview = info.enablePreview;
        // background
        theme.backgroundColor = info.backgroundColor;
        // foreground
        theme.foregroundPaint.setColor(info.foregroundColor);
        theme.foregroundPaint.setTextSize(theme.fontHeight);
        theme.foregroundPaint.setTextAlign(Paint.Align.CENTER);
        theme.foregroundPaint.setAntiAlias(true);
        theme.foregroundPaint.setTypeface(Typeface.DEFAULT);
        // button body
        theme.buttonBodyPaint.setColor(info.backgroundColor);
        return theme;
    }
}

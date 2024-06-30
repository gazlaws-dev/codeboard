package com.gazlaws.codeboard.theme;

public class ThemeInfo {
    public int foregroundColor;
    public int backgroundColor;
    public boolean enablePreview;
    public boolean enableBorder;
    public float size;
    public float fontSize;
    public float sizeLandscape;

    // Add these fields for the gradient colors
    public int buttonBodyStartColor;
    public int buttonBodyEndColor;


    // Constructor
    public ThemeInfo(int foregroundColor, int backgroundColor, boolean enablePreview, boolean enableBorder, 
                     float size, float fontSize, float sizeLandscape, int buttonBodyStartColor, int buttonBodyEndColor) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.enablePreview = enablePreview;
        this.enableBorder = enableBorder;
        this.size = size;
        this.fontSize = fontSize;
        this.sizeLandscape = sizeLandscape;
        this.buttonBodyStartColor = buttonBodyStartColor;
        this.buttonBodyEndColor = buttonBodyEndColor;
    }
}

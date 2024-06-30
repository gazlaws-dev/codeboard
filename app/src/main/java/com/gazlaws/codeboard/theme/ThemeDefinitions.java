package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;
    private static int blueColor = 0xff0000ff;
    private static int lightBlueColor = 0xffadd8e6;
    private static int purpleColor = 0xff800080;
    private static int lightPurpleColor = 0xffba55d3;
    private static int pinkColor = 0xffffc0cb;
    private static int lightPinkColor = 0xffffb6c1;
    private static int cyanColor = 0xff00ffff;
    

    private static int buttonBodyStartColor;
    private static int buttonBodyEndColor;

    public static ThemeInfo Default(){
        return MaterialDark();
    }

    public static ThemeInfo MaterialDark(){
        return new ThemeInfo(
            whiteColor,
            0xff263238,
            true,
            true,
            14.0f,                  // Size
            16.0f,
            18.0f,
            buttonBodyStartColor,
            buttonBodyEndColor
            );
    }

    public static ThemeInfo MaterialWhite(){
        ThemeInfo theme = Default();
        theme.foregroundColor = blackColor;
        theme.backgroundColor = lightBlueColor;
        theme.buttonBodyStartColor = lightBlueColor;
        theme.buttonBodyEndColor = lightPurpleColor;
        return theme;
    }

    public static ThemeInfo PureBlack(){
        ThemeInfo theme = MaterialDark();
        theme.foregroundColor = whiteColor;
        theme.backgroundColor = blackColor;
        theme.buttonBodyStartColor = blackColor;
        theme.buttonBodyEndColor = blueColor;
        return theme;
    }

    public static ThemeInfo White(){
        ThemeInfo theme = MaterialWhite();
        theme.backgroundColor = whiteColor;
        theme.buttonBodyStartColor = whiteColor;
        theme.buttonBodyEndColor = lightPinkColor;
        return theme;
    }

    public static ThemeInfo Blue(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff0d47a1;
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = blueColor;
        theme.buttonBodyEndColor = lightBlueColor;
        return theme;
    }

    public static ThemeInfo Purple(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff4a148c;
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = lightPurpleColor;
        theme.buttonBodyEndColor =  cyanColor;
        return theme;
    }
}

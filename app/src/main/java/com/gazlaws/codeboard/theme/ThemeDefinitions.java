package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;

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
        theme.backgroundColor = 0xffeceff1;
        theme.buttonBodyStartColor = 0xff000000;
        theme.buttonBodyEndColor = 0xff00ffff;
        return theme;
    }

    public static ThemeInfo PureBlack(){
        ThemeInfo theme = MaterialDark();
        theme.foregroundColor = whiteColor;
        theme.backgroundColor = blackColor;
        theme.buttonBodyStartColor = 0xff000000; // Black
        theme.buttonBodyEndColor = 0xff00ffff; // Color = Cyan
        return theme;
    }

    public static ThemeInfo White(){
        ThemeInfo theme = MaterialWhite();
        theme.backgroundColor = whiteColor;
        theme.buttonBodyStartColor = 0xff000000; // Black
        theme.buttonBodyEndColor = 0xff00feff; // Color = Cyan
        return theme;
    }

    public static ThemeInfo Blue(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff0d47a1;
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = 0xff000000;
        theme.buttonBodyEndColor = 0xff00ffff;
        return theme;
    }

    public static ThemeInfo Purple(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff4a148c;
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = 0xff000000; // Black
        theme.buttonBodyEndColor = 0xff00ffff; // Cyan
        return theme;
    }
}

package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;

    public static ThemeInfo Default(){
        return MaterialDark();
    }


    public static ThemeInfo MaterialDark(){
        return new ThemeInfo(
            whiteColor,             // Foreground color
            0xff263238,             // Background color
            true,                   // Enable preview
            true,                   // Enable border
            14.0f,                  // Size
            16.0f,                  // Font size
            18.0f                   // Landscape size
        );
    }

    public static ThemeInfo MaterialWhite(){
        ThemeInfo theme = Default();
        theme.foregroundColor = blackColor;
        theme.backgroundColor = 0xffeceff1;
        return theme;
    }

    public static ThemeInfo PureBlack(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = blackColor;
        return theme;
    }

    public static ThemeInfo White(){
        ThemeInfo theme = MaterialWhite();
        theme.backgroundColor = whiteColor;
        return theme;
    }

    public static ThemeInfo Blue(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff0d47a1;
        return theme;
    }

    public static ThemeInfo Purple(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff4a148c;
        return theme;
    }
}

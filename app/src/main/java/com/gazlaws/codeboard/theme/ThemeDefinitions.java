package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;

    public static ThemeInfo Default(){
        return MaterialDark();
    }


    public static ThemeInfo MaterialDark(){
        ThemeInfo theme = new ThemeInfo();
        theme.foregroundColor = whiteColor;
        theme.backgroundColor = 0xff263238;
        return theme;
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

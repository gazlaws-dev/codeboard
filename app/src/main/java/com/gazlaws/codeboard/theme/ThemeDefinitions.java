package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;
    private static int blueColor = 0xff1e88e5; // New shade of blue
    private static int lightBlueColor = 0xff90caf9; // Softer blue
    private static int purpleColor = 0xff6a1b9a; // Deep purple
    private static int lightPurpleColor = 0xffab47bc; // Softer purple
    private static int pinkColor = 0xffe91e63; // Vibrant pink
    private static int lightPinkColor = 0xfff8bbd0; // Softer pink
    private static int cyanColor = 0xff00acc1; // Teal blue
    private static int redColor = 0xffd32f2f; // Deep red
    private static int lightRedColor = 0xffe57373; // Soft red
    private static int darkGrayColor = 0xff263238; // Neutral dark gray

    private static int buttonBodyStartColor;
    private static int buttonBodyEndColor;

    public static ThemeInfo Default() {
        return MaterialDark();
    }

    public static ThemeInfo MaterialDark() {
        return new ThemeInfo(
            whiteColor,
            darkGrayColor, // Dark grey background
            true,
            true,
            14.0f, // Size
            16.0f,
            18.0f,
            buttonBodyStartColor,
            buttonBodyEndColor
        );
    }

    public static ThemeInfo MaterialWhite() {
        ThemeInfo theme = Default();
        theme.foregroundColor = lightBlueColor;
        theme.backgroundColor = whiteColor; // Bright white
        theme.buttonBodyStartColor = lightBlueColor;
        theme.buttonBodyEndColor = lightPurpleColor;
        return theme;
    }

    public static ThemeInfo PureBlack() {
        ThemeInfo theme = MaterialDark();
        theme.foregroundColor = whiteColor;
        theme.backgroundColor = 0xff121212; // Off-black, for a softer feel
        theme.buttonBodyStartColor = darkGrayColor; // Darker neutral start color
        theme.buttonBodyEndColor = blueColor; // Blue as accent
        return theme;
    }

    public static ThemeInfo White() {
        ThemeInfo theme = MaterialWhite();
        theme.foregroundColor = cyanColor;
        theme.backgroundColor = 0xfff5f5f5; // Off-white, less strain on eyes
        theme.buttonBodyStartColor = lightBlueColor;
        theme.buttonBodyEndColor = lightPurpleColor;
        return theme;
    }

    public static ThemeInfo Blue() {
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff1565c0; // Muted, deep blue
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = blueColor;
        theme.buttonBodyEndColor = lightBlueColor;
        return theme;
    }

    public static ThemeInfo Purple() {
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = purpleColor; // Deep purple
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = purpleColor;
        theme.buttonBodyEndColor = lightPurpleColor; // Softer purple for contrast
        return theme;
    }

    public static ThemeInfo Red() {
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = redColor; // Deep, rich red
        theme.foregroundColor = whiteColor;
        theme.buttonBodyStartColor = redColor; // Strong red
        theme.buttonBodyEndColor = lightRedColor; // Softer red for gradient
        return theme;
    }
}

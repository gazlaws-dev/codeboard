package com.gazlaws.codeboard.theme;

public class ThemeDefinitions {

    private static int whiteColor = 0xffffffff;
    private static int blackColor = 0xff000000;

    private static int buttonStartColour = 0xff000000;
    private static int buttonEndColour = 0xff00ffff;

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
            18.0f,                  // Landscape size
            buttonStartColour,      // Button body start color
            buttonEndColour         // Button body end color
        );
    }

    public static ThemeInfo MaterialWhite(){
        ThemeInfo theme = Default();
        theme.foregroundColor = blackColor;
        theme.backgroundColor = 0xffeceff1;
        theme.buttonStartColour = 0xff000000;
        theme.buttonEndColour = 0xff00ffff;
        return theme;
    }

    public static ThemeInfo PureBlack(){
        ThemeInfo theme = MaterialDark();
        theme.foregroundColor = whiteColor;
        theme.backgroundColor = blackColor;
        theme.buttonStartColour = 0xff000000; // Black
        theme.buttonEndColour = 0xff00ffff; // Color = Cyan
        return theme;
    }

    public static ThemeInfo White(){
        ThemeInfo theme = MaterialWhite();
        theme.backgroundColor = whiteColor;
        theme.buttonStartColour = 0xff000000; // Black
        theme.buttonEndColour = 0xff00feff; // Color = Cyan
        return theme;
    }

    public static ThemeInfo Blue(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff0d47a1;
        // For blue theme, we will use white color as button text color
        theme.foregroundColor = whiteColor;
        theme.buttonStartColour = 0xff000000; // Black  
        theme.buttonEndColour = 0xff00ffff; // Cyan
        return theme;
    }

    public static ThemeInfo Purple(){
        ThemeInfo theme = MaterialDark();
        theme.backgroundColor = 0xff4a148c;
        // For purple theme, we will use white color as button text color
        // and black color as button background color
        theme.foregroundColor = whiteColor;
        theme.buttonStartColour = 0xff000000; // Black
        theme.buttonEndColour = 0xff00ffff; // Cyan
        return theme;
    }
}



The `UiTheme` class in the `com.gazlaws.codeboard.theme` package manages the visual appearance of the keyboard UI. Below is a detailed explanation of its structure and functionality along with code snippets:

1. **Class Overview**:
   - The `UiTheme` class provides a representation of the visual theme applied to the keyboard UI.

```java
public class UiTheme {
    // Fields and methods...
}
```

2. **Fields**:
   - `foregroundPaint`: A `Paint` object for drawing foreground elements such as text.
   - `backgroundColor`: An integer representing the background color of the keyboard.
   - `fontHeight`: The height of the font used for text rendering.
   - `buttonBodyPadding`: The padding around the buttons.
   - `buttonBodyPaint`: A `Paint` object for drawing the background of buttons.
   - `buttonBodyBorderRadius`: The border radius of the buttons.
   - `enablePreview`: A boolean indicating whether key previews are enabled.
   - `enableBorder`: A boolean indicating whether borders around buttons are enabled.
   - `portraitSize`: The size of the keyboard in portrait mode.
   - `landscapeSize`: The size of the keyboard in landscape mode.
   - `buttonBodyStartColor` and `buttonBodyEndColor`: The start and end colors for the gradient applied to button backgrounds.

```java
public class UiTheme {
    public Paint foregroundPaint;
    public int backgroundColor;
    public float fontHeight;
    public float buttonBodyPadding = 5.0f;
    public Paint buttonBodyPaint;
    public float buttonBodyBorderRadius = 8.0f;
    public boolean enablePreview = false;
    public boolean enableBorder;
    public float portraitSize;
    public float landscapeSize;
    public int buttonBodyStartColor;
    public int buttonBodyEndColor;

    // Constructor and methods...
}
```

3. **Constructor**:
   - Initializes default values for the theme, such as background color and paints.

```java
private UiTheme(){
    this.foregroundPaint = new Paint();
    this.buttonBodyPaint = new Paint();
    backgroundColor = 0xff000000;
}
```

4. **buildFromInfo Method**:
   - Constructs a `UiTheme` object based on a `ThemeInfo` object.
   - Extracts relevant information from the `ThemeInfo`, such as colors and sizes.
   - Applies the extracted information to configure the `UiTheme` object accordingly.

```java
public static UiTheme buildFromInfo(ThemeInfo info){
    UiTheme theme = new UiTheme();
    theme.portraitSize = info.size;
    theme.landscapeSize = info.sizeLandscape;
    theme.enablePreview = info.enablePreview;
    theme.enableBorder = info.enableBorder;

    if(info.enableBorder){
        theme.backgroundColor = ColorUtils.blendARGB(info.backgroundColor, Color.BLACK, 0.2f);
    } else {
        theme.backgroundColor = info.backgroundColor;
    }

    theme.buttonBodyPaint.setColor(info.backgroundColor);
    theme.buttonBodyStartColor = info.buttonBodyStartColor;
    theme.buttonBodyEndColor = info.buttonBodyEndColor;

    theme.foregroundPaint.setColor(info.foregroundColor);
    theme.fontHeight = info.fontSize;
    theme.foregroundPaint.setTextSize(theme.fontHeight);
    theme.foregroundPaint.setTextAlign(Paint.Align.CENTER);
    theme.foregroundPaint.setAntiAlias(true);
    theme.foregroundPaint.setTypeface(Typeface.DEFAULT);

    return theme;
}
```

This class acts as a bridge between the `ThemeInfo` data structure, which holds theme-related information, and the actual visual representation of the keyboard UI. It encapsulates the logic for building a visual theme based on the provided theme information.

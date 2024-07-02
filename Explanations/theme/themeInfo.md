
The `ThemeInfo` class in the `com.gazlaws.codeboard.theme` package holds information about the visual theme applied to the keyboard UI. Below is a detailed explanation of its structure and functionality along with code snippets:

1. **Class Overview**:
   - The `ThemeInfo` class represents the properties of a visual theme applied to the keyboard UI.

```java
public class ThemeInfo {
    // Fields and constructor...
}
```

2. **Fields**:
   - `foregroundColor`: An integer representing the color of foreground elements such as text.
   - `backgroundColor`: An integer representing the background color of the keyboard.
   - `enablePreview`: A boolean indicating whether key previews are enabled.
   - `enableBorder`: A boolean indicating whether borders around buttons are enabled.
   - `size`: The size of the keyboard.
   - `fontSize`: The font size used for text rendering.
   - `sizeLandscape`: The size of the keyboard in landscape mode.
   - `buttonBodyStartColor` and `buttonBodyEndColor`: The start and end colors for the gradient applied to button backgrounds.

```java
public class ThemeInfo {
    public int foregroundColor;
    public int backgroundColor;
    public boolean enablePreview;
    public boolean enableBorder;
    public float size;
    public float fontSize;
    public float sizeLandscape;
    public int buttonBodyStartColor;
    public int buttonBodyEndColor;

    // Constructor...
}
```

3. **Constructor**:
   - Initializes the `ThemeInfo` object with provided values.

```java
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
```

This class serves as a container for theme-related information, encapsulating properties such as colors, sizes, and gradient colors for button backgrounds. It provides a convenient way to organize and pass theme information between different components of the keyboard UI.

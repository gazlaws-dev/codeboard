
The `Definitions` class in the `com.gazlaws.codeboard.layout` package defines various keyboard layouts and actions. Below is a beginner-friendly explanation of its structure and functionality along with code snippets:

1. **Class Overview**:
   - The `Definitions` class helps in defining different keyboard layouts and actions for the app.

```java
public class Definitions {
    // Methods to define keyboard layouts and actions...
}
```

2. **Arrow Keys Row**:
   - This method adds a row with arrow keys and other control keys like Escape and Symbols.

```java
public void addArrowsRow(KeyboardLayoutBuilder keyboard) {
    // Adding arrow keys and control keys to the keyboard layout
}
```

3. **Copy Paste Row**:
   - This method adds a row with copy, cut, paste, and other clipboard actions.

```java
public void addCopyPasteRow(KeyboardLayoutBuilder keyboard) {
    // Adding clipboard actions to the keyboard layout
}
```

4. **Custom Symbol Row**:
   - This method adds a custom row with symbols provided as input.

```java
public static void addCustomRow(KeyboardLayoutBuilder keyboard, String symbols) {
    // Adding a custom row with symbols to the keyboard layout
}
```

5. **Qwerty Keyboard Rows**:
   - These methods add rows for Qwerty keyboard layout with letters, numbers, and special keys.

```java
public static void addQwertyRows(KeyboardLayoutBuilder keyboard) {
    // Adding Qwerty keyboard rows to the layout
}

public static void addQwertzRows(KeyboardLayoutBuilder keyboard) {
    // Adding Qwertz keyboard rows to the layout
}

public static void addAzertyRows(KeyboardLayoutBuilder keyboard) {
    // Adding Azerty keyboard rows to the layout
}

public static void addDvorakRows(KeyboardLayoutBuilder keyboard) {
    // Adding Dvorak keyboard rows to the layout
}
```

6. **Symbol Rows and Clipboard Actions**:
   - These methods add rows for symbols and clipboard actions respectively.

```java
public void addSymbolRows(KeyboardLayoutBuilder keyboard) {
    // Adding symbol keys to the keyboard layout
}

public void addClipboardActions(KeyboardLayoutBuilder keyboard) {
    // Adding clipboard actions to the keyboard layout
}
```

7. **Custom Space Row**:
   - This method adds a custom row with symbols and a larger space key.

```java
public void addCustomSpaceRow(KeyboardLayoutBuilder keyboard, String symbols) {
    // Adding a custom row with symbols and a larger space key to the keyboard layout
}
```

These methods provide a convenient way to define different keyboard layouts and actions for the app. You can use them to create various keyboard configurations based on your app's requirements.

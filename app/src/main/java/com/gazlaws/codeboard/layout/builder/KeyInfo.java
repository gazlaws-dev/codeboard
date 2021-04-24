package com.gazlaws.codeboard.layout.builder;


import android.graphics.drawable.Drawable;

/**
 * contains information on how to build up the real key
 */
public class KeyInfo {
    /**
     * key press code sent when pressing the key
     */
    public int code;

    /**
     * label is shown on the keyboard
     */
    public String label;

    /**
     * size relative to other keys in the same row
     */
    public float size;

    /**
     * Key can be held to repeat
     */
    public boolean isRepeatable;

    /**
     * This key is a modifier (Shift/Ctrl)
     */
    public boolean isModifier;

    /**
     * When key is pressed output this text
     */
    public String outputText;

    /**
     * When shift modifier is pressed, show this label instead
     */
    public String onShiftLabel;

    /**
     * When control modifier is pressed, show this label instead
     */
    public String onCtrlLabel;

    /**
     * Drawable is shown on the keyboard
     */
    public Drawable icon;
}

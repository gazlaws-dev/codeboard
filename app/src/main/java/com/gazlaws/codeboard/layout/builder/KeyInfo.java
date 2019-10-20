package com.gazlaws.codeboard.layout.builder;


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
    public boolean isModifier;
    public String outputText;
}

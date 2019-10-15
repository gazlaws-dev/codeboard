package com.gazlaws.codeboard.layout.builder;


/**
 * contains information on how to build up the real key
 */
public class KeyBlueprint {
    /**
     * size relative to other keys in the same row
     */
    public float size;
    public char key;

    public KeyBlueprint(float relativeSize, char key) {
        this.size = relativeSize;
        this.key = key;
    }
}

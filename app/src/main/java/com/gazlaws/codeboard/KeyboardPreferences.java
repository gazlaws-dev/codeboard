package com.gazlaws.codeboard;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

public class KeyboardPreferences {

    private static String SETTINGS_FILE = "KEYBOARD_PREFERENCES";
    private static String FIRST_START = "FIRST_START";
    private static String THEME_INDEX = "RADIO_INDEX_COLOUR";
    private static String LAYOUT_INDEX = "RADIO_INDEX_LAYOUT";
    private static String ENABLE_PREVIEW = "PREVIEW";
    private static String ENABLE_SOUND = "SOUND";
    private static String ENABLE_VIBRATE = "VIBRATE";
    private static String ENABLE_ARROW_ROW = "ARROW_ROW";
    private static String SIZE_PORTRAIT = "SIZE";
    private static String SIZE_LANDSCAPE = "SIZE_LANDSCAPE";

    private SharedPreferences preferences;

    public KeyboardPreferences(ContextWrapper context) {
        this.preferences = context.getSharedPreferences(SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    public boolean isFirstStart(){
        return read(FIRST_START, true);
    }

    public void setFirstStart(boolean value){
        write(FIRST_START, value);
    }

    public int getLayoutIndex(){
        return read(LAYOUT_INDEX, 0);
    }

    public void setLayoutIndex(int value){
        write(LAYOUT_INDEX, value);
    }

    public int getThemeIndex(){
        return read(THEME_INDEX, 0);
    }

    public void setThemeIndex(int value){
        write(THEME_INDEX, value);
    }

    public boolean isSoundEnabled(){
        return read(ENABLE_SOUND, true);
    }

    public void setSoundEnabled(boolean value){
        write(THEME_INDEX, value);
    }

    public boolean isVibrateEnabled(){
        return read(ENABLE_VIBRATE, true);
    }

    public void setVibrateEnabled(boolean value){
        write(ENABLE_VIBRATE, value);
    }

    public boolean isArrowRowEnabled(){
        return read(ENABLE_ARROW_ROW, true);
    }

    public void setArrowRowEnabled(boolean value){
        write(ENABLE_ARROW_ROW, value);
    }

    public boolean isPreviewEnabled(){
        return read(ENABLE_PREVIEW, false);
    }

    public void setPreviewEnabled(boolean value){
        write(ENABLE_PREVIEW, value);
    }

    public int getPortraitSize(){
        return read(SIZE_PORTRAIT, 40);
    }

    public void setPortraitSize(int value){
        write(SIZE_PORTRAIT, value);
    }

    public int getLandscapeSize(){
        return read(SIZE_LANDSCAPE, 70);
    }

    public void setLandscapeSize(int value){
        write(SIZE_LANDSCAPE, value);
    }

    private boolean read(String key, boolean defaultValue){
        return preferences.getInt(key, defaultValue ? 1 : 0) != 0;
    }

    private void write(String key, boolean value){
        write(key, value ? 1 : 0);
    }

    private int read(String key, int defaultValue){
        return preferences.getInt(key, defaultValue);
    }

    private void write(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}

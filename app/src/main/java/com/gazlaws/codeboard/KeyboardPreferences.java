package com.gazlaws.codeboard;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.preference.PreferenceManager;

public class KeyboardPreferences {
    private SharedPreferences preferences;
    private Resources res;

    public KeyboardPreferences(ContextWrapper contextWrapper) {
        res = contextWrapper.getResources();
        this.preferences =
                PreferenceManager.getDefaultSharedPreferences(contextWrapper);
    }

    public boolean isFirstStart() {
        return read("FIRST_START", true);
    }

    public void setFirstStart(boolean value) {
        write("FIRST_START", value);
    }

    public boolean isSoundEnabled() {
        return read("sound",
                res.getBoolean(R.bool.sound));
    }

    public void setSoundEnabled(boolean bool) {
        write("sound", bool);
    }

    public boolean isVibrateEnabled() {
        return read("vibrate",
                res.getBoolean(R.bool.vibrate));
    }

    //Note: EditTextPreference saves these as strings. Could be null
    public int getVibrateLength() {
        return Integer.parseInt(safeRead("vibrate_ms",
                String.valueOf(res.getInteger(R.integer.vibrate_length))));
    }

    public void setVibrateLength(int length) {
        write("vibrate_ms", String.valueOf(length));
    }

    public int getBgColor() {
        return Integer.parseInt(safeRead("bg_colour_picker",
                String.valueOf(res.getInteger(R.integer.bg_color))));
    }

    public void setBgColor(String color) {
        write("bg_colour_picker",
                color);
    }

    public int getFgColor() {
        return Integer.parseInt(safeRead("fg_colour_picker",
                String.valueOf(res.getInteger(R.integer.fg_color))));
    }

    public void setFgColor(String color) {
        write("fg_colour_picker",
                color);
    }

    public int getPortraitSize() {
        return Integer.parseInt(safeRead("size_portrait",
                String.valueOf(res.getInteger(R.integer.size_portrait))));
    }

    public int getLandscapeSize() {
        return Integer.parseInt(safeRead("size_landscape",
                String.valueOf(res.getInteger(R.integer.size_landscape))));
    }


    public float getFontSizeAsSp() {
        String fontSize = safeRead("font_size",
                String.valueOf(res.getInteger(R.integer.font_size)));
        DisplayMetrics dm = res.getDisplayMetrics();
        float sp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                Float.parseFloat(fontSize), dm);
        return sp;
    }

    public boolean isPreviewEnabled() {
        return read("preview",
                res.getBoolean(R.bool.preview));
    }

    public boolean isBorderEnabled() {
        return read("borders",
                res.getBoolean(R.bool.borders));
    }

    public String getCustomSymbolsMain() {
        return read("input_symbols_main", res.getString(R.string.input_symbols_main));
    }

    public void setCustomSymbolsMain(String symbols) {
        write("input_symbols_main", symbols);
    }

    public String getCustomSymbolsMain2() {
        return read("input_symbols_main_2", res.getString(R.string.input_symbols_main_2));
    }

    public void setCustomSymbolsMain2(String symbols) {
        write("input_symbols_main_2", symbols);
    }

    public String getCustomSymbolsMainBottom() {
        return read("input_symbols_main_bottom", res.getString(R.string.input_symbols_main_bottom));
    }

    public void setCustomSymbolsMainBottom(String symbols) {
        write("input_symbols_main_bottom", symbols);
    }

    public String getCustomSymbolsSym() {
        return read("input_symbols_sym", res.getString(R.string.input_symbols_sym_2));
    }

    public void setCustomSymbolsSym(String symbols) {
        write("input_symbols_sym", symbols);
    }

    public String getCustomSymbolsSym2() {
        return read("input_symbols_sym_2", res.getString(R.string.input_symbols_sym));
    }

    public String getCustomSymbolsSym3() {
        return read("input_symbols_sym_3", res.getString(R.string.input_symbols_sym_3));
    }

    public String getCustomSymbolsSym4() {
        return read("input_symbols_sym_4", res.getString(R.string.input_symbols_sym_4));
    }
    public void setCustomSymbolsSym2(String symbols) {
        write("input_symbols_sym_2", symbols);
    }
    public void setCustomSymbolsSym3(String symbols) {
        write("input_symbols_sym_3", symbols);
    }
    public void setCustomSymbolsSym4(String symbols) {
        write("input_symbols_sym_4", symbols);
    }


    public void setCustomSymbolsSymBottom(String symbols) {
        write("input_symbols_sym_bottom", symbols);
    }

    public boolean getNavBar() {
        return read("navbar", res.getBoolean(R.bool.navbar));
    }

    public boolean getNavBarDark() {
        return read("navbar_dark", res.getBoolean(R.bool.navbar_dark));
    }

    public int getLayoutIndex() {
        return Integer.parseInt(safeRead("layout", "0"));
    }

    public int getThemeIndex() {
        return Integer.parseInt(safeRead("theme", "0"));
    }

    public boolean getCustomTheme() {
        return read("custom_theme", res.getBoolean(R.bool.custom_theme));
    }

    public String getPin1() {
        return read("pin1", res.getString(R.string.pin1));
    }

    public String getPin2() {
        return read("pin2", res.getString(R.string.pin2));
    }

    public String getPin3() {
        return read("pin3", res.getString(R.string.pin3));
    }

    public String getPin4() {
        return read("pin4", res.getString(R.string.pin4));
    }

    public String getPin5() {
        return read("pin5", res.getString(R.string.pin5));
    }

    public String getPin6() {
        return read("pin6", res.getString(R.string.pin6));
    }

    public String getPin7() {
        return read("pin7", res.getString(R.string.pin7));
    }

    public boolean getNotification() {
        return read("notification", res.getBoolean(R.bool.notification));
    }

    public Boolean getTopRowActions() {
        return read("top_row_actions",
                res.getBoolean(R.bool.top_row_actions));
    }

    public void resetAllToDefault() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        setFirstStart(false);
    }

    private boolean read(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    private void write(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private int read(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    private String read(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    private String safeRead(String key, String defaultValue) {
        String s = read(key, defaultValue);
        if (s == null) {
            return "0";
        }
        return s;
    }

    private void write(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}

package com.gazlaws.codeboard.backup;

/**
 * Data model to be exported and imported.
 *
 * Three groups of data:
 *   1. preferences — keyboard settings (theme, size, sound, etc.)
 *   2. symbols     — 6 rows of custom symbols
 *   3. pins        — 7 pins/snippets in clipboard mode
 *
 * All fields are of type String because SharedPreferences stores them as String.
 * Boolean/int fields are stored as String ("true"/"false", "0"/"1") to stay
 * consistent and easy to serialize to JSON.
 */
public class BackupData {

    public static final String BACKUP_VERSION = "1";

    // -------------------------------------------------------------------------
    // Metadata
    // -------------------------------------------------------------------------
    public String version;
    public String exportedAt; // ISO 8601 timestamp

    // -------------------------------------------------------------------------
    // Preferences
    // -------------------------------------------------------------------------
    public String sound;
    public String vibrate;
    public String vibrateMs;
    public String fontsize;
    public String sizePortrait;
    public String sizeLandscape;
    public String preview;
    public String borders;
    public String navbar;
    public String navbarDark;
    public String layout;
    public String theme;
    public String customTheme;
    public String bgColor;
    public String fgColor;
    public String topRowActions;
    public String notification;

    // -------------------------------------------------------------------------
    // Custom symbols
    // -------------------------------------------------------------------------
    public String symbolsMain;
    public String symbolsMain2;
    public String symbolsMainBottom;
    public String symbolsSym;
    public String symbolsSym2;
    public String symbolsSym3;
    public String symbolsSym4;

    // -------------------------------------------------------------------------
    // Pins
    // -------------------------------------------------------------------------
    public String pin1;
    public String pin2;
    public String pin3;
    public String pin4;
    public String pin5;
    public String pin6;
    public String pin7;
}

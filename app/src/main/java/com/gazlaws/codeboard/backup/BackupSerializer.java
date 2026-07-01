package com.gazlaws.codeboard.backup;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Serialization and deserialization of BackupData to/from JSON.
 *
 * Uses org.json which is already available on Android (no extra library needed).
 *
 * Format JSON:
 * {
 *   "version": "1",
 *   "exportedAt": "2026-06-28T12:00:00+08:00",
 *   "preferences": {
 *     "sound": "true",
 *     "vibrate": "true",
 *     ...
 *   },
 *   "symbols": {
 *     "main": "...",
 *     "main2": "...",
 *     ...
 *   },
 *   "pins": {
 *     "pin1": "...",
 *     ...
 *   }
 * }
 */
public class BackupSerializer {

    // -------------------------------------------------------------------------
    // Serialize: BackupData → JSON string
    // -------------------------------------------------------------------------

    public static String toJson(BackupData data) throws JSONException {
        JSONObject root = new JSONObject();

        root.put("version",    data.version);
        root.put("exportedAt", data.exportedAt);

        // Preferences
        JSONObject prefs = new JSONObject();
        prefs.put("sound",         nullSafe(data.sound));
        prefs.put("vibrate",       nullSafe(data.vibrate));
        prefs.put("vibrateMs",     nullSafe(data.vibrateMs));
        prefs.put("fontSize",      nullSafe(data.fontsize));
        prefs.put("sizePortrait",  nullSafe(data.sizePortrait));
        prefs.put("sizeLandscape", nullSafe(data.sizeLandscape));
        prefs.put("preview",       nullSafe(data.preview));
        prefs.put("borders",       nullSafe(data.borders));
        prefs.put("navbar",        nullSafe(data.navbar));
        prefs.put("navbarDark",    nullSafe(data.navbarDark));
        prefs.put("layout",        nullSafe(data.layout));
        prefs.put("theme",         nullSafe(data.theme));
        prefs.put("customTheme",   nullSafe(data.customTheme));
        prefs.put("bgColor",       nullSafe(data.bgColor));
        prefs.put("fgColor",       nullSafe(data.fgColor));
        prefs.put("topRowActions", nullSafe(data.topRowActions));
        prefs.put("notification",  nullSafe(data.notification));
        root.put("preferences", prefs);

        // Symbols
        JSONObject symbols = new JSONObject();
        symbols.put("main",       nullSafe(data.symbolsMain));
        symbols.put("main2",      nullSafe(data.symbolsMain2));
        symbols.put("mainBottom", nullSafe(data.symbolsMainBottom));
        symbols.put("sym",        nullSafe(data.symbolsSym));
        symbols.put("sym2",       nullSafe(data.symbolsSym2));
        symbols.put("sym3",       nullSafe(data.symbolsSym3));
        symbols.put("sym4",       nullSafe(data.symbolsSym4));
        root.put("symbols", symbols);

        // Pins
        JSONObject pins = new JSONObject();
        pins.put("pin1", nullSafe(data.pin1));
        pins.put("pin2", nullSafe(data.pin2));
        pins.put("pin3", nullSafe(data.pin3));
        pins.put("pin4", nullSafe(data.pin4));
        pins.put("pin5", nullSafe(data.pin5));
        pins.put("pin6", nullSafe(data.pin6));
        pins.put("pin7", nullSafe(data.pin7));
        root.put("pins", pins);

        return root.toString(2); // indent of 2 spaces for readability
    }

    // -------------------------------------------------------------------------
    // Deserialize: JSON string → BackupData
    // -------------------------------------------------------------------------

    public static BackupData fromJson(String json) throws JSONException {
        JSONObject root = new JSONObject(json);

        BackupData data = new BackupData();
        data.version    = root.optString("version", "1");
        data.exportedAt = root.optString("exportedAt", "");

        // Preferences
        JSONObject prefs = root.optJSONObject("preferences");
        if (prefs != null) {
            data.sound         = prefs.optString("sound",         "");
            data.vibrate       = prefs.optString("vibrate",       "");
            data.vibrateMs     = prefs.optString("vibrateMs",     "");
            data.fontsize      = prefs.optString("fontSize",      "");
            data.sizePortrait  = prefs.optString("sizePortrait",  "");
            data.sizeLandscape = prefs.optString("sizeLandscape", "");
            data.preview       = prefs.optString("preview",       "");
            data.borders       = prefs.optString("borders",       "");
            data.navbar        = prefs.optString("navbar",        "");
            data.navbarDark    = prefs.optString("navbarDark",    "");
            data.layout        = prefs.optString("layout",        "0");
            data.theme         = prefs.optString("theme",         "0");
            data.customTheme   = prefs.optString("customTheme",   "false");
            data.bgColor       = prefs.optString("bgColor",       "");
            data.fgColor       = prefs.optString("fgColor",       "");
            data.topRowActions = prefs.optString("topRowActions", "");
            data.notification  = prefs.optString("notification",  "");
        }

        // Symbols
        JSONObject symbols = root.optJSONObject("symbols");
        if (symbols != null) {
            data.symbolsMain       = symbols.optString("main",       "");
            data.symbolsMain2      = symbols.optString("main2",      "");
            data.symbolsMainBottom = symbols.optString("mainBottom", "");
            data.symbolsSym        = symbols.optString("sym",        "");
            data.symbolsSym2       = symbols.optString("sym2",       "");
            data.symbolsSym3       = symbols.optString("sym3",       "");
            data.symbolsSym4       = symbols.optString("sym4",       "");
        }

        // Pins
        JSONObject pins = root.optJSONObject("pins");
        if (pins != null) {
            data.pin1 = pins.optString("pin1", "");
            data.pin2 = pins.optString("pin2", "");
            data.pin3 = pins.optString("pin3", "");
            data.pin4 = pins.optString("pin4", "");
            data.pin5 = pins.optString("pin5", "");
            data.pin6 = pins.optString("pin6", "");
            data.pin7 = pins.optString("pin7", "");
        }

        return data;
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    public static String generateTimestamp() {
        // ISO 8601 format with a timezone offset in +HH:MM format
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    public static String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return "codeboard_backup_" + sdf.format(new Date()) + ".json";
    }

    private static String nullSafe(String s) {
        return s != null ? s : "";
    }
}

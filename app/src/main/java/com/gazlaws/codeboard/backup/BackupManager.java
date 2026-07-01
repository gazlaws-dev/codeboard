package com.gazlaws.codeboard.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Core logic for exporting and importing backups.
 *
 * Export:
 *   1. Read all settings from SharedPreferences via KeyboardPreferences
 *   2. Package into BackupData
 *   3. Serialize to JSON via BackupSerializer
 *   4. Write to the Uri chosen by the user (via SAF — Storage Access Framework)
 *
 * Import:
 *   1. Read the file from the Uri chosen by the user
 *   2. Parse JSON into BackupData via BackupSerializer
 *   3. Validate the version
 *   4. Write all values to SharedPreferences
 */
public class BackupManager {

    private static final String TAG = "BackupManager";

    public interface Callback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    private final Context            context;
    private final SharedPreferences  prefs;

    public BackupManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs   = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    // -------------------------------------------------------------------------
    // Export
    // -------------------------------------------------------------------------

    /**
     * Export all settings to a JSON file at the Uri chosen by the user.
     *
     * @param uri      Uri from the SAF file picker (ACTION_CREATE_DOCUMENT)
     * @param callback success/failure result
     */
    public void exportTo(Uri uri, Callback callback) {
        try {
            BackupData data = collectData();
            String json     = BackupSerializer.toJson(data);
            writeToUri(uri, json);
            callback.onSuccess("Backup saved successfully");
            Log.d(TAG, "Export succeeded to: " + uri);
        } catch (JSONException e) {
            String msg = "Failed to create JSON: " + e.getMessage();
            Log.e(TAG, msg, e);
            callback.onFailure(msg);
        } catch (IOException e) {
            String msg = "Failed to write file: " + e.getMessage();
            Log.e(TAG, msg, e);
            callback.onFailure(msg);
        }
    }

    // -------------------------------------------------------------------------
    // Import
    // -------------------------------------------------------------------------

    /**
     * Import settings from a JSON file at the Uri chosen by the user.
     *
     * @param uri      Uri from the SAF file picker (ACTION_OPEN_DOCUMENT)
     * @param callback success/failure result
     */
    public void importFrom(Uri uri, Callback callback) {
        try {
            String json     = readFromUri(uri);
            BackupData data = BackupSerializer.fromJson(json);
            applyData(data);
            callback.onSuccess("Backup restored successfully");
            Log.d(TAG, "Import succeeded from: " + uri);
        } catch (JSONException e) {
            String msg = "Backup file is invalid or corrupted: " + e.getMessage();
            Log.e(TAG, msg, e);
            callback.onFailure(msg);
        } catch (IOException e) {
            String msg = "Failed to read file: " + e.getMessage();
            Log.e(TAG, msg, e);
            callback.onFailure(msg);
        }
    }

    // -------------------------------------------------------------------------
    // Collect data from SharedPreferences
    // -------------------------------------------------------------------------

    private BackupData collectData() {
        BackupData data = new BackupData();

        data.version    = BackupData.BACKUP_VERSION;
        data.exportedAt = BackupSerializer.generateTimestamp();

        // Preferences — read directly from SharedPreferences using the same keys
        data.sound         = String.valueOf(prefs.getBoolean("sound",          true));
        data.vibrate       = String.valueOf(prefs.getBoolean("vibrate",        true));
        data.vibrateMs     = prefs.getString("vibrate_ms",     "30");
        data.fontsize      = prefs.getString("font_size",      "14");
        data.sizePortrait  = prefs.getString("size_portrait",  "40");
        data.sizeLandscape = prefs.getString("size_landscape", "40");
        data.preview       = String.valueOf(prefs.getBoolean("preview",        false));
        data.borders       = String.valueOf(prefs.getBoolean("borders",        true));
        data.navbar        = String.valueOf(prefs.getBoolean("navbar",         false));
        data.navbarDark    = String.valueOf(prefs.getBoolean("navbar_dark",    false));
        data.layout        = prefs.getString("layout",         "0");
        data.theme         = prefs.getString("theme",          "0");
        data.customTheme   = String.valueOf(prefs.getBoolean("custom_theme",   false));
        data.bgColor       = prefs.getString("bg_colour_picker", "");
        data.fgColor       = prefs.getString("fg_colour_picker", "");
        data.topRowActions = String.valueOf(prefs.getBoolean("top_row_actions", false));
        data.notification  = String.valueOf(prefs.getBoolean("notification",    false));

        // Custom symbols
        data.symbolsMain       = prefs.getString("input_symbols_main",        "");
        data.symbolsMain2      = prefs.getString("input_symbols_main_2",      "");
        data.symbolsMainBottom = prefs.getString("input_symbols_main_bottom", "");
        data.symbolsSym        = prefs.getString("input_symbols_sym",         "");
        data.symbolsSym2       = prefs.getString("input_symbols_sym_2",       "");
        data.symbolsSym3       = prefs.getString("input_symbols_sym_3",       "");
        data.symbolsSym4       = prefs.getString("input_symbols_sym_4",       "");

        // Pins
        data.pin1 = prefs.getString("pin1", "");
        data.pin2 = prefs.getString("pin2", "");
        data.pin3 = prefs.getString("pin3", "");
        data.pin4 = prefs.getString("pin4", "");
        data.pin5 = prefs.getString("pin5", "");
        data.pin6 = prefs.getString("pin6", "");
        data.pin7 = prefs.getString("pin7", "");

        return data;
    }

    // -------------------------------------------------------------------------
    // Apply data to SharedPreferences
    // -------------------------------------------------------------------------

    private void applyData(BackupData data) {
        SharedPreferences.Editor editor = prefs.edit();

        // Preferences
        applyBool(editor, "sound",           data.sound);
        applyBool(editor, "vibrate",         data.vibrate);
        applyStr(editor,  "vibrate_ms",      data.vibrateMs);
        applyStr(editor,  "font_size",       data.fontsize);
        applyStr(editor,  "size_portrait",   data.sizePortrait);
        applyStr(editor,  "size_landscape",  data.sizeLandscape);
        applyBool(editor, "preview",         data.preview);
        applyBool(editor, "borders",         data.borders);
        applyBool(editor, "navbar",          data.navbar);
        applyBool(editor, "navbar_dark",     data.navbarDark);
        applyStr(editor,  "layout",          data.layout);
        applyStr(editor,  "theme",           data.theme);
        applyBool(editor, "custom_theme",    data.customTheme);
        applyStr(editor,  "bg_colour_picker", data.bgColor);
        applyStr(editor,  "fg_colour_picker", data.fgColor);
        applyBool(editor, "top_row_actions", data.topRowActions);
        applyBool(editor, "notification",    data.notification);

        // Custom symbols
        applyStr(editor, "input_symbols_main",        data.symbolsMain);
        applyStr(editor, "input_symbols_main_2",      data.symbolsMain2);
        applyStr(editor, "input_symbols_main_bottom", data.symbolsMainBottom);
        applyStr(editor, "input_symbols_sym",         data.symbolsSym);
        applyStr(editor, "input_symbols_sym_2",       data.symbolsSym2);
        applyStr(editor, "input_symbols_sym_3",       data.symbolsSym3);
        applyStr(editor, "input_symbols_sym_4",       data.symbolsSym4);

        // Pins
        applyStr(editor, "pin1", data.pin1);
        applyStr(editor, "pin2", data.pin2);
        applyStr(editor, "pin3", data.pin3);
        applyStr(editor, "pin4", data.pin4);
        applyStr(editor, "pin5", data.pin5);
        applyStr(editor, "pin6", data.pin6);
        applyStr(editor, "pin7", data.pin7);

        editor.apply();
    }

    /** Write string to prefs only if it is not null and not empty. */
    private void applyStr(SharedPreferences.Editor editor, String key, String value) {
        if (value != null && !value.isEmpty()) {
            editor.putString(key, value);
        }
    }

    /** Write boolean to prefs only if it is not null and not empty. */
    private void applyBool(SharedPreferences.Editor editor, String key, String value) {
        if (value != null && !value.isEmpty()) {
            editor.putBoolean(key, Boolean.parseBoolean(value));
        }
    }

    // -------------------------------------------------------------------------
    // File I/O via SAF Uri
    // -------------------------------------------------------------------------

    private void writeToUri(Uri uri, String content) throws IOException {
        OutputStream os = context.getContentResolver().openOutputStream(uri, "wt");
        if (os == null) throw new IOException("Cannot open output stream for: " + uri);
        try (Writer writer = new OutputStreamWriter(os, "UTF-8")) {
            writer.write(content);
        }
    }

    private String readFromUri(Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        if (is == null) throw new IOException("Cannot open input stream for: " + uri);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}

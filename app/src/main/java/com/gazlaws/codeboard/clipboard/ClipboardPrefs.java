package com.gazlaws.codeboard.clipboard;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * SharedPreferences wrapper for clipboard history feature settings.
 *
 * Keys managed:
 *   clipboard_history_enabled        : boolean — whether the feature is active
 *                                      (same key as SwitchPreferenceCompat in preferences.xml)
 *   clipboard_history_expire_preset  : String  — duration in HOURS, "-1" = on reboot, "0" = custom
 *                                      (same key as ListPreference in preferences.xml)
 *   clipboard_history_expire_custom_hours : String — custom hours from EditTextPreference
 */
public class ClipboardPrefs {

    // Keys must exactly match those in preferences.xml
    private static final String KEY_ENABLED        = "clipboard_history_enabled";
    private static final String KEY_EXPIRE_PRESET  = "clipboard_history_expire_preset";
    private static final String KEY_EXPIRE_CUSTOM  = "clipboard_history_expire_custom_hours";

    // Default: feature active, expire after 1 day (24 hours)
    private static final boolean DEFAULT_ENABLED      = true;
    private static final String  DEFAULT_EXPIRE_HOURS = "24";

    private final SharedPreferences prefs;

    public ClipboardPrefs(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // -------------------------------------------------------------------------
    // Enabled
    // -------------------------------------------------------------------------

    public boolean isEnabled() {
        return prefs.getBoolean(KEY_ENABLED, DEFAULT_ENABLED);
    }

    public void setEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    // -------------------------------------------------------------------------
    // Expire duration
    // -------------------------------------------------------------------------

    /**
     * Read expire duration in milliseconds.
     * Reads from key "clipboard_history_expire_preset" (hours) then converts to millis.
     * If preset = "0" (custom), read from key "clipboard_history_expire_custom_hours".
     * If preset = "-1" (reboot), return EXPIRE_ON_REBOOT.
     */
    public long getExpireMillis() {
        // Try reading as String (normal path via ListPreference)
        // If it fails (value stored as int from an old version), fall back to default
        String presetStr;
        try {
            presetStr = prefs.getString(KEY_EXPIRE_PRESET, DEFAULT_EXPIRE_HOURS);
        } catch (ClassCastException e) {
            // Value stored as another type (int) — use default
            presetStr = DEFAULT_EXPIRE_HOURS;
        }
        if (presetStr == null || presetStr.isEmpty()) presetStr = DEFAULT_EXPIRE_HOURS;

        try {
            long hours = Long.parseLong(presetStr);
            if (hours == -1L) {
                return ClipboardExpireManager.EXPIRE_ON_REBOOT;
            }
            if (hours == 0L) {
                // Custom: read from EditTextPreference
                String customStr;
                try {
                    customStr = prefs.getString(KEY_EXPIRE_CUSTOM, "24");
                } catch (ClassCastException e) {
                    customStr = "24";
                }
                if (customStr == null || customStr.isEmpty()) customStr = "24";
                long customHours = Long.parseLong(customStr);
                return ClipboardExpireManager.validateCustomDuration(customHours * 3_600_000L);
            }
            return hours * 3_600_000L;
        } catch (NumberFormatException e) {
            return ClipboardExpireManager.EXPIRE_1_DAY;
        }
    }

    /**
     * Currently active expire label, to display in the UI.
     */
    public String getExpireLabel() {
        return ClipboardExpireManager.getLabel(getExpireMillis());
    }
}

package com.gazlaws.codeboard.clipboard;

import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Manages expiration and cleanup of clipboard history files.
 *
 * Rules:
 *  - Each clipboard_log.txt file represents one day.
 *  - A file is considered expired if (current time - file date) > expire duration.
 *  - Expired files are automatically deleted when cleanup runs.
 *
 * Available duration options:
 *  - 1 hour, 8 hours, 1 day, 2 days, 1 week, 30 days
 *  - On reboot (handled in ClipboardMonitor via boot receiver)
 *  - Custom: 1 hour up to 90 days
 */
public class ClipboardExpireManager {

    private static final String TAG = "ClipboardExpireManager";

    // -------------------------------------------------------------------------
    // Duration constants (in milliseconds)
    // -------------------------------------------------------------------------

    public static final long EXPIRE_1_HOUR    = TimeUnit.HOURS.toMillis(1);
    public static final long EXPIRE_8_HOURS   = TimeUnit.HOURS.toMillis(8);
    public static final long EXPIRE_1_DAY     = TimeUnit.DAYS.toMillis(1);
    public static final long EXPIRE_2_DAYS    = TimeUnit.DAYS.toMillis(2);
    public static final long EXPIRE_1_WEEK    = TimeUnit.DAYS.toMillis(7);
    public static final long EXPIRE_30_DAYS   = TimeUnit.DAYS.toMillis(30);
    public static final long EXPIRE_ON_REBOOT = -1L; // special flag

    public static final long EXPIRE_MIN_MS    = TimeUnit.HOURS.toMillis(1);
    public static final long EXPIRE_MAX_MS    = TimeUnit.DAYS.toMillis(90);

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Delete all files that have passed the expire duration.
     *
     * @param rootDir      root directory .localcache/copy
     * @param expireMillis expire duration in ms. If EXPIRE_ON_REBOOT, delete everything.
     */
    public static void cleanup(File rootDir, long expireMillis) {
        if (rootDir == null || !rootDir.exists()) return;

        long now        = System.currentTimeMillis();
        long cutoffTime = (expireMillis == EXPIRE_ON_REBOOT) ? now : now - expireMillis;

        Log.d(TAG, "Starting cleanup. Expire=" + expireMillis + "ms, cutoff=" + cutoffTime);
        deleteExpiredFiles(rootDir, cutoffTime);
        deleteEmptyDirs(rootDir);
    }

    /**
     * Cleanup on reboot — delete all files.
     */
    public static void cleanupOnReboot(File rootDir) {
        cleanup(rootDir, EXPIRE_ON_REBOOT);
    }

    // -------------------------------------------------------------------------
    // Validate custom duration
    // -------------------------------------------------------------------------

    /**
     * Validate a custom duration from the user.
     * Minimum 1 hour, maximum 90 days.
     *
     * @param millis duration in ms
     * @return duration clamped to the valid range
     */
    public static long validateCustomDuration(long millis) {
        if (millis < EXPIRE_MIN_MS) return EXPIRE_MIN_MS;
        if (millis > EXPIRE_MAX_MS) return EXPIRE_MAX_MS;
        return millis;
    }

    /**
     * Human-readable label for the expire duration.
     */
    public static String getLabel(long expireMillis) {
        if (expireMillis == EXPIRE_ON_REBOOT) return "On reboot";
        if (expireMillis == EXPIRE_1_HOUR)    return "1 hour";
        if (expireMillis == EXPIRE_8_HOURS)   return "8 hours";
        if (expireMillis == EXPIRE_1_DAY)     return "1 day";
        if (expireMillis == EXPIRE_2_DAYS)    return "2 days";
        if (expireMillis == EXPIRE_1_WEEK)    return "1 week";
        if (expireMillis == EXPIRE_30_DAYS)   return "30 days";

        // Custom
        long hours = TimeUnit.MILLISECONDS.toHours(expireMillis);
        long days  = TimeUnit.MILLISECONDS.toDays(expireMillis);
        if (days > 0) return days + " days (custom)";
        return hours + " hours (custom)";
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /**
     * Recursively delete files whose lastModified < cutoffTime.
     * Only deletes files named "clipboard_log.txt".
     */
    private static void deleteExpiredFiles(File dir, long cutoffTime) {
        File[] children = dir.listFiles();
        if (children == null) return;

        for (File child : children) {
            if (child.isDirectory()) {
                deleteExpiredFiles(child, cutoffTime);
            } else if (child.getName().equals("clipboard_log.txt")) {
                // Use the date from the folder structure (year/Month/day)
                // instead of lastModified(), which can change when the file is updated
                long fileDate = parseDateFromPath(child);
                long dateToCheck = fileDate > 0 ? fileDate : child.lastModified();
                if (dateToCheck < cutoffTime) {
                    boolean deleted = child.delete();
                    Log.d(TAG, (deleted ? "Deleted: " : "Failed to delete: ") + child.getPath());
                }
            }
        }
    }

    /**
     * Parse the date from the folder path: .../year/Month/day/clipboard_log.txt
     * Example: .../2026/June/29/clipboard_log.txt → epoch millis for 2026-06-29
     * Returns -1 if it cannot be parsed.
     */
    private static long parseDateFromPath(File file) {
        try {
            // parent = day dir, parent.parent = month dir, parent.parent.parent = year dir
            File dayDir   = file.getParentFile();
            File monthDir = dayDir != null ? dayDir.getParentFile() : null;
            File yearDir  = monthDir != null ? monthDir.getParentFile() : null;
            if (dayDir == null || monthDir == null || yearDir == null) return -1;

            int day   = Integer.parseInt(dayDir.getName().trim());
            int year  = Integer.parseInt(yearDir.getName().trim());
            // Parse the month name using SimpleDateFormat
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("MMMM", java.util.Locale.ENGLISH);
            java.util.Date monthDate = sdf.parse(monthDir.getName().trim());
            if (monthDate == null) return -1;

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(monthDate);
            int month = cal.get(java.util.Calendar.MONTH); // 0-based

            cal.set(year, month, day, 23, 59, 59);
            cal.set(java.util.Calendar.MILLISECOND, 999);
            return cal.getTimeInMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Recursively delete empty directories from the inside out.
     */
    private static void deleteEmptyDirs(File dir) {
        File[] children = dir.listFiles();
        if (children == null) return;

        for (File child : children) {
            if (child.isDirectory()) {
                deleteEmptyDirs(child);
                // Check again after recursion
                File[] remaining = child.listFiles();
                if (remaining != null && remaining.length == 0) {
                    child.delete();
                    Log.d(TAG, "Empty directory deleted: " + child.getPath());
                }
            }
        }
    }
}

package com.gazlaws.codeboard.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main orchestrator for the clipboard history feature.
 *
 * Responsibilities:
 *  1. Listen for clipboard changes via ClipboardManager.OnPrimaryClipChangedListener
 *  2. Read newly copied text
 *  3. Save it to file via ClipboardStorage
 *  4. Run cleanup of expired entries via ClipboardExpireManager
 *
 * Usage:
 *   ClipboardMonitor monitor = new ClipboardMonitor(context, prefs);
 *   monitor.start();   // start listening (call when keyboard is active)
 *   monitor.stop();    // stop listening (call when keyboard is inactive)
 */
public class ClipboardMonitor {

    private static final String TAG = "ClipboardMonitor";

    private final Context             context;
    private final ClipboardStorage    storage;
    private final ClipboardPrefs      prefs;
    private final ExecutorService     executor;

    private ClipboardManager                          clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener listener;

    private boolean isRunning = false;

    public ClipboardMonitor(Context context, ClipboardPrefs prefs) {
        this.context  = context.getApplicationContext();
        this.prefs    = prefs;
        this.storage  = new ClipboardStorage(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Start listening for clipboard changes.
     * Also runs expired file cleanup in the background.
     */
    public void start() {
        if (!prefs.isEnabled()) {
            Log.d(TAG, "Clipboard history is disabled, skipping start.");
            return;
        }
        if (isRunning) return;
        isRunning = true;

        setupListener();
        runCleanupAsync();
    }

    private void setupListener() {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            Log.e(TAG, "ClipboardManager is not available.");
            return;
        }

        listener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                handleClipboardChange();
            }
        };

        clipboardManager.addPrimaryClipChangedListener(listener);
        Log.d(TAG, "ClipboardMonitor started.");
    }

    /**
     * Stop listening for clipboard changes.
     */
    public void stop() {
        if (!isRunning) return;
        isRunning = false;

        if (clipboardManager != null && listener != null) {
            clipboardManager.removePrimaryClipChangedListener(listener);
        }

        // Shut down the executor to avoid leaking a thread when the keyboard closes
        executor.shutdown();
        Log.d(TAG, "ClipboardMonitor stopped.");
    }

    /**
     * Run cleanup when the device reboots.
     * Called from BootReceiver — no executor needed because BootReceiver
     * already runs on a separate background thread.
     */
    public static void cleanupOnReboot(ClipboardStorage storage, ClipboardPrefs prefs) {
        long expireMillis = prefs.getExpireMillis();
        if (expireMillis == ClipboardExpireManager.EXPIRE_ON_REBOOT) {
            ClipboardExpireManager.cleanupOnReboot(storage.getRootDir());
        } else {
            ClipboardExpireManager.cleanup(storage.getRootDir(), expireMillis);
        }
    }

    // -------------------------------------------------------------------------
    // Core logic
    // -------------------------------------------------------------------------

    private void handleClipboardChange() {
        try {
            if (clipboardManager == null || !clipboardManager.hasPrimaryClip()) return;

            ClipData clip = clipboardManager.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) return;

            CharSequence raw = clip.getItemAt(0).getText();
            if (raw == null) return;

            final String text = raw.toString().trim();
            if (text.isEmpty()) return;

            // Save or update the timestamp on a background thread
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    saveOrUpdate(text);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error while handling clipboard: " + e.getMessage(), e);
        }
    }

    private void saveOrUpdate(String text) {
        long now      = System.currentTimeMillis();
        File todayFile = storage.getTodayFile(now);

        // 1. Check today's file first
        boolean updatedToday = storage.updateTimestampIfExists(text, now);
        if (updatedToday) {
            Log.d(TAG, "Timestamp updated in today's file.");
            return;
        }

        // 2. Check files from previous days
        boolean foundInOld = storage.findAndRemoveFromOldFiles(text, todayFile);
        if (foundInOld) {
            // Found and removed from an old file → append to today's file
            Log.d(TAG, "Entry moved from an old file to today's file.");
        }

        // 3. Append to today's file (new entry or moved entry)
        ClipboardEntry entry = new ClipboardEntry(text, now);
        boolean ok = storage.append(entry);
        if (ok) {
            Log.d(TAG, "Entry saved in today's file: id=" + entry.id);
        }
    }

    private void runCleanupAsync() {
        final long expireMillis = prefs.getExpireMillis();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Cleanup async, expire=" + ClipboardExpireManager.getLabel(expireMillis));
                ClipboardExpireManager.cleanup(storage.getRootDir(), expireMillis);
            }
        });
    }
}

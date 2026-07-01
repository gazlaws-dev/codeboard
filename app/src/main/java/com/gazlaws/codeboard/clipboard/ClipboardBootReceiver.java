package com.gazlaws.codeboard.clipboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver that listens for ACTION_BOOT_COMPLETED.
 *
 * Responsibilities:
 *   - If expire is set to "on reboot", delete all clipboard history files.
 *   - If expire is not reboot-based, still run normal cleanup
 *     (e.g. files from 3 days ago when the setting is 1 day).
 *
 * Registered in AndroidManifest.xml with:
 *   <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
 *   <receiver android:name=".clipboard.ClipboardBootReceiver" android:exported="true">
 *       <intent-filter>
 *           <action android:name="android.intent.action.BOOT_COMPLETED"/>
 *       </intent-filter>
 *   </receiver>
 */
public class ClipboardBootReceiver extends BroadcastReceiver {

    private static final String TAG = "ClipboardBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        Log.d(TAG, "Boot completed, running clipboard history cleanup...");

        ClipboardPrefs prefs   = new ClipboardPrefs(context);
        ClipboardStorage store = new ClipboardStorage(context);

        // Use static method — no need to instantiate ClipboardMonitor
        ClipboardMonitor.cleanupOnReboot(store, prefs);
        Log.d(TAG, "Cleanup on reboot finished.");
    }
}

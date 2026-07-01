package com.gazlaws.codeboard.clipboard;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Handles all read/write operations for clipboard history files.
 *
 * Directory structure:
 *   /storage/emulated/0/Android/media/<package>/.localcache/copy/
 *     └── <year>/
 *           └── <Month>/         ← month name in English, e.g. "February"
 *                 └── <date>/    ← 2-digit date, e.g. "25"
 *                       └── clipboard_log.txt
 *
 * One file per day. Each entry is appended to the bottom of the file.
 */
public class ClipboardStorage {

    private static final String TAG        = "ClipboardStorage";
    private static final String DIR_BASE   = ".localcache/copy";
    private static final String FILE_NAME  = "clipboard_log.txt";

    private static final String FMT_YEAR  = "yyyy";
    private static final String FMT_MONTH = "MMMM";   // January, February, ...
    private static final String FMT_DATE  = "dd";

    private final File rootDir;

    public ClipboardStorage(Context context) {
        // /storage/emulated/0/Android/media/<package>/
        File mediaDir = new File(
            "/storage/emulated/0/Android/media/" + context.getPackageName()
        );
        this.rootDir = new File(mediaDir, DIR_BASE);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Save one entry to today's file.
     * The directory is created automatically if it doesn't exist.
     *
     * @return true on success
     */
    public boolean append(ClipboardEntry entry) {
        File file = getTodayFile(entry.timestamp);
        if (!ensureDir(file.getParentFile())) {
            Log.e(TAG, "Failed to create directory: " + file.getParentFile());
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(entry.serialize());
            bw.newLine();
            Log.d(TAG, "Entry saved: " + file.getPath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write entry: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Failed to serialize entry: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Find an entry with the same text in today's file.
     * If found, update its TIMESTAMP line with the current time.
     * If not found, return false (caller must append).
     *
     * @param text         text to search for
     * @param newTimestamp new time to write
     * @return true if the entry was found and updated successfully
     */
    public boolean updateTimestampIfExists(String text, long newTimestamp) {
        File file = getTodayFile(newTimestamp);
        if (!file.exists()) return false;

        try {
            String content = readRaw(file);
            if (content == null) return false;

            String encoded;
            try {
                encoded = android.util.Base64.encodeToString(
                        text.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
            } catch (java.io.UnsupportedEncodingException e) {
                return false;
            }

            // Look for an exact line match (not substring) to avoid false positives
            if (!containsExactLine(content, encoded)) return false;

            String newTs = "TIMESTAMP: " + ClipboardEntry.formatTimestamp(newTimestamp);
            String updatedContent = replaceTimestampForEncoded(content, encoded, newTs);
            if (updatedContent == null) return false;

            writeRaw(file, updatedContent);
            Log.d(TAG, "Timestamp updated for existing text.");
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Failed to update timestamp: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check whether the encoded text exists as a full line within content.
     * Avoids false positives from substring matches.
     */
    private boolean containsExactLine(String content, String encoded) {
        for (String line : content.split("\n")) {
            if (line.trim().equals(encoded)) return true;
        }
        return false;
    }

    /**
     * Replace the TIMESTAMP line in the block that contains the encoded text.
     * Only the first matching block is changed.
     */
    private String replaceTimestampForEncoded(String content, String encoded, String newTsLine) {
        // Find the line that is an exact match with encoded (not substring)
        // then replace the TIMESTAMP line right before it
        String[] lines = content.split("\n");
        StringBuilder sb = new StringBuilder();
        boolean replaced = false;

        for (int i = 0; i < lines.length; i++) {
            if (!replaced && lines[i].trim().equals(encoded) && i > 0
                    && lines[i - 1].trim().startsWith("TIMESTAMP:")) {
                // Replace the TIMESTAMP line that was previously appended
                // Remove the last suffix (old TIMESTAMP line + \n) then add the new one
                String built = sb.toString();
                int lastNl = built.lastIndexOf("\n", built.length() - 2);
                sb = new StringBuilder(built.substring(0, lastNl + 1));
                sb.append(newTsLine).append("\n");
                sb.append(lines[i]).append("\n");
                replaced = true;
            } else {
                sb.append(lines[i]).append("\n");
            }
        }
        return replaced ? sb.toString() : null;
    }

    /**
     * Search for the same text in all old files (excluding today's file).
     * If found, remove that entry from the old file.
     * Delete empty files and directories after removal.
     *
     * @param text       text to search for
     * @param todayFile  today's file (excluded from the search)
     * @return true if found and successfully removed from an old file
     */
    public boolean findAndRemoveFromOldFiles(String text, File todayFile) {
        String encoded;
        try {
            encoded = android.util.Base64.encodeToString(
                    text.getBytes("UTF-8"), android.util.Base64.NO_WRAP);
        } catch (java.io.UnsupportedEncodingException e) {
            return false;
        }
        return searchAndRemove(rootDir, encoded, todayFile);
    }

    /**
     * Recursively search for the encoded text across all clipboard_log.txt
     * files, except todayFile. If found, remove its entry block.
     */
    private boolean searchAndRemove(File dir, String encoded, File todayFile) {
        File[] children = dir.listFiles();
        if (children == null) return false;

        for (File child : children) {
            if (child.isDirectory()) {
                boolean found = searchAndRemove(child, encoded, todayFile);
                if (found) {
                    // Clean up empty directories after removal
                    deleteEmptyDirsUpTo(child, rootDir);
                    return true;
                }
            } else if (child.getName().equals(FILE_NAME)
                    && !child.getAbsolutePath().equals(todayFile.getAbsolutePath())) {
                try {
                    String content = readRaw(child);
                    if (content != null && containsExactLine(content, encoded)) {
                        String removed = removeEntryByEncoded(content, encoded);
                        if (removed != null) {
                            if (removed.trim().isEmpty()) {
                                // File became empty, just delete it
                                child.delete();
                                Log.d(TAG, "Old file empty after removal, deleted: " + child.getPath());
                            } else {
                                writeRaw(child, removed);
                                Log.d(TAG, "Entry removed from old file: " + child.getPath());
                            }
                            return true;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to read/write old file: " + e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * Remove one entry block from content based on the encoded text.
     * Block format:
     *   === {id} ===\n
     *   TIMESTAMP: ...\n
     *   {encoded}\n
     *   === END {id} ===\n
     *
     * @return new content without that block, or null if not found
     */
    private String removeEntryByEncoded(String content, String encoded) {
        // Find the position of the encoded text
        int encodedPos = content.indexOf(encoded);
        if (encodedPos < 0) return null;

        // Find the start of the block: walk backward from encodedPos to find "=== " at line start
        // Structure: header\nTIMESTAMP\nencoded\nfooter
        // So we need to step back 2 lines from encodedPos

        // Find the start of the encoded line
        int encodedLineStart = content.lastIndexOf("\n", encodedPos - 1);
        if (encodedLineStart < 0) encodedLineStart = 0; else encodedLineStart += 1;

        // TIMESTAMP line: one line before encoded
        int tsLineStart = content.lastIndexOf("\n", encodedLineStart - 2);
        if (tsLineStart < 0) tsLineStart = 0; else tsLineStart += 1;

        // Header line: one line before TIMESTAMP
        int headerLineStart = content.lastIndexOf("\n", tsLineStart - 2);
        if (headerLineStart < 0) headerLineStart = 0; else headerLineStart += 1;

        // Find the end of the block: the "=== END {id} ===" line after encoded
        int encodedLineEnd = content.indexOf("\n", encodedPos);
        if (encodedLineEnd < 0) return null;
        int footerLineEnd = content.indexOf("\n", encodedLineEnd + 1);
        if (footerLineEnd < 0) footerLineEnd = content.length();
        else footerLineEnd += 1; // include \n

        // Validate: header must start with "=== "
        String headerLine = content.substring(headerLineStart,
                content.indexOf("\n", headerLineStart));
        if (!headerLine.trim().startsWith("===") || !headerLine.trim().endsWith("===")) {
            return null;
        }

        // Remove the block from headerLineStart to footerLineEnd
        return content.substring(0, headerLineStart)
             + content.substring(footerLineEnd);
    }

    /**
     * Delete empty directories upward from dir, stopping at stopAt.
     */
    private void deleteEmptyDirsUpTo(File dir, File stopAt) {
        File current = dir;
        while (current != null && !current.getAbsolutePath().equals(stopAt.getAbsolutePath())) {
            File[] files = current.listFiles();
            if (files != null && files.length == 0) {
                current.delete();
                Log.d(TAG, "Empty directory deleted: " + current.getPath());
                current = current.getParentFile();
            } else {
                break;
            }
        }
    }

    /**
     * Read the entire file content as a String.
     * Uses DataInputStream.readFully() to guarantee all bytes are read,
     * unlike InputStream.read() which can do a partial read on large files.
     */
    private String readRaw(File file) throws IOException {
        if (!file.exists()) return null;
        byte[] bytes = new byte[(int) file.length()];
        java.io.DataInputStream dis = new java.io.DataInputStream(
                new java.io.FileInputStream(file));
        try {
            dis.readFully(bytes);
        } finally {
            dis.close();
        }
        return new String(bytes, "UTF-8");
    }

    /**
     * Read the most recent clipboard entries from all files, sorted newest first.
     * Used to display HIST mode on the keyboard.
     *
     * @param maxEntries maximum number of entries to return
     * @return list of entries, newest at index 0
     */
    public java.util.List<ClipboardEntry> readRecentEntries(int maxEntries) {
        java.util.List<ClipboardEntry> result = new java.util.ArrayList<>();
        collectEntries(rootDir, result);
        java.util.Collections.sort(result, new java.util.Comparator<ClipboardEntry>() {
            @Override
            public int compare(ClipboardEntry a, ClipboardEntry b) {
                return Long.compare(b.timestamp, a.timestamp);
            }
        });
        if (result.size() > maxEntries) {
            return result.subList(0, maxEntries);
        }
        return result;
    }

    private void collectEntries(File dir, java.util.List<ClipboardEntry> out) {
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isDirectory()) {
                collectEntries(child, out);
            } else if (child.getName().equals(FILE_NAME)) {
                parseEntriesFromFile(child, out);
            }
        }
    }

    private void parseEntriesFromFile(File file, java.util.List<ClipboardEntry> out) {
        try {
            String content = readRaw(file);
            if (content == null || content.trim().isEmpty()) return;

            java.util.List<Integer> starts = new java.util.ArrayList<>();
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.startsWith("=== ") && line.endsWith("===") && !line.startsWith("=== END")) {
                    starts.add(i);
                }
            }

            for (int s = 0; s < starts.size(); s++) {
                int from = starts.get(s);
                int to   = (s + 1 < starts.size()) ? starts.get(s + 1) : lines.length;

                StringBuilder sb = new StringBuilder();
                for (int i = from; i < to; i++) {
                    sb.append(lines[i]).append("\n");
                }
                ClipboardEntry entry = ClipboardEntry.deserialize(sb.toString().trim());
                if (entry != null) out.add(entry);
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to parse entries from: " + file.getPath(), e);
        }
    }

    /**
     * Overwrite the entire content of the file.
     */
    private void writeRaw(File file, String content) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(file, false);
        try {
            fos.write(content.getBytes("UTF-8"));
        } finally {
            fos.close();
        }
    }

    /**
     * Return the File for today's log (based on the timestamp).
     */
    public File getTodayFile(long epochMillis) {
        Date date = new Date(epochMillis);
        String year  = fmt(FMT_YEAR,  date);
        String month = fmt(FMT_MONTH, date);
        String day   = fmt(FMT_DATE,  date);

        return new File(rootDir, year + "/" + month + "/" + day + "/" + FILE_NAME);
    }

    /**
     * Return the root directory .localcache/copy
     */
    public File getRootDir() {
        return rootDir;
    }

    /**
     * Check whether the WRITE_EXTERNAL_STORAGE permission is available
     * (for Android < 10) or the media directory is writable (Android 10+).
     *
     * On Android 10+, accessing Android/media/<package> requires no special permission.
     */
    public boolean isStorageAvailable() {
        try {
            File parent = rootDir.getParentFile();
            if (parent == null) return false;
            // Try creating the test directory
            if (!parent.exists()) {
                parent.mkdirs();
            }
            return parent.canWrite();
        } catch (Exception e) {
            Log.e(TAG, "Storage is not available: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean ensureDir(File dir) {
        if (dir == null) return false;
        if (dir.exists()) return true;
        return dir.mkdirs();
    }

    private String fmt(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}

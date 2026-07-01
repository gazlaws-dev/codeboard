package com.gazlaws.codeboard.clipboard;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Data model for a single clipboard entry.
 *
 * Every time the user copies text, one ClipboardEntry is created.
 * This entry stores:
 *   - id       : short unique identifier (8 characters from a UUID)
 *   - text     : the original copied text
 *   - timestamp: time it was copied (epoch millis)
 *
 * Format when written to file:
 *
 *   === {id} ===
 *   TIMESTAMP: {timestamp human-readable}
 *   {text base64-encoded}
 *   === END {id} ===
 */
public class ClipboardEntry {

    private static final String TIMESTAMP_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    public final String id;
    public final String text;
    public final long timestamp;

    public ClipboardEntry(String text, long timestamp) {
        this.id        = generateShortId();
        this.text      = text;
        this.timestamp = timestamp;
    }

    // Internal constructor for deserialize — preserves the ID from the file
    private ClipboardEntry(String id, String text, long timestamp) {
        this.id        = id;
        this.text      = text;
        this.timestamp = timestamp;
    }

    // -------------------------------------------------------------------------
    // Serialize → string (to be written to file)
    // -------------------------------------------------------------------------

    /**
     * Convert this entry into a text block ready to be written to file.
     * The text content is Base64-encoded so it is safe for any character.
     */
    public String serialize() throws java.io.UnsupportedEncodingException {
        String encoded = Base64.encodeToString(text.getBytes("UTF-8"), Base64.NO_WRAP);
        String tsHuman = formatTimestamp(timestamp);

        return "=== " + id + " ===\n"
             + "TIMESTAMP: " + tsHuman + "\n"
             + encoded + "\n"
             + "=== END " + id + " ===\n";
    }

    // -------------------------------------------------------------------------
    // Deserialize ← string (to be read from file)
    // -------------------------------------------------------------------------

    /**
     * Parse a single entry block from a string.
     * Returns null if the format is invalid.
     *
     * Expected format:
     *   === {id} ===
     *   TIMESTAMP: {ts}
     *   {base64}
     *   === END {id} ===
     */
    public static ClipboardEntry deserialize(String block) {
        try {
            String[] lines = block.trim().split("\n");
            if (lines.length < 4) return null;

            // Line 0: === {id} ===
            String headerLine = lines[0].trim();
            if (!headerLine.startsWith("=== ") || !headerLine.endsWith(" ===")) return null;
            // Strip the leading "=== " and trailing " ==="
            String id = headerLine.substring(4, headerLine.length() - 4).trim();
            if (id.isEmpty()) return null;

            // Find the TIMESTAMP: line (no fixed index assumed, search from line 1)
            String tsStr = null;
            int encodedLineIdx = -1;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.startsWith("TIMESTAMP:") && tsStr == null) {
                    tsStr = line.substring("TIMESTAMP:".length()).trim();
                } else if (tsStr != null && !line.startsWith("=== END") && !line.isEmpty()) {
                    encodedLineIdx = i;
                    break;
                }
            }
            if (tsStr == null || encodedLineIdx < 0) return null;

            long ts = parseTimestamp(tsStr);

            // Decode Base64 with an explicit charset
            String encoded = lines[encodedLineIdx].trim();
            byte[] decoded = Base64.decode(encoded, Base64.NO_WRAP);
            String text = new String(decoded, "UTF-8");

            return new ClipboardEntry(id, text, ts);
        } catch (Exception e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String generateShortId() {
        // Take the first 8 characters of a UUID, without hyphens
        // Example: "7f29c1e8"
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 6) + "-" + uuid.substring(6, 8);
    }

    public static String formatTimestamp(long epochMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(epochMillis));
    }

    private static long parseTimestamp(String tsStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getDefault());
            Date d = sdf.parse(tsStr);
            return d != null ? d.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}

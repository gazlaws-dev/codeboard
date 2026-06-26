package com.gazlaws.codeboard

import android.content.Context
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.io.ByteArrayOutputStream

object SettingsManager {

    private const val MAX_FILE_SIZE = 512 * 1024 // 512KB

    @JvmStatic
    fun exportSettings(context: Context, outputStream: OutputStream): String? {
        return try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val allPrefs = prefs.all
            val jsonObject = JSONObject()

            for ((key, value) in allPrefs) {
                jsonObject.put(key, value)
            }

            outputStream.bufferedWriter().use { writer ->
                writer.write(jsonObject.toString())
            }
            null
        } catch (t: Throwable) {
            t.localizedMessage ?: "Unknown export error"
        } finally {
            try { outputStream.close() } catch (ignored: Exception) {}
        }
    }

    @JvmStatic
    fun importSettings(context: Context, inputStream: InputStream): String? {
        return try {
            val buffer = ByteArray(60 * 1024) // 60KB
            val output = ByteArrayOutputStream()
            var totalBytes = 0
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                totalBytes += bytesRead
                if (totalBytes > MAX_FILE_SIZE) {
                    return "File too large (max 512KB)"
                }
                output.write(buffer, 0, bytesRead)
            }

            val content = output.toString("UTF-8")
            if (content.isBlank()) return "File is empty"

            val jsonObject = try {
                JSONObject(content)
            } catch (e: Exception) {
                return "Invalid JSON format: ${e.localizedMessage}"
            }

            if (jsonObject.length() == 0) {
                return "This is not a valid settings file (empty)"
            }

            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = prefs.edit()

            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (jsonObject.isNull(key)) continue

                val value = jsonObject.get(key)
                when (value) {
                    is Boolean -> editor.putBoolean(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Double -> editor.putFloat(key, value.toFloat())
                    is Float -> editor.putFloat(key, value)
                    is String -> editor.putString(key, value)
                }
            }
            editor.apply()
            null
        } catch (t: Throwable) {
            t.localizedMessage ?: "Invalid file or read error"
        } finally {
            try { inputStream.close() } catch (ignored: Exception) {}
        }
    }
}

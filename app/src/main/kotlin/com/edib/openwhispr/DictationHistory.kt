package com.edib.openwhispr

import android.content.SharedPreferences
import org.json.JSONArray

/** The last few dictations, newest first, so the overlay's long-press panel
 * can insert one again. Kept in the app's private prefs; never leaves the
 * device. */
object DictationHistory {
    const val MAX_ITEMS = 5
    private const val KEY = "dictation_history"

    fun load(prefs: SharedPreferences): List<String> = decode(prefs.getString(KEY, null))

    fun add(prefs: SharedPreferences, text: String) {
        prefs.edit().putString(KEY, encode(push(load(prefs), text))).apply()
    }

    fun clear(prefs: SharedPreferences) {
        prefs.edit().remove(KEY).apply()
    }

    /** Puts [text] first, dropping an older copy of the same text and
     * anything past [MAX_ITEMS]. Blank text leaves the list unchanged. */
    fun push(items: List<String>, text: String): List<String> {
        val entry = text.trim()
        if (entry.isEmpty()) return items
        return (listOf(entry) + items.filter { it != entry }).take(MAX_ITEMS)
    }

    fun encode(items: List<String>): String = JSONArray(items).toString()

    fun decode(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { array.optString(it, "").takeIf(String::isNotBlank) }
                .take(MAX_ITEMS)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** The start of [text] on one line, for the panel's rows. */
    fun preview(text: String, maxChars: Int = 80): String {
        val oneLine = text.replace(Regex("\\s+"), " ").trim()
        return if (oneLine.length <= maxChars) oneLine else oneLine.take(maxChars).trimEnd() + "…"
    }
}

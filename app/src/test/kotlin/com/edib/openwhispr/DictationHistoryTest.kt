package com.edib.openwhispr

import org.junit.Assert.assertEquals
import org.junit.Test

class DictationHistoryTest {

    @Test
    fun `newest dictation goes first and the list keeps ten`() {
        var items = emptyList<String>()
        for (i in 1..12) items = DictationHistory.push(items, "dictation $i")
        assertEquals((12 downTo 3).map { "dictation $it" }, items)
    }

    @Test
    fun `repeating a dictation moves it to the top instead of duplicating it`() {
        val items = DictationHistory.push(listOf("b", "a"), "a")
        assertEquals(listOf("a", "b"), items)
    }

    @Test
    fun `blank text is not recorded and text is trimmed`() {
        assertEquals(listOf("a"), DictationHistory.push(listOf("a"), "   "))
        assertEquals(listOf("hi there", "a"), DictationHistory.push(listOf("a"), "  hi there \n"))
    }

    @Test
    fun `encode and decode round trip, including quotes and newlines`() {
        val items = listOf("line one\nline two", "she said \"hi\"", "emoji 👍")
        assertEquals(items, DictationHistory.decode(DictationHistory.encode(items)))
    }

    @Test
    fun `corrupt or missing stored history reads as empty`() {
        assertEquals(emptyList<String>(), DictationHistory.decode(null))
        assertEquals(emptyList<String>(), DictationHistory.decode("not json"))
    }

    @Test
    fun `preview collapses whitespace and cuts long text with an ellipsis`() {
        assertEquals("one two three", DictationHistory.preview("one\n two\t three"))
        assertEquals("abcde…", DictationHistory.preview("abcdefgh", maxChars = 5))
    }
}

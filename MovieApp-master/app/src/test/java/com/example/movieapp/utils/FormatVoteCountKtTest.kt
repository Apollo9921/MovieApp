package com.example.movieapp.utils

import com.example.movieapp.core.utils.formatVoteCount
import org.junit.Test
import kotlin.test.assertEquals

class FormatVoteCountKtTest {

    @Test
    fun `Test count less than 1000`() {
        for (i in 0 until 1000) {
            assertEquals(i.toString(), formatVoteCount(i))
        }
    }

    @Test
    fun `Test count equal to 0`() {
        assertEquals("0", formatVoteCount(0))
    }

    @Test
    fun `Test count exactly 1000`() {
        assertEquals("1K", formatVoteCount(1000))
    }

    @Test
    fun `Test count exactly 999`() {
        assertEquals("999", formatVoteCount(999))
    }

    @Test
    fun `Test thousands formatting with rounding`() {
        assertEquals("1,3K", formatVoteCount(1260))
    }

    @Test
    fun `Test thousands formatting without decimal`() {
        assertEquals("2K", formatVoteCount(2000))
    }

    @Test
    fun `Test thousands formatting with decimal`() {
        assertEquals("2,1K", formatVoteCount(2100))
    }

    @Test
    fun `Test upper boundary before millions`() {
        assertEquals("1000K", formatVoteCount(999999))
    }

    @Test
    fun `Test count exactly 1 000 000`() {
        assertEquals("1M", formatVoteCount(1000000))
    }

    @Test
    fun `Test millions formatting with rounding`() {
        assertEquals("1,3M", formatVoteCount(1250001))
    }

    @Test
    fun `Test millions formatting without decimal`() {
        assertEquals("2M", formatVoteCount(2000000))
    }

    @Test
    fun `Test millions formatting with decimal`() {
        assertEquals("2,1M", formatVoteCount(2100000))
    }

    @Test
    fun `Test with Int MAX VALUE`() {
       assertEquals("2147,5M", formatVoteCount(Int.MAX_VALUE))
    }

    @Test
    fun `Test count just below a million`() {
        assertEquals("1000K", formatVoteCount(999999))
    }

    @Test
    fun `Test with negative input value`() {
        assertEquals("-1", formatVoteCount(-1))
    }

}
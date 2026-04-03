package com.gery711k.yettelteszt.ui.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class DateUtilsTest {

    @Test
    fun `toReadableString formats date correctly`() {
        // given
        val date = LocalDateTime.of(2023, 10, 27, 12, 30)
        
        // when
        val result = date.toReadableString()
        
        // then
        assertEquals("2023 October 27, 12:30", result)
    }
}
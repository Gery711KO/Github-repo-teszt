package com.gery711k.yettelteszt.ui.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun LocalDateTime.toReadableString(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd, HH:mm")

    return format(formatter)
}
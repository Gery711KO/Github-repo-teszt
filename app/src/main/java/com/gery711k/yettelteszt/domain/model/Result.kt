package com.gery711k.yettelteszt.domain.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface Result<out T: Any> {
    val data: T?

    data class Loading<out T: Any>(override val data: T?): Result<T>

    data class Success<out T: Any>(override val data: T): Result<T>

    data class Error<out T: Any>(
        val error: Exception,
        override val data: T? = null,
    ): Result<T>
}
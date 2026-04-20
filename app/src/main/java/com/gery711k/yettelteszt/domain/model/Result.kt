package com.gery711k.yettelteszt.domain.model

sealed interface Result<out T: Any> {
    val data: T?

    data class Loading<out T: Any>(override val data: T?): Result<T>

    data class Success<out T: Any>(override val data: T): Result<T>

    data class Error<out T: Any>(
        val error: Exception,
        override val data: T? = null,
    ): Result<T>
}

fun <T: Any> Result<T>?.toLoadingOrDefault() = this?.let { result ->
    Result.Loading(
        data = result.data
    )
} ?: Result.Loading(null)

fun <T: Any> Result<T>?.toErrorOrDefault(error: Exception) = this?.let { result ->
    Result.Error(
        error = error,
        data = result.data
    )
} ?: Result.Error(error = error)
package com.trackmybus.theBauncer.core.mapper

import kotlin.fold
import kotlin.runCatching

object ResultMapper {
    inline fun <T, R> Result<T>.mapResult(transform: (T) -> R): Result<R> =
        fold(
            onSuccess = { value ->
                runCatching { transform(value) }
            },
            onFailure = { error ->
                Result.failure(error)
            },
        )
}

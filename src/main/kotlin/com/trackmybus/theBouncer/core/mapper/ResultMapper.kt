package com.trackmybus.theBouncer.core.mapper

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.fold
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.DataError
import kotlinx.serialization.SerializationException

object ResultMapper {
    inline fun <D, reified E : RootError, R> Result<D, E>.mapResult(transform: (D) -> R?): Result<R, DataError.Local> =
        fold(
            onSuccess = { value ->
                try {
                    val mappedValue = transform(value)
                    if (mappedValue == null) {
                        Result.Error(DataError.Local.NullMappingResult)
                    } else {
                        Result.Success(mappedValue)
                    }
                } catch (e: SerializationException) {
                    Result.Error(DataError.Local.SerializationError)
                } catch (e: IllegalArgumentException) {
                    Result.Error(DataError.Local.InvalidDataType)
                } catch (e: Exception) {
                    Result.Error(DataError.Local.MappingFailed)
                }
            },
            onFailure = { _ ->
                Result.Error(DataError.Local.MappingFailed)
            },
        )
}

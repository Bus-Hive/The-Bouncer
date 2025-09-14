package app.bushive.theBouncer.core.mapper

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.ResultHandler.fold
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.core.result.errors.DataError
import kotlinx.serialization.SerializationException

object ResultMapper {
    inline fun <D, reified E : RootError, R> Result<D, E>.mapResult(transform: (D) -> R?): Result<R, E> =
        fold(
            onSuccess = { value ->
                try {
                    val mappedValue = transform(value)
                    if (mappedValue == null) {
                        Result.Error<R, E>(DataError.Local.NullMappingResult as E)
                    } else {
                        Result.Success<R, E>(mappedValue)
                    }
                } catch (e: SerializationException) {
                    Result.Error<R, E>(DataError.Local.SerializationError as E)
                } catch (e: IllegalArgumentException) {
                    Result.Error<R, E>(DataError.Local.InvalidDataType as E)
                } catch (e: Exception) {
                    Result.Error<R, E>(DataError.Local.MappingFailed as E)
                }
            },
            onFailure = { error ->
                Result.Error<R, E>(error)
            },
        )
}

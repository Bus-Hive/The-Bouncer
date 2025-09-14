package app.bushive.theBouncer.core.result

import app.bushive.theBouncer.core.result.errors.Error
import io.ktor.http.HttpStatusCode
typealias RootError = Error

sealed interface Result<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(
        val data: D,
        val code: HttpStatusCode = HttpStatusCode.OK,
        val message: String? = null,
    ) : Result<D, E>

    data class Error<out D, out E : RootError>(
        val error: E,
        val code: HttpStatusCode = error.defaultCode,
        val data: D? = null,
        val message: String? = null,
    ) : Result<D, E>

    fun getDataOrNull(): D? = (this as? Success)?.data

    fun getErrorOrNull(): E? = (this as? Error)?.error
}

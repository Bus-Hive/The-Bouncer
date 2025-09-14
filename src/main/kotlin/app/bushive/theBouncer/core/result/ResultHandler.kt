package app.bushive.theBouncer.core.result

import app.bushive.theBouncer.core.api.ApiResponse
import app.bushive.theBouncer.core.api.respondError
import app.bushive.theBouncer.core.api.respondSuccess
import app.bushive.theBouncer.core.result.Result.Success
import app.bushive.theBouncer.core.result.errors.DataError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.SerializationException
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.DatabaseConnectionAutoRegistration
import java.sql.SQLException

object ResultHandler {
    suspend inline fun <reified D, E : RootError> ApplicationCall.respondResult(result: Result<D, E>) {
        when (result) {
            is Success -> respondSuccess(data = result.data, message = result.message, code = result.code)
            is Result.Error ->
                respondError(
                    statusCode = result.code,
                    errorCode = result.error.defaultCode.value,
                    message = result.message ?: "An error occurred",
                    details = result.data as? String ?: "Unknown error",
                )
        }
    }

    fun <D, E : RootError> Result<D, E>.toApiResponse(): ApiResponse<D> =
        when (this) {
            is Result.Success -> ApiResponse.Success(data, message, code?.value)
            is Result.Error ->
                ApiResponse.Error(
                    code = error.defaultCode.value,
                    message = message ?: "An error occurred",
                    details = data as? String ?: "Unknown error",
                )
        }

    fun <D, E : RootError> Result<D, E>.modify(
        data: D? = null,
        error: E? = null,
        code: HttpStatusCode? = null,
        message: String? = null,
    ): Result<D, E> =
        when (this) {
            is Result.Success ->
                copy(
                    data = data ?: this.data,
                    code = code ?: this.code,
                    message = message ?: this.message,
                )

            is Result.Error ->
                copy(
                    error = error ?: this.error,
                    code = code ?: this.code,
                    data = data ?: this.data,
                    message = message ?: this.message,
                )
        }

    fun <D, E : RootError> Result<D, E>.addMessage(
        success: String? = null,
        failure: String? = null,
    ): Result<D, E> =
        when (this) {
            is Result.Success -> modify(message = success)
            is Result.Error -> modify(message = failure)
        }

    fun Exception.toLocalError(): DataError.Local =
        when (this) {
            is SerializationException -> DataError.Local.SerializationError

            is ExposedSQLException ->
                when {
                    this.message?.contains(
                        "constraint",
                        ignoreCase = true,
                    ) == true -> DataError.Local.ConstraintViolation

                    this.message?.contains("deadlock", ignoreCase = true) == true -> DataError.Local.DeadlockDetected
                    this.message?.contains("timeout", ignoreCase = true) == true -> DataError.Local.Timeout
                    this.message?.contains("rollback", ignoreCase = true) == true -> DataError.Local.TransactionRollback
                    this.message?.contains("not found", ignoreCase = true) == true -> DataError.Local.RecordNotFound
                    else -> DataError.Local.QueryFailed
                }

            is SQLException ->
                when (this.sqlState) {
                    "08001", "08006" -> DataError.Local.DatabaseConnectionFailed // Database connection issues
                    "40001" -> DataError.Local.DeadlockDetected // Deadlock
                    "42P01" -> DataError.Local.QueryFailed // Table not found
                    "23505" -> DataError.Local.ConstraintViolation // Unique constraint violation
                    else -> DataError.Local.Unknown
                }

            is SecurityException -> DataError.Local.PermissionDenied

            is EntityNotFoundException -> DataError.Local.RecordNotFound

            is DatabaseConnectionAutoRegistration -> DataError.Local.DatabaseConnectionFailed

            else -> DataError.Local.Unknown
        }

    inline fun <D, reified E : RootError> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
        if (this is Result.Success) {
            action(data)
        }
        return this
    }

    inline fun <D, reified E : RootError> Result<D, E>.onFailure(action: (E) -> Unit): Result<D, E> {
        if (this is Result.Error) {
            action(error)
        }
        return this
    }

    inline fun <D, reified E : RootError> Result<D, E>.isSuccess(): Boolean = this is Result.Success

    inline fun <D, reified E : RootError> Result<D, E>.isFailure(): Boolean = this is Result.Error

    inline fun <D, reified E : RootError, R> Result<D, E>.fold(
        onSuccess: (D) -> R,
        onFailure: (E) -> R,
    ): R =
        when (this) {
            is Result.Success -> onSuccess(data)
            is Result.Error -> onFailure(error)
        }

    inline fun <D, reified E : RootError> Result<D, E>.getError(): E =
        when (this) {
            is Result.Success -> throw IllegalStateException("Result is not an error")
            is Result.Error -> error
        }

    inline fun <reified D2, reified E : RootError> Result<*, E>.toErrorResult(): Result<D2, E> =
        when (this) {
            is Result.Success -> throw IllegalStateException("Cannot convert Success to Error")
            is Result.Error ->
                Result.Error(
                    error = this.error,
                    data = null,
                    code = this.code,
                    message = this.message,
                )
        }
}

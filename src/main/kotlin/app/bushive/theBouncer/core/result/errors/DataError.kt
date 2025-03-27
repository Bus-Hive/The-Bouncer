package app.bushive.theBouncer.core.result.errors

import io.ktor.http.HttpStatusCode

sealed interface DataError : Error {
    sealed interface Network : DataError {
        object RequestTimeout : Network {
            override val defaultCode = HttpStatusCode.Companion.RequestTimeout
        }

        object TooManyRequests : Network {
            override val defaultCode = HttpStatusCode.Companion.TooManyRequests
        }

        object NoInternet : Network {
            override val defaultCode = HttpStatusCode.Companion.ServiceUnavailable
        }

        object PayloadTooLarge : Network {
            override val defaultCode = HttpStatusCode.Companion.PayloadTooLarge
        }

        object ServerError : Network {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object Unauthorized : Network {
            override val defaultCode = HttpStatusCode.Companion.Unauthorized
        }

        object Serialization : Network {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object InternalServerError : Network {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object NotFound : Network {
            override val defaultCode = HttpStatusCode.Companion.NotFound
        }

        object BadRequest : Network {
            override val defaultCode = HttpStatusCode.Companion.BadRequest
        }

        object Unknown : Network {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object ConnectionFailed : Network {
            override val defaultCode = HttpStatusCode.Companion.ServiceUnavailable
        }

        object ConnectionAlreadyExists : Network {
            override val defaultCode = HttpStatusCode.Companion.Conflict
        }

        object ConnectionNotEstablished : Network {
            override val defaultCode = HttpStatusCode.Companion.ServiceUnavailable
        }

        object SendFailed : Network {
            override val defaultCode = HttpStatusCode.Companion.BadGateway
        }
    }

    sealed interface Local : DataError {
        object DatabaseConnectionFailed : Local {
            override val defaultCode = HttpStatusCode.Companion.ServiceUnavailable
        }

        object QueryFailed : Local {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object TransactionRollback : Local {
            override val defaultCode = HttpStatusCode.Companion.Conflict
        }

        object ConstraintViolation : Local {
            override val defaultCode = HttpStatusCode.Companion.BadRequest
        }

        object RecordNotFound : Local {
            override val defaultCode = HttpStatusCode.Companion.NotFound
        }

        object DeadlockDetected : Local {
            override val defaultCode = HttpStatusCode.Companion.Conflict
        }

        object Timeout : Local {
            override val defaultCode = HttpStatusCode.Companion.RequestTimeout
        }

        object PermissionDenied : Local {
            override val defaultCode = HttpStatusCode.Companion.Forbidden
        }

        object SerializationError : Local {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object Unknown : Local {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object MappingFailed : Local {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object NullMappingResult : Local {
            override val defaultCode = HttpStatusCode.Companion.InternalServerError
        }

        object InvalidDataType : Local {
            override val defaultCode = HttpStatusCode.Companion.BadRequest
        }

        object FieldMissing : Local {
            override val defaultCode = HttpStatusCode.Companion.BadRequest
        }
    }
}

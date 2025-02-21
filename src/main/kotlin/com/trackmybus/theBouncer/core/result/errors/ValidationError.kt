package com.trackmybus.theBouncer.core.result.errors

import io.ktor.http.HttpStatusCode

sealed interface ValidationError : Error {
    data object InvalidEmail : ValidationError {
        override val defaultCode = HttpStatusCode.Companion.BadRequest
    }

    data object EmptyField : ValidationError {
        override val defaultCode = HttpStatusCode.Companion.BadRequest
    }

    data object InvalidCredentials : ValidationError {
        override val defaultCode = HttpStatusCode.Companion.BadRequest
    }

    data object EmailAlreadyExists : ValidationError {
        override val defaultCode = HttpStatusCode.Companion.Conflict
    }

    data object UnknownError : ValidationError {
        override val defaultCode = HttpStatusCode.Companion.InternalServerError
    }
}

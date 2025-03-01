package com.trackmybus.theBouncer.core.result.errors

import io.ktor.http.HttpStatusCode

sealed interface JwtError : Error {
    data object InvalidToken : JwtError {
        override val defaultCode = HttpStatusCode.Unauthorized
    }

    data object InvalidTokenType : JwtError {
        override val defaultCode = HttpStatusCode.BadRequest
    }

    data object MissingToken : JwtError {
        override val defaultCode = HttpStatusCode.Unauthorized
    }

    data object SessionNotFound : JwtError {
        override val defaultCode = HttpStatusCode.Unauthorized
    }

    data object UserNotFound : JwtError {
        override val defaultCode = HttpStatusCode.NotFound
    }

    data object TokenGenerationFailed : JwtError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object InvalidClaims : JwtError {
        override val defaultCode = HttpStatusCode.BadRequest
    }

    data object MemoryError : JwtError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object InvalidArguments : JwtError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object TokenExpired : JwtError {
        override val defaultCode = HttpStatusCode.Unauthorized
    }

    data object TokenRefreshFailed : JwtError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object TokenDecodingFailed : JwtError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }
}

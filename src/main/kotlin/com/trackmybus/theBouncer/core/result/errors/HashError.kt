package com.trackmybus.theBouncer.core.result.errors

import io.ktor.http.HttpStatusCode

sealed interface HashError : Error {
    data object HashGenerationFailed : HashError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object HashVerificationFailed : HashError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object InvalidArgument : HashError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object OutOfMemoryError : HashError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }

    data object InvalidHash : HashError {
        override val defaultCode = HttpStatusCode.InternalServerError
    }
}

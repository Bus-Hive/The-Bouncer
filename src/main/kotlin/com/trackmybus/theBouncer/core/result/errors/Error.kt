package com.trackmybus.theBouncer.core.result.errors

import io.ktor.http.HttpStatusCode

sealed interface Error {
    val defaultCode: HttpStatusCode
}

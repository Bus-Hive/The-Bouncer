package com.trackmybus.theBouncer.core.errors

import com.trackmybus.theBouncer.core.result.errors.JwtError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class JwtErrorTest {
    @Test
    fun invalidTokenHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Unauthorized, JwtError.InvalidToken.defaultCode)
    }

    @Test
    fun invalidTokenTypeHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, JwtError.InvalidTokenType.defaultCode)
    }

    @Test
    fun missingTokenHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Unauthorized, JwtError.MissingToken.defaultCode)
    }

    @Test
    fun sessionNotFoundHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Unauthorized, JwtError.SessionNotFound.defaultCode)
    }

    @Test
    fun userNotFoundHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.NotFound, JwtError.UserNotFound.defaultCode)
    }

    @Test
    fun tokenGenerationFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, JwtError.TokenGenerationFailed.defaultCode)
    }

    @Test
    fun invalidClaimsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, JwtError.InvalidClaims.defaultCode)
    }

    @Test
    fun memoryErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, JwtError.MemoryError.defaultCode)
    }

    @Test
    fun invalidArgumentsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, JwtError.InvalidArguments.defaultCode)
    }

    @Test
    fun tokenExpiredHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Unauthorized, JwtError.TokenExpired.defaultCode)
    }

    @Test
    fun tokenRefreshFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, JwtError.TokenRefreshFailed.defaultCode)
    }

    @Test
    fun tokenDecodingFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, JwtError.TokenDecodingFailed.defaultCode)
    }
}

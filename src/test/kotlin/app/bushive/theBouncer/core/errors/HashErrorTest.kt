package app.bushive.theBouncer.core.errors

import app.bushive.theBouncer.core.result.errors.HashError
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class HashErrorTest {
    @Test
    fun hashGenerationFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, HashError.HashGenerationFailed.defaultCode)
    }

    @Test
    fun hashVerificationFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, HashError.HashVerificationFailed.defaultCode)
    }

    @Test
    fun invalidArgumentHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, HashError.InvalidArgument.defaultCode)
    }

    @Test
    fun outOfMemoryErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, HashError.OutOfMemoryError.defaultCode)
    }

    @Test
    fun invalidHashHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, HashError.InvalidHash.defaultCode)
    }
}

import app.bushive.theBouncer.core.result.errors.DataError.Local
import app.bushive.theBouncer.core.result.errors.DataError.Network
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class DataErrorTest {
    @Test
    fun requestTimeoutHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.RequestTimeout, Network.RequestTimeout.defaultCode)
    }

    @Test
    fun tooManyRequestsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.TooManyRequests, Network.TooManyRequests.defaultCode)
    }

    @Test
    fun noInternetHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.ServiceUnavailable, Network.NoInternet.defaultCode)
    }

    @Test
    fun payloadTooLargeHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.PayloadTooLarge, Network.PayloadTooLarge.defaultCode)
    }

    @Test
    fun serverErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Network.ServerError.defaultCode)
    }

    @Test
    fun unauthorizedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Unauthorized, Network.Unauthorized.defaultCode)
    }

    @Test
    fun serializationHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Network.Serialization.defaultCode)
    }

    @Test
    fun internalServerErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Network.InternalServerError.defaultCode)
    }

    @Test
    fun notFoundHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.NotFound, Network.NotFound.defaultCode)
    }

    @Test
    fun badRequestHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, Network.BadRequest.defaultCode)
    }

    @Test
    fun unknownNetworkErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Network.Unknown.defaultCode)
    }

    @Test
    fun connectionFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.ServiceUnavailable, Network.ConnectionFailed.defaultCode)
    }

    @Test
    fun connectionAlreadyExistsHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Conflict, Network.ConnectionAlreadyExists.defaultCode)
    }

    @Test
    fun connectionNotEstablishedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.ServiceUnavailable, Network.ConnectionNotEstablished.defaultCode)
    }

    @Test
    fun sendFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadGateway, Network.SendFailed.defaultCode)
    }

    @Test
    fun databaseConnectionFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.ServiceUnavailable, Local.DatabaseConnectionFailed.defaultCode)
    }

    @Test
    fun queryFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Local.QueryFailed.defaultCode)
    }

    @Test
    fun transactionRollbackHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Conflict, Local.TransactionRollback.defaultCode)
    }

    @Test
    fun constraintViolationHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, Local.ConstraintViolation.defaultCode)
    }

    @Test
    fun recordNotFoundHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.NotFound, Local.RecordNotFound.defaultCode)
    }

    @Test
    fun deadlockDetectedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Conflict, Local.DeadlockDetected.defaultCode)
    }

    @Test
    fun timeoutHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.RequestTimeout, Local.Timeout.defaultCode)
    }

    @Test
    fun permissionDeniedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.Forbidden, Local.PermissionDenied.defaultCode)
    }

    @Test
    fun serializationErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Local.SerializationError.defaultCode)
    }

    @Test
    fun unknownLocalErrorHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Local.Unknown.defaultCode)
    }

    @Test
    fun mappingFailedHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Local.MappingFailed.defaultCode)
    }

    @Test
    fun nullMappingResultHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.InternalServerError, Local.NullMappingResult.defaultCode)
    }

    @Test
    fun invalidDataTypeHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, Local.InvalidDataType.defaultCode)
    }

    @Test
    fun fieldMissingHasCorrectDefaultCode() {
        assertEquals(HttpStatusCode.BadRequest, Local.FieldMissing.defaultCode)
    }
}

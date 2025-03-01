import com.trackmybus.theBouncer.core.api.ApiResponse
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.modify
import com.trackmybus.theBouncer.core.result.ResultHandler.toApiResponse
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.DataError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ResultHandlerTest {
    @Test
    fun respondResult_withSuccessResult_returnsSuccessResponse() =
        runBlocking {
            val result = Result.Success<String, RootError>(data = "data", message = "Success", code = HttpStatusCode.OK)
            val response = result.toApiResponse()

            assertEquals(ApiResponse.Success("data", "Success", HttpStatusCode.OK.value), response)
        }

    @Test
    fun respondResult_withErrorResult_returnsErrorResponse() =
        runBlocking {
            val error = DataError.Local.Unknown
            val result =
                Result.Error(error = error, data = "error details", message = "Error", code = HttpStatusCode.BadRequest)
            val response = result.toApiResponse()

            assertEquals(ApiResponse.Error(error.defaultCode.value, "Error", "error details"), response)
        }

    @Test
    fun modify_withSuccessResult_modifiesFieldsCorrectly() {
        val result = Result.Success<String, RootError>(data = "data", message = "Success", code = HttpStatusCode.OK)
        val modifiedResult = result.modify(data = "new data", message = "New Success")

        assertEquals(
            Result.Success<String, RootError>(
                data = "new data",
                message = "New Success",
                code = HttpStatusCode.OK,
            ),
            modifiedResult,
        )
    }

    @Test
    fun modify_withErrorResult_modifiesFieldsCorrectly() {
        val error = DataError.Local.Unknown
        val result =
            Result.Error(error = error, data = "error details", message = "Error", code = HttpStatusCode.BadRequest)
        val modifiedResult = result.modify(data = "new error details", message = "New Error")

        assertEquals(
            Result.Error(
                error = error,
                data = "new error details",
                message = "New Error",
                code = HttpStatusCode.BadRequest,
            ),
            modifiedResult,
        )
    }
}

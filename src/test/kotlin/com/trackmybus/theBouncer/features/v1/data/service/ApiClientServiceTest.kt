import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.features.v1.data.remote.service.apiClient.ApiClientService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiClientServiceTest {
    private val mockEngine =
        MockEngine { request ->
            when (request.url.encodedPath) {
                "/success" -> respond("{}", HttpStatusCode.OK)
                "/unauthorized" -> respond("Unauthorized", HttpStatusCode.Unauthorized)
                "/badrequest" -> respond("Bad Request", HttpStatusCode.BadRequest)
                "/notfound" -> respond("Not Found", HttpStatusCode.NotFound)
                "/servererror" -> respond("Internal Server Error", HttpStatusCode.InternalServerError)
                else -> respond("Unknown Error", HttpStatusCode.ServiceUnavailable)
            }
        }

    private val httpClient = HttpClient(mockEngine)
    private val logger = org.slf4j.LoggerFactory.getLogger(ApiClientService::class.java)
    private val apiClientService =
        ApiClientService(httpClient, logger)

    @Test
    fun get_success() =
        runBlocking {
            val result = apiClientService.get<String>("/success")
            assertTrue(result is Result.Success)
            assertEquals("{}", result.data)
        }

    @Test
    fun get_unauthorized() =
        runBlocking {
            val result = apiClientService.get<String>("/unauthorized")
            assertTrue(result is Result.Error)
            assertEquals(DataError.Network.Unauthorized, result.error)
        }

    @Test
    fun get_badRequest() =
        runBlocking {
            val result = apiClientService.get<String>("/badrequest")
            assertTrue(result is Result.Error)
            assertEquals(DataError.Network.BadRequest, result.error)
        }

    @Test
    fun get_notFound() =
        runBlocking {
            val result = apiClientService.get<String>("/notfound")
            assertTrue(result is Result.Error)
            assertEquals(DataError.Network.NotFound, result.error)
        }

    @Test
    fun get_internalServerError() =
        runBlocking {
            val result = apiClientService.get<String>("/servererror")
            assertTrue(result is Result.Error)
            assertEquals(DataError.Network.InternalServerError, result.error)
        }

    @Test
    fun get_unknownError() =
        runBlocking {
            val result = apiClientService.get<String>("/unknown")
            assertTrue(result is Result.Error)
            assertEquals(DataError.Network.NoInternet, result.error)
        }
}

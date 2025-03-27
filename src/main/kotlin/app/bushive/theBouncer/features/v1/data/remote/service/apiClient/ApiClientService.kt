package app.bushive.theBouncer.features.v1.data.remote.service.apiClient

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.util.logging.Logger
import kotlinx.serialization.SerializationException

/**
 * Service class for making API requests using Ktor HttpClient.
 *
 * @property httpClient The HttpClient instance used for making requests.
 *
 * @author Jakub Olszewski
 */
class ApiClientService(
    val httpClient: HttpClient,
    val logger: Logger,
) {
    /**
     * Makes a GET request to the specified path.
     *
     * @param path The URL path for the GET request.
     * @param header The headers to include in the request.
     * @param queryParams The query parameters to include in the request.
     * @return A [Result] containing the response body or a [DataError.Network] error.
     */
    suspend inline fun <reified T> get(
        path: String,
        header: Map<String, String> = mapOf(),
        queryParams: Map<String, String> = mapOf(),
    ): Result<T, DataError.Network> =
        executeRequest {
            httpClient
                .get(path) {
                    queryParams.forEach {
                        parameter(it.key, it.value)
                    }
                    headers {
                        this.mapToFormHeaderDataContent(header)
                    }
                }.body()
        }

    /**
     * Makes a POST request to the specified path.
     *
     * @param path The URL path for the POST request.
     * @param header The headers to include in the request.
     * @param bodyJson The JSON body to include in the request.
     * @param queryParams The query parameters to include in the request.
     * @return A [Result] containing the response body or a [DataError.Network] error.
     */
    suspend inline fun <reified T> post(
        path: String,
        header: Map<String, String> = mapOf(),
        body: Map<String, String> = mapOf(),
        queryParams: Map<String, String> = mapOf(),
    ): Result<T, DataError.Network> =
        executeRequest {
            httpClient
                .post(path) {
                    queryParams.forEach {
                        parameter(it.key, it.value)
                    }
                    headers {
                        this.mapToFormHeaderDataContent(header)
                    }
                    setBody(toJson(body))
                }.body()
        }

    /**
     * Makes a PUT request to the specified path.
     *
     * @param path The URL path for the PUT request.
     * @param header The headers to include in the request.
     * @param bodyJson The JSON body to include in the request.
     * @param queryParams The query parameters to include in the request.
     * @return A [Result] containing the response body or a [DataError.Network] error.
     */
    suspend inline fun <reified T> put(
        path: String,
        header: Map<String, String> = mapOf(),
        bodyJson: String = "",
        queryParams: Map<String, String> = mapOf(),
    ): Result<T, DataError.Network> =
        executeRequest {
            httpClient
                .put(path) {
                    queryParams.forEach {
                        parameter(it.key, it.value)
                    }
                    headers {
                        this.mapToFormHeaderDataContent(header)
                    }
                    setBody(bodyJson)
                }.body()
        }

    /**
     * Makes a DELETE request to the specified path.
     *
     * @param path The URL path for the DELETE request.
     * @param header The headers to include in the request.
     * @param queryParams The query parameters to include in the request.
     * @param bodyJson The JSON body to include in the request.
     * @return A [Result] containing the response body or a [DataError.Network] error.
     */
    suspend inline fun <reified T> delete(
        path: String,
        header: Map<String, String> = mapOf(),
        queryParams: Map<String, String> = mapOf(),
        bodyJson: String = "",
    ): Result<T, DataError.Network> =
        executeRequest {
            httpClient
                .delete(path) {
                    queryParams.forEach {
                        parameter(it.key, it.value)
                    }
                    headers {
                        this.mapToFormHeaderDataContent(header)
                    }
                    setBody(bodyJson)
                }.body()
        }

    /**
     * Makes a DELETE request to the specified path.
     *
     * @param path The URL path for the DELETE request.
     * @param header The headers to include in the request.
     * @param queryParams The query parameters to include in the request.
     * @param bodyJson The JSON body to include in the request.
     * @return A [Result] containing the response body or a [DataError.Network] error.
     */
    suspend inline fun <reified T> executeRequest(requestBlock: () -> HttpResponse): Result<T, DataError.Network> {
        val result: Result<T, DataError.Network> =
            try {
                val response = requestBlock()
                when {
                    response.status.isSuccess() -> {
                        return Result.Success(response.body(), response.status)
                    }

                    response.status == HttpStatusCode.Unauthorized -> {
                        logger.error("User is unauthorized. Token may be expired.")

                        return Result.Error(
                            DataError.Network.Unauthorized,
                            response.status,
                            response.body(),
                        )
                    }

                    else -> {
                        logger.error("Error response: ${response.status}.")
                        return Result.Error(
                            when (response.status) {
                                HttpStatusCode.BadRequest -> DataError.Network.BadRequest
                                HttpStatusCode.NotFound -> DataError.Network.NotFound
                                HttpStatusCode.InternalServerError -> DataError.Network.InternalServerError
                                HttpStatusCode.RequestTimeout -> DataError.Network.RequestTimeout
                                HttpStatusCode.TooManyRequests -> DataError.Network.TooManyRequests
                                HttpStatusCode.Unauthorized -> DataError.Network.Unauthorized
                                HttpStatusCode.InternalServerError -> DataError.Network.ServerError
                                HttpStatusCode.ServiceUnavailable -> DataError.Network.NoInternet
                                else -> DataError.Network.Unknown
                            },
                            response.status,
                            response.body(),
                        )
                    }
                }
            } catch (e: Exception) {
                Result.Error(DataError.Network.Unknown)
            } catch (e: SerializationException) {
                Result.Error(DataError.Network.Serialization)
            } catch (e: Throwable) {
                Result.Error(DataError.Network.Unknown)
            }

        return result
    }
}

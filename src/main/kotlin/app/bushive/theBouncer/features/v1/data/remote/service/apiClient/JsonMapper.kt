package app.bushive.theBouncer.features.v1.data.remote.service.apiClient

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Converts a given data object into a JSON string.
 *
 * @param T The type of the data object. Object must be serializable.
 * @param data The data object to be converted into a JSON string.
 * @return A JSON string representation of the data object.
 */
inline fun <reified T> toJson(data: T): String = Json.encodeToString(data)

/**
 * Converts a given JSON string into a data object of type T.
 *
 * @param T The type of the data object.
 * @param json The JSON string to be converted into a data object.
 * @return A serializable data object of type T.
 */
inline fun <reified T> fromJson(json: String): T = Json.decodeFromString(json)

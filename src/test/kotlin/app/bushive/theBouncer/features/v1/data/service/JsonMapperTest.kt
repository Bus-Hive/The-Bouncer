import app.bushive.theBouncer.features.v1.data.remote.service.apiClient.fromJson
import app.bushive.theBouncer.features.v1.data.remote.service.apiClient.toJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonMapperTest {
    @Test
    fun toJson_convertsDataObjectToJsonString() {
        val data = DataClass("value1", 123)
        val jsonString = toJson(data)
        assertEquals("""{"field1":"value1","field2":123}""", jsonString)
    }

    @Test
    fun fromJson_convertsJsonStringToDataObject() {
        val jsonString = """{"field1":"value1","field2":123}"""
        val data = fromJson<DataClass>(jsonString)
        assertEquals(DataClass("value1", 123), data)
    }

    @Test
    fun fromJson_throwsSerializationExceptionForInvalidJson() {
        val invalidJsonString = """{"field1":"value1","field2":"invalid"}"""
        assertFailsWith<SerializationException> {
            fromJson<DataClass>(invalidJsonString)
        }
    }

    @Test
    fun toJson_handlesNullValues() {
        val data = DataClass(null, 123)
        val jsonString = toJson(data)
        assertEquals("""{"field1":null,"field2":123}""", jsonString)
    }

    @Test
    fun fromJson_handlesNullValues() {
        val jsonString = """{"field1":null,"field2":123}"""
        val data = fromJson<DataClass>(jsonString)
        assertEquals(DataClass(null, 123), data)
    }

    @Serializable
    data class DataClass(
        val field1: String?,
        val field2: Int,
    )
}

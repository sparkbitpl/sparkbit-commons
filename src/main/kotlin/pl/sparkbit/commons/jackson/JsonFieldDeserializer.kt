package pl.sparkbit.commons.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import pl.sparkbit.commons.json.JsonField
import java.io.IOException

open class JsonFieldDeserializer(
    private val cache: Cache<JavaType, JsonFieldDeserializer>,
    private val wrapped: JavaType?
) : StdScalarDeserializer<JsonField<*>?>(JsonField::class.java), ContextualDeserializer {

    constructor() : this(CacheBuilder.newBuilder().build<JavaType, JsonFieldDeserializer>(), null)

    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, deserializationContext: DeserializationContext): JsonField<*> {
        check(wrapped != null) {
            "Unknown wrapped type"
        }
        val deserializer = deserializationContext.findNonContextualValueDeserializer(wrapped)
        return JsonField.wrap(deserializer.deserialize(jp, deserializationContext))
    }

    override fun getNullValue(ctxt: DeserializationContext?): JsonField<*>? {
        return JsonField.wrap(null)
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty): JsonDeserializer<*> {
        val wrappedType = property.type.bindings.getBoundType(0)
        return cache.get(wrappedType) {
            JsonFieldDeserializer(cache, wrappedType)
        }
    }
}

package pl.sparkbit.commons.openapi

import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.media.Schema

abstract class PropertyCustomizerTest {
    fun getSchema(beanType: Class<*>, property: String): Schema<*> {
        return getSchema(beanType).properties[property]!!
    }

    fun getSchema(beanType: Class<*>): Schema<*> {
        val schemaMap = ModelConverters.getInstance().read(beanType)
        return schemaMap[beanType.simpleName]!!
    }
}

package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.format.support.FormattingConversionService
import pl.sparkbit.commons.validators.EnumValue

class EnumPropertyCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(EnumPropertyCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)
            .withBean("mvcConversionService", FormattingConversionService::class.java)

    @Test
    fun testNoEnumValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithEnumValue::class.java, "plainString")

            assertThat(schema.enum).isNull()
        }
    }

    @Test
    fun testEnumValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithEnumValue::class.java, "enumerated")
            assertThat(schema.enum).isEqualTo(listOf("VAL1", "VAL2"))
        }
    }

}

private data class WithEnumValue(
    @field:EnumValue(value = KotlinEnum::class)
    val enumerated: String,
    val plainString: String
)

private enum class KotlinEnum {
    VAL1, VAL2
}

package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import jakarta.validation.constraints.PositiveOrZero
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.format.support.FormattingConversionService

class PositiveOrZeroCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
                SpringDocConfiguration::class.java,
                SpringDocConfigProperties::class.java
        ))
        .withBean(PositiveOrZeroCustomizer::class.java)
        .withBean("mvcConversionService", FormattingConversionService::class.java)
        .withBean(BeansValidationModel::class.java)

    @Test
    fun testNoPositiveOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithPositiveOrZero::class.java, "value")

            Assertions.assertThat(schema.maximum).isNull()
            Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            Assertions.assertThat(schema.minimum).isNull()
            Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testPositiveOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            listOf("valueList", "valuePositiveOrZero").forEach { fieldName ->
                val schema = getSchema(WithPositiveOrZero::class.java, fieldName)

                Assertions.assertThat(schema.maximum).isNull()
                Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
                Assertions.assertThat(schema.minimum).isEqualTo(BigDecimal.ZERO)
                Assertions.assertThat(schema.exclusiveMinimum).isFalse()
            }
        }
    }
}

private data class WithPositiveOrZero(
    val value: BigDecimal,
    @field:PositiveOrZero
    val valuePositiveOrZero: BigDecimal,
    @field:PositiveOrZero.List(PositiveOrZero())
    val valueList: BigDecimal
)

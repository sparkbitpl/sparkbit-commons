package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import jakarta.validation.constraints.Positive
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.format.support.FormattingConversionService

class PositiveCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(PositiveCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)
            .withBean("mvcConversionService", FormattingConversionService::class.java)

    @Test
    fun testNoPositiveValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithPositive::class.java, "value")

            Assertions.assertThat(schema.maximum).isNull()
            Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            Assertions.assertThat(schema.minimum).isNull()
            Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testPositiveValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            listOf("valueList", "valuePositive").forEach { fieldName ->
                val schema = getSchema(WithPositive::class.java, fieldName)

                Assertions.assertThat(schema.maximum).isNull()
                Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
                Assertions.assertThat(schema.minimum).isEqualTo(BigDecimal.ZERO)
                Assertions.assertThat(schema.exclusiveMinimum).isTrue()
            }
        }
    }
}

private data class WithPositive(
    val value: BigDecimal,
    @field:Positive
    val valuePositive: BigDecimal,
    @field:Positive.List(Positive())
    val valueList: BigDecimal
)

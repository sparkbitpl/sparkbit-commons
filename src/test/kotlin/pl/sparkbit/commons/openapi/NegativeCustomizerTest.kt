package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import jakarta.validation.constraints.Negative
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.format.support.FormattingConversionService

class NegativeCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(NegativeCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)
            .withBean("mvcConversionService", FormattingConversionService::class.java)

    @Test
    fun testNonNegativeValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithNegative::class.java, "value")

            Assertions.assertThat(schema.maximum).isNull()
            Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            Assertions.assertThat(schema.minimum).isNull()
            Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testNegativeValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            listOf("valueList", "valueNegative").forEach { fieldName ->
                val schema = getSchema(WithNegative::class.java, fieldName)

                Assertions.assertThat(schema.maximum).isEqualTo(BigDecimal.ZERO)
                Assertions.assertThat(schema.exclusiveMaximum).isTrue()
                Assertions.assertThat(schema.minimum).isNull()
                Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
            }
        }
    }
}

private data class WithNegative(
    val value: BigDecimal,
    @field:Negative
    val valueNegative: BigDecimal,
    @field:Negative.List(Negative())
    val valueList: BigDecimal
)


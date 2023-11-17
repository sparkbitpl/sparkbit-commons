package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import jakarta.validation.constraints.Digits
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.format.support.FormattingConversionService

class DigitsCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(DigitsCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)
        .withBean("mvcConversionService", FormattingConversionService::class.java)

    @Test
    fun testNoDigits() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithDigits::class.java, "value")
            assertThat(schema.maximum).isNull()
            assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            assertThat(schema.multipleOf).isNull()
            assertThat(schema.minimum).isNull()
            assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testDigitsWithFraction() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            listOf("valueWithFraction", "valueList").forEach { fieldName ->
                val schema = getSchema(WithDigits::class.java, fieldName)
                assertThat(schema.maximum).isEqualTo("10000")
                assertThat(schema.exclusiveMaximum).isTrue()
                assertThat(schema.multipleOf).isEqualTo("0.01")
                assertThat(schema.minimum).isNull()
                assertThat(schema.exclusiveMinimum as Boolean?).isNull()
            }
        }
    }

    @Test
    fun testDigitsWithNoFraction() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithDigits::class.java, "valueNoFraction")
            // println(schema)
            assertThat(schema.maximum).isEqualTo("10000")
            assertThat(schema.exclusiveMaximum).isTrue()
            assertThat(schema.multipleOf).isEqualTo("1")
            assertThat(schema.minimum).isNull()
            assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }
}

private data class WithDigits(
    val value: BigDecimal,
    @field:Digits(fraction = 2, integer = 4)
    val valueWithFraction: BigDecimal,
    @field:Digits.List(Digits(fraction = 2, integer = 4))
    val valueList: BigDecimal,
    @field:Digits(fraction = 0, integer = 4)
    val valueNoFraction: BigDecimal
)

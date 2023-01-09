package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springdoc.core.SpringDocConfigProperties
import org.springdoc.core.SpringDocConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import pl.sparkbit.commons.validators.InRange
import java.math.BigDecimal

class InRangeCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(InRangeCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)

    @Test
    fun testNoNegativeOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithInRange::class.java, "value")

            Assertions.assertThat(schema.maximum).isNull()
            Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            Assertions.assertThat(schema.minimum).isNull()
            Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testNegativeOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithInRange::class.java, "valueInRage")

            Assertions.assertThat(schema.maximum).isEqualTo(BigDecimal("2.22"))
            Assertions.assertThat(schema.exclusiveMaximum).isFalse()
            Assertions.assertThat(schema.minimum).isEqualTo(BigDecimal("1.11"))
            Assertions.assertThat(schema.exclusiveMinimum).isFalse()
        }
    }
}

private data class WithInRange(
    val value: BigDecimal,
    @field:InRange(min = 1.11, max = 2.22)
    val valueInRage: BigDecimal
)

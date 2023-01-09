package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springdoc.core.SpringDocConfigProperties
import org.springdoc.core.SpringDocConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import javax.validation.constraints.NegativeOrZero

class NegativeOrZeroCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(NegativeOrZeroCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)

    @Test
    fun testNoNegativeOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithNegativeOrZero::class.java, "value")

            Assertions.assertThat(schema.maximum).isNull()
            Assertions.assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            Assertions.assertThat(schema.minimum).isNull()
            Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
        }
    }

    @Test
    fun testNegativeOrZeroValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            listOf("valueList", "valueNegativeOrZero").forEach { fieldName ->
                val schema = getSchema(WithNegativeOrZero::class.java, fieldName)

                Assertions.assertThat(schema.maximum).isEqualTo(BigDecimal.ZERO)
                Assertions.assertThat(schema.exclusiveMaximum).isFalse()
                Assertions.assertThat(schema.minimum).isNull()
                Assertions.assertThat(schema.exclusiveMinimum as Boolean?).isNull()
            }
        }
    }
}

private data class WithNegativeOrZero(
    val value: BigDecimal,
    @field:NegativeOrZero
    val valueNegativeOrZero: BigDecimal,
    @field:NegativeOrZero.List(NegativeOrZero())
    val valueList: BigDecimal
)

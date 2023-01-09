package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springdoc.core.SpringDocConfigProperties
import org.springdoc.core.SpringDocConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.Digits
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero
import kotlin.reflect.KClass


class MetaAnnotationTest : PropertyCustomizerTest() {

    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, OpenApiAutoConfiguration::class.java, SpringDocConfigProperties::class.java))

    @Test
    fun testDefaultConfig() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val beanSchema = getSchema(WithProbability::class.java)
            Assertions.assertThat(beanSchema.required).contains("value")

            val schema = getSchema(WithProbability::class.java, "value")
            Assertions.assertThat(schema.multipleOf).isEqualTo("0.01")
            Assertions.assertThat(schema.minimum).isEqualByComparingTo(BigDecimal.ZERO)
            Assertions.assertThat(schema.exclusiveMinimum).isFalse()
            Assertions.assertThat(schema.maximum).isEqualByComparingTo(BigDecimal.ONE)
            Assertions.assertThat(schema.exclusiveMaximum).isFalse()
        }
    }
}

@Digits(fraction = 2, integer = 1)
@PositiveOrZero
@DecimalMax("1.00")
@NotNull
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@MustBeDocumented
annotation class Probability(
    val message: String = "{pl.sparkbit.commons.openapi.constraints.Probability}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

private data class WithProbability(
    @field:Probability
    val value: BigDecimal?,
)

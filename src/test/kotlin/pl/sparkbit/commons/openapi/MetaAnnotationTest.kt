package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import java.math.BigDecimal
import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.springdoc.core.configuration.SpringDocConfiguration
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springframework.format.support.FormattingConversionService
import kotlin.reflect.KClass


class MetaAnnotationTest : PropertyCustomizerTest() {

    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java,
                OpenApiAutoConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean("mvcConversionService", FormattingConversionService::class.java)

    @Test
    fun testMetaAnnotation() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val beanSchema = getSchema(WithProbability::class.java)
            assertThat(beanSchema.required).contains("value")

            val schema = getSchema(WithProbability::class.java, "value")
            assertThat(schema.multipleOf).isEqualTo("0.01")
            assertThat(schema.minimum).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(schema.exclusiveMinimum as Boolean?).isFalse()
            assertThat(schema.maximum).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(schema.exclusiveMaximum as Boolean?).isFalse()
            assertThat(schema.pattern).isNull()
            assertThat(schema.minLength as Int?).isNull()
            assertThat(schema.maxLength as Int?).isNull()
        }
    }

    @Test
    fun testMultipleAnnotations() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val beanSchema = getSchema(WithProbability::class.java)
            assertThat(beanSchema.required).contains("pattern")

            val schema = getSchema(WithProbability::class.java, "pattern")
            assertThat(schema.multipleOf).isNull()
            assertThat(schema.minimum).isNull()
            assertThat(schema.exclusiveMinimum as Boolean?).isNull()
            assertThat(schema.maximum).isNull()
            assertThat(schema.exclusiveMaximum as Boolean?).isNull()
            assertThat(schema.pattern).isEqualTo("[A-Z0-9]{4,8}")
            assertThat(schema.minLength).isEqualTo(4)
            assertThat(schema.maxLength).isEqualTo(8)
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
    @field:NotNull
    @field:NotBlank
    @field:Size(max = 8, min = 4)
    @field:Pattern(regexp = "[A-Z0-9]{4,8}")
    val pattern: String?
)

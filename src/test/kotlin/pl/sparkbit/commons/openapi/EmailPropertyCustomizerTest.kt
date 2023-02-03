package pl.sparkbit.commons.openapi

import org.assertj.core.api.Assertions
import org.junit.Test
import org.springdoc.core.SpringDocConfigProperties
import org.springdoc.core.SpringDocConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import javax.validation.constraints.Email

class EmailPropertyCustomizerTest : PropertyCustomizerTest() {
    private val contextRunner = WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SpringDocConfiguration::class.java, SpringDocConfigProperties::class.java))
        .withBean(EmailCustomizer::class.java)
        .withBean(BeansValidationModel::class.java)

    @Test
    fun testNoEmailValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithEmailValue::class.java, "otherString")

            Assertions.assertThat(schema.format).isNull()
        }
    }

    @Test
    fun testEmailValue() {
        contextRunner.run { _: AssertableWebApplicationContext? ->
            val schema = getSchema(WithEmailValue::class.java, "email")
            Assertions.assertThat(schema.format).isEqualTo("email")
        }
    }

}

private data class WithEmailValue(
    @field:Email
    val email: String,
    val otherString: String,
)

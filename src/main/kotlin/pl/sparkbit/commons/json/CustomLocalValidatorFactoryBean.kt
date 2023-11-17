package pl.sparkbit.commons.json

import jakarta.validation.ClockProvider
import jakarta.validation.Configuration
import jakarta.validation.valueextraction.ValueExtractor
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

open class CustomLocalValidatorFactoryBean(private val valueExtractors: List<ValueExtractor<*>>) : LocalValidatorFactoryBean() {

    override fun postProcessConfiguration(configuration: Configuration<*>) {
        valueExtractors.forEach {
            configuration.addValueExtractor(it)
        }
    }

    /**
     * Beans Validation 2.0 adds this method, but Spring
     * use 1.x as compile-time dependency so we need to implement this
     * (see java-doc for LocalValidatorFactoryBean).
     */
    override fun getClockProvider(): ClockProvider {
        return unwrap(ValidatorFactoryImpl::class.java).clockProvider
    }
}

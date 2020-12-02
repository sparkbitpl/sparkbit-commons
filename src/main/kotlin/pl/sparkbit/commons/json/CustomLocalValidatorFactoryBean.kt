package pl.sparkbit.commons.json

import org.hibernate.validator.internal.engine.ValidatorFactoryImpl
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.ClockProvider
import javax.validation.Configuration
import javax.validation.valueextraction.ValueExtractor

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

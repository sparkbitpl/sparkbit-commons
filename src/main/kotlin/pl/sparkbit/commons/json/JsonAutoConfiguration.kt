package pl.sparkbit.commons.json

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.validation.Validator
import javax.validation.valueextraction.ValueExtractor

@Configuration
open class JsonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Validator::class)
    open fun custom(valueExtractors: List<ValueExtractor<*>>): CustomLocalValidatorFactoryBean {
        return CustomLocalValidatorFactoryBean(valueExtractors)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun jsonFieldValueExtractor(): JsonFieldValueExtractor {
        return JsonFieldValueExtractor()
    }
}

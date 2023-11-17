package pl.sparkbit.commons.json

import jakarta.validation.Validator
import jakarta.validation.valueextraction.ValueExtractor
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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

package pl.sparkbit.commons.exception

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.sparkbit.commons.CommonsProperties
import pl.sparkbit.commons.i18n.Messages

@Configuration
@ConditionalOnProperty(value = [CommonsProperties.REST_ERROR_ATTRIBUTES_ENABLED], havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(ErrorMvcAutoConfiguration::class)
open class RestErrorAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ErrorAttributes::class)
    open fun restErrorAttributes(messages: ObjectProvider<Messages>): RestErrorAttributes {
        return RestErrorAttributes(messages)
    }
}

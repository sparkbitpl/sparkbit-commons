package pl.sparkbit.commons.exception

import org.springframework.boot.context.properties.ConfigurationProperties
import pl.sparkbit.commons.CommonsProperties.REST_ERROR

@ConfigurationProperties(REST_ERROR)
data class RestErrorProperties(
    var includeStacktraceForErrors: Boolean = false
)

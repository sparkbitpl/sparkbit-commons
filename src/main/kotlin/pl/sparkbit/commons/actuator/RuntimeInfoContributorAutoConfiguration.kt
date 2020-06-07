package pl.sparkbit.commons.actuator

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import pl.sparkbit.commons.CommonsProperties.RUNTIME_INFO_CONTRIBUTOR_ENABLED

@Configuration
@ConditionalOnProperty(value = [RUNTIME_INFO_CONTRIBUTOR_ENABLED], havingValue = "true", matchIfMissing = true)
open class RuntimeInfoContributorAutoConfiguration {
    @Bean
    open fun runtimeInfoContributor(environment: Environment): RuntimeInfoContributor {
        return RuntimeInfoContributor(environment)
    }
}

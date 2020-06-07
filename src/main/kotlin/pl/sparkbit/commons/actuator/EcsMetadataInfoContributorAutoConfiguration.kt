package pl.sparkbit.commons.actuator

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.sparkbit.commons.CommonsProperties.ECS_METADATA_INFO_CONTRIBUTOR_ENABLED

@Configuration
@ConditionalOnProperty(value = [ECS_METADATA_INFO_CONTRIBUTOR_ENABLED], havingValue = "true", matchIfMissing = true)
open class EcsMetadataInfoContributorAutoConfiguration {
    @Bean
    open fun ecsMetadataInfoContributor(): EcsMetadataInfoContributor {
        return EcsMetadataInfoContributor()
    }
}

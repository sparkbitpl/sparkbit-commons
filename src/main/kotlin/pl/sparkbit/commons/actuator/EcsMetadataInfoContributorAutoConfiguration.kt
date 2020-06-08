package pl.sparkbit.commons.actuator

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.sparkbit.commons.CommonsProperties.ECS_METADATA_INFO_CONTRIBUTOR_ENABLED

@Configuration
@ConditionalOnProperty(value = [ECS_METADATA_INFO_CONTRIBUTOR_ENABLED], havingValue = "true", matchIfMissing = true)
open class EcsMetadataInfoContributorAutoConfiguration {
    @Bean
    open fun ecsMetadataInfoContributor(
        @Value("\${ECS_CONTAINER_METADATA_URI:}") uri: String
    ): EcsMetadataInfoContributor {
        return EcsMetadataInfoContributor(uri)
    }
}

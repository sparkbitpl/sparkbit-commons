package pl.sparkbit.commons.actuator

import mu.KotlinLogging
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.http.ResponseEntity
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import javax.annotation.PostConstruct

class EcsMetadataInfoContributor(
    private val metadataUri: String
) : InfoContributor {
    private val details = HashMap<String, Map<String, Any>>()
    private val log = KotlinLogging.logger {}

    @PostConstruct
    fun setup() {
        if (metadataUri.isNotEmpty()) {
            val restTemplate = RestTemplate()
            val responseEntity: ResponseEntity<Map<String, String>> = try {
                restTemplate.getForEntity(metadataUri)
            } catch (e: ResourceAccessException) {
                log.warn { "Can't access metadata endpoint: ${e.message}" }
                return
            }
            if (responseEntity.statusCode.is2xxSuccessful) {
                val metadata = responseEntity.body
                if (metadata != null) {
                    val ecsDetails = HashMap<String, Any>()

                    ecsDetails["docker-id"] = metadata.getOrDefault("DockerId", "")
                    ecsDetails["docker-name"] = metadata.getOrDefault("DockerName", "")
                    ecsDetails["image"] = metadata.getOrDefault("Image", "")
                    ecsDetails["image-id"] = metadata.getOrDefault("ImageID", "")
                    ecsDetails["created-at"] = metadata.getOrDefault("CreatedAt", "")
                    ecsDetails["started_at"] = metadata.getOrDefault("StartedAt", "")
                    ecsDetails["type"] = metadata.getOrDefault("Type", "")

                    val labels = metadata["Labels"] as Map<String, Any>
                    ecsDetails["cluster"] = labels.getOrDefault("com.amazonaws.ecs.cluster", "")
                    ecsDetails["container-name"] = labels.getOrDefault("com.amazonaws.ecs.container-name", "")
                    ecsDetails["task-arn"] = labels.getOrDefault("com.amazonaws.ecs.task-arn", "")
                    ecsDetails["task-definition-family"] =
                        labels.getOrDefault("com.amazonaws.ecs.task-definition-family", "")
                    ecsDetails["task-definition-version"] =
                        labels.getOrDefault("com.amazonaws.ecs.task-definition-version", "")

                    details["ecs-metadata"] = ecsDetails
                }
            } else {
                log.warn { "Error status code from metadataUri" }
            }
        } else {
            log.info { "No ECS metadata available. Probably not running on ECS..." }
        }
    }

    override fun contribute(builder: Info.Builder) {
        builder.withDetails(details.toMap())
    }
}

package pl.sparkbit.commons.actuator

import org.springframework.boot.SpringBootVersion
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.core.SpringVersion
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class RuntimeInfoContributor(
    private val environment: Environment
) : InfoContributor {

    private val details = HashMap<String, Map<String, Any?>>()

    @PostConstruct
    fun setup() {
        val runtimeDetails = HashMap<String, Any?>()

        runtimeDetails["java-version"] = System.getProperty("java.version")
        runtimeDetails["kotlin-version"] = KotlinVersion.CURRENT
        runtimeDetails["spring-profiles"] = environment.activeProfiles
        runtimeDetails["spring-version"] = SpringVersion.getVersion()
        runtimeDetails["spring-boot-version"] = SpringBootVersion.getVersion()

        details["runtime"] = runtimeDetails
    }

    override fun contribute(builder: Info.Builder) {
        builder.withDetails(details.toMap())
    }
}

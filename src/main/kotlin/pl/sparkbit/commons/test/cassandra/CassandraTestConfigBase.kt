package pl.sparkbit.commons.test.cassandra

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.testcontainers.containers.CassandraContainer
import org.testcontainers.containers.ContainerState

@Suppress("SpringFacetCodeInspection")
open class CassandraTestConfigBase : AbstractCassandraConfiguration() {

    @Value("\${$TEST_CASSANDRA_IMAGE:cassandra:3.11}")
    private val cassandraImage: String? = null

    @Value("\${$TEST_CASSANDRA_KEYSPACE}")
    private val keyspace: String? = null

    @Value("\${$TEST_CASSANDRA_BASE_PACKAGE}")
    private val basePackage: String? = null

    @Value("\${$TEST_CASSANDRA_INIT_SCRIPTS:}")
    private val initScripts: Array<String> = emptyArray()

    @Bean
    open fun cassandraContainerState(): ContainerState {
        val cassandra = KCassandraContainer(cassandraImage!!)
            .withInitScript("cassandra_keyspace.cql")
            .withTmpFs(mapOf("/var/lib/cassandra" to "rw"))
            // Gossip not needed if we're starting just one node. Disabling waiting for gossip speeds up container starting time
            .withEnv("JVM_OPTS", "-Dcassandra.skip_wait_for_gossip_to_settle=0 -Xmx384M -Xms384M")
        cassandra.start()
        return cassandra
    }

    override fun getContactPoints(): String = cassandraContainerState().containerIpAddress
    override fun getPort(): Int = cassandraContainerState().getMappedPort(CassandraContainer.CQL_PORT)
    override fun getKeyspaceName() = keyspace
    override fun getEntityBasePackages() = if (basePackage != null) arrayOf(basePackage) else arrayOf()
    override fun getLocalDataCenter(): String = "datacenter1"

    override fun getStartupScripts(): MutableList<String> {
        val result: MutableList<String> = mutableListOf()
        initScripts.forEach { script ->
            val text = ClassPathResource(script).inputStream.bufferedReader().use { it.readText() }
            val lines = prepareStartupScript(text)
            result.addAll(lines)
        }
        return result
    }

    // Startup scripts must have each CQL command in one, separate line. This method convert pretty printed CQL script
    // to one-command per line format.
    private fun prepareStartupScript(text: String): List<String> {
        fun removeEmptyLines(text: String) = text.replace(Regex("([\r\n])[\r\n]+"), "$1")
        fun removeNewLineIfNotAfterSemicolon(text: String) = text.replace(Regex("([^;])[\r\n]+"), "$1")
        fun removeTrailingNewLine(text: String) = text.replace(Regex("[\r\n]+$"), "")

        return removeTrailingNewLine(removeNewLineIfNotAfterSemicolon(removeEmptyLines(text))).lines()
    }

    @Bean
    open fun metersRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }

    companion object {
        private const val TEST_CASSANDRA_IMAGE = "cassandra-test.image"
        const val TEST_CASSANDRA_KEYSPACE = "cassandra-test.keyspace"
        private const val TEST_CASSANDRA_BASE_PACKAGE = "cassandra-test.base-package"
        private const val TEST_CASSANDRA_INIT_SCRIPTS = "cassandra-test.init-scripts"
    }
}

class KCassandraContainer(imageName: String) : CassandraContainer<KCassandraContainer>(imageName)

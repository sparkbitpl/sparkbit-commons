package pl.sparkbit.commons.test.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.testcontainers.containers.ContainerState
import org.testcontainers.containers.GenericContainer

@Suppress("unused")
open class RedisTestConfigBase {

    @Value("\${$TEST_REDIS_IMAGE:redis:5.0.8-alpine}")
    private val redisImage: String? = null

    @Value("\${$TEST_REDIS_EXPOSED_PORT:6379}")
    private val redisExposedPort: Int? = null

    @Bean
    open fun redisContainerState(): ContainerState {
        val redis = RedisContainer(redisImage!!).withExposedPorts(redisExposedPort)
        redis.start()
        return redis
    }

    @Bean
    open fun testConnectionFactory(containerState: ContainerState): RedisConnectionFactory {
        return JedisConnectionFactory(
            RedisStandaloneConfiguration(
                containerState.containerIpAddress,
                containerState.firstMappedPort
            )
        )
    }

    companion object {
        private const val TEST_REDIS_IMAGE = "redis-test.image"
        private const val TEST_REDIS_EXPOSED_PORT = "redis-test.exposed-port"
    }
}

class RedisContainer(imageName: String) : GenericContainer<RedisContainer>(imageName)

package pl.sparkbit.commons.test.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;

@SuppressWarnings("unused")
public class RedisTestConfigBase {

    private static final String TEST_REDIS_IMAGE = "redisTest.image";
    private static final String TEST_REDIS_EXPOSED_PORT = "redisTest.exposedPort";

    @Value("${" + TEST_REDIS_IMAGE + ":redis:5.0.8-alpine}")
    private String redisImage;

    @Value("${" + TEST_REDIS_EXPOSED_PORT + ":6379}")
    private Integer redisExposedPort;

    @Bean
    public ContainerState redisContainerState() {
        RedisContainer redis = new RedisContainer(redisImage).withExposedPorts(redisExposedPort);
        redis.start();
        return redis;
    }

    @Bean
    public RedisConnectionFactory testConnectionFactory(ContainerState redisContainerState) {

        return new JedisConnectionFactory(
                new RedisStandaloneConfiguration(
                        redisContainerState.getContainerIpAddress(),
                        redisContainerState.getFirstMappedPort()
                )
        );
    }

    private static class RedisContainer extends GenericContainer<RedisContainer> {
        RedisContainer(String image) {
            super(image);
        }
    }
}

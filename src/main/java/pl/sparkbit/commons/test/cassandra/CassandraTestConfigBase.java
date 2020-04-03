package pl.sparkbit.commons.test.cassandra;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.containers.ContainerState;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.apache.commons.codec.Charsets.UTF_8;

@SuppressWarnings({"unused", "SpringFacetCodeInspection"})
public class CassandraTestConfigBase extends AbstractCassandraConfiguration {

    static final String TEST_CASSANDRA_KEYSPACE = "cassandraTest.keyspace";
    private static final String TEST_CASSANDRA_IMAGE = "cassandraTest.image";
    private static final String TEST_CASSANDRA_BASE_PACKAGE = "cassandraTest.basePackage";
    private static final String TEST_CASSANDRA_INIT_SCRIPTS = "cassandraTest.initScripts";

    @Value("${" + TEST_CASSANDRA_IMAGE + ":cassandra:3.11}")
    private String cassandraImage;

    @Value("${" + TEST_CASSANDRA_KEYSPACE + "}")
    private String keyspace;

    @Value("${" + TEST_CASSANDRA_BASE_PACKAGE + "}")
    private String basePackage;

    @Value("${" + TEST_CASSANDRA_INIT_SCRIPTS + ":}")
    private String[] initScripts;

    @Bean
    public ContainerState cassandraContainerState() {
        CassandraContainer cassandra = new CassandraContainer(cassandraImage)
                .withInitScript("cassandra_keyspace.cql")
                .withTmpFs(singletonMap("/var/lib/cassandra", "rw"))
                // Gossip not needed if we're starting just one node
                // Disabling waiting for gossip speeds up container starting time
                .withEnv("JVM_OPTS", "-Dcassandra.skip_wait_for_gossip_to_settle=0 -Xmx384M -Xms384M");
        cassandra.start();
        return cassandra;
    }

    @NotNull
    @Override
    protected String getContactPoints() {
        return cassandraContainerState().getContainerIpAddress();
    }

    @Override
    protected int getPort() {
        return cassandraContainerState().getMappedPort(CassandraContainer.CQL_PORT);
    }

    @NotNull
    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected boolean getMetricsEnabled() {
        return false;
    }

    @NotNull
    @Override
    public String[] getEntityBasePackages() {
        return basePackage != null ? new String[]{basePackage} : new String[0];
    }

    @NotNull
    @Override
    protected List<String> getStartupScripts() {
        List<String> result = new ArrayList<>();
        Arrays.stream(initScripts).forEach(script -> {
            Resource resource = new ClassPathResource(script);
            try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
                String text = FileCopyUtils.copyToString(reader);
                List<String> lines = prepareStartupScript(text);
                result.addAll(lines);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return result;
    }

    @Bean
    public MeterRegistry metersRegistry() {
        return new SimpleMeterRegistry();
    }

    private List<String> prepareStartupScript(String text) {
        // removeEmptyLines
        text = text.replaceAll("([\\r\\n])[\\r\\n]+", "$1");
        // removeNewLineIfNotAfterSemicolon
        text = text.replaceAll("([^;])[\\r\\n]+", "$1");
        // removeTrailingNewLine
        text = text.replaceAll("[\\r\\n]+$", "");

        return Arrays.asList(text.split("\\r?\\n"));
    }

    private static class CassandraContainer
            extends org.testcontainers.containers.CassandraContainer<CassandraContainer> {
        CassandraContainer(String image) {
            super(image);
        }
    }
}

package pl.sparkbit.commons.mybatis.metrics;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MyBatisMetricsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MyBatisMetricsAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> assertThat(context).doesNotHaveBean(PerformancePlugin.class));
    }

    @Test
    public void testCreatePluginIfCollectorIsDefined() {
        this.contextRunner.withUserConfiguration(MyBatisMetricsCollectorConfiguration.class)
            .run(context -> assertThat(context).hasSingleBean(PerformancePlugin.class));
    }

    @Test
    public void testDisabledPlugin() {
        this.contextRunner.withUserConfiguration(MyBatisMetricsCollectorConfiguration.class)
            .withPropertyValues("sparkbit.commons.mybatis-metrics.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(PerformancePlugin.class));
    }

    @Configuration
    protected static class MyBatisMetricsCollectorConfiguration {

        @Bean
        public MyBatisMetricsCollector myBatisMetricsCollector() {
            return mock(MyBatisMetricsCollector.class);
        }

    }
}
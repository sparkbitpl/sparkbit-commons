package pl.sparkbit.commons.restlogger;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

public class RestLoggingFilterAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RestLoggingFilterAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestLoggingFilter.class);
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
            RestLoggerProperties properties = context.getBean(RestLoggerProperties.class);
            assertThat(properties.getExcludeUrlPatterns()).isEmpty();
        });
    }

    @Test
    public void testSetExcludedUrls() {
        this.contextRunner.withPropertyValues("sparkbit.commons.rest-logger.exclude-url-patterns=/metrics,/info")
            .run(context -> {
                RestLoggerProperties properties = context.getBean(RestLoggerProperties.class);
                assertThat(properties.getExcludeUrlPatterns()).containsExactly("/metrics", "/info");
            });
    }

    @Test
    public void testDisableLogging() {
        this.contextRunner.withPropertyValues("sparkbit.commons.rest-logger.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(RestLoggingFilter.class);
                assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
                assertThat(context).doesNotHaveBean(RestLoggerProperties.class);
            });
    }
}
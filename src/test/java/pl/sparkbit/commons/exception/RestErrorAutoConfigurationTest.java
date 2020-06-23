package pl.sparkbit.commons.exception;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;

import static org.assertj.core.api.Assertions.assertThat;

public class RestErrorAutoConfigurationTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            DispatcherServletAutoConfiguration.class,
            ErrorMvcAutoConfiguration.class,
            RestErrorAutoConfiguration.class
        ));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestErrorAttributes.class);
            assertThat(context).hasSingleBean(DefaultErrorAttributes.class);
            assertThat(context).hasSingleBean(ErrorAttributes.class);
        });
    }

    @Test
    public void testRestErrorAttributesDisabled() {
        this.contextRunner.withPropertyValues(
            "sparkbit.commons.rest-error-attributes-enabled=false"
        ).run(context -> {
            assertThat(context).doesNotHaveBean(RestErrorAttributes.class);
            // default beans defined by ErrorMvcAutoConfiguration
            assertThat(context).hasSingleBean(DefaultErrorAttributes.class);
            assertThat(context).hasSingleBean(ErrorAttributes.class);
        });
    }
}
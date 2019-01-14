package pl.sparkbit.commons.exception;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class RestErrorAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(RestErrorAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestErrorAttributes.class);
        });
    }

    @Test
    public void testRestErrorAttributesDisabled() {
        this.contextRunner.withPropertyValues(
            "sparkbit.commons.rest-error-attributes-enabled=false"
        ).run(context -> {
            assertThat(context).doesNotHaveBean(RestErrorAttributes.class);
        });
    }
}
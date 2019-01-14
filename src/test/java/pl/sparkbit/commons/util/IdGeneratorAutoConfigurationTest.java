package pl.sparkbit.commons.util;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class IdGeneratorAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IdGeneratorAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> assertThat(context).hasSingleBean(IdGenerator.class));
    }

    @Test
    public void testIdGeneratorEnabled() {
        this.contextRunner.withPropertyValues("sparkbit.commons.id-generator-enabled=true")
            .run(context -> assertThat(context).hasSingleBean(IdGenerator.class));
    }
}
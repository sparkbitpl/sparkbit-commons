package pl.sparkbit.commons.util;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

public class ClockAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ClockAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> {
            Clock clock = context.getBean(Clock.class);
            assertThat(clock).isEqualTo(Clock.systemUTC());
        });
    }

    @Test
    public void testDisabledClock() {
        this.contextRunner.withPropertyValues("sparkbit.commons.clock-enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(Clock.class));
    }
}
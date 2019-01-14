package pl.sparkbit.commons.mail;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class MailAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(MailAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> assertThat(context).doesNotHaveBean(MailService.class));
    }

    @Test
    public void testMissingMailProperties() {
        this.contextRunner.withPropertyValues("sparkbit.commons.mail.sendgrid-enabled=true")
            .run(context -> {
                assertThat(context).hasFailed();
            });
    }

    @Test
    public void testSendgridEnabled() {
        this.contextRunner.withPropertyValues(
            "sparkbit.commons.mail.sendgrid-enabled=true",
            "sparkbit.commons.mail.default-sender-address=noreply@sparkbit.pl",
            "sparkbit.commons.mail.default-sender-name=Sparkbit",
            "sparkbit.commons.mail.sendgrid-api-key=fake-key"
        ).run(context -> {
            assertThat(context).hasSingleBean(MailService.class);
            MailProperties props = context.getBean(MailProperties.class);
            assertThat(props.getDefaultSenderAddress()).isEqualTo("noreply@sparkbit.pl");
            assertThat(props.getDefaultSenderName()).isEqualTo("Sparkbit");
            assertThat(props.getSendgridApiKey()).isEqualTo("fake-key");
        });
    }
}
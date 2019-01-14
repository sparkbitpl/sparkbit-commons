package pl.sparkbit.commons.util;

import com.github.ziplet.filter.compression.CompressingFilter;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentCompressionAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ContentCompressionAutoConfiguration.class));

    @Test
    public void testDefaultConfig() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(FilterRegistrationBean.class);
            assertThat(context).hasSingleBean(CompressingFilter.class);
        });
    }

    @Test
    public void testCompressingFilterDisabled() {
        this.contextRunner.withPropertyValues("sparkbit.commons.content-compression.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(FilterRegistrationBean.class);
                assertThat(context).doesNotHaveBean(CompressingFilter.class);
            });
    }

    @Test
    public void testConfigureFilter() {
        this.contextRunner.withPropertyValues(
            "sparkbit.commons.content-compression.enabled=true",
            "sparkbit.commons.content-compression.threshold=512")
            .run(context -> {
                assertThat(context).hasSingleBean(FilterRegistrationBean.class);
                assertThat(context).hasSingleBean(CompressingFilter.class);
                assertThat(context).getBean(ContentCompressionProperties.class)
                    .extracting(ContentCompressionProperties::getThreshold, ContentCompressionProperties::isEnabled)
                    .containsExactly(512, true);
            });
    }

}
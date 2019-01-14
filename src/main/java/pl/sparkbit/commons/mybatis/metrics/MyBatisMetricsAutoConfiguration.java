package pl.sparkbit.commons.mybatis.metrics;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.sparkbit.commons.CommonsProperties.MYBATIS_METRICS_ENABLED;

@ConditionalOnProperty(value = MYBATIS_METRICS_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
public class MyBatisMetricsAutoConfiguration {

    @Bean
    @ConditionalOnBean(MyBatisMetricsCollector.class)
    public PerformancePlugin mybatisPerformancePlugin(MyBatisMetricsCollector collector) {
        return new PerformancePlugin(collector);
    }
}

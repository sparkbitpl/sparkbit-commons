package pl.sparkbit.commons.mybatis.metrics;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.sparkbit.commons.CommonsProperties.MYBATIS_METRICS_ENABLED;

@ConditionalOnProperty(value = MYBATIS_METRICS_ENABLED, havingValue = "true", matchIfMissing = true)
@Configuration
@ConditionalOnClass(PerformancePlugin.class)
// PerformancePlugin implements MyBatis interface org.apache.ibatis.plugin.Interceptor.
// If the interface is not present on classpath (MyBatis is optional Maven/project dependency),
// Spring will get class not found error for PerformancePlugin,
// and it will not instantiate beans defined in this autoconfiguration class
public class MyBatisMetricsAutoConfiguration {

    @Bean
    @ConditionalOnBean(MyBatisMetricsCollector.class)
    public PerformancePlugin mybatisPerformancePlugin(MyBatisMetricsCollector collector) {
        return new PerformancePlugin(collector);
    }
}

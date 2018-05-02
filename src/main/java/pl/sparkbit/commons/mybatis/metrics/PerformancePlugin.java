package pl.sparkbit.commons.mybatis.metrics;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

import static pl.sparkbit.commons.CommonsProperties.MYBATIS_METRICS_ENABLED;

/**
 * To use this class it must be registered with MyBatis and implementation of MyBatisMetricsCollector
 * must be present in the Spring context.
 */
@ConditionalOnProperty(value = MYBATIS_METRICS_ENABLED, havingValue = "true")
@Component
@Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class,
                        CacheKey.class, BoundSql.class})
})
@RequiredArgsConstructor
@SuppressWarnings({"unused", "checkstyle:indentation"})
public class PerformancePlugin implements Interceptor {

    private final MyBatisMetricsCollector collector;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Instant start = Instant.now();
        Object firstArg = invocation.getArgs()[0];
        MappedStatement ms = (MappedStatement) firstArg;
        try {
            return invocation.proceed();
        } finally {
            Instant end = Instant.now();
            collector.recordSQLStatementExecutionTime(ms.getId(), Duration.between(start, end));
            collector.recordSQLStatement(ms.getId());
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}

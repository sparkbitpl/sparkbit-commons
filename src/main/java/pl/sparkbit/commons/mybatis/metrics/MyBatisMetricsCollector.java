package pl.sparkbit.commons.mybatis.metrics;

import java.time.Duration;

public interface MyBatisMetricsCollector {

    void recordSQLStatementExecutionTime(String queryId, Duration executionTime);

    void recordSQLStatement(String queryId);
}

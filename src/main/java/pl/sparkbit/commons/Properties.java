package pl.sparkbit.commons;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class Properties {

    public static final String BUILD_INFO_ENABLED = "sparkbit.commons.buildInfo.enabled";
    public static final String CLOCK_ENABLED = "sparkbit.commons.clock.enabled";
    public static final String ID_GENERATOR_ENABLED = "sparkbit.commons.idGenerator.enabled";
    public static final String MYBATIS_METRICS_ENABLED = "sparkbit.commons.mybatisMetrics.enabled";
    public static final String REQUEST_LOGGING_ENABLED = "sparkbit.commons.requestLogging.enabled";
    public static final String STATSD_ENABLED = "sparkbit.commons.statsd.enabled";
    public static final String STATSD_HOST = "sparkbit.commons.statsd.host";
    public static final String STATSD_PORT = "sparkbit.commons.statsd.port";
    public static final String STATSD_PREFIX = "sparkbit.commons.statsd.prefix";
}

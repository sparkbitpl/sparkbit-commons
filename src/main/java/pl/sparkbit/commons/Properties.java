package pl.sparkbit.commons;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class Properties {

    private static final String PREFIX = "sparkbit.commons.";

    public static final String BUILD_INFO_ENABLED = PREFIX + "buildInfo.enabled";
    public static final String CLOCK_ENABLED = PREFIX + "clock.enabled";
    public static final String CONTENT_COMPRESSION_ENABLED = PREFIX + "contentCompression.enabled";
    public static final String CONTENT_COMPRESSION_THRESHOLD = PREFIX + "contentCompression.threshold";
    public static final String ID_GENERATOR_ENABLED = PREFIX + "idGenerator.enabled";
    public static final String MYBATIS_METRICS_ENABLED = PREFIX + "mybatisMetrics.enabled";
    public static final String REQUEST_LOGGING_ENABLED = PREFIX + "requestLogging.enabled";
    public static final String STATSD_ENABLED = PREFIX + "statsd.enabled";
    public static final String STATSD_HOST = PREFIX + "statsd.host";
    public static final String STATSD_PORT = PREFIX + "statsd.port";
    public static final String STATSD_PREFIX = PREFIX + "statsd.prefix";
}

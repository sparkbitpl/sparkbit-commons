package pl.sparkbit.commons;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class Properties {

    private static final String PREFIX = "sparkbit.commons.";
    private static final String TEST_DB_PREFIX = "test.db.";

    public static final String BUILD_INFO_ENABLED = PREFIX + "buildInfo.enabled";
    public static final String CLOCK_ENABLED = PREFIX + "clock.enabled";
    public static final String CONTENT_COMPRESSION_ENABLED = PREFIX + "contentCompression.enabled";
    public static final String CONTENT_COMPRESSION_THRESHOLD = PREFIX + "contentCompression.threshold";
    public static final String ID_GENERATOR_ENABLED = PREFIX + "idGenerator.enabled";
    public static final String MAIL_DEFAULT_SENDER_ADDRESS = PREFIX + "mail.defaultSender.address";
    public static final String MAIL_DEFAULT_SENDER_NAME = PREFIX + "mail.defaultSender.name";
    public static final String MAIL_SENDGRID_API_KEY = PREFIX + "mail.sendgrid.apiKey";
    public static final String MAIL_SENDGRID_ENABLED = PREFIX + "mail.sendgrid.enabled";
    public static final String MYBATIS_METRICS_ENABLED = PREFIX + "mybatisMetrics.enabled";
    public static final String REQUEST_LOGGING_ENABLED = PREFIX + "requestLogging.enabled";
    public static final String STATSD_ENABLED = PREFIX + "statsd.enabled";
    public static final String STATSD_HOST = PREFIX + "statsd.host";
    public static final String STATSD_PORT = PREFIX + "statsd.port";
    public static final String STATSD_PREFIX = PREFIX + "statsd.prefix";
    public static final String TEST_DB_DRIVER_CLASS_NAME = TEST_DB_PREFIX + "driverClassName";
    public static final String TEST_DB_HANDLER_PACKAGES = TEST_DB_PREFIX + "handlerPackages";
    public static final String TEST_DB_PASSWORD = TEST_DB_PREFIX + "password";
    public static final String TEST_DB_SCHEMA_FILES = TEST_DB_PREFIX + "schemaFiles";
    public static final String TEST_DB_TYPE_ALIASES_PACKAGE = TEST_DB_PREFIX + "typeAliasesPackage";
    public static final String TEST_DB_URL = TEST_DB_PREFIX + "url";
    public static final String TEST_DB_USERNAME = TEST_DB_PREFIX + "username";
}

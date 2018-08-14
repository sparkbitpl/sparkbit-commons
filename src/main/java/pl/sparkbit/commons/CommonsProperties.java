package pl.sparkbit.commons;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("sparkbit.commons")
@Data
@SuppressWarnings("WeakerAccess")
@Validated
public class CommonsProperties {

    private static final String PREFIX = "sparkbit.commons.";

    public static final String CLOCK_ENABLED = PREFIX + "clock-enabled";
    public static final String CONTENT_COMPRESSION_ENABLED = PREFIX + "content-compression.enabled";
    public static final String ID_GENERATOR_ENABLED = PREFIX + "id-generator-enabled";
    public static final String MAIL_SENDGRID_ENABLED = PREFIX + "mail.sendgrid-enabled";
    public static final String MYBATIS_METRICS_ENABLED = PREFIX + "mybatis-metrics.enabled";
    public static final String REQUEST_LOGGING_ENABLED = PREFIX + "request-logging-enabled";
    public static final String REST_ERROR_ATTRIBUTES_ENABLED = PREFIX + "rest-error-attributes-enabled";
    public static final String STATSD_ENABLED = PREFIX + "statsd.enabled";

    @NotNull
    private Boolean clockEnabled;
    @NotNull
    private ContentCompression contentCompression;
    @NotNull
    private Boolean idGeneratorEnabled;
    @NotNull
    private Mail mail;
    @NotNull
    private Boolean mybatisMetricsEnabled;
    @NotNull
    private Boolean requestLoggingEnabled;
    @NotNull
    private Boolean restErrorAttributesEnabled;
    @NotNull
    private Statsd statsd;

    @Data
    @Validated
    public static class ContentCompression {
        @NotNull
        private Boolean enabled;
        @NotNull
        private Integer threshold;
    }

    @Data
    @Validated
    public static class Mail {
        private String defaultSenderAddress;
        private String defaultSenderName;
        private String sendgridApiKey;
        @NotNull
        private Boolean sendgridEnabled;
    }

    @Data
    @Validated
    public static class Statsd {
        @NotNull
        private Boolean enabled;
        private String host;
        private Integer port;
        private String prefix;
    }
}

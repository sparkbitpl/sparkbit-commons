package pl.sparkbit.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CommonsProperties {

    private static final String PREFIX = "sparkbit.commons";
    public static final String CLOCK_ENABLED = PREFIX + ".clock-enabled";
    public static final String MAIL = PREFIX + ".mail";
    public static final String MAIL_SENDGRID_ENABLED = MAIL + ".sendgrid-enabled";
    public static final String CONTENT_COMPRESSION = PREFIX + ".content-compression";
    public static final String CONTENT_COMPRESSION_ENABLED = CONTENT_COMPRESSION + ".enabled";
    public static final String ID_GENERATOR_ENABLED = PREFIX + ".id-generator-enabled";
    public static final String MYBATIS_METRICS_ENABLED = PREFIX + ".mybatis-metrics.enabled";
    public static final String REST_ERROR_ATTRIBUTES_ENABLED = PREFIX + ".rest-error-attributes-enabled";
    public static final String ECS_METADATA_INFO_CONTRIBUTOR_ENABLED =
            PREFIX + ".ecs-metadata-info-contributor-enabled";
    public static final String RUNTIME_INFO_CONTRIBUTOR_ENABLED = PREFIX + ".runtime-info-contributor-enabled";
    public static final String REST_ERROR = PREFIX + ".rest-error";
}

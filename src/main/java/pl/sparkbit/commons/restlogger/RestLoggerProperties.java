package pl.sparkbit.commons.restlogger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(RestLoggerProperties.REST_LOGGER_PROPERTIES_PREFIX)
@Data
@Validated
public class RestLoggerProperties {
    static final String REST_LOGGER_PROPERTIES_PREFIX = "sparkbit.commons.rest-logger";
    static final String REST_LOGGER_ENABLED = REST_LOGGER_PROPERTIES_PREFIX + ".enabled";

    private List<String> excludeUrlPatterns = new ArrayList<>();
}

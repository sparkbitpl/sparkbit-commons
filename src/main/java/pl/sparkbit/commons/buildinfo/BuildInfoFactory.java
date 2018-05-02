package pl.sparkbit.commons.buildinfo;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import pl.sparkbit.commons.buildinfo.domain.BuildInfo;

import java.util.Properties;

import static pl.sparkbit.commons.CommonsProperties.BUILD_INFO_ENABLED;

@Component
@ConditionalOnProperty(value = BUILD_INFO_ENABLED, havingValue = "true")
public class BuildInfoFactory {

    private static final String BUILD_PROPERTIES_FILE = "build.properties";

    private static final String APPLICATION_VERSION_PROPERTY = "application_version";
    private static final String BUILD_NUMBER_PROPERTY = "build_number";
    private static final String BUILD_TIMESTAMP_PROPERTY = "build_timestamp";
    private static final String GIT_BRANCH_PROPERTY = "git_branch";
    private static final String GIT_COMMIT_PROPERTY = "git_commit";
    private static final String JOB_NAME_PROPERTY = "job_name";

    private static final String UNKNOWN = "Unknown";

    @Getter
    private final BuildInfo buildInfo;

    public BuildInfoFactory() throws Exception {
        Resource resource = new ClassPathResource(BUILD_PROPERTIES_FILE);
        Properties props = PropertiesLoaderUtils.loadProperties(resource);

        buildInfo = BuildInfo.builder()
                .applicationVersion(getValue(props, APPLICATION_VERSION_PROPERTY))
                .buildNumber(getValue(props, BUILD_NUMBER_PROPERTY))
                .buildTimestamp(getValue(props, BUILD_TIMESTAMP_PROPERTY))
                .gitBranch(getValue(props, GIT_BRANCH_PROPERTY))
                .gitCommit(getValue(props, GIT_COMMIT_PROPERTY))
                .jobName(getValue(props, JOB_NAME_PROPERTY))
                .build();
    }

    private String getValue(Properties properties, String property) {
        final String value = properties.getProperty(property);
        return (value == null || value.startsWith("@") || value.startsWith("${")) ? UNKNOWN : value;
    }
}

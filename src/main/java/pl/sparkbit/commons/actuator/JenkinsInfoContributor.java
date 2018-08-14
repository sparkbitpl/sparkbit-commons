package pl.sparkbit.commons.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

@Component
public class JenkinsInfoContributor implements InfoContributor {

    @Value("${BUILD_NUMBER:#{null}}")
    private String buildNumber;

    @Value("${JOB_NAME:#{null}}")
    private String jobName;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> details = new HashMap<>();

        if (buildNumber != null) {
            details.put("buildNumber", buildNumber);
        }

        if (jobName != null) {
            details.put("jobName", jobName);
        }

        if (!details.isEmpty()) {
            builder.withDetails(singletonMap("jenkins", details));
        }
    }
}

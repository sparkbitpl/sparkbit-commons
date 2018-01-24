package pl.sparkbit.commons.buildinfo.domain;

import lombok.*;

@Builder
@Data
public class BuildInfo {

    private final String applicationVersion;
    private final String buildNumber;
    private final String buildTimestamp;
    private final String gitBranch;
    private final String gitCommit;
    private final String jobName;
}

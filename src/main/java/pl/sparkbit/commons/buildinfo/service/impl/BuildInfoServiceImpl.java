package pl.sparkbit.commons.buildinfo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.sparkbit.commons.buildinfo.BuildInfoFactory;
import pl.sparkbit.commons.buildinfo.domain.BuildInfo;
import pl.sparkbit.commons.buildinfo.service.BuildInfoService;

import static pl.sparkbit.commons.CommonsProperties.BUILD_INFO_ENABLED;

@ConditionalOnProperty(value = BUILD_INFO_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Service
public class BuildInfoServiceImpl implements BuildInfoService {

    private final BuildInfoFactory buildInfoFactory;

    @Override
    public BuildInfo getBuildInfo() {
        return buildInfoFactory.getBuildInfo();
    }
}

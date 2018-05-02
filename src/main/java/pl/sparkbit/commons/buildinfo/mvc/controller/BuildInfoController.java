package pl.sparkbit.commons.buildinfo.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sparkbit.commons.buildinfo.domain.BuildInfo;
import pl.sparkbit.commons.buildinfo.mvc.dto.out.BuildInfoDTO;
import pl.sparkbit.commons.buildinfo.service.BuildInfoService;

import static pl.sparkbit.commons.CommonsProperties.BUILD_INFO_ENABLED;
import static pl.sparkbit.commons.Paths.BUILD_INFO;

@ConditionalOnProperty(value = BUILD_INFO_ENABLED, havingValue = "true")
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class BuildInfoController {

    private final BuildInfoService buildInfoService;

    @GetMapping(BUILD_INFO)
    public BuildInfoDTO getBuildInfo() {
        BuildInfo buildInfo = buildInfoService.getBuildInfo();
        return new BuildInfoDTO(buildInfo);
    }
}

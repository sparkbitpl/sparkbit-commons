package pl.sparkbit.commons.buildinfo.mvc.dto.out;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import pl.sparkbit.commons.buildinfo.domain.BuildInfo;

@RequiredArgsConstructor
public class BuildInfoDTO {

    @Delegate
    private final BuildInfo buildInfo;
}

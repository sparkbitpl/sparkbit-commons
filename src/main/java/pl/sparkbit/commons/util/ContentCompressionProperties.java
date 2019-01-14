package pl.sparkbit.commons.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

import static pl.sparkbit.commons.CommonsProperties.CONTENT_COMPRESSION;

@ConfigurationProperties(CONTENT_COMPRESSION)
@Data
@Validated
public class ContentCompressionProperties {
    private boolean enabled = true;
    @Min(1)
    private int threshold = 1024;
}

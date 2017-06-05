package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationAsSecondsSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (null == duration) {
            jgen.writeNull();
        } else {
            long seconds = duration.getSeconds();
            jgen.writeNumber(seconds);
        }
    }
}

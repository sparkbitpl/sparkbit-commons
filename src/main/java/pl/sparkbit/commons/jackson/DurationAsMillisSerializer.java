package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

@SuppressWarnings("unused")
public class DurationAsMillisSerializer extends JsonSerializer<Duration> {

    @Override
    public void serialize(Duration duration, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (null == duration) {
            jgen.writeNull();
        } else {
            long millis = duration.toMillis();
            jgen.writeNumber(millis);
        }
    }
}

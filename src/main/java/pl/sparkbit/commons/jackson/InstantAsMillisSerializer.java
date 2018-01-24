package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;

@SuppressWarnings("unused")
public class InstantAsMillisSerializer extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant instant, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (null == instant) {
            jgen.writeNull();
        } else {
            long millis = instant.toEpochMilli();
            jgen.writeNumber(millis);
        }
    }
}

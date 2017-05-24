package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.Instant;

public class InstantFromMillisDeserializer extends StdScalarDeserializer<Instant> {

    public InstantFromMillisDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            String s = jp.getText().trim();
            if (s.length() == 0) {
                return null;
            }
            long millis = Long.parseLong(s);
            return Instant.ofEpochMilli(millis);
        }
        throw deserializationContext.wrongTokenException(jp, JsonToken.NOT_AVAILABLE, "expected Number");
    }
}

package pl.sparkbit.commons.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import java.io.IOException;
import java.time.Instant;

import static com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;

@SuppressWarnings("unused")
public class InstantFromMillisDeserializer extends StdScalarDeserializer<Instant> {

    public InstantFromMillisDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        if (jp.getCurrentToken() != VALUE_NUMBER_INT) {
            deserializationContext.reportWrongTokenException(Instant.class, VALUE_NUMBER_INT, "expected integer");
        }

        String s = jp.getText().trim();
        if (s.length() == 0) {
            return null;
        }
        long millis = Long.parseLong(s);
        return Instant.ofEpochMilli(millis);
    }
}
